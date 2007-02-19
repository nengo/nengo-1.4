/*
 * Created on May 4, 2006
 */
package ca.neo.model.neuron.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ca.neo.model.Probeable;
import ca.neo.model.SimulationException;
import ca.neo.model.Units;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.util.TimeSeries;
import ca.neo.util.TimeSeries1D;
import ca.neo.util.impl.TimeSeries1DImpl;

/**
 * <p>A leaky-integrate-and-fire model of spike generation. From Koch, 1999,
 * the subthreshold model is: C dV(t)/dt + V(t)/R = I(t). When V reaches 
 * a threshold, a spike occurs (spike-related currents are not modelled).</p>
 * 
 * <p>For simplicity we take Vth = R = 1, which does not limit the behaviour 
 * of the model, although transformations may be needed if it is desired to 
 * convert to more realistic parameter ranges. </p>
 * 
 * @author Bryan Tripp
 */
public class LIFSpikeGenerator implements SpikeGenerator, Probeable {

	private static final long serialVersionUID = 1L;
	
	private float myMaxTimeStep;
	private float myTauRC;
	private float myTauRef;
	private float myInitialVoltage;
	
	private float myVoltage;
	private float myTimeSinceLastSpike;
	private float myTauRefNext; //varies randomly to avoid bias and synchronized variations due to floating point comparisons 
	
	private float[] myTime;
	private float[] myVoltageHistory;
	private List mySpikeTimes;
	
	private static final float[] ourNullTime = new float[0]; 
	private static final float[] ourNullVoltageHistory = new float[0];
	
	/**
	 * @param maxTimeStep maximum integration time step (s). Shorter time steps may be used if a 
	 * 		run(...) is requested with a length that is not an integer multiple of this value.  
	 * @param tauRC resistive-capacitive time constant (s) 
	 * @param tauRef refracory period (s)
	 */
	public LIFSpikeGenerator(float maxTimeStep, float tauRC, float tauRef) {
		myMaxTimeStep = maxTimeStep * 1.01f; //increased slightly because float/float != integer 
		myTauRC = tauRC;
		myTauRef = tauRef;
		myTauRefNext = tauRef;
		myInitialVoltage = 0;
		
		reset(false);
	}

	/**
	 * @param maxTimeStep maximum integration time step (s). Shorter time steps may be used if a 
	 * 		run(...) is requested with a length that is not an integer multiple of this value.  
	 * @param tauRC resistive-capacitive time constant (s) 
	 * @param tauRef refracory period (s)
	 * @param initialVoltage initial condition on V
	 */
	public LIFSpikeGenerator(float maxTimeStep, float tauRC, float tauRef, float initialVoltage) {
		myMaxTimeStep = maxTimeStep;
		myTauRC = tauRC;
		myTauRef = tauRef;
		myInitialVoltage = initialVoltage;
		
		reset(false);
	}
	
	public void reset(boolean randomize) {
		myTimeSinceLastSpike = myTauRef;
		myVoltage = myInitialVoltage;
		myTime = ourNullTime;
		myVoltageHistory = ourNullVoltageHistory;
		mySpikeTimes = new ArrayList(10);
	}		

	/**
	 * @see ca.neo.model.neuron.SpikeGenerator#run(float[], float[])
	 */
	public boolean run(float[] time, float[] current) {
		if (time.length < 2) {
			throw new IllegalArgumentException("Arg time must have length at least 2");
		}
		if (time.length != current.length) {
			throw new IllegalArgumentException("Args time and current must have equal length");
		}
		
		float len = time[time.length - 1] - time[0];
		int steps = (int) Math.ceil(len / myMaxTimeStep);
		float dt = len / steps;
		
		myTime = new float[steps];
		myVoltageHistory = new float[steps];
		mySpikeTimes = new ArrayList(10);
		
		int inputIndex = 0;

		float R = 1;
		float Vth = 1;
		boolean spiking = false;
		for (int i = 0; i < steps; i++) {
			myTime[i] = time[0] + i*dt;

			while (time[inputIndex+1] <= myTime[i]) {
				inputIndex++; 
			}			 
			float I = current[inputIndex];
			   
			float dV = (1 / myTauRC) * (I*R - myVoltage);			 
			myTimeSinceLastSpike = myTimeSinceLastSpike + dt;
			if (myTimeSinceLastSpike < myTauRefNext) {
				dV = 0;
			}			
			myVoltage = Math.max(0, myVoltage + dt*dV);
			myVoltageHistory[i] = myVoltage;
			
			if (myVoltage > Vth) {
				spiking = true;
				myTimeSinceLastSpike = 0;
				myVoltage = 0;		
				myTauRefNext = myTauRef - (float) Math.random() * myMaxTimeStep;
				mySpikeTimes.add(new Float(myTime[i]));
			}
		}
		
		return spiking;	
	}
	
	/**
	 * @return true
	 * @see ca.neo.model.neuron.SpikeGenerator#knownConstantRate()
	 */
	public boolean knownConstantRate() {
		return true;
	}

	/**
	 * Note that no voltage history is available after a constant-rate run. 
	 *  
	 * @see ca.neo.model.neuron.SpikeGenerator#runConstantRate(float, float)
	 */
	public float runConstantRate(float time, float current) {
		myTime = ourNullTime;
		myVoltageHistory = ourNullVoltageHistory;
		
		//implicitly Vth == R == 1		
		return current > 1 ? 1f / ( myTauRef - myTauRC * ((float) Math.log(1 - 1/current)) ) : 0;
	}

	/**
	 * @see Probeable#getHistory(String) 
	 */
	public TimeSeries getHistory(String stateName) throws SimulationException {
		TimeSeries1D result = null;
		
		if (stateName.equals("V")) {
			result = new TimeSeries1DImpl(myTime, myVoltageHistory, Units.AVU); 
		} else if (stateName.equalsIgnoreCase("spikes")) {
			float[] times = new float[mySpikeTimes.size()];
			float[] values = new float[mySpikeTimes.size()];
			for (int i = 0; i < times.length; i++) {
				times[i] = ((Float) mySpikeTimes.get(i)).floatValue();
				values[i] = 1;
			}
			result = new TimeSeries1DImpl(times, values, Units.SPIKES);
		} else {
			throw new SimulationException("The state name " + stateName + " is unknown.");
		}
		
		return result;
	}

	/**
	 * @see Probeable#listStates()
	 */
	public Properties listStates() {
		Properties p = new Properties();
		p.setProperty("V", "membrane potential (arbitrary units)");
		return p;
	}

}
