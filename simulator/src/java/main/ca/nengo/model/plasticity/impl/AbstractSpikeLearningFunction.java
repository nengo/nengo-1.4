/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "ErrorLearningFunction.java". Description: 
"A learning function that uses information from the ensemble to modulate the rate
of synaptic change.

 @author Trevor Bekolay"

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
 * Created on 03-Jul-10
 */
package ca.nengo.model.plasticity.impl;

import ca.nengo.math.impl.AbstractFunction;

/**
 * Defines the input available to a spike-based learning rule function.
 * 
 * @author Trevor Bekolay
 */
public abstract class AbstractSpikeLearningFunction extends AbstractFunction {
	
	private static final long serialVersionUID = 1L;
	protected float myLearningRate=1e-4f;

	/**
	 * Ensures that the function is of the correct dimension.
	 */
	public AbstractSpikeLearningFunction() {
		super(7);
	}

	/**
	 * Calls deltaOmega.
	 * 
	 * @see ca.nengo.math.Function#map(float[])
	 */
	public float map(float[] from) {
		// from is a 7-dimensional vector of inputs:
		// from[0] = time since last out spike
		//     [1] = time since last in spike
		//     [2] = transform[i][j]
		//     [3] = myModInputArray[k]
		//     [4] = i = post population index
		//     [5] = j = pre population index
		//     [6] = k = mod input dimension
		
		int i = Math.round(from[4]);
		int j = Math.round(from[5]);
		int k = Math.round(from[6]);

		return deltaOmega(from[0],from[1],from[2],from[3],i,j,k);
	}
	
	/**
	 * A learning rule that defines how the connection weight changes when
	 * a particular spike happens (either presynaptic spike or postsynaptic).
	 * 
	 * @param timeSinceDifferent The amount of time passed since the last spike
	 *		of the different type -- that is, if this is an onInSpike function, it would
	 *		be the amount of time since the last out spike
	 * @param timeSinceSame The amount of time passed since the last spike
	 *		of the same type -- that is, if this is an onInSpike function, it would
	 *		be the amount of time since the last in spike
	 * @param currentWeight The current connection weight between the pre and post neurons
	 * @param modInput The modulatory input, for this particular dimension (see dim)
	 * @param postIndex The neuron index in the post-synaptic population
	 * @param preIndex The neuron index in the pre-synaptic population
	 * @param dim The dimension of the modulatory input
	 */
	protected abstract float deltaOmega(float timeSinceDifferent, float timeSinceSame,
			float currentWeight, float modInput, int postIndex, int preIndex, int dim);
	
	/**
	 * A function that is called before deltaOmega is evaluated.
	 * Useful if certain variables or activity traces need to be updated.
	 * 
	 * @param spiking An array representing whether or not neurons in the
	 *  population of interest are spiking.
	 */
	public void beforeDOmega(boolean[] spiking) {}
	
	/**
	 * A function that is called after deltaOmega is evaluated.
	 * Useful if certain variables or activity traces need to be updated.
	 * 
	 * @param spiking An array representing whether or not neurons in the
	 *  population of interest are spiking.
	 */
	public void afterDOmega(boolean[] spiking) {}
	
	/**
	 * Initializes activity traces in the rule, if any exist.
	 */
	public void initActivityTraces(int postLength, int preLength) {}
	
	public AbstractSpikeLearningFunction clone() throws CloneNotSupportedException {
		return (AbstractSpikeLearningFunction) super.clone();
	}
	
	/**
	 * @param rate New learning rate
	 */
	public void setLearningRate(float rate){
		myLearningRate = rate;
	}
	
	/**
	 * @returns The current learning rate
	 */
	public float getLearningRate(){
		return myLearningRate;
	}
}