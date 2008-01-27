/*
 * Created on May 4, 2006
 */
package ca.neo.model.neuron.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Probeable;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.Units;
import ca.neo.model.impl.RealOutputImpl;
import ca.neo.model.impl.SpikeOutputImpl;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.util.TimeSeries;
import ca.neo.util.TimeSeries1D;
import ca.neo.util.impl.TimeSeries1DImpl;

/**
 * <p>A simple but powerful spiking model; simplified from Hodgkin-Huxley
 * models using bifurcation methodologies. From Izhikevich, 2003, the model
 * is: v' = 0.04v*v + 5v + 140 - u + I. 
 *     u' = a(bv - u) 
 *     if v >= 30 mV, then v := c and u := u + d (reset after spike)
 * </p>
 * 
 * <p>v represents the membrane potential;
 * u is a membrane recovery variable;
 * a, b, c, and d are modifiable parameters</p>
 * 
 * TODO (bryan): write rate mode; fix max step at 1/2 ms; review
 * 
 * @author Hussein
 */
public class IzhikevichSpikeGenerator implements SpikeGenerator, Probeable {

	private static final long serialVersionUID = 1L;

	private float myMaxTimeStep;
	
	private float myA;
	private float myB;
	private float myC;
	private float myD;
	private float myInitialVoltage;
	private float Vth = 30;
	
	private float myVoltage;
	private float myRecovery;
	private float myTimeSinceLastSpike; 
	
	private float[] myTime;
	private float[] myVoltageHistory;
	private List mySpikeTimes;
	
	private SimulationMode myMode;
	private SimulationMode[] mySupportedModes;
	
	private static final float[] ourNullTime = new float[0]; 
	private static final float[] ourNullVoltageHistory = new float[0];
	
	/**
	 * Constructor using "default" parameters
	 */
	public IzhikevichSpikeGenerator() {
		this(.0005f, 0.02f, .2f, -65f, 2f);
	}

	/**
	 * @param maxTimeStep maximum integration time step (s). Shorter time steps may be used if a 
	 * 		run(...) is requested with a length that is not an integer multiple of this value.  
	 * @param a time scale of recovery variable 
	 * @param b sensitivity of recovery variable
	 * @param c voltage reset value
	 * @param d recovery variable reset change
	 */
	public IzhikevichSpikeGenerator(float maxTimeStep, float a, float b, float c, float d) {
		myMaxTimeStep = maxTimeStep; 
		myInitialVoltage = -65f;
		myA = a;
		myB = b;
		myC = c;
		myD = d;
		
		myMode = SimulationMode.DEFAULT;
		mySupportedModes = new SimulationMode[]{SimulationMode.DEFAULT, SimulationMode.CONSTANT_RATE, SimulationMode.RATE};

		reset(false);
	}
	
	/**
	 * @param maxTimeStep maximum integration time step (s). Shorter time steps may be used if a 
	 * 		run(...) is requested with a length that is not an integer multiple of this value.  
	 * @param a time scale of recovery variable 
	 * @param b sensitivity of recovery variable
	 * @param c voltage reset value
	 * @param d recovery variable reset change
	 * @param initialVoltage initial voltage value
	 */
	public IzhikevichSpikeGenerator(float maxTimeStep, float a, float b, float c, float d, float initialVoltage) {
		this(maxTimeStep, a, b, c, d);
		myInitialVoltage = initialVoltage;
	}

	/**
	 * @return time scale of recovery variable
	 */
	public float getA() {
		return myA;
	}

	/**
	 * @param a time scale of recovery variable
	 */
	public void setA(float a) {
		myA = a;
	}

	/**
	 * @return sensitivity of recovery variable
	 */
	public float getB() {
		return myB;
	}

	/**
	 * @param b sensitivity of recovery variable
	 */
	public void setB(float b) {
		myB = b;
	}

	/**
	 * @return voltage reset value
	 */
	public float getC() {
		return myC;
	}

	/**
	 * @param c voltage reset value
	 */
	public void setC(float c) {
		myC = c;
	}

	/**
	 * @return recovery variable reset change
	 */
	public float getD() {
		return myD;
	}

	/**
	 * @param d recovery variable reset change
	 */
	public void setD(float d) {
		myD = d;
	}
	
	public void reset(boolean randomize) {
		myTimeSinceLastSpike = 0f;
		myVoltage = myInitialVoltage;
		myRecovery = 0f;
		myTime = ourNullTime;
		myVoltageHistory = ourNullVoltageHistory;
		mySpikeTimes = new ArrayList(10);
	}		

	/**
	 * @see ca.neo.model.neuron.SpikeGenerator#run(float[], float[])
	 */
	public InstantaneousOutput run(float[] time, float[] current) {
		InstantaneousOutput result = null;
		
		if (myMode.equals(SimulationMode.CONSTANT_RATE) || myMode.equals(SimulationMode.RATE)) {
			result = new RealOutputImpl(new float[]{doConstantRateRun(time[0], current[0])}, Units.SPIKES_PER_S, time[time.length-1]);
		} else {
			result = new SpikeOutputImpl(new boolean[]{doSpikingRun(time, current)}, Units.SPIKES, time[time.length-1]);
		}
		
		return result;
	}
	
	private boolean doSpikingRun(float[] time, float[] current) {
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

		boolean spiking = false;
		for (int i = 0; i < steps; i++) {
			myTime[i] = time[0] + i*dt;

			while (time[inputIndex+1] <= myTime[i]) {
				inputIndex++; 
			}			 
			float I = current[inputIndex];
			   
			float dv = 0.04f*myVoltage*myVoltage + 5*myVoltage + 140 - myRecovery + I;			 
						
			myVoltage = Math.max(0, myVoltage + dt*dv);
			myVoltageHistory[i] = myVoltage;
			
			float du = myA*(myB*myVoltage - myRecovery);
			myRecovery = Math.max(0, myRecovery + dt*du);
			
			myTimeSinceLastSpike = myTimeSinceLastSpike + dt;
			
			if (myVoltage >= Vth) {
				spiking = true;
				myTimeSinceLastSpike = 0;
				myVoltage = myC;		
				myRecovery = myRecovery + myD; 
				mySpikeTimes.add(new Float(myTime[i]));
			}
		}
		
		return spiking;	
	}
	
	//Note that no voltage history is available after a constant-rate run.
	private float doConstantRateRun(float time, float current) {
		myTime = ourNullTime;
		myVoltageHistory = ourNullVoltageHistory;
		
		//implicitly Vth == R == 1		
		return current > 1 ? 1f : 0; //TODO: this isn't right
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
		p.setProperty("v", "membrane potential (arbitrary units)");
		p.setProperty("u", "membrane recovery variable (arbitrary units)");
		return p;
	}

	/**
	 * @see ca.neo.model.SimulationMode.ModeConfigurable#getMode()
	 */
	public SimulationMode getMode() {
		return myMode;
	}

	/**
	 * @see ca.neo.model.SimulationMode.ModeConfigurable#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
		myMode = SimulationMode.getClosestMode(mode, mySupportedModes);
	}

}
