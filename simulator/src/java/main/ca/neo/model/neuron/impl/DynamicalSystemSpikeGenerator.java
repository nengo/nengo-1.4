/*
 * Created on 2-Apr-07
 */
package ca.neo.model.neuron.impl;

import ca.neo.dynamics.DynamicalSystem;
import ca.neo.dynamics.Integrator;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.SimulationMode;
import ca.neo.model.Units;
import ca.neo.model.impl.SpikeOutputImpl;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.util.TimeSeries;
import ca.neo.util.impl.TimeSeries1DImpl;

/**
 * A SpikeGenerator for which spiking dynamics are expressed in terms of a DynamicalSystem. 
 * 
 * TODO: make probeable
 * 
 * @author Bryan Tripp
 */
public class DynamicalSystemSpikeGenerator implements SpikeGenerator {

	private static final long serialVersionUID = 1L;
	
	private DynamicalSystem myDynamics; 
	private Integrator myIntegrator;
	private int myVDim;
	private float mySpikeThreshold;
	private float myMinIntraSpikeTime;
	private float myLastSpikeTime;
	
	/**
	 * @param dynamics A DynamicalSystem that defines the dynamics of spike generation. 
	 * @param integrator An integrator with which to simulate the DynamicalSystem
	 * @param voltageDim Dimension of output that corresponds to membrane potential
	 * @param spikeThreshold Threshold membrane potential at which a spike is considered to have occurred
	 * @param minIntraSpikeTime Minimum time between spike onsets. If there appears to be a spike onset at the 
	 * 		beginning of a timestep, this value is used to determine whether this is just the continuation of a spike 
	 * 		onset that was already registered in the last timestep 
	 */
	public DynamicalSystemSpikeGenerator(DynamicalSystem dynamics, Integrator integrator, int voltageDim, float spikeThreshold, float minIntraSpikeTime) {
		myDynamics = dynamics;
		myIntegrator = integrator;
		myVDim = voltageDim;
		mySpikeThreshold = spikeThreshold;
		myMinIntraSpikeTime = minIntraSpikeTime;
	}
	
	/**
	 * Runs the spike generation dynamics and returns a spike if membrane potential rises above spike threshold. 
	 * 
	 * @see ca.neo.model.neuron.SpikeGenerator#run(float[], float[])
	 */
	public InstantaneousOutput run(float[] time, float[] current) {
		boolean spike = false;
		
		TimeSeries result = myIntegrator.integrate(myDynamics, new TimeSeries1DImpl(time, current, Units.uAcm2));
		float[][] values = result.getValues();
		
		for (int i = 0; i < values.length && !spike; i++) {
			if (values[i][myVDim] >= mySpikeThreshold) {
				boolean crossingThreshold = i > 0 && values[i-1][myVDim] < mySpikeThreshold;
				boolean nonDuplicateAtStart 
					= i == 0 && values[1][myVDim] > values[0][myVDim] && result.getTimes()[0] > myLastSpikeTime + myMinIntraSpikeTime;
				
				if (crossingThreshold || nonDuplicateAtStart) {
					spike = true;
					myLastSpikeTime = result.getTimes()[i];
				}
			}
		}
		
		return new SpikeOutputImpl(new boolean[]{spike}, Units.SPIKES, time[time.length-1]);
	}
	
	/**
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		myDynamics.setState(new float[myDynamics.getState().length]);
	}

	/**
	 * @see ca.neo.model.SimulationMode.ModeConfigurable#getMode()
	 */
	public SimulationMode getMode() {
		return SimulationMode.DEFAULT;
	}

	/**
	 * @see ca.neo.model.SimulationMode.ModeConfigurable#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
	}
	
}
