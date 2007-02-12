/*
 * Created on May 4, 2006
 */
package ca.neo.model.neuron;

import java.io.Serializable;

import ca.neo.model.Resettable;
import ca.neo.model.Termination;
import ca.neo.util.TimeSeries1D;

/**
 * <p>Model of synaptic integration in a dendritic tree and soma.</p>
 * 
 * <p>The model receives input from external sources (normally other neurons) 
 * and produces a net current which can be fed into a <code>SpikeGenerator</code>
 * and/or can produce other outputs of a Neuron.    
 * 
 * @author Bryan Tripp
 */
public interface SynapticIntegrator extends Resettable, Serializable {
	
	/**
	 * @return List of distinct inputs (eg sets of synapses from different ensembles).    
	 */
	public Termination[] getTerminations();
	
	/**
	 * <p>Runs the model for a given time interval. Input to each Termination
	 * should be set prior to calling this method, and is held constant during a run.</p>
	 * 
	 * <p>The model is responsible for maintaining its internal state, and the 
	 * state is assumed to be consistent with the start time. That is, if a caller
	 * calls run(0, 1, ...) and then run(5, 6, ...), the results may not 
	 * make any sense, but this is not the model's responsibility. Start
	 * and end times are provided to support explicitly time-varying models, 
	 * and for the convenience of Probeable models.</p>
	 * 
	 * <p>Note that a run(...) is expected to cover a very short interval of time, 
	 * e.g. 1/2 ms, during which inputs can be assumed to be constant. Normally 
	 * a number of neurons in a network will run for this short length of time, 
	 * possibly with diverse or varying internal time steps, and at the end of this 
	 * time will communicate spikes to each other and then start again. </p>
	 * 
	 * @param startTime Simulation time at which running starts (s)
	 * @param endTime Simulation time at which running ends (s)
	 * @return Time series of net current, including at least the start and end times, and 
	 * 		optionally other times. Generally speaking additional values should be 
	 * 		provided if the current varies substantially during the interval, but it is 
	 * 		left to the implementation to interpret 'substantially'.  
	 */
	public TimeSeries1D run(float startTime, float endTime);
	
}
