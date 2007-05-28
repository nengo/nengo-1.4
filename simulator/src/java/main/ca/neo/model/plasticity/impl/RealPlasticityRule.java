/*
 * Created on 30-Jan-2007
 */
package ca.neo.model.plasticity.impl;

import ca.neo.math.Function;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.plasticity.PlasticityRule;

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

	private final String myModTermName;
	private final int myModTermDim;
	private final Function myFunction;
	private final String myOriginName;
	
	private float myModInput;
	private float[] myOriginState;	
	
	/**
	 * @param modTermName Name of the Termination from which modulatory input is drawn (can be null if not used)
	 * @param modTermDim Dimension index of the modulatory input within above Termination 
	 * @param function Four-dimensional function defining the rate of change of transformation matrix weights. The (scalar) 
	 * 		inputs are presynaptic state, postsynaptic state, existing weight, and modulatory input. This function is 
	 * 		applied to each element of the transformation matrix on the Termination to which this PlastityRule applies.
	 * 		See class documentation for more details.     
	 */
	public RealPlasticityRule(String modTermName, int modTermDim, Function function, String originName) {
		myModTermName = modTermName;
		myModTermDim = modTermDim;
		myFunction = function;
		myOriginName = originName;
		
		if (function.getDimension() != 4) {
			throw new IllegalArgumentException("Learning rate function has dimension " 
					+ function.getDimension() + " (should be 4)");
		}
	}

	/**
	 * @see ca.neo.model.plasticity.PlasticityRule#setTerminationState(java.lang.String, float[])
	 */
	public void setTerminationState(String name, float[] state) {
		if (name.equals(myModTermName)) {
			myModInput = state[myModTermDim];
		}
	}

	/**
	 * @see ca.neo.model.plasticity.PlasticityRule#setOriginState(java.lang.String, float[])
	 */
	public void setOriginState(String name, float[] state) {
		if (name.equals(myOriginName)) {
			myOriginState = state;
		}
	}

	/**
	 * @see ca.neo.model.plasticity.PlasticityRule#getDerivative(float[][], float[])
	 */
	public float[][] getDerivative(float[][] transform, float[] input) {
		float[][] result = new float[transform.length][];
		for (int i = 0; i < transform.length; i++) {
			result[i] = new float[transform[i].length];
			for (int j = 0; j < transform[i].length; j++) {
				result[i][j] = myFunction.map(new float[]{input[j], myOriginState[i], transform[i][j], myModInput});
			}
		}
		
		return result;
	}

}
