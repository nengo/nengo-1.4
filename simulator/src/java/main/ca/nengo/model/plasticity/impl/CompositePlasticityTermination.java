/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "CompositePlasticityRule.java". Description: 
"A PlasticityRule that delegates to underlying spike-based and rate-based rules"

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
 * Created on 29-May-07
 */
package ca.nengo.model.plasticity.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.SpikeOutput;
import ca.nengo.model.SimulationException;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Node;
import ca.nengo.model.plasticity.PlasticityRule;
import ca.nengo.model.impl.PlasticEnsembleTermination;
import ca.nengo.model.impl.LinearExponentialTermination;

/**
 * <p>A PlasticTermination that delegates to underlying spike-based and rate-based rules.  
 * This enables switching between rate and spiking modes in simulations with plasticity.
 * The spike-based rule is used whenever activity at both the plastic termination and 
 * at least one origin are of type SpikeOutput. If either none of the origins has spiking 
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
 * @author Jonathan Lai
 */
public class CompositePlasticityTermination extends PlasticEnsembleTermination {

	private static final long serialVersionUID = 1L;
	
	private PlasticityRule mySpikeRule;
	private PlasticityRule myRealRule;
	private Map<String, InstantaneousOutput> myOriginStates;
	private Map<String, InstantaneousOutput> myTerminationStates;

	/**
	 * @param node The parent Node
	 * @param name Name of this Termination
	 * @param nodeTerminations Node-level Terminations that make up this Termination. Must be
	 *        all LinearExponentialTerminations
	 * @throws StructuralException If dimensions of different terminations are not all the same
	 */
	public CompositePlasticityTermination(Node node, String name, LinearExponentialTermination[] nodeTerminations) throws StructuralException {
		super(node, name, nodeTerminations);
        init(new NullRule(), new NullRule());
	}
	
	/**
	 * @param spikeRule Rule to use when both inputs and outputs are spiking
	 * @param realRule Rule to use when either input or output is real-valued
	 */
	public void init(PlasticityRule spikeRule, PlasticityRule realRule) {
		mySpikeRule = spikeRule;
		myRealRule = realRule;
		myOriginStates = new HashMap<String, InstantaneousOutput>(10);
		myTerminationStates = new HashMap<String, InstantaneousOutput>(10);
	}
	
	/**
	 * @see ca.nengo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		mySpikeRule.reset(randomize);
		myRealRule.reset(randomize);
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
	 * @see ca.nengo.model.plasticity.PlasticityRule#getDerivative(float[][], ca.nengo.model.InstantaneousOutput, float)
	 */
	public float[][] getDerivative(float[][] transform, InstantaneousOutput input, float time) throws StructuralException {
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
			rule.setModTerminationState(name, myTerminationStates.get(name), time);
		}
		
		return rule.getDerivative(transform, input, time);
	}

	/**
	 * @see ca.nengo.model.plasticity.PlasticityRule#getDerivative(float[][], ca.nengo.model.InstantaneousOutput, float)
	 */
	public float[][] getDerivative(float[][] transform, InstantaneousOutput input, float time, int start, int end) throws StructuralException {
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
			rule.setModTerminationState(name, myTerminationStates.get(name), time);
		}
		
		return rule.getDerivative(transform, input, time, start, end);
	}

	/**
	 * @see ca.nengo.model.plasticity.PlasticityRule#setOriginState(java.lang.String, ca.nengo.model.InstantaneousOutput, float)
	 */
	public void setOriginState(String name, InstantaneousOutput state, float time) throws StructuralException {
		myOriginStates.put(name, state);
	}

	/**
	 * @see ca.nengo.model.plasticity.PlasticityRule#setTerminationState(java.lang.String, ca.nengo.model.InstantaneousOutput, float)
	 */
	public void setModTerminationState(String name, InstantaneousOutput state, float time) throws StructuralException {
		myTerminationStates.put(name, state);
	}

	@Override
	public PlasticEnsembleTermination clone() throws CloneNotSupportedException {
		CompositePlasticityTermination result = (CompositePlasticityTermination) super.clone();
		result.myRealRule = myRealRule.clone();
		result.mySpikeRule = mySpikeRule.clone();
		return result;
	}

	/**
	 * A null plasticity rule that always returns zeros from getDerivative(). This can be used within a 
	 * composite rule if learning is to occur only in spiking modes, or only in rate modes.  
	 *  
	 * @author Bryan Tripp
	 */
	public static class NullRule implements PlasticityRule {
		
		private static final long serialVersionUID = 1L;

		/**
		 * @return A zero matrix the same size as the given transform
		 * @see ca.nengo.model.plasticity.PlasticityRule#getDerivative(float[][], ca.nengo.model.InstantaneousOutput, float)
		 */
		public float[][] getDerivative(float[][] transform, InstantaneousOutput input, float time) {
			float[][] result = new float[transform.length][];
			for (int i = 0; i < result.length; i++) {
				result[i] = new float[transform[i].length];
			}
			return result;
		}

		/**
		 * @return A zero matrix the same size as the given transform
		 * @see ca.nengo.model.plasticity.PlasticityRule#getDerivative(float[][], ca.nengo.model.InstantaneousOutput, float, int, int)
		 */
		public float[][] getDerivative(float[][] transform, InstantaneousOutput input, float time, int start, int end) {
			float[][] result = new float[transform.length][];
			for (int i = 0; i < result.length; i++) {
				result[i] = new float[transform[i].length];
			}
			return result;
		}
		
		/**
		 * @see ca.nengo.model.Resettable#reset(boolean)
		 */
		public void reset(boolean randomize) {}

		/**
		 * Does nothing. 
		 * 
		 * @see ca.nengo.model.plasticity.PlasticityRule#setOriginState(java.lang.String, ca.nengo.model.InstantaneousOutput, float)
		 */
		public void setOriginState(String name, InstantaneousOutput state, float time) {}

		/**
		 * Does nothing. 
		 * 
		 * @see ca.nengo.model.plasticity.PlasticityRule#setTerminationState(java.lang.String, ca.nengo.model.InstantaneousOutput, float)
		 */
		public void setModTerminationState(String name, InstantaneousOutput state, float time) {}

		@Override
		public PlasticityRule clone() throws CloneNotSupportedException {
			return (PlasticityRule) super.clone();
		}	
		
	}

}
