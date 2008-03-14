/*
 * Created on May 4, 2006
 */
package ca.neo.model.neuron.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Node;
import ca.neo.model.SimulationException;
import ca.neo.model.SpikeOutput;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.impl.LinearExponentialTermination;
import ca.neo.model.impl.RealOutputImpl;
import ca.neo.model.neuron.ExpandableSynapticIntegrator;
import ca.neo.model.neuron.SynapticIntegrator;
import ca.neo.model.plasticity.Plastic;
import ca.neo.model.plasticity.PlasticityRule;
import ca.neo.util.TimeSeries1D;
import ca.neo.util.impl.TimeSeries1DImpl;

/**
 * <p>A basic linear <code>SynapticIntegrator</code> model.</p>
 * 
 * <p>Synaptic inputs are individually weighted, passed through decaying 
 * exponential dynamics, and summed.</p> 
 *
 * <p>A synaptic weight corresponds to the time integral of the current induced by 
 * one spike, or to the time integral of current induced by a real-valued input of 
 * 1 over 1 second. Thus a real-valued firing-rate input has roughly the same effect 
 * as a series spikes at the same rate. So a simulation can switch between spike 
 * and rate inputs, with minimal impact and without the need to modify synaptic 
 * weights. </p>
 *  
 * @author Bryan Tripp
 */
public class LinearSynapticIntegrator implements ExpandableSynapticIntegrator, Plastic {

	private static final long serialVersionUID = 1L;
	
	private static final float ourTimeStepCorrection = 1.01f;
	
	private Node myNode;  
	private float myMaxTimeStep;
	private Units myCurrentUnits;
	private Map<String, LinearExponentialTermination> myTerminations;	
	private Map<String, PlasticityRule> myPlasticityRules;
	private float myPlasticityInterval;
	private float myLastPlasticityTime;

	/**
	 * @param maxTimeStep Maximum length of integration time step. Shorter steps may be used to better match
	 * 		length of run(...) 
	 * @param currentUnits Units of current in input weights, scale, bias, and result of run(...)  
	 */	
	public LinearSynapticIntegrator(float maxTimeStep, Units currentUnits) {
		myMaxTimeStep = maxTimeStep * 1.01f; //increased slightly because float/float != integer 
		myCurrentUnits = currentUnits;
		myTerminations = new HashMap<String, LinearExponentialTermination>(10);
		myPlasticityRules = new HashMap<String, PlasticityRule>(10);
		myPlasticityInterval = -1;
		myLastPlasticityTime = 0;		
	}
	
	/**
	 * Defaults to max timestep 1ms and units Units.ACU. 
	 */
	public LinearSynapticIntegrator() {
		this(.001f, Units.ACU);
	}
	
	/**
	 * @see ca.neo.model.neuron.SynapticIntegrator#run(float, float)
	 */
	public TimeSeries1D run(float startTime, float endTime) throws SimulationException {
		float len = endTime - startTime;
		int steps = (int) Math.ceil(len / myMaxTimeStep);
		float dt = len / steps;
		
		float[] times = new float[steps+1];
		float[] currents = new float[steps+1];
		
		times[0] = startTime;
		if (myTerminations.size() == 0) {
			for (int i = 1; i <= steps; i++) {
				times[i] = startTime + (float)i * dt;
			}			
		} else {
			//Note: we leave out decay and real input integration at start time, to make total  
			//decay and integration times equal to simulation time (previously left integration out of 
			//end step, but some spike generators need accurate value at end time)
		
			times[0] = startTime;
			currents[0] = update(myTerminations.values(), true, 0, 0); 
		
			for (int i = 1; i <= steps; i++) {
				times[i] = startTime + (float)i * dt;
				currents[i] = update(myTerminations.values(), false, dt, dt); 
			}			
		}
		
		if (myPlasticityInterval <= 0) {
			learn(startTime, endTime);			
		} else if (endTime >= myLastPlasticityTime + myPlasticityInterval) {
			learn(myLastPlasticityTime, endTime);
			myLastPlasticityTime = endTime;
		}
				
		return new TimeSeries1DImpl(times, currents, myCurrentUnits);
	}
	
	//run plasticity rules (assume constant input/state over given elapsed time)
	private void learn(float startTime, float endTime) throws SimulationException {
		Iterator ruleIter = myPlasticityRules.keySet().iterator();
		while (ruleIter.hasNext()) {
			String name = (String) ruleIter.next();
			LinearExponentialTermination termination = myTerminations.get(name);
			PlasticityRule rule = myPlasticityRules.get(name);
			
			Iterator termIter = myTerminations.keySet().iterator();
			while (termIter.hasNext()) {
				LinearExponentialTermination t = myTerminations.get(termIter.next());
				InstantaneousOutput input = new RealOutputImpl(new float[]{t.getOutput()}, Units.UNK, endTime);
				rule.setTerminationState(t.getName(), input, endTime);					
			}
			
			float[] weights = termination.getWeights();
			float[][] derivative = rule.getDerivative(new float[][]{weights}, termination.getInput(), endTime);
			float scale = (termination.getInput() instanceof SpikeOutput) ? 1 : (endTime - startTime); 			
			for (int i = 0; i < weights.length; i++) {
				weights[i] += derivative[0][i] * scale;
			}			
		}
	}

	//update current in all Terminations 
	private static float update(Collection<LinearExponentialTermination> terminations, boolean spikes, float intTime, float decayTime) {
		float result = 0f;
		
		Iterator<LinearExponentialTermination> it = terminations.iterator();
		while (it.hasNext()) {
			LinearExponentialTermination t = it.next();
			float current = t.updateCurrent(spikes, intTime, decayTime);
			Boolean isModulatory = (Boolean) t.getConfiguration().getProperty(Termination.MODULATORY);
			if (!isModulatory.booleanValue()) result += current;
		}
		
		return result;
	}
	
	/**
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		Iterator<LinearExponentialTermination> it = myTerminations.values().iterator();
		while (it.hasNext()) {
			it.next().reset(false);
		}
	}
	
	public float getMaxTimeStep() {
		return myMaxTimeStep / ourTimeStepCorrection;
	}
	
	public void setMaxTimeStep(float maxTimeStep) {
		myMaxTimeStep = maxTimeStep * ourTimeStepCorrection; //increased slightly because float/float != integer 
	}
	
	public Units getCurrentUnits() {
		return myCurrentUnits;
	}
	
	public void setCurrentUnits(Units units) {
		myCurrentUnits = units;
	}

	/**
	 * @see ca.neo.model.neuron.SynapticIntegrator#getTerminations()
	 */
	public Termination[] getTerminations() {
		return myTerminations.values().toArray(new Termination[0]);
	}

	/**
	 * @see ca.neo.model.neuron.ExpandableSynapticIntegrator#addTermination(java.lang.String, float[], float, boolean)
	 */
	public Termination addTermination(String name, float[] weights, float tauPSC, boolean modulatory) throws StructuralException {
		if (myTerminations.containsKey(name)) {
			throw new StructuralException("This SynapticIntegrator already has a Termination named " + name);
		}
		
		LinearExponentialTermination result = new LinearExponentialTermination(myNode, name, weights, tauPSC); 
		result.getConfiguration().setProperty(Termination.MODULATORY, new Boolean(modulatory));
		myTerminations.put(name, result);
		
		return result;
	}

	/**
	 * @see ca.neo.model.neuron.ExpandableSynapticIntegrator#removeTermination(java.lang.String)
	 */
	public void removeTermination(String name) throws StructuralException {
		myTerminations.remove(name);
	}

	/**
	 * @see ca.neo.model.neuron.SynapticIntegrator#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		return myTerminations.get(name);
	}

	/**
	 * @see ca.neo.model.plasticity.Plastic#setPlasticityRule(java.lang.String, ca.neo.model.plasticity.PlasticityRule)
	 */
	public void setPlasticityRule(String terminationName, PlasticityRule rule) throws StructuralException {
		myPlasticityRules.put(terminationName, rule);
	}

	/**
	 * @see ca.neo.model.plasticity.Plastic#setPlasticityInterval(float)
	 */
	public void setPlasticityInterval(float time) {
		myPlasticityInterval = time;
	}
	
	/**
	 * @param node The parent node (Terminations need a reference to this)
	 */
	public void setNode(Node node) {
		myNode = node;
		
		for (LinearExponentialTermination t : myTerminations.values()) {
			t.setNode(myNode);
		}		
	}

	/**
	 * @see ca.neo.model.plasticity.Plastic#getPlasticityInterval()
	 */
	public float getPlasticityInterval() {
		return myPlasticityInterval;
	}

	/**
	 * @see ca.neo.model.plasticity.Plastic#getPlasticityRule(java.lang.String)
	 */
	public PlasticityRule getPlasticityRule(String terminationName) throws StructuralException {
		return myPlasticityRules.get(terminationName);
	}

	/**
	 * @see ca.neo.model.plasticity.Plastic#getPlasticityRuleNames()
	 */
	public String[] getPlasticityRuleNames() {
		return myTerminations.keySet().toArray(new String[0]);
	}
		
	@Override
	public SynapticIntegrator clone() throws CloneNotSupportedException {
		LinearSynapticIntegrator result = (LinearSynapticIntegrator) super.clone();
		
		result.myTerminations = new HashMap<String, LinearExponentialTermination>(10);
		result.myPlasticityRules = new HashMap<String, PlasticityRule>(10);
		for (LinearExponentialTermination oldTerm : myTerminations.values()) {
			String name = oldTerm.getName();
			LinearExponentialTermination newTerm = (LinearExponentialTermination) oldTerm.clone();
			newTerm.setNode(result.myNode);
			result.myTerminations.put(name, newTerm);
			
			if (myPlasticityRules.containsKey(name)) {
				result.myPlasticityRules.put(name, myPlasticityRules.get(name).clone());
			}
		}
		
		return result;
	}
	

	public static class Factory implements SynapticIntegratorFactory {

		private static final long serialVersionUID = 1L;
		
		private Units myUnits;
		private float myMaxTimeStep;
		
		public Factory() {
			myUnits = Units.ACU;
			myMaxTimeStep = .0005f;
		}
		
		/**
		 * @return Units of output current value 
		 */
		public Units getUnits() {
			return myUnits;
		}
		
		/**
		 * @param units Units of output current value 
		 */
		public void setUnits(Units units){
			myUnits = units;
		}
		
		/**
		 * @return Maximum time step taken by the synaptic integrators produced here, 
		 * 		regardless of network time step
		 */
		public float getMaxTimeStep() {
			return myMaxTimeStep;
		}
		
		/**
		 * @param maxTimeStep Maximum time step taken by the synaptic integrators produced here, 
		 * 		regardless of network time step
		 */
		public void setMaxTimeStep(float maxTimeStep) {
			myMaxTimeStep = maxTimeStep;
		}
		
		/**
		 * @see ca.neo.model.neuron.impl.SynapticIntegratorFactory#make()
		 */
		public SynapticIntegrator make() {
			return new LinearSynapticIntegrator(myMaxTimeStep, myUnits);
		}
		
	}

}
