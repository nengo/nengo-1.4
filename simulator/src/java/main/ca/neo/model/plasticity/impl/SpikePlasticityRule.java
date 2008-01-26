/*
 * Created on 28-May-07
 */
package ca.neo.model.plasticity.impl;

import ca.neo.math.Function;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.RealOutput;
import ca.neo.model.SpikeOutput;
import ca.neo.model.plasticity.PlasticityRule;
import ca.neo.util.MU;

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
		setOriginName(origin);
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
		if (function.getDimension() != 3) {
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
		if (function.getDimension() != 3) {
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
	 * @see ca.neo.model.plasticity.PlasticityRule#setOriginState(java.lang.String, ca.neo.model.InstantaneousOutput, float)
	 */
	public void setOriginState(String name, InstantaneousOutput state, float time) {
		if (name.equals(myOriginName)) {
			update(myLastOutSpike, myOutSpiking, state, time);
		}
	}

	/**
	 * @see ca.neo.model.plasticity.PlasticityRule#setTerminationState(java.lang.String, ca.neo.model.InstantaneousOutput, float)
	 */
	public void setTerminationState(String name, InstantaneousOutput state, float time) {
		if (name.equals(myModTermName)) {
			myModInput = ((RealOutput) state).getValues()[myModTermDim];
		}
	}

	/**
	 * @see ca.neo.model.plasticity.PlasticityRule#getDerivative(float[][], ca.neo.model.InstantaneousOutput, float)
	 */
	public float[][] getDerivative(float[][] transform, InstantaneousOutput input, float time) {
		update(myLastInSpike, myInSpiking, input, time);
		
		float[][] result = new float[transform.length][];
		for (int i = 0; i < transform.length; i++) {
			result[i] = new float[transform[i].length];
			for (int j = 0; j < transform[i].length; j++) {
				if (myInSpiking[j] && myLastOutSpike[i] >= 0) {
					result[i][j] += myOnInSpikeFunction.map(new float[]{time - myLastOutSpike[i], transform[i][j], myModInput});					
				}
				if (myOutSpiking[i] && myLastInSpike[j] >= 0) {
					result[i][j] += myOnOutSpikeFunction.map(new float[]{time - myLastInSpike[j], transform[i][j], myModInput});
				}
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

//	private static float[] initialize(int dim) {
//		float[] result = new float[dim];
//		for (int i = 0; i < dim; i++) {
//			result[i] = -1f;
//		}
//		return result;
//	}
	
}
