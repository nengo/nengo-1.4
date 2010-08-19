/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "SpikePlasticityRule.java". Description: 
"A PlasticityRule that accepts spiking input.
  
  Spiking input must be dealt with in order to run learning rules in 
  a spiking SimulationMode"

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
 * Created on 28-May-07
 */
package ca.nengo.model.plasticity.impl;

import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SpikeOutput;
import ca.nengo.model.plasticity.PlasticityRule;
import ca.nengo.util.MU;

/**
 * <p>A PlasticityRule that accepts spiking input.</p>
 * 
 * <p>Spiking input must be dealt with in order to run learning rules in 
 * a spiking SimulationMode. Spiking input is also the only way to simulate spike-timing-dependent 
 * plasticity.</p> 
 * 
 * @author Bryan Tripp
 */
public class SpikePlasticityRule implements PlasticityRule {
	
	private static final long serialVersionUID = 1L;
	// Remember 2 spikes in the past, for triplet based learning rules
	private static final int HISTORY_LENGTH = 2;
	
	private String myOriginName;
	private String myModTermName;
	private AbstractSpikeLearningFunction myOnInSpikeFunction;
	private AbstractSpikeLearningFunction myOnOutSpikeFunction;
	
	private float[][] myInSpikeHistory;
	private float[][] myOutSpikeHistory;
	private boolean[] myInSpiking;
	private boolean[] myOutSpiking;
	
	private float[] myModInputArray;
	
	/**
	 * @param onInSpike AbstractSpikeLearningFunction defining synaptic weight change when there is an <bold>incoming</bold> spike.
	 * @param onOutSpike AbstractSpikeLearningFunction defining synaptic weight change when there is an <bold>outgoing</bold> spike.
	 * @param originName Name of Origin from which post-synaptic activity is drawn
	 * @param modTerm Name of the Termination from which modulatory input is drawn (can be null if not used)
	 */
	public SpikePlasticityRule(AbstractSpikeLearningFunction onInSpike, AbstractSpikeLearningFunction onOutSpike, String originName, String modTermName) {
		setOnInSpike(onInSpike);
		setOnOutSpike(onOutSpike);
		setOriginName(originName);
		setModTermName(modTermName);
	}
	
	/**
	 * @see ca.nengo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		if (myInSpiking != null) {
			setTermDim(myInSpiking.length);
		}
		if (myOutSpiking != null) {
			setOriginDim(myOutSpiking.length);
		}
		myModInputArray = null;
	}
	
	// Sets up the in spiking arrays
	private void setTermDim(int dim) {
		myInSpikeHistory = MU.uniform(HISTORY_LENGTH, dim, -1);
		myInSpiking = new boolean[dim];
	}
	
	// Sets up the out spiking arrays
	private void setOriginDim(int dim) {
		myOutSpikeHistory = MU.uniform(HISTORY_LENGTH, dim, -1);
		myOutSpiking = new boolean[dim];
	}

	/**
	 * @return Name of Origin from which post-synaptic activity is drawn
	 */
	public String getOriginName() {
		return myOriginName;
	}
	
	/**
	 * 
	 * @param name Name of Origin from which post-synaptic activity is drawn
	 */
	public void setOriginName(String name) {
		myOriginName = (name == null) ? "" : name;
	}

	/**
	 * @return Name of the Termination from which modulatory input is drawn (can be null if not used)
	 */
	public String getModTermName() {
		return myModTermName;
	}

	/**
	 * 
	 * @param name Name of the Termination from which modulatory input is drawn (can be null if not used)
	 */
	public void setModTermName(String name) {
		myModTermName = (name == null) ? "" : name;
	}
	
	/**
	 * @return Function defining synaptic weight change when there is an <bold>incoming</bold> spike. 
	 */
	public AbstractSpikeLearningFunction getOnInSpike() {
		return myOnInSpikeFunction;
	}
	
	/**
	 * @param function Function defining synaptic weight change when there is an <bold>incoming</bold> spike. 
	 */
	public void setOnInSpike(AbstractSpikeLearningFunction function) {
		myOnInSpikeFunction = function;
	}
	
	/**
	 * @return Function defining synaptic weight change when there is an <bold>outgoing</bold> spike.
	 */
	public AbstractSpikeLearningFunction getOnOutSpike() {
		return myOnOutSpikeFunction;
	}
	
	/**
	 * 
	 * @param function Function defining synaptic weight change when there is an <bold>outgoing</bold> spike.
	 */
	public void setOnOutSpike(AbstractSpikeLearningFunction function) {
		myOnOutSpikeFunction = function;
	}
	
