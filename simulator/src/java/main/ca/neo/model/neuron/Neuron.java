/*
 * Created on May 3, 2006
 */
package ca.neo.model.neuron;

import ca.neo.model.Node;

/**
 * <p>A model of a single neuron cell. Neurons are Nodes which can have multiple Terminations, and 
 * multiple Origins (corresponding to axonal output and possibly other outpus such as gap junctions), 
 * but they always have a primary Origin which is named Neuron.AXON.</p>
 * 
 * @author Bryan Tripp
 */
public interface Neuron extends Node {

	/**
	 * Standard name for the primary Origin of a Neuron, which outputs its spikes or firing rate 
	 * depending on SimulationMode.  
	 */
	public static final String AXON = "AXON";
	
}
