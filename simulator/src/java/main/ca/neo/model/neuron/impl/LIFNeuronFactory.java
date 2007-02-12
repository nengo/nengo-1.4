/*
 * Created on 21-Jun-2006
 */
package ca.neo.model.neuron.impl;

import ca.neo.math.PDF;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.neuron.Neuron;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.model.neuron.SynapticIntegrator;

/**
 * A factory for leaky-integrate-and-fire neurons. 
 * 
 * @author Bryan Tripp
 */
public class LIFNeuronFactory implements NeuronFactory {

	private float myTauRC;
	private float myTauRef;
	private PDF myMaxRate;
	private PDF myIntercept;
	
	private static float ourMaxTimeStep = .00025f;
	private static Units ourCurrentUnits = Units.ACU;

	/**
	 * @param tauRC Spike generator membrane time constant (s)  
	 * @param tauRef Spike generator refractory time (s)
	 * @param maxRate Maximum firing rate distribution (spikes/s)  
	 * @param intercept Level of summed input at which spiking begins (arbitrary current units) 
	 */
	public LIFNeuronFactory(float tauRC, float tauRef, PDF maxRate, PDF intercept) {
		myTauRC = tauRC;
		myTauRef = tauRef;
		myMaxRate = maxRate;
		myIntercept = intercept;
	}

	/**
	 * @see ca.neo.model.neuron.impl.NeuronFactory#make()
	 */
	public Neuron make() throws StructuralException {
		float maxRate = myMaxRate.sample()[0];
		float intercept = myIntercept.sample()[0];
		
		if (maxRate < 0) {
			throw new StructuralException("Max firing rate must be > 0");
		}
		
		float x = 1f / (1f - (float) Math.exp( (myTauRef - (1f / maxRate)) / myTauRC));
		float scale = (x - 1f) / (1f - intercept);
		
		float bias = 1f - scale * intercept;

		SynapticIntegrator integrator = new LinearSynapticIntegrator(scale, bias, ourMaxTimeStep, ourCurrentUnits);
		SpikeGenerator generator = new LIFSpikeGenerator(ourMaxTimeStep, myTauRC, myTauRef);
		
		return new SpikingNeuron(integrator, generator);		
	}

}
