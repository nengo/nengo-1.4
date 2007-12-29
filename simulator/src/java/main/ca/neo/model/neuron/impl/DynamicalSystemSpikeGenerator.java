/*
 * Created on 2-Apr-07
 */
package ca.neo.model.neuron.impl;

import java.util.Arrays;
import java.util.Properties;

import ca.neo.config.ConfigUtil;
import ca.neo.dynamics.DynamicalSystem;
import ca.neo.dynamics.Integrator;
import ca.neo.dynamics.impl.EulerIntegrator;
import ca.neo.dynamics.impl.SimpleLTISystem;
import ca.neo.math.CurveFitter;
import ca.neo.math.Function;
import ca.neo.math.impl.LinearCurveFitter;
import ca.neo.model.Configuration;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Probeable;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.SpikeOutput;
import ca.neo.model.Units;
import ca.neo.model.impl.RealOutputImpl;
import ca.neo.model.impl.SpikeOutputImpl;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.util.MU;
import ca.neo.util.TimeSeries;
import ca.neo.util.impl.TimeSeries1DImpl;
import ca.neo.util.impl.TimeSeriesImpl;

/**
 * A SpikeGenerator for which spiking dynamics are expressed in terms of a DynamicalSystem. 
 * 
 * @author Bryan Tripp
 */
public class DynamicalSystemSpikeGenerator implements SpikeGenerator, Probeable {

	private static final long serialVersionUID = 1L;
	
	public static final String DYNAMICS = "dynamics";
	
	private DynamicalSystem myDynamics; 
	private Integrator myIntegrator;
	private TimeSeries myDynamicsOutput;
	private int myVDim;
	private float mySpikeThreshold;
	private float myMinIntraSpikeTime;
	private float myLastSpikeTime;
	private SimulationMode myMode;
	private SimulationMode[] mySupportedModes;
	private Function myConstantRateFunction;
	private boolean myConstantRateFunctionOK; //false if there are relevant configuration changes since function calculated
	private float[] myCurrents;
	private float myTransientTime;
	private Configuration myConfiguration;
	
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

		Units[] units = new Units[dynamics.getOutputDimension()];
		for (int i = 0; i < units.length; i++) {
			units[i] = dynamics.getOutputUnits(i);
		}
		myDynamicsOutput = new TimeSeriesImpl(new float[]{0}, MU.uniform(1, dynamics.getOutputDimension(), 0), units);
	
		myMode = SimulationMode.DEFAULT;
		mySupportedModes = new SimulationMode[]{SimulationMode.DEFAULT};
		myConfiguration = ConfigUtil.defaultConfiguration(this);
	}
	
	/**
	 * Creates a SpikeGenerator that supports CONSTANT_RATE mode. The rate for a given driving current is estimated by 
	 * interpolating steady-state spike counts for simulations with different driving currents (given in the currents arg). 
	 *    
	 * @param dynamics A DynamicalSystem that defines the dynamics of spike generation. 
	 * @param integrator An integrator with which to simulate the DynamicalSystem
	 * @param voltageDim Dimension of output that corresponds to membrane potential
	 * @param spikeThreshold Threshold membrane potential at which a spike is considered to have occurred
	 * @param minIntraSpikeTime Minimum time between spike onsets. If there appears to be a spike onset at the 
	 * 		beginning of a timestep, this value is used to determine whether this is just the continuation of a spike 
	 * 		onset that was already registered in the last timestep
	 * @param currentRange Range of driving currents at which to simulate to find steady-state firing rates for CONSTANT_RATE mode
	 * @param transientTime Simulation time to ignore before counting spikes when finding steady-state rates 
	 */
	public DynamicalSystemSpikeGenerator(DynamicalSystem dynamics, Integrator integrator, int voltageDim, float spikeThreshold, float minIntraSpikeTime, float[] currentRange, float transientTime) {
		myDynamics = dynamics;
		myIntegrator = integrator;
		myVDim = voltageDim;
		mySpikeThreshold = spikeThreshold;
		myMinIntraSpikeTime = minIntraSpikeTime;
		setCurrentRange(new float[]{currentRange[0], currentRange[currentRange.length-1]});
		myTransientTime = transientTime;
		setConstantRateFunction(); 

		Units[] units = new Units[dynamics.getOutputDimension()];
		for (int i = 0; i < units.length; i++) {
			units[i] = dynamics.getOutputUnits(i);
		}
		myDynamicsOutput = new TimeSeriesImpl(new float[]{0}, MU.uniform(1, dynamics.getOutputDimension(), 0), units);
	
		myMode = SimulationMode.DEFAULT;
		mySupportedModes = new SimulationMode[]{SimulationMode.DEFAULT, SimulationMode.CONSTANT_RATE};
		myConfiguration = ConfigUtil.defaultConfiguration(this);
	}
	
	/**
	 * Uses default parameters to allow later configuration.
	 */
	public DynamicalSystemSpikeGenerator() {
		this(getDefaultDynamics(), new EulerIntegrator(.001f), 0, 0.99f, .002f, new float[]{0, 1}, .5f);		
	}
	
	/**
	 * @see ca.neo.model.Configurable#getConfiguration()
	 */
	public Configuration getConfiguration() {
		return myConfiguration;
	}
	
	public DynamicalSystem getDynamics() {
		return myDynamics;
	}
	
	public void setDynamics(DynamicalSystem dynamics) {
		myDynamics = dynamics;
		myConstantRateFunctionOK = false;
	}
	
	public Integrator getIntegrator() {
		return myIntegrator;
	}
	
	public void setIntegrator(Integrator integrator) {
		myIntegrator = integrator;
		myConstantRateFunctionOK = false;
	}
	
	public int getVoltageDim() {
		return myVDim;
	}
	
	public void setVoltageDim(int dim) {
		if (dim < 0 || dim >= myDynamics.getOutputDimension()) {
			throw new IllegalArgumentException(dim 
					+ " is out of range for dynamics with output of dimension " + myDynamics.getOutputDimension());
		}
		myVDim = dim;
		myConstantRateFunctionOK = false;
	}
	
	public float getSpikeThreshold() {
		return mySpikeThreshold;
	}
	
	public void setSpikeThreshold(float threshold) {
		mySpikeThreshold = threshold;
		myConstantRateFunctionOK = false;
	}
	
	public float getMinIntraSpikeTime() {
		return myMinIntraSpikeTime;
	}
	
	public void setMinIntraSpikeTime(float min) {
		myMinIntraSpikeTime = min;
		myConstantRateFunctionOK = false;
	}
	
	public float[] getCurrentRange() {
		return new float[]{myCurrents[0], myCurrents[myCurrents.length - 1]};
	}
	
	public void setCurrentRange(float[] range) {
		if (range.length != 2) {
			throw new IllegalArgumentException("Expected range of length 2: [low high]");
		}
		myCurrents = MU.makeVector(range[0], (range[1]-range[0])/20f, range[1]);
		myConstantRateFunctionOK = false;
	}

	public float getTransientTime() {
		return myTransientTime;
	}
	
	public void setTransientTime(float transientTime) {
		myTransientTime = transientTime;
		myConstantRateFunctionOK = false;
	}
	
	public boolean getConstantRateModeSupported() {
		return mySupportedModes.length == 2;
	}
	
	private static DynamicalSystem getDefaultDynamics() {
		return new SimpleLTISystem(
				new float[]{2*(float)Math.PI, 2*(float)Math.PI}, 
				new float[][]{new float[1], new float[]{1}},
				new float[][]{new float[]{1, 0}}, 
				new float[2], 
				Units.uniform(Units.UNK, 1));
	}
	
	private void setConstantRateFunction() {
		//make sure currents are in ascending order
		Arrays.sort(myCurrents);

		SimulationMode mode = myMode;
		myMode = SimulationMode.DEFAULT;
		float dt = .001f;
		float simTime = 1f; 
		float[] rates = new float[myCurrents.length];
		for (int i = 0; i < myCurrents.length; i++) {
			countSpikes(myCurrents[i], dt, myTransientTime);
			rates[i] = (float) countSpikes(myCurrents[i], dt, simTime) / simTime; 
		}
		
		CurveFitter cf = new LinearCurveFitter();
		Function result = cf.fit(myCurrents, rates);
		myConstantRateFunction = result;
		myConstantRateFunctionOK = true;
		myMode = mode;
	}
	
	private int countSpikes(float current, float dt, float time) {
		int steps = (int) Math.ceil(time / dt);
		int spikes = 0;
		for (int i = 0; i < steps; i++) {
			SpikeOutput output = (SpikeOutput) run(new float[]{(float) i*dt, (float) (i+1)*dt}, new float[]{current, current});
			if (output.getValues()[0]) spikes += 1;
		}
		return spikes;
	}
	
	/**
	 * Runs the spike generation dynamics and returns a spike if membrane potential rises above spike threshold. 
	 * 
	 * @see ca.neo.model.neuron.SpikeGenerator#run(float[], float[])
	 */
	public InstantaneousOutput run(float[] time, float[] current) {
		if (myMode.equals(SimulationMode.CONSTANT_RATE)) {
			if (!myConstantRateFunctionOK) setConstantRateFunction();
			float rate = myConstantRateFunction.map(new float[]{current[current.length-1]});			
			return new RealOutputImpl(new float[]{rate}, Units.SPIKES_PER_S, time[time.length-1]);
		} else {
			boolean spike = false;

			myDynamicsOutput = myIntegrator.integrate(myDynamics, new TimeSeries1DImpl(time, current, Units.uAcm2));
			float[][] values = myDynamicsOutput.getValues();
			
			for (int i = 0; i < values.length && !spike; i++) {
				if (values[i][myVDim] >= mySpikeThreshold) {
					boolean crossingThreshold = i > 0 && values[i-1][myVDim] < mySpikeThreshold;
					boolean nonDuplicateAtStart 
						= i == 0 && values[1][myVDim] > values[0][myVDim] && myDynamicsOutput.getTimes()[0] > myLastSpikeTime + myMinIntraSpikeTime;
					
					if (crossingThreshold || nonDuplicateAtStart) {
						spike = true;
						myLastSpikeTime = myDynamicsOutput.getTimes()[i];
					}
				}
			}
			
			return new SpikeOutputImpl(new boolean[]{spike}, Units.SPIKES, time[time.length-1]);
		}
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
		return myMode;
	}

	/**
	 * @see ca.neo.model.SimulationMode.ModeConfigurable#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
		myMode = SimulationMode.getClosestMode(mode, mySupportedModes);
	}

	/** 
	 * @see ca.neo.model.Probeable#getHistory(java.lang.String)
	 */
	public TimeSeries getHistory(String stateName) throws SimulationException {
		if (stateName.equals(DYNAMICS)) {
			return myDynamicsOutput;
		} else {
			throw new SimulationException("Unknown state: " + stateName);
		}
	}

	/** 
	 * @see ca.neo.model.Probeable#listStates()
	 */
	public Properties listStates() {
		Properties result = new Properties();
		result.setProperty(DYNAMICS, "Result of spike generation dynamics");
		return result;
	}
	
}
