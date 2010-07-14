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

/**
 * A learning function that uses information from the ensemble to modulate the rate
 * of synaptic change.
 * 
 * @author Trevor Bekolay
 */
public class ErrorLearningFunction extends AbstractRealLearningFunction {
	
	private static final long serialVersionUID = 1L;
	private static final float LEARNING_RATE = 6e-6f;
	
	private float[] myGain;
	private float[][] myEncoders;
	
	/**
	 * Requires information from the post population to modulate learning.
	 * 
	 * @param gain Gain (scale) of the neurons in the post population
	 * @param encoders Encoders (phi tilde) of the neurons in the post population
	 */
	public ErrorLearningFunction(float[] gain, float[][] encoders) {
		super();
		
		myGain = gain;
		myEncoders = encoders;
	}
	
	/**
	 * @see ca.nengo.model.plasticity.impl.AbstractRealLearningFunction#deltaOmega(float,float,float,float,float,int,int,int)
	 */
	protected float deltaOmega(float input, float time, float currentWeight,
			float modInput, float originState, int postIndex, int preIndex, int dim) {
		// With Oja smoothing
		return LEARNING_RATE * (input * modInput * myEncoders[postIndex][dim] * myGain[postIndex]  - (input*input*currentWeight));
	}
	
	@Override
	public ErrorLearningFunction clone() throws CloneNotSupportedException {
		ErrorLearningFunction result = (ErrorLearningFunction) super.clone();
		result.myGain = myGain.clone();
		result.myEncoders = myEncoders.clone();
		
		return result;
	}
}