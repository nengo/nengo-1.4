/*
 * Created on May 4, 2006
 */
package ca.neo.model.neuron.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.impl.LinearExponentialTermination;
import ca.neo.model.neuron.ExpandableSynapticIntegrator;
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
	
	private float myMaxTimeStep;
	private Units myCurrentUnits;
	private Map<String, LinearExponentialTermination> myTerminations;	
	private Map<String, PlasticityRule> myPlasticityRules;

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
//			LinearExponentialTermination[] terminations 
//				= (LinearExponentialTermination[]) myTerminations.toArray(new LinearExponentialTermination[0]);
		
			//Note: we leave out decay at start time and real input integration at end time, to make total  
			//decay and integration times equal to simulation time
		
			times[0] = startTime;
			currents[0] = update(myTerminations.values(), true, dt, 0); 
		
			for (int i = 1; i <= steps; i++) {
				times[i] = startTime + (float)i * dt;
				//Note (cont'd): real-valued input not applied in last step (we quit at end time - delta)
				currents[i] = update(myTerminations.values(), false, i < steps ? dt : 0, dt); 
			}			
		}
		
		learn(endTime - startTime);
				
		return new TimeSeries1DImpl(times, currents, myCurrentUnits);
	}
	
	//run plasticity rules (assume constant input/state over given elapsed time)
	private void learn(float elapsedTime) throws SimulationException {
		Iterator ruleIter = myPlasticityRules.keySet().iterator();
		while (ruleIter.hasNext()) {
			String name = (String) ruleIter.next();
			LinearExponentialTermination termination = myTerminations.get(name);
			PlasticityRule rule = myPlasticityRules.get(name);
			
			Iterator termIter = myTerminations.keySet().iterator();
			while (termIter.hasNext()) {
				LinearExponentialTermination t = myTerminations.get(termIter.next());
				InstantaneousOutput input = t.getInput();
				//TODO: allow spikes when rules support spikes
				if (input instanceof RealOutput) {
					rule.setTerminationState(t.getName(), ((RealOutput) input).getValues());					
				}
			}
			
			//TODO: allow spikes when rules support spikes
			InstantaneousOutput input = termination.getInput();
			if (input instanceof RealOutput) {
				float[] weights = termination.getWeights();
				float[][] derivative = rule.getDerivative(new float[][]{weights}, ((RealOutput) termination.getInput()).getValues());
				for (int i = 0; i < weights.length; i++) {
					weights[i] += derivative[0][i] * elapsedTime;
				}					
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
		
		LinearExponentialTermination result = new LinearExponentialTermination(name, weights, tauPSC); 
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

}
