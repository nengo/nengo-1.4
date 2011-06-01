/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "OutSpikeErrorFunction.java". Description: 
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

import ca.nengo.model.Node;
import ca.nengo.model.nef.impl.NEFEnsembleImpl;
import ca.nengo.model.neuron.impl.SpikingNeuron;

/**
 * A learning function that uses information from the ensemble to modulate the rate
 * of synaptic change.
 * 
 * @author Trevor Bekolay
 */
public class OutSpikeErrorFunction extends AbstractSpikeLearningFunction {
	
	private static final long serialVersionUID = 1L;
	
	private float[] myGain;
	private float[][] myEncoders;
	private float[] myR1;
	private float[] myO2;
	//default values (from Pfister & Gerstner 2006)
	// These need to be in ms!
	private float myA2Plus = 8.8e-11f;
	private float myA3Plus = 5.3e-2f;
	private float myTauPlus = 16.8f;
	private float myTauY = 125.0f;

	/**
	 * Requires information from the post population to modulate learning.
	 * 
	 * @param gain Gain (scale) of the neurons in the post population
	 * @param encoders Encoders (phi tilde) of the neurons in the post population
	 * @param a2Plus Amplitude constant (see Pfister & Gerstner 2006)
	 * @param a3Plus Amplitude constant (see Pfister & Gerstner 2006)
	 * @param tauPlus Time constant (see Pfister & Gerstner 2006)
	 * @param tauY Time constant (see Pfister & Gerstner 2006)
	 */
	public OutSpikeErrorFunction(float[] gain, float[][] encoders, float a2Plus, float a3Plus, float tauPlus, float tauY) {
		super();
		
		myGain = gain;
		myEncoders = encoders;
		myA2Plus = a2Plus;
		myA3Plus = a3Plus;
		myTauPlus = tauPlus;
		myTauY = tauY;
	}

	/**
	 * Requires information from the post population to modulate learning.
	 * 
	 * @param gain Gain (scale) of the neurons in the post population
	 * @param encoders Encoders (phi tilde) of the neurons in the post population
	 */
	public OutSpikeErrorFunction(float[] gain, float[][] encoders) {
		super();
		
		myGain = gain;
		myEncoders = encoders;
	}

	/**
	 *  Extracts information from the post population to modulate learning.
	 *  
	 * @param ens Post population
	 */
	public OutSpikeErrorFunction(NEFEnsembleImpl ens){
		Node[] nodes = ens.getNodes();
		myGain = new float[nodes.length];
		for (int i=0;i<nodes.length;i++){
			myGain[i]=((SpikingNeuron) nodes[i]).getScale();
		}
		myEncoders=ens.getEncoders();
	}

	@Override
	public void initActivityTraces(int postLength, int preLength) {
		myR1 = new float[preLength];
		myO2 = new float[postLength];
	}
	
	@Override
	public void beforeDOmega(boolean[] preSpiking) {
		for (int i = 0; i < myR1.length; i++) {
			if (preSpiking[i]) {
				myR1[i] += 1.0f;
			}
			myR1[i] -= myR1[i] / myTauPlus;
			if (myR1[i] < 0.0f) {myR1[i] = 0.0f;}
		}
	}

	/**
	 * @see ca.nengo.model.plasticity.impl.AbstractSpikeLearningFunction#deltaOmega(float,float,float,float,int,int,int)
	 */
	protected float deltaOmega(float timeSinceDifferent, float timeSinceSame,
			float currentWeight, float modInput, int postIndex, int preIndex, int dim) {
		float result = myR1[preIndex] * (myA2Plus + myO2[postIndex] * myA3Plus);

		return myLearningRate * result * modInput * myEncoders[postIndex][dim] * myGain[postIndex];
	}
	
	@Override
	public void afterDOmega(boolean[] postSpiking) {
		for (int j = 0; j < myO2.length; j++) {
			if (postSpiking[j]) {
				myO2[j] += 1.0f;
			}
			myO2[j] -= myO2[j] / myTauY;
			if (myO2[j] < 0.0f) {myO2[j] = 0.0f;}
		}
	}
	
	@Override
	public OutSpikeErrorFunction clone() throws CloneNotSupportedException {
		OutSpikeErrorFunction result = (OutSpikeErrorFunction) super.clone();
		result.myGain = myGain.clone();
		result.myEncoders = myEncoders.clone();
		result.myA2Plus = myA2Plus;
		result.myA3Plus = myA3Plus;
		result.myTauPlus = myTauPlus;
		result.myTauY = myTauY;
		
		return result;
	}
}