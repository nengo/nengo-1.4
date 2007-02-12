/*
 * Created on May 3, 2006
 */
package ca.neo.model.neuron;

import java.io.Serializable;

import ca.neo.model.Origin;
import ca.neo.model.Resettable;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;

/**
 * <p>A model of a single neuron cell. A Neuron object consists of a SynapticIntegrator, and some 
 * means of transforming integrated input into outputs. Neurons can have multiple Terminations, 
 * and multiple Origins (corresponding to axonal output and possibly other outpus such as gap 
 * junctions.</p>
 * 
 * TODO: could easily support Node here, which would allow Network of Neurons
 * 
 * @author Bryan Tripp
 */
public interface Neuron extends Resettable, Serializable {

	/**
	 * Standard name for the primary output channel of a Neuron, ie its spikes or 
	 * firing rate. 
	 */
	public static final String AXON = "AXON";
	
	/**
	 * @return This Neuron's SynapticIntegrator, which contains its Terminations. 
	 */
	public SynapticIntegrator getIntegrator();
	
	/**
	 * @return Sources of information from this Neuron (eg spiking output, etc). Note that Origins 
	 * 		of the same name from different neurons in an Ensemble may be grouped at the 
	 * 		Ensemble level into a higher-dimensional output of the same name.   
	 */
	public Origin[] getOrigins();
	
	/**
	 * @param name The name of an Origin
	 * @return The Origin with that name
	 * @throws SimulationException if the named Origin doesn't exist
	 */
	public Origin getOrigin(String name) throws SimulationException;
	
	/**
	 * Sets the Neuron to run in either the given mode or the closest mode that the Neuron supports 
	 * (all Neurons must support SimulationMode.DEFAULT and default to this mode).  
	 * 
	 * @param mode Requested simulation mode 
	 */
	public void setMode(SimulationMode mode);
	
	/**
	 * @return The mode in which the Neuron is currently running. 
	 */
	public SimulationMode getMode();

	/**
	 * <p>Runs the neuron model for a given time interval. Before calling this method for 
	 * a given time step, inputs should be applied to all the Terminations of the 
	 * Neuron's SynapticIntegrator. These inputs are assumed to be constant for the 
	 * time span of a run().</p>
	 * 
	 * <p>The model is responsible for maintaining its internal state, and the 
	 * state is assumed to be consistent with the start time. That is, if a caller
	 * calls run(0, 1) and then run(5, 6), the results may not make any sense, but this 
	 * is the caller's responsibility. Start and end times are provided to support 
	 * explicitly time-varying models, and for the convenience of Probeable models.</p>
	 * 
	 * <p>Note that a run(...) is expected to cover a very short interval of time, 
	 * e.g. 1/2 ms, during which inputs can be assumed to be constant. Normally 
	 * a number of neurons in a network will run for this short length of time 
	 * (possibly with diverse or varying internal time steps) and at the end of this 
	 * time will communicate spikes to each other and then start again. </p>
	 * 
	 * @param startTime simulation time at which running starts (s)
	 * @param endTime simulation time at which running ends (s)
	 * @throws SimulationException if a problem is encountered while trying to run 
	 */
	public void run(float startTime, float endTime) throws SimulationException; 
		
}
