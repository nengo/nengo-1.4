/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "RealPlasticityRule.java". Description: 
"A basic implementation of PlasticityRule for real valued input"

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

import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.RealOutput;
import ca.nengo.model.plasticity.PlasticityRule;

/**
 * A basic implementation of PlasticityRule for real valued input.
 * 
 * The learning rate is defined by an AbstractRealLearningFunction (see its declaration for
 * the inputs it receives). This learning rate function is applied to each In each case, the presynaptic-variable 
 * input to the function is the corresponding dimension of input to the Termination. The postsynaptic variable is taken 
 * as the corresponding dimension of the Origin NEFEnsemble.X. This implementation supports only a single separate 
 * modulatory variable, though it can be multi-dimensional. This is also user-defined, as some other Termination
 * onto the same NEFEnsemble.   
 * 
 * TODO: test
 * 
 * @author Bryan Tripp
 */
public class RealPlasticityRule implements PlasticityRule {

	private static final long serialVersionUID = 1L;
	
	private AbstractRealLearningFunction myFunction;
	
	private String myModTermName;
	private String myOriginName;
	
	private float[] myModInputArray;
	private float[] myOriginState;
	
	/**
	 * @param function AbstractRealLearningFunction defining the rate of change of transformation matrix weights.
	 * @param originName Name of Origin from which post-synaptic activity is drawn
	 * @param modTermName Name of the Termination from which modulatory input is drawn (can be null if not used)
	 */
	public RealPlasticityRule(AbstractRealLearningFunction function, String originName, String modTermName) {
		setFunction(function);
		setOriginName(originName);
		setModTermName(modTermName);
	}
	
	/**
	 * @see ca.nengo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		myOriginState = null;
		myModInputArray = null;
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
	 * @return AbstractRealLearningFunction defining the rate of change of transformation matrix weights.
	 */
	public AbstractRealLearningFunction getFunction() {
		return myFunction;
	}
	
	/**
	 * 
	 * @param function AbstractRealLearningFunction defining the rate of change of transformation matrix weights (as in constructor)
	 */
	public void setFunction(AbstractRealLearningFunction function) {
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
	 * @see ca.nengo.model.plasticity.PlasticityRule#setModTerminationState(java.lang.String, ca.nengo.model.InstantaneousOutput, float)
	 */
	public void setModTerminationState(String name, InstantaneousOutput state, float time) {
		if (name.equals(myModTermName)) {
			checkType(state);
			myModInputArray=((RealOutput) state).getValues();
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
					for (int k = 0; k < myModInputArray.length; k++) {
						float os = (myOriginState != null && myOriginState.length > k) ? myOriginState[k] : 0;
						result[i][j] = myFunction.map(new float[]{values[j],time,
								transform[i][j],myModInputArray[k],os,i,j,k});
						
					}
				}
			}
		} else {
			for (int i = 0; i < transform.length; i++) {
				result[i] = new float[transform[i].length];
				for (int j = 0; j < transform[i].length; j++) {
					float os = (myOriginState != null) ? myOriginState[0] : 0;
					result[i][j] = myFunction.map(new float[]{values[j],time,
						transform[i][j],os,0,i,j,0});
				}
			}	
		}
		
		return result;
	}
	
	// Ensure that InstantaneousOutput is real, not spiking
	private static void checkType(InstantaneousOutput state) {
		if (!(state instanceof RealOutput)) {
			throw new IllegalArgumentException("This rule does not support input of type " + state.getClass().getName());
		}
	}

	@Override
	public PlasticityRule clone() throws CloneNotSupportedException {
		RealPlasticityRule result = (RealPlasticityRule) super.clone();
		result.myFunction = myFunction.clone();
		return result;
	}
	
}
