/*
 * Created on 12-May-07
 */
package ca.neo.model.neuron.impl;

import org.apache.log4j.Logger;

import ca.neo.math.PDF;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.NodeFactory;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.model.neuron.SynapticIntegrator;

/**
 * A factory for adapting leaky integrate-and-fire neurons. 
 * 
 * @author Bryan Tripp
 */
public class ALIFNeuronFactory implements NodeFactory {

	private static Logger ourLogger = Logger.getLogger(ALIFNeuronFactory.class);
	
	private static float ourMaxTimeStep = 0.001f;
	private static Units ourCurrentUnits = Units.ACU;
	
	private PDF myMaxRate;
	private PDF myIntercept;
	private PDF myIncN;
	private float myTauRef;
	private float myTauRC;
	private float myTauN;
	
	/**
	 * @param maxRate Maximum firing rate distribution (spikes/s)  
	 * @param intercept Level of summed input at which spiking begins (arbitrary current units) 
	 * @param incN Increment of adaptation-related ion concentration with each spike(arbitrary units)
	 * @param tauRef Spike generator refractory time (s)
	 * @param tauRC Spike generator membrane time constant (s)  
	 * @param tauN Time constant of adaptation-related ion decay (s)
	 */
	public ALIFNeuronFactory(PDF maxRate, PDF intercept, PDF incN, float tauRef, float tauRC, float tauN) {
		myMaxRate = maxRate;
		myIntercept = intercept;
		myIncN = incN;
		myTauRef = tauRef;
		myTauRC = tauRC;
		myTauN = tauN;
	}

	/**
	 * @see ca.neo.model.impl.NodeFactory#make(java.lang.String)
	 */
	public Node make(String name) throws StructuralException {
		float maxRate = myMaxRate.sample()[0];		
		float intercept = myIntercept.sample()[0];
		
		if (maxRate < 0) {
			throw new StructuralException("Max firing rate must be > 0");
		}
		if (maxRate > 1f / myTauRef) {
			ourLogger.warn("Decreasing maximum firing rate which was greater than inverse of refractory period");
			maxRate = (1f / myTauRef) - .001f;
		}
		
		float x = 1f / (1f - (float) Math.exp( (myTauRef - (1f / maxRate)) / myTauRC));
		float scale = (x - 1f) / (1f - intercept);
		
		float bias = 1f - scale * intercept;
		
		SynapticIntegrator integrator = new LinearSynapticIntegrator(ourMaxTimeStep, ourCurrentUnits);
		SpikeGenerator generator = new ALIFSpikeGenerator(myTauRef, myTauRC, myTauN, myIncN.sample()[0]);
		
		return new PlasticExpandableSpikingNeuron(integrator, generator, scale, bias, name);		
	}

}
