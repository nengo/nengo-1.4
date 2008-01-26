/*
 * Created on 29-May-07
 */
package ca.neo.model.plasticity.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ca.neo.config.ConfigUtil;
import ca.neo.config.Configuration;
import ca.neo.config.impl.ConfigurationImpl;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.SpikeOutput;
import ca.neo.model.plasticity.PlasticityRule;

/**
 * <p>A PlasticityRule that delegates to underlying spike-based and rate-based rules.  
 * This enables switching between rate and spiking modes in simulations with plasticity.
 * The spike-based rule is used whenever activity at both the plastic termination and 
 * at least one origin are of type SpikeOutput. Is either none of the origins has spiking 
 * activity, or the plastic termination doesn't receive spiking input, or both, then the 
 * rate-based rule is used. The type of input to other (potentially modulatory) 
 * terminations is not considered.</p>
 * 
 * <p>Note that using this class introduces substantial overhead. It may be worthwhile to 
 * switch rules manually, or to handle both types explicitly in a custom rule implementation.</p>
 * 
 * TODO: test
 *  
 * @author Bryan Tripp
 */
public class CompositePlasticityRule implements PlasticityRule {

	private PlasticityRule mySpikeRule;
	private PlasticityRule myRealRule;
	private Map<String, InstantaneousOutput> myOriginStates;
	private Map<String, InstantaneousOutput> myTerminationStates;
	
	/**
	 * @param spikeRule Rule to use when both inputs and outputs are spiking
	 * @param realRule Rule to use when either input or output is real-valued
	 */
	public CompositePlasticityRule(PlasticityRule spikeRule, PlasticityRule realRule) {
		mySpikeRule = spikeRule;
		myRealRule = realRule;
		myOriginStates = new HashMap<String, InstantaneousOutput>(10);
		myTerminationStates = new HashMap<String, InstantaneousOutput>(10);
	}
	
	/**
	 * Defaults to <code>NullRule</code>s.
	 */
	public CompositePlasticityRule() {
		this(new NullRule(), new NullRule());
	}
	
	/**
	 * @return Rule to use when both inputs and outputs are spiking
	 */
	public PlasticityRule getSpikeRule() {
		return mySpikeRule;
	}
	
	/**
	 * 
	 * @param rule Rule to use when both inputs and outputs are spiking
	 */
	public void setSpikeRule(PlasticityRule rule) {
		mySpikeRule = rule;
	}
	
	/**
	 * @return Rule to use when either input or output is real-valued
	 */
	public PlasticityRule getRealRule() {
		return myRealRule;
	}
	
	/**
	 * @param rule Rule to use when either input or output is real-valued
	 */
	public void setRealRule(PlasticityRule rule) {
		myRealRule = rule;
	}

	/**
	 * @see ca.neo.model.plasticity.PlasticityRule#getDerivative(float[][], ca.neo.model.InstantaneousOutput, float)
	 */
	public float[][] getDerivative(float[][] transform, InstantaneousOutput input, float time) {
		boolean spikingOutput = false;
		Iterator<InstantaneousOutput> it = myOriginStates.values().iterator();
		while (it.hasNext() && !spikingOutput) {
			if (it.next() instanceof SpikeOutput) spikingOutput = true;
		}		
		boolean spikingInput = (input instanceof SpikeOutput);
		
		PlasticityRule rule = (spikingInput && spikingOutput) ? mySpikeRule : myRealRule; 
		
		Iterator<String> origins = myOriginStates.keySet().iterator();
		while (origins.hasNext()) {
			String name = origins.next();
			rule.setOriginState(name, myOriginStates.get(name), time);
		}
		
		Iterator<String> terminations = myTerminationStates.keySet().iterator();
		while (terminations.hasNext()) {
			String name = terminations.next();
			rule.setTerminationState(name, myTerminationStates.get(name), time);
		}
		
		return rule.getDerivative(transform, input, time);
	}

	/**
	 * @see ca.neo.model.plasticity.PlasticityRule#setOriginState(java.lang.String, ca.neo.model.InstantaneousOutput, float)
	 */
	public void setOriginState(String name, InstantaneousOutput state, float time) {
		myOriginStates.put(name, state);
	}

	/**
	 * @see ca.neo.model.plasticity.PlasticityRule#setTerminationState(java.lang.String, ca.neo.model.InstantaneousOutput, float)
	 */
	public void setTerminationState(String name, InstantaneousOutput state, float time) {
		myTerminationStates.put(name, state);
	}

	/**
	 * A null plasticity rule that always returns zeros from getDerivative(). This can be used within a 
	 * composite rule if learning is to occur only in spiking modes, or only in rate modes.  
	 *  
	 * @author Bryan Tripp
	 */
	public static class NullRule implements PlasticityRule {
		
		/**
		 * @return A zero matrix the same size as the given transform
		 * @see ca.neo.model.plasticity.PlasticityRule#getDerivative(float[][], ca.neo.model.InstantaneousOutput, float)
		 */
		public float[][] getDerivative(float[][] transform, InstantaneousOutput input, float time) {
			float[][] result = new float[transform.length][];
			for (int i = 0; i < result.length; i++) {
				result[i] = new float[transform[i].length];
			}
			return result;
		}

		/**
		 * Does nothing. 
		 * 
		 * @see ca.neo.model.plasticity.PlasticityRule#setOriginState(java.lang.String, ca.neo.model.InstantaneousOutput, float)
		 */
		public void setOriginState(String name, InstantaneousOutput state, float time) {
		}

		/**
		 * Does nothing. 
		 * 
		 * @see ca.neo.model.plasticity.PlasticityRule#setTerminationState(java.lang.String, ca.neo.model.InstantaneousOutput, float)
		 */
		public void setTerminationState(String name, InstantaneousOutput state, float time) {
		}		
	}

}
