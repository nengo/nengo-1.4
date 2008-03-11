/*
 * Created on 21-Jun-2006
 */
package ca.neo.model.neuron.impl;

import org.apache.log4j.Logger;

import ca.neo.math.PDF;
import ca.neo.math.impl.IndicatorPDF;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.NodeFactory;
import ca.neo.model.neuron.Neuron;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.model.neuron.SynapticIntegrator;

/**
 * A factory for leaky-integrate-and-fire neurons. 
 * 
 * @author Bryan Tripp
 */
public class LIFNeuronFactory implements NodeFactory {

	private static final long serialVersionUID = 1L;

	private static Logger ourLogger = Logger.getLogger(LIFNeuronFactory.class);
	
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
	 * Uses default parameters. 
	 */
	public LIFNeuronFactory() {
		this(.02f, .001f, new IndicatorPDF(200, 400), new IndicatorPDF(-.9f, .9f));
	}
	
	/**
	 * @return Spike generator refractory time (s)
	 */
	public float getTauRef() {
		return myTauRef;
	}
	
	/**
	 * @param tauRef Spike generator refractory time (s)
	 */
	public void setTauRef(float tauRef) {
		myTauRef = tauRef;
	}

	/**
	 * @return Spike generator membrane time constant (s)  
	 */
	public float getTauRC() {
		return myTauRC;
	}
	
	/**
	 * @param tauRC Spike generator membrane time constant (s)  
	 */
	public void setTauRC(float tauRC) {
		myTauRC = tauRC;
	}
	
	/**
	 * @return Maximum firing rate distribution (spikes/s) 
	 */
	public PDF getMaxRate() {
		return myMaxRate;
	}
	
	/**
	 * @param maxRate Maximum firing rate distribution (spikes/s) 
	 */
	public void setMaxRate(PDF maxRate) {
		myMaxRate = maxRate;
	}

	/**
	 * @return Level of summed input at which spiking begins (arbitrary current units)
	 */
	public PDF getIntercept() {
		return myIntercept;
	}
	
	/**
	 * @param intercept Level of summed input at which spiking begins (arbitrary current units)
	 */
	public void setIntercept(PDF intercept) {
		myIntercept = intercept;
	}

	/**
	 * @see ca.neo.model.impl.NodeFactory#make(String)
	 */
	public Neuron make(String name) throws StructuralException {
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
		SpikeGenerator generator = new LIFSpikeGenerator(ourMaxTimeStep, myTauRC, myTauRef);
		
		return new PlasticExpandableSpikingNeuron(integrator, generator, scale, bias, name);		
	}

	/**
	 * @see ca.neo.model.impl.NodeFactory#getTypeDescription()
	 */
	public String getTypeDescription() {
		return "LIF Neuron";
	}

}
