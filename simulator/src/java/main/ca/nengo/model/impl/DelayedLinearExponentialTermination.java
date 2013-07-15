package ca.nengo.model.impl;

import java.util.ArrayList;

import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.SimulationException;
import ca.nengo.model.Units;

/**
 * A LinearExponentialTermination where all inputs are delayed by a fixed number of
 * timesteps.
 * 
 * @author Daniel Rasmussen
 */
public class DelayedLinearExponentialTermination extends LinearExponentialTermination {
	
	private static final long serialVersionUID = 1L;
	
	private int myDelay;
	private ArrayList<InstantaneousOutput> myQueue;
	
	
	 /**
	  *  
	 * @param delay delay in timesteps between when input arrives at this termination and when it will be processed
	 * @see LinearExponentialTermination#LinearExponentialTermination(Node, String, float[], float)
	 */
	public DelayedLinearExponentialTermination(Node node, String name, float[] weights, float tauPSC, int delay) {
		super(node, name, weights, tauPSC);
		myDelay = delay;
		myQueue = new ArrayList<InstantaneousOutput>(myDelay*3);
		for(int i=0; i < myDelay; i++)
			myQueue.add(new RealOutputImpl(new float[weights.length], Units.UNK, 0.0f));
	}
	
	/**
	 * Adds a value to this termination's queue.  That value will not actually be
	 * processed until myDelay timesteps have called (we are assuming this function
	 * will be called once per timestep).
	 * 
	 * @see LinearExponentialTermination#setValues(InstantaneousOutput)
	 */
	public void setValues(InstantaneousOutput values) throws SimulationException {
		myQueue.add(values);
		InstantaneousOutput v = myQueue.remove(0);
		super.setValues(v);
	}
}