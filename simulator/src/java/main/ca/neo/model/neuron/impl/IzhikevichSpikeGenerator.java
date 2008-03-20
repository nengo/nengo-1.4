/*
 * Created on May 4, 2006
 */
package ca.neo.model.neuron.impl;

import java.util.Properties;

import ca.neo.math.impl.LinearCurveFitter;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Probeable;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.Units;
import ca.neo.model.impl.SpikeOutputImpl;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.util.TimeSeries;
import ca.neo.util.TimeSeries1D;
import ca.neo.util.impl.TimeSeries1DImpl;

/**
 * <p>From Izhikevich, 2003, the model is:<br> 
 * v' = 0.04v*v + 5v + 140 - u + I <br>
 * u' = a(bv - u) </p>
 *  
 * <p>If v >= 30 mV, then v := c and u := u + d (reset after spike)</p>
 * 
 * <p>v represents the membrane potential;
 * u is a membrane recovery variable;
 * a, b, c, and d are modifiable parameters</p>
 * 
 * TODO (bryan): test
 * 
 * @author Hussein
 */
public class IzhikevichSpikeGenerator implements SpikeGenerator, Probeable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Voltage state variable 
	 */
	public static final String V = "V";
	
	/**
	 * Recovery state variable 
	 */
	public static final String U = "U";
	
	private static SimulationMode[] ourSupportedModes = new SimulationMode[]{SimulationMode.DEFAULT};

	private static float myMaxTimeStep = .0005f;
	private static float Vth = 30;
	
	private float myA;
	private float myB;
	private float myC;
	private float myD;
	private float myInitialVoltage;
	
	private float myVoltage;
	private float myRecovery;
	
	private float[] myTime;
	private float[] myVoltageHistory;
	private float[] myRecoveryHistory;
	
	private SimulationMode myMode;
	
	private static final float[] ourNullTime = new float[0]; 
	private static final float[] ourNullVoltageHistory = new float[0];
	private static final float[] ourNullRecoveryHistory = new float[0];
	
	/**
	 * Constructor using "default" parameters
	 */
	public IzhikevichSpikeGenerator() {
		this(0.02f, .2f, -65f, 2f);
	}

	/**
	 * @param a time scale of recovery variable 
	 * @param b sensitivity of recovery variable
	 * @param c voltage reset value
	 * @param d recovery variable reset change
	 */
	public IzhikevichSpikeGenerator(float a, float b, float c, float d) {
		this(a, b, c, d, -65);
	}
	
	/**
	 * @param maxTimeStep maximum integration time step (s). Shorter time steps may be used if a 
	 * 		run(...) is requested with a length that is not an integer multiple of this value.  
	 * @param a time scale of recovery variable 
	 * @param b sensitivity of recovery variable
	 * @param c voltage reset value
	 * @param d recovery variable reset change
	 * @param initialVoltage initial voltage value (varying across neurons can prevent synchrony 
	 * 		at start of simulation)
	 */
	public IzhikevichSpikeGenerator(float a, float b, float c, float d, float initialVoltage) {
		myA = a;
		myB = b;
		myC = c;
		myD = d;
		myInitialVoltage = initialVoltage;
		
		myMode = SimulationMode.DEFAULT;

		reset(false);
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
	
	/**
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		myVoltage = myInitialVoltage;
		myRecovery = 0f;
		myTime = ourNullTime;
		myVoltageHistory = ourNullVoltageHistory;
		myRecoveryHistory = ourNullRecoveryHistory;
	}		

	/**
	 * @see ca.neo.model.neuron.SpikeGenerator#run(float[], float[])
	 */
	public InstantaneousOutput run(float[] time, float[] current) {
		assert time.length >= 2;
		assert time.length == current.length;
		
		return new SpikeOutputImpl(new boolean[]{doSpikingRun(time, current)}, Units.SPIKES, time[time.length-1]);
	}
	
	private boolean doSpikingRun(float[] time, float[] current) {
		float len = time[time.length - 1] - time[0];
		int steps = (int) Math.ceil(len / myMaxTimeStep);
		float dt = len / steps;
		
		myTime = new float[steps];
		myVoltageHistory = new float[steps];
		myRecoveryHistory = new float[steps];
		
		boolean spiking = false;
		for (int i = 0; i < steps; i++) {
			myTime[i] = time[0] + i*dt;
			float I = LinearCurveFitter.InterpolatedFunction.interpolate(time, current, myTime[i]+dt/2f);
			
			float dv = 0.04f*myVoltage*myVoltage + 5*myVoltage + 140 - myRecovery + I;					
			myVoltage += 1000*dt*dv;
			myVoltageHistory[i] = myVoltage;
			
			float du = myA*(myB*myVoltage - myRecovery);
			myRecovery = myRecovery + 1000*dt*du;
			myRecoveryHistory[i] = myRecovery;
			
			if (myVoltage >= Vth) {
				spiking = true;
				myVoltage = myC;		
				myRecovery = myRecovery + myD; 
			}
		}
		
		return spiking;	
	}
	
	/**
	 * @see Probeable#getHistory(String) 
	 */
	public TimeSeries getHistory(String stateName) throws SimulationException {
		TimeSeries1D result = null;
		
		if (stateName.equals(V)) {
			result = new TimeSeries1DImpl(myTime, myVoltageHistory, Units.AVU);
		} else if (stateName.equals(U)){
			result = new TimeSeries1DImpl(myTime, myRecoveryHistory, Units.UNK);
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
		p.setProperty(V, "Membrane potential (arbitrary units)");
		p.setProperty(U, "Recovery variable (arbitrary units)");
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
		myMode = SimulationMode.getClosestMode(mode, ourSupportedModes);
	}

	@Override
	public SpikeGenerator clone() throws CloneNotSupportedException {
		IzhikevichSpikeGenerator result = (IzhikevichSpikeGenerator) super.clone();
		result.myTime = myTime.clone();
		result.myVoltageHistory = myVoltageHistory.clone();
		result.myRecoveryHistory = myRecoveryHistory.clone();
		return result;
	}

}
