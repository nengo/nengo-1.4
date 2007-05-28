/*
 * Created on 28-May-07
 */
package ca.neo.model.plasticity.impl;

import ca.neo.math.Function;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.RealOutput;
import ca.neo.model.SpikeOutput;
import ca.neo.model.plasticity.PlasticityRule;

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
	public SpikePlasticityRule(String origin, String modTerm, int modTermDim, Function onInSpike, Function onOutSpike, int termDim, int originDim) {
		myOriginName = origin;
		myModTermName = modTerm;
		myModTermDim = modTermDim;
		myOnInSpikeFunction = onInSpike;
		myOnOutSpikeFunction = onOutSpike;
		
		myLastInSpike = new float[termDim];
		myLastOutSpike = new float[originDim];		
	}
	
	/**
	 * @see ca.neo.model.plasticity.PlasticityRule#setOriginState(java.lang.String, ca.neo.model.InstantaneousOutput)
	 */
	public void setOriginState(String name, InstantaneousOutput state) {
		if (name.equals(myOriginName)) {
			update(myLastOutSpike, state);
		}
	}

	/**
	 * @see ca.neo.model.plasticity.PlasticityRule#setTerminationState(java.lang.String, ca.neo.model.InstantaneousOutput)
	 */
	public void setTerminationState(String name, InstantaneousOutput state) {
		if (name.equals(myModTermName)) {
			myModInput = ((RealOutput) state).getValues()[myModTermDim];
		}
	}

	/**
	 * @see ca.neo.model.plasticity.PlasticityRule#getDerivative(float[][], ca.neo.model.InstantaneousOutput)
	 */
	public float[][] getDerivative(float[][] transform, InstantaneousOutput input) {
		update(myLastInSpike, input);
		
		float[][] result = new float[transform.length][];
		for (int i = 0; i < transform.length; i++) {
			result[i] = new float[transform[i].length];
			for (int j = 0; j < transform[i].length; j++) {
				if (myInSpiking[i]) result[i][j] += myOnInSpikeFunction.map(new float[]{myLastOutSpike[j], transform[i][j], myModInput});
				if (myOutSpiking[j]) result[i][j] += myOnOutSpikeFunction.map(new float[]{myLastInSpike[j], transform[i][j], myModInput});
			}
		}
		return result;
	}
	
	//updates last spike times if there are any spikes
	private static void update(float[] lastSpikeTimes, InstantaneousOutput state) {
		if (!(state instanceof SpikeOutput)) {
			throw new IllegalArgumentException("This rule does not support input of type " + state.getClass().getName());
		}
				
		SpikeOutput so = (SpikeOutput) state;
		boolean[] spikes = so.getValues();
		for (int i = 0; i < spikes.length; i++) {
			if (spikes[i]) lastSpikeTimes[i] = state.getTime();
		}
	}
	
}
