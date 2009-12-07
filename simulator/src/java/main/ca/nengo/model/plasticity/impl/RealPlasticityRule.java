/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "RealPlasticityRule.java". Description: 
"A basic implementation of EnsemblePlasticityRule"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU 
Public License license (the GPL License), in which case the provisions of GPL 
License are applicable  instead of those above. If you wish to allow use of your 
version of this file only under the terms of the GPL License and not to allow 
others to use your version of this file under the MPL, indicate your decision 
by deleting the provisions above and replace  them with the notice and other 
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
*/

/*
 * Created on 30-Jan-2007
 */
package ca.nengo.model.plasticity.impl;

import ca.nengo.math.Function;
import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.RealOutput;
import ca.nengo.model.plasticity.PlasticityRule;

/**
 * A basic implementation of EnsemblePlasticityRule. The learning rate is defined by a function of four inputs: 
 * a presynaptic variable, a postsynaptic variable, a transformation weight, and a single modulatory input. The  
 * function is user-defined and may ignore any of these inputs. This learning rate function is applied to each 
 * element of the transformation matrix of the Termination to which this rule applies. In each case, the presynaptic-variable 
 * input to the function is the corresponding dimension of input to the Termination. The postsynaptic variable is taken 
 * as the corresponding dimension of the Origin NEFEnsemble.X. This implementation supports only a single separate 
 * modulatory variable. This is also user-defined, as one dimension of some other Termination onto the same NEFEnsemble.   
 * 
 * TODO: test
 * 
 * @author Bryan Tripp
 */
public class RealPlasticityRule implements PlasticityRule {

	private static final long serialVersionUID = 1L;
	
	private String myModTermName;
	private int myModTermDim;
	private Function myFunction;
	private String myOriginName;
	
	private float myModInput;
	private float[] myModInputArray;
	private float[] myOriginState;	
	
	/**
	 * @param modTermName Name of the Termination from which modulatory input is drawn (can be null if not used)
	 * @param modTermDim Dimension index of the modulatory input within above Termination 
	 * @param function Four-dimensional function defining the rate of change of transformation matrix weights. The (scalar) 
	 * 		inputs are presynaptic state, postsynaptic state, existing weight, and modulatory input. This function is 
	 * 		applied to each element of the transformation matrix on the Termination to which this PlastityRule applies.
	 * 		See class documentation for more details.     
	 * @param originName Name of Origin from which post-synaptic activity is drawn
	 */
	public RealPlasticityRule(String modTermName, int modTermDim, Function function, String originName) {
		setModTermName(modTermName);
		setModTermDim(modTermDim);
		setFunction(function);
		setOriginName(originName);
	}

	/**
	 * @return Name of the Termination from which modulatory input is drawn (can be null if not used)
	 */
	public String getModTermName() {
		return myModTermName;
	}

	/**
	 * @param name Name of the Termination from which modulatory input is drawn (can be null if not used)
	 */
	public void setModTermName(String name) {
		myModTermName = (name == null) ? "" : name; 
	}

	/**
	 * @return Dimension index of the modulatory input within above Termination
	 */
	public int getModTermDim() {
		return myModTermDim;
	}
	
	/**
	 * @param dim Dimension index of the modulatory input within above Termination 
	 */
	public void setModTermDim(int dim) {
		myModTermDim = dim;
	}

	/**
	 * @return Four-dimensional function defining the rate of change of transformation matrix weights.
	 */
	public Function getFunction() {
		return myFunction;
	}
	
	/**
	 * 
	 * @param function Four-dimensional function defining the rate of change of transformation matrix weights (as in constructor)
	 */
	public void setFunction(Function function) {
		if (function.getDimension() != 7) {
			throw new IllegalArgumentException("Learning rate function has dimension " 
					+ function.getDimension() + " (should be 7)");
		}
		
		myFunction = function;
	}
	
	/**
	 * @return Name of Origin from which post-synaptic activity is drawn
	 */
	public String getOriginName() {
		return myOriginName;
	}
	
	/**
	 * @param originName Name of Origin from which post-synaptic activity is drawn
	 */
	public void setOriginName(String originName) {
		myOriginName = (originName == null) ? "" : originName;
	}
	
	/**
	 * @see ca.nengo.model.plasticity.PlasticityRule#setTerminationState(java.lang.String, ca.nengo.model.InstantaneousOutput, float)
	 */
	public void setTerminationState(String name, InstantaneousOutput state, float time) {
		if (name.equals(myModTermName)) {
			checkType(state);
			myModInputArray=((RealOutput) state).getValues();
			myModInput = myModInputArray[myModTermDim];
		}
	}

	/**
	 * @see ca.nengo.model.plasticity.PlasticityRule#setOriginState(java.lang.String, ca.nengo.model.InstantaneousOutput, float)
	 */
	public void setOriginState(String name, InstantaneousOutput state, float time) {
		if (name.equals(myOriginName)) {
			checkType(state);
			myOriginState = ((RealOutput) state).getValues();
		}
	}

	/**
	 * @see ca.nengo.model.plasticity.PlasticityRule#getDerivative(float[][], ca.nengo.model.InstantaneousOutput, float)
	 */
	public float[][] getDerivative(float[][] transform, InstantaneousOutput input, float time) {
		checkType(input);
		float[] values = ((RealOutput) input).getValues();
		float[][] result = new float[transform.length][];
		if (myModInputArray != null) {
			for (int i = 0; i < transform.length; i++) {
				result[i] = new float[transform[i].length];
				for (int j = 0; j < transform[i].length; j++) {
					float os = (myOriginState != null) ? myOriginState[i] : 0;
					result[i][j] = myFunction.map(new float[]{values[j], os, transform[i][j], myModInput,i,j,myModInputArray[i]});
				}
			}
		} else {
			for (int i = 0; i < transform.length; i++) {
				result[i] = new float[transform[i].length];
					for (int j = 0; j < transform[i].length; j++) {
						float os = (myOriginState != null) ? myOriginState[i] : 0;
						result[i][j] = myFunction.map(new float[]{values[j], os, transform[i][j], myModInput,i,j,0});
					}
				}	
			}
		
		return result;
	}
	
	private static void checkType(InstantaneousOutput state) {
		if (!(state instanceof RealOutput)) {
			throw new IllegalArgumentException("This rule does not support input of type " + state.getClass().getName());
		}
	}

	@Override
	public PlasticityRule clone() throws CloneNotSupportedException {
		RealPlasticityRule result = (RealPlasticityRule) super.clone();
		result.myFunction = myFunction.clone();
		result.myOriginState = myOriginState.clone();
		return result;
	}
	
}
