/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "SpikePlasticityRule.java". Description: 
"A PlasticityRule that accepts spiking input.
  
  Spiking input must be dealt with in order to run firing-rate-based learning rules in 
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

import ca.nengo.math.Function;
import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SpikeOutput;
import ca.nengo.model.plasticity.PlasticityRule;
import ca.nengo.util.MU;

/**
 * <p>A PlasticityRule that accepts spiking input.</p>
 * 
 * <p>Spiking input must be dealt with in order to run firing-rate-based learning rules in 
 * a spiking SimulationMode. Spiking input is also the only way to simulate spike-timing-dependent 
 * plasticity.</p> 
 * 
 * @author Bryan Tripp
 */
public class SpikePlasticityRule implements PlasticityRule {
	
	private static final long serialVersionUID = 1L;
	
	private String myTerminationName;
	private String myOriginName;
	private String myModTermName;
	private int myModTermDim;
	private Function myOnInSpikeFunction;
	private Function myOnOutSpikeFunction;
	
	private float[] myLastInSpike;
	private float[] myLastOutSpike;
	private boolean[] myInSpiking;
	private boolean[] myOutSpiking;
	private float myModInput;
	private float[] myModInputArray;
	private float[] myOriginState;	
	private float[] myTerminationState;	
	
	/**
	 * @param origin Name of Origin from which post-synaptic activity is drawn
	 * @param modTerm Name of the Termination from which modulatory input is drawn (can be null if not used)
	 * @param modTermDim Dimension index of the modulatory input within above Termination 
	 * @param onInSpike Function defining synaptic weight change when there is an <bold>incoming</bold> spike. The function  
	 * 		must have three dimensions: 1) time since last post-synaptic spike; 2) existing weight; 3) modulatory input.  
	 * 		(Any of these can be ignored.) 
	 * @param onOutSpike Function defining synaptic weight change when there is an <bold>outgoing</bold> spike. The function 
	 * 		must have three dimensions: 1) time since last pre-synaptic spike; 2) existing weight; 3) modulatory input.
	 * 		(Any of these can be ignored.)
	 * @param termDim Dimension of Termination this rule applies to 
	 * @param originDim Dimension of post-synaptic activity (eg number of neurons if rule belongs to an Ensemble) 
	 */
	public SpikePlasticityRule(String termination, String origin, String modTerm, int modTermDim, Function onInSpike, Function onOutSpike, int termDim, int originDim) {
		setOriginName(origin);
		setTerminationName(termination);
		setModTermName(modTerm);
		myModTermDim = modTermDim;
		setOnInSpike(onInSpike);
		setOnOutSpike(onOutSpike);
	
		setTermDim(termDim);
		setOriginDim(originDim);
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
	 * @return Name of Origin from which post-synaptic activity is drawn
	 */
	public String getTerminationName() {
		return myTerminationName;
	}
	
	/**
	 * 
	 * @param name Name of Origin from which post-synaptic activity is drawn
	 */
	public void setTerminationName(String name) {
		myTerminationName = (name == null) ? "" : name;
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
	 * @return Dimension index of the modulatory input within above Termination 
	 */
	public int getModTermDim() {
		return myModTermDim;
	}

	/**
	 * 
	 * @param dim Dimension index of the modulatory input within above Termination 
	 */
	public void setModTermDim(int dim) {
		myModTermDim = dim;
	}
	
	/**
	 * @return Function defining synaptic weight change when there is an <bold>incoming</bold> spike. 
	 */
	public Function getOnInSpike() {
		return myOnInSpikeFunction;
	}
	
	/**
	 * @param function Function defining synaptic weight change when there is an <bold>incoming</bold> spike. 
	 */
	public void setOnInSpike(Function function) {
		if (function.getDimension() != 6) {
			throw new IllegalArgumentException("Function must have three dimensions: " +
					"1) time since last post-synaptic spike; 2) existing weight; 3) modulatory input.");
		}
		myOnInSpikeFunction = function;
	}
	
	/**
	 * @return Function defining synaptic weight change when there is an <bold>outgoing</bold> spike.
	 */
	public Function getOnOutSpike() {
		return myOnOutSpikeFunction;
	}
	
	/**
	 * 
	 * @param function Function defining synaptic weight change when there is an <bold>outgoing</bold> spike.
	 */
	public void setOnOutSpike(Function function) {
		if (function.getDimension() != 6) {
			throw new IllegalArgumentException("Function must have three dimensions: " +
					"1) time since last post-synaptic spike; 2) existing weight; 3) modulatory input.");
		}
		myOnOutSpikeFunction = function;
	}

	/**
	 * @return Dimension of Termination this rule applies to 
	 */
	public int getTermDim() {
		return myInSpiking.length;
	}
	
	/**
	 * 
	 * @param dim Dimension of Termination this rule applies to 
	 */
	public void setTermDim(int dim) {
		myLastInSpike = MU.uniform(1, dim, -1)[0];
		myInSpiking = new boolean[dim];
	}

	/**
	 * @return Dimension of post-synaptic activity (eg number of neurons if rule belongs to an Ensemble) 
	 */
	public int getOriginDim() {
		return myOutSpiking.length;
	}

	/**
	 * @param dim Dimension of post-synaptic activity (eg number of neurons if rule belongs to an Ensemble) 
	 */
	public void setOriginDim(int dim) {
		myLastOutSpike = MU.uniform(1, dim, -1)[0];		
		myOutSpiking = new boolean[dim];		
	}
	
	/**
	 * @see ca.nengo.model.plasticity.PlasticityRule#setOriginState(java.lang.String, ca.nengo.model.InstantaneousOutput, float)
	 */
	public void setOriginState(String name, InstantaneousOutput state, float time) {
		if (name.equals(myOriginName)) {
			if (state instanceof SpikeOutput) {
				update(myLastOutSpike, myOutSpiking, state, time);
			} else if (state instanceof RealOutput) {
				myOriginState = ((RealOutput) state).getValues();
			}			
		}
	}

	/**
	 * @see ca.nengo.model.plasticity.PlasticityRule#setTerminationState(java.lang.String, ca.nengo.model.InstantaneousOutput, float)
	 */
	public void setTerminationState(String name, InstantaneousOutput state, float time) {
		if (name.equals(myModTermName)) {
			checkType(state);
			myModInputArray=((RealOutput) state).getValues();
			myModInput = myModInputArray[myModTermDim];
		} else if (name.equals(myTerminationName)) {
			myTerminationState = ((RealOutput) state).getValues();
		}
	}

	/**
	 * @see ca.nengo.model.plasticity.PlasticityRule#getDerivative(float[][], ca.nengo.model.InstantaneousOutput, float)
	 */
	public float[][] getDerivative(float[][] transform, InstantaneousOutput input, float time) {
		if (input instanceof SpikeOutput) {
			update(myLastInSpike, myInSpiking, input, time);
		} 
		
		float[][] result = new float[transform.length][];
		for (int i = 0; i < transform.length; i++) {
			result[i] = new float[transform[i].length];
			for (int j = 0; j < transform[i].length; j++) {
				float os = (myOriginState != null) ? myOriginState[i] : 0;
				//if (myInSpiking[j] && myLastOutSpike[i] >= 0) {
					result[i][j] += myOnInSpikeFunction.map(new float[]{os, myTerminationState[i], time - myLastOutSpike[i], transform[i][j], myModInput, myModInputArray[i]});					
				//}
				//if (myOutSpiking[i] && myLastInSpike[j] >= 0) {
				//	result[i][j] += myOnOutSpikeFunction.map(new float[]{os, myTerminationState[i], time - myLastInSpike[j], transform[i][j], myModInput, myModInputArray[i]});
				//}
			}
		}
		return result;
	}
	
	//updates last spike times if there are any spikes
	private static void update(float[] lastSpikeTimes, boolean[] spiking, InstantaneousOutput state, float time) {
		if (!(state instanceof SpikeOutput)) {
			throw new IllegalArgumentException("This rule does not support input of type " + state.getClass().getName());
		}
		if (lastSpikeTimes.length != state.getDimension()) {
			throw new IllegalArgumentException("Expected activity of dimension " + lastSpikeTimes.length 
					+ ", got dimension " + state.getDimension());
		}
				
		SpikeOutput so = (SpikeOutput) state;
		boolean[] spikes = so.getValues();
		for (int i = 0; i < spikes.length; i++) {
			if (spikes[i]) {
				lastSpikeTimes[i] = time;
				spiking[i] = true;
			} else {
				spiking[i] = false;
			}
		}
	}
	
	private static void checkType(InstantaneousOutput state) {
		if (!(state instanceof RealOutput)) {
			throw new IllegalArgumentException("This rule does not support input of type " + state.getClass().getName());
		}
	}
	
	@Override
	public PlasticityRule clone() throws CloneNotSupportedException {
		SpikePlasticityRule result = (SpikePlasticityRule) super.clone();
		result.myInSpiking = myInSpiking.clone();
		result.myLastInSpike = myLastInSpike.clone();
		result.myLastOutSpike = myLastOutSpike.clone();
		result.myOnInSpikeFunction = myOnInSpikeFunction.clone();
		result.myOnOutSpikeFunction = myOnOutSpikeFunction.clone();
		result.myOutSpiking = myOutSpiking.clone();
		return result;
	}

//	private static float[] initialize(int dim) {
//		float[] result = new float[dim];
//		for (int i = 0; i < dim; i++) {
//			result[i] = -1f;
//		}
//		return result;
//	}
	
}
