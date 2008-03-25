/*
 * Created on May 19, 2006
 */
package ca.nengo.util;

import ca.nengo.model.Probeable;
import ca.nengo.model.SimulationException;

/**
 * Reads state variables from Probeable objects (eg membrane potential from a Neuron).
 * Collected data can be displayed during a simluation or kept for plotting afterwards.   
 * 
 * @author Bryan Tripp
 */
public interface Probe {

	/**
	 * @param ensembleName
	 *            Name of the Ensemble the target object belongs to. Null, if
	 *            the target is a top-level node.
	 * @param target
	 *            The object about which state history is to be collected
	 * @param stateName
	 *            The name of the state variable to collect
	 * @param record
	 *            If true, getData() returns history since last connect() or
	 *            reset(), otherwise getData() returns most recent sample
	 * @throws SimulationException
	 *             if the given target does not have the given state
	 */
	public void connect(String ensembleName, Probeable target,
			String stateName, boolean record) throws SimulationException;

	/**
	 * @param target
	 *            The object about which state history is to be collected
	 * @param stateName
	 *            The name of the state variable to collect
	 * @param record
	 *            If true, getData() returns history since last connect() or
	 *            reset(), otherwise getData() returns most recent sample
	 * @throws SimulationException
	 *             if the given target does not have the given state
	 */
	public void connect(Probeable target, String stateName, boolean record) throws SimulationException;
	
	/**
	 * Clears collected data. 
	 */
	public void reset();
	
	/**
	 * Processes new data. To be called after every Network time step. 
	 */
	public void collect(float time);	
	
	/**
	 * @param rate Rate in samples per second. The default is one sample per network time step, and it is 
	 * 		not possible to sample faster than this (specifying a higher sampling rate has no effect).   
	 */
	public void setSamplingRate(float rate);

	/**
	 * @return All collected data since last reset()
	 */
	public TimeSeries getData();

	/**
	 * @return The object about which state history is to be collected
	 */
	public Probeable getTarget();

	/**
	 * @return The name of the state variable to collect
	 */
	public String getStateName();

	/**
	 * @return Whether the target the node is attached to is inside an Ensemble
	 */
	public boolean isInEnsemble();

	/**
	 * @return The name of the Ensemble the target the Probe is attached to is
	 *         in. Null if it's not in one
	 */
	public String getEnsembleName();
	
}
