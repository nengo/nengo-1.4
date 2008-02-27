/*
 * Created on 31-May-07
 */
package ca.neo.model.neuron.impl;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.SimulationMode;
import ca.neo.model.Units;
import ca.neo.model.impl.RealOutputImpl;
import ca.neo.model.neuron.SpikeGenerator;

/**
 * A rate-only generator where rate is a linear function of driving current.
 *  
 * @author Bryan Tripp
 */
public class LinearSpikeGenerator implements SpikeGenerator {
	
	private static final long serialVersionUID = 1L;
	
	public LinearSpikeGenerator(float intercept, float slope, boolean rectified) {
		
	}
	
	/**
	 * Returns rate based on final current value. 
	 * 
	 * @see ca.neo.model.neuron.SpikeGenerator#run(float[], float[])
	 */
	public InstantaneousOutput run(float[] time, float[] current) {
		float rate = current[0];
		return new RealOutputImpl(new float[]{rate}, Units.SPIKES_PER_S, time[time.length-1]);
	}

	/**
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
	}

	/**
	 * @see ca.neo.model.SimulationMode.ModeConfigurable#getMode()
	 */
	public SimulationMode getMode() {
		return SimulationMode.CONSTANT_RATE;
	}

	/**
	 * @see ca.neo.model.SimulationMode.ModeConfigurable#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
	}
	
}
