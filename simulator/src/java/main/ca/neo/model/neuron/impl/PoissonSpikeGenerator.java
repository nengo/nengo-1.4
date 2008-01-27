package ca.neo.model.neuron.impl;

import ca.neo.math.Function;
import ca.neo.math.impl.FourierFunction;
import ca.neo.math.impl.SigmoidFunction;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.SpikeOutput;
import ca.neo.model.Units;
import ca.neo.model.impl.RealOutputImpl;
import ca.neo.model.impl.SpikeOutputImpl;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.plot.Plotter;
import ca.neo.util.SpikePattern;

/**
 * A phenomenological SpikeGenerator that produces spikes according to a Poisson 
 * process with a rate that varies as a function of current. 
 * 
 * TODO: test
 *  
 * @author Bryan Tripp
 */
public class PoissonSpikeGenerator implements SpikeGenerator {

	private static final long serialVersionUID = 1L;
	
	private Function myRateFunction;
	private SimulationMode myMode;
	private SimulationMode[] mySupportedModes;
//	private float myRefractoryPeriod;
//	private float myLastSpikeTime;

	/**
	 * @param rateFunction Maps input current to Poisson spiking rate
	 */
	public PoissonSpikeGenerator(Function rateFunction) {
		setRateFunction(rateFunction);
		myMode = SimulationMode.DEFAULT;
		mySupportedModes = new SimulationMode[]{SimulationMode.DEFAULT, SimulationMode.CONSTANT_RATE};
//		myRefractoryPeriod = refractoryPeriod;
//		myLastSpikeTime = -myRefractoryPeriod;
	}
	
	/**
	 * Uses a default sigmoid rate function
	 */
	public PoissonSpikeGenerator() {
		this(new SigmoidFunction(.5f, 10f, 0f, 20f));
	}
	
	/**
	 * @return Function that maps input current to Poisson spiking rate
	 */
	public Function getRateFunction() {
		return myRateFunction;
	}

	/**
	 * @param function Function that maps input current to Poisson spiking rate
	 */
	public void setRateFunction(Function function) {
		if (function.getDimension() != 1) {
			throw new IllegalArgumentException("Function must be one-dimensional (mapping from driving current to rate)");
		}
		myRateFunction = function;
	}

	/**
	 * @see ca.neo.model.neuron.SpikeGenerator#run(float[], float[]) 
	 */
	public InstantaneousOutput run(float[] time, float[] current) {
		InstantaneousOutput result = null;
		
		if (myMode.equals(SimulationMode.CONSTANT_RATE)) {
			result = new RealOutputImpl(new float[]{myRateFunction.map(new float[]{current[0]})}, Units.SPIKES_PER_S, time[time.length-1]);
		} else {
			boolean spike = false;
			for (int i = 0; i < time.length - 1 && !spike; i++) {
				float timeSpan = time[i+1] - time[i];
				
				//TODO: reconsider refractory period
//				float netRate = myRateFunction.map(new float[]{current[i]});			
//				float poissonPeriod = 1 / netRate - myRefractoryPeriod; 
//				if (poissonPeriod <= 0) {
//					throw new RuntimeException("Spike rate of " + netRate + " is not achievable with refractory period " + myRefractoryPeriod);
//				}
				
				float rate = myRateFunction.map(new float[]{current[i]});						
				double probNoSpikes = Math.exp(-rate*timeSpan);
				spike = (Math.random() > probNoSpikes);
			}
			
			result = new SpikeOutputImpl(new boolean[]{spike}, Units.SPIKES, time[time.length-1]); 
		}
		
		return result;
	}

	/**
	 * @see ca.neo.model.neuron.SpikeGenerator#runConstantRate(float, float)
	 */
	public float runConstantRate(float time, float current) throws SimulationException {
		return myRateFunction.map(new float[]{current});
	}

	/**
	 * This method does nothing, because a Poisson process is stateless. 
	 * 
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
	}
	
	//functional test
	public static void main(String[] args) {
		Function current = new FourierFunction(1f, 5f, 1f, (long) Math.random());
		Function rate = new SigmoidFunction(0, 5, 0, 40);
		PoissonSpikeGenerator generator = new PoissonSpikeGenerator(rate);
		
		float T = 1;
		float dt = .0005f;
		int steps = (int) Math.floor(T/dt);
		float[] spikeTimes = new float[steps];
		int spikes = 0;
		for (int i = 0; i < steps; i++) {
			float time = dt * (float) i;
			float c1 = current.map(new float[]{time});
			float c2 = current.map(new float[]{time+dt});
			boolean spike = ((SpikeOutput) generator.run(new float[]{time, time+dt}, new float[]{c1, c2})).getValues()[0];
			
			if (spike) {
				spikeTimes[spikes] = time+dt;
				spikes++;
			}
		}

		final float[] spikeTimesTrimmed = new float[spikes];
		System.arraycopy(spikeTimes, 0, spikeTimesTrimmed, 0, spikes);
		
		SpikePattern pattern = new SpikePattern() {
			private static final long serialVersionUID = 1L;
			public int getNumNeurons() {
				return 1;
			}
			public float[] getSpikeTimes(int neuron) {
				return spikeTimesTrimmed;
			}			
		};
		
		Plotter.plot(rate, -1, .001f, 1, "rate");
		Plotter.plot(current, 0, dt, T, "current");
		Plotter.plot(pattern);
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
