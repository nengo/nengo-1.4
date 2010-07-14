/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "AbstractRealLearningFunction.java". Description: 
"Defines the input available to a real-valued learning rule function.

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
 * Defines the input available to a real-valued learning rule function.
 * 
 * @author Trevor Bekolay
 */
public abstract class AbstractRealLearningFunction extends AbstractFunction {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Ensures that the function is of the correct dimension.
	 */
	public AbstractRealLearningFunction() {
		super(8);
	}
	
	/**
	 * Calls deltaOmega.
	 * 
	 * @see ca.nengo.math.Function#map(float[])
	 */
	public float map(float[] from) {
		// from is an 8-dimensional vector of inputs:
		// from[0] = values[j]
		//     [1] = time
		//     [2] = transform[i][j]
		//     [3] = myModInputArray[k]
		//     [4] = origin state
		//     [5] = i = post population index
		//     [6] = j = pre population index
		//     [7] = k = mod and origin state input dimension
		
		int i = Math.round(from[5]);
		int j = Math.round(from[6]);
		int k = Math.round(from[7]);

		return deltaOmega(from[0],from[1],from[2],from[3],from[4],i,j,k);
	}
	
	/**
	 * A learning rule that defines how the connection weight changes on
	 * each timestep (or longer, depending on the plasticity interval).
	 * 
	 * @param input The activity coming into the synapse from its inputs
	 * @param time The current simulation time
	 * @param currentWeight The current connection weight between the pre and post neurons
	 * @param modInput The modulatory input, for this particular dimension (see dim)
	 * @param originState The state of the origin from the postsynaptic population,
	 * 		for this particular dimension (see dim)
	 * @param postIndex The neuron index in the post-synaptic population
	 * @param preIndex The neuron index in the pre-synaptic population
	 * @param dim The dimension of the modulatory input and the origin state
	 */
	protected abstract float deltaOmega(float input, float time, float currentWeight,
			float modInput, float originState, int postIndex, int preIndex, int dim);
	
	public AbstractRealLearningFunction clone() throws CloneNotSupportedException {
		return (AbstractRealLearningFunction) super.clone();
	}
}