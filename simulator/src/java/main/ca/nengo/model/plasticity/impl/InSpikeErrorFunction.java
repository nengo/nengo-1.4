/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "InSpikeErrorFunction.java". Description: 
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
public class InSpikeErrorFunction extends AbstractSpikeLearningFunction {
	
	private static final long serialVersionUID = 1L;
	private static final float LEARNING_RATE = 1e-4f;
	
	private float[] myGain;
	private float[][] myEncoders;
	private float myA2Minus;
	private float myA3Minus;
	private float myTauMinus;
	private float myTauX;

	/**
	 * Requires information from the post population to modulate learning.
	 * 
	 * @param gain Gain (scale) of the neurons in the post population
	 * @param encoders Encoders (phi tilde) of the neurons in the post population
	 * @param a2Minus Amplitude constant (see Pfister & Gerstner 2006)
	 * @param a3Minus Amplitude constant (see Pfister & Gerstner 2006)
	 * @param tauMinus Time constant (see Pfister & Gerstner 2006)
	 * @param tauX Time constant (see Pfister & Gerstner 2006)
	 */
	public InSpikeErrorFunction(float[] gain, float[][] encoders, float a2Minus, float a3Minus, float tauMinus, float tauX) {
		super();
		
		myGain = gain;
		myEncoders = encoders;
		myA2Minus = a2Minus;
		myA3Minus = a3Minus;
		myTauMinus = tauMinus;
		myTauX = tauX;
	}
	
	/**
	 * Requires information from the post population to modulate learning.
	 * 
	 * @param gain Gain (scale) of the neurons in the post population
	 * @param encoders Encoders (phi tilde) of the neurons in the post population
	 */
	public InSpikeErrorFunction(float[] gain, float[][] encoders) {
		super();
		
		// Set default values (from Pfister & Gerstner 2006)
		myGain = gain;
		myEncoders = encoders;
		myA2Minus = 6.6e-3f;
		myA3Minus = 3.1e-3f;
		myTauMinus = 0.0337f;
		myTauX = 0.714f;
	}

	/**
	 * @see ca.nengo.model.plasticity.impl.AbstractSpikeLearningFunction#deltaOmega(float,float,float,float,int,int,int)
	 */
	protected float deltaOmega(float timeSinceDifferent, float timeSinceSame,
			float currentWeight, float modInput, int postIndex, int preIndex, int dim) {
		float o1, r2;
		float result;
		
		if (timeSinceDifferent < myTauMinus) {
			o1 = (float)Math.exp(-timeSinceDifferent/myTauMinus);
		} else {
			o1 = 0.0f;
		}
		
		if (timeSinceSame < myTauX) {
			r2 = (float)Math.exp(-timeSinceSame/myTauX);
		} else {
			r2 = 0.0f;
		}
		
		result = o1 * (myA2Minus + r2 * myA3Minus);

		return LEARNING_RATE * result * modInput * myEncoders[postIndex][dim] * myGain[postIndex];
	}
	
	@Override
	public InSpikeErrorFunction clone() throws CloneNotSupportedException {
		InSpikeErrorFunction result = (InSpikeErrorFunction) super.clone();
		result.myGain = myGain.clone();
		result.myEncoders = myEncoders.clone();
		result.myA2Minus = myA2Minus;
		result.myA3Minus = myA3Minus;
		result.myTauMinus = myTauMinus;
		result.myTauX = myTauX;
		
		return result;
	}
}