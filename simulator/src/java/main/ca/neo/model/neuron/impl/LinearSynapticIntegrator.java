/*
 * Created on May 4, 2006
 */
package ca.neo.model.neuron.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.impl.LinearExponentialTermination;
import ca.neo.model.nef.NEFSynapticIntegrator;
import ca.neo.model.neuron.ExpandableSynapticIntegrator;
import ca.neo.util.TimeSeries1D;
import ca.neo.util.impl.TimeSeries1DImpl;

/**
 * <p>A basic linear <code>SynapticIntegrator</code> model.</p>
 * 
 * <p>Synaptic inputs are individually weighted, passed through decaying 
 * exponential dynamics, and summed. The combined input is then scaled and 
 * biased. This means that two synaptic integrators receiving the same inputs with 
 * the same weights could produce different (linearly related) currents.</p> 
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
public class LinearSynapticIntegrator implements ExpandableSynapticIntegrator, NEFSynapticIntegrator {

	private static final long serialVersionUID = 1L;
	
	private float myScale;
	private float myBias;	
	private float myMaxTimeStep;
	private Units myCurrentUnits;
	private List myTerminations;	
	private float myRadialInput;

	/**
	 * Note: current = scale * (weighted sum of inputs at each termination) + bias.   
	 * 
	 * @param scale Coefficient that scales summed input globally   
	 * @param bias Global bias current that models other current sources, eg intrinsic currents or unaccounted-for inputs 
	 * @param maxTimeStep Maximum length of integration time step. Shorter steps may be used to better match
	 * 		length of run(...) 
	 * @param currentUnits Units of current in input weights, scale, bias, and result of run(...)  
	 */	
	public LinearSynapticIntegrator(float scale, float bias, float maxTimeStep, Units currentUnits) {
		myScale = scale;
		myBias = bias;
		myMaxTimeStep = maxTimeStep * 1.01f; //increased slightly because float/float != integer 
		myCurrentUnits = currentUnits;
		myTerminations = new ArrayList();
		myRadialInput = 0f;
	}
	
	/**
	 * @see ca.neo.model.neuron.SynapticIntegrator#run(float, float)
	 */
	public TimeSeries1D run(float startTime, float endTime) {
		float len = endTime - startTime;
		int steps = (int) Math.ceil(len / myMaxTimeStep);
		float dt = len / steps;
		
		float[] times = new float[steps+1];
		float[] currents = new float[steps+1];
		
		if (myTerminations.size() == 0) {
			times[0] = startTime;
			currents[0] = myBias + myScale * myRadialInput; 
			
			for (int i = 1; i <= steps; i++) {
				times[i] = startTime + (float)i * dt;
				currents[i] = currents[0]; 
			}			
		} else {
			LinearExponentialTermination[] terminations 
				= (LinearExponentialTermination[]) myTerminations.toArray(new LinearExponentialTermination[0]);
		
			//Note: we leave out decay at start time and real input integration at end time, to make total  
			//decay and integration times equal to simulation time
		
			times[0] = startTime;
			currents[0] = myBias + myScale * (update(terminations, true, dt, 0) + myRadialInput); 
		
			for (int i = 1; i <= steps; i++) {
				times[i] = startTime + (float)i * dt;
				//Note (cont'd): real-valued input not applied in last step (we quit at end time - delta)
				currents[i] = myBias + myScale * (update(terminations, false, i < steps ? dt : 0, dt) + myRadialInput); 
			}			
		}
				
		return new TimeSeries1DImpl(times, currents, myCurrentUnits);
	}

	//update current in all Terminations 
	private static float update(LinearExponentialTermination[] terminations, boolean spikes, float intTime, float decayTime) {
		float result = 0f;
		
		for (int i = 0; i < terminations.length; i++) {
			float current = terminations[i].updateCurrent(spikes, intTime, decayTime);
			String isModulatory = terminations[i].getProperty(Termination.MODULATORY);
			if (!"true".equalsIgnoreCase(isModulatory)) result += current;
		}
		
		return result;
	}
	
	/**
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		Iterator it = myTerminations.iterator();
		while (it.hasNext()) {
			((LinearExponentialTermination) it.next()).reset(false);
		}
	}

	/**
	 * @see ca.neo.model.neuron.SynapticIntegrator#getTerminations()
	 */
	public Termination[] getTerminations() {
		return (Termination[]) myTerminations.toArray(new Termination[0]);
	}

	/**
	 * @see ca.neo.model.neuron.ExpandableSynapticIntegrator#addTermination(java.lang.String, float[], float)
	 */
	public Termination addTermination(String name, float[] weights, float tauPSC) throws StructuralException {
		Termination t = findTermination(name);
		
		if (t != null) {
			throw new StructuralException("This SynapticIntegrator already has a Termination named " + name);
		}
		
		Termination result = new LinearExponentialTermination(name, weights, tauPSC); 
		myTerminations.add(result);
		return result;
	}

	/**
	 * @see ca.neo.model.neuron.ExpandableSynapticIntegrator#removeTermination(java.lang.String)
	 */
	public void removeTermination(String name) throws StructuralException {
		Termination t = findTermination(name);
		
		if (t != null) {
			myTerminations.remove(t);
		} else {
			throw new StructuralException("There is no Termination named " + name + " on this SynapticIntegrator");
		}
	}

	//find in list by name 
	private Termination findTermination(String name) {
		Termination result = null;
		Iterator it = myTerminations.iterator();
		while (result == null && it.hasNext()) {
			Termination t = (Termination) it.next();
			if ( t.getName().equals(name) ) {
				result = t;
			}
		}		
		return result;
	}

	/**
	 * Sets direct NEF input. This is summed with the input at each Termination, and 
	 * the sum is scaled and biased. 
	 *   
	 * @see ca.neo.model.nef.NEFSynapticIntegrator#setRadialInput(float)
	 */
	public void setRadialInput(float value) {
		myRadialInput = value;
	}

}
