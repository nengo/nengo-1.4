/*
 * Created on 12-May-07
 */
package ca.neo.model.neuron.impl;

import org.apache.log4j.Logger;

import ca.neo.config.ConfigUtil;
import ca.neo.config.Configuration;
import ca.neo.math.PDF;
import ca.neo.math.impl.IndicatorPDF;
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
	
	private Configuration myConfiguration;
	
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
		myConfiguration = ConfigUtil.defaultConfiguration(this);
	}
	
	/**
	 * Uses default parameters. 
	 */
	public ALIFNeuronFactory() {
		this(new IndicatorPDF(200, 400), new IndicatorPDF(-.9f, .9f), new IndicatorPDF(.1f, .2f), .001f, .02f, .2f);
	}

	/**
	 * @see ca.neo.config.Configurable#getConfiguration()
	 */
	public Configuration getConfiguration() {
		return myConfiguration;
	}

	public PDF getMaxRate() {
		return myMaxRate;
	}
	
	public void setMaxRate(PDF maxRate) {
		myMaxRate = maxRate;
	}
	
	public PDF getIntercept() {
		return myIntercept;
	}
	
	public void setIntercept(PDF intercept) {
		myIntercept = intercept;
	}
	
	public PDF getIncN() {
		return myIncN;
	}
	
	public void setIncN(PDF incN) {
		myIncN = incN;
	}
	
	public float getTauRef() {
		return myTauRef;
	}
	
	public void setTauRef(float tauRef) {
		myTauRef = tauRef;
	}

	public float getTauRC() {
		return myTauRC;
	}
	
	public void setTauRC(float tauRC) {
		myTauRC = tauRC;
	}

	public float getTauN() {
		return myTauN;
	}
	
	public void setTauN(float tauN) {
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