	/**
	 * @see ca.nengo.model.plasticity.PlasticityRule#setOriginState(java.lang.String, ca.nengo.model.InstantaneousOutput, float)
	 */
	public void setOriginState(String name, InstantaneousOutput state, float time) {
		if (name.equals(myOriginName)) {
			if (myOutSpiking == null) {
				setOriginDim(state.getDimension());
			}
			
			if (state.getDimension() != myOutSpiking.length) {
				throw new IllegalArgumentException("Origin dimensions have changed; should be " 
						+ myOutSpiking.length + ".");
			}
			
			update(myOutSpikeHistory, myOutSpiking, state, time);
		}
	}


	/**
	 * @see ca.nengo.model.plasticity.PlasticityRule#setTerminationState(java.lang.String, ca.nengo.model.InstantaneousOutput, float)
	 */
	public void setModTerminationState(String name, InstantaneousOutput state, float time) {
		if (name.equals(myModTermName)) {
			if (!(state instanceof RealOutput)) {
				throw new IllegalArgumentException("This rule does not support input of type " +
						state.getClass().getName() + " for modulatory input.");
			}
			
			myModInputArray = ((RealOutput) state).getValues();
		}
	}

	/**
	 * @see ca.nengo.model.plasticity.PlasticityRule#getDerivative(float[][], ca.nengo.model.InstantaneousOutput, float)
	 */
	public float[][] getDerivative(float[][] transform, InstantaneousOutput input, float time) {
		if (myInSpiking == null) {
			setTermDim(input.getDimension());
		}
		if (input.getDimension() != myInSpiking.length) {
			throw new IllegalArgumentException("Termination dimensions have changed; should be " 
					+ myInSpiking.length + ".");
		}
		
		update(myInSpikeHistory, myInSpiking, input, time);
		
		// i is post, j is pre
		float[][] result = new float[transform.length][];
		for (int i = 0; i < transform.length; i++) {
			result[i] = new float[transform[i].length];
			
			if (myModInputArray != null) {
				for (int j = 0; j < transform[i].length; j++) {
					if (myInSpiking[j]) {
						for (int k = 0; k < myModInputArray.length; k++) {
							result[i][j] += myOnInSpikeFunction.map(new float[]{time - myOutSpikeHistory[0][i],
								time - myInSpikeHistory[1][i], transform[i][j], myModInputArray[k], i, j, k});
						}
					}
					if (myOutSpiking[i]) {
						for (int k = 0; k < myModInputArray.length; k++) {
							result[i][j] += myOnOutSpikeFunction.map(new float[]{time - myInSpikeHistory[0][j],
								time - myOutSpikeHistory[1][i], transform[i][j], myModInputArray[k], i, j, k});
						}
					}
				}
			} else {
				for (int j = 0; j < transform[i].length; j++) {
					if (myInSpiking[j]) {
						result[i][j] += myOnInSpikeFunction.map(new float[]{time - myOutSpikeHistory[0][i],
							time - myInSpikeHistory[1][i], transform[i][j], 0, i, j, 0});
					}
					if (myOutSpiking[i]) {
						result[i][j] += myOnOutSpikeFunction.map(new float[]{time - myInSpikeHistory[0][j],
							time - myOutSpikeHistory[1][i], transform[i][j], 0, i, j, 0});
					}
				}
			}
		}
		return result;
	}
	
	//updates last spike times if there are any spikes
	private static void update(float[][] spikeHistory, boolean[] spiking, InstantaneousOutput state, float time) {
 		checkType(state);

		if (spikeHistory[0].length != state.getDimension()) {
			throw new IllegalArgumentException("Expected activity of dimension " + spikeHistory[0].length 
					+ ", got dimension " + state.getDimension());
		}
		
		SpikeOutput so = (SpikeOutput) state;
		boolean[] spikes = so.getValues();
		for (int i = 0; i < spikes.length; i++) {
			if (spikes[i]) {
				for (int j = HISTORY_LENGTH-1; j > 0; j--) {
					spikeHistory[j][i] = spikeHistory[j-1][i];
				}
				spikeHistory[0][i] = time;
				spiking[i] = true;
			} else {
				spiking[i] = false;
			}
		}
	}
	
	// Ensure that InstantaneousOutput is spiking, not real
	private static void checkType(InstantaneousOutput state) {
		if (!(state instanceof SpikeOutput)) {
			throw new IllegalArgumentException("This rule does not support input of type " + state.getClass().getName());
		}
	}
	
	@Override
	public PlasticityRule clone() throws CloneNotSupportedException {
		SpikePlasticityRule result = (SpikePlasticityRule) super.clone();
		result.myOnInSpikeFunction = myOnInSpikeFunction.clone();
		result.myOnOutSpikeFunction = myOnOutSpikeFunction.clone();
		result.myOutSpikeHistory = myOutSpikeHistory.clone();
		result.myOutSpiking = myOutSpiking.clone();
		return result;
	}
}