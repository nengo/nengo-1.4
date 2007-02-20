package ca.neo.model.neuron.impl;

import ca.neo.math.Function;
import ca.neo.math.impl.FourierFunction;
import ca.neo.math.impl.GaussianPDF;
import ca.neo.math.impl.SigmoidFunction;
import ca.neo.model.SimulationException;
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
//	private float myRefractoryPeriod;
//	private float myLastSpikeTime;

	/**
	 * @param rateFunction Maps input current to Poisson spiking rate
	 */
	public PoissonSpikeGenerator(Function rateFunction) {
		assert rateFunction.getDimension() == 1;
		
		myRateFunction = rateFunction;
//		myRefractoryPeriod = refractoryPeriod;
//		myLastSpikeTime = -myRefractoryPeriod;
	}
	
	/**
	 * @return true
	 * 
	 * @see ca.neo.model.neuron.SpikeGenerator#knownConstantRate()
	 */
	public boolean knownConstantRate() {
		return true;
	}

	/**
	 * @see ca.neo.model.neuron.SpikeGenerator#run(float[], float[]) 
	 */
	public boolean run(float[] time, float[] current) {
		boolean spike = false;
		for (int i = 0; i < time.length - 1 && !spike; i++) {
			float timeSpan = time[i+1] - time[i];
			
			//TODO: reconsider refractory period
//			float netRate = myRateFunction.map(new float[]{current[i]});			
//			float poissonPeriod = 1 / netRate - myRefractoryPeriod; 
//			if (poissonPeriod <= 0) {
//				throw new RuntimeException("Spike rate of " + netRate + " is not achievable with refractory period " + myRefractoryPeriod);
//			}
			
			float rate = myRateFunction.map(new float[]{current[i]});						
			double probNoSpikes = Math.exp(-rate*timeSpan);
			spike = (Math.random() > probNoSpikes);
		}
		
		return spike;
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
		Function current = new FourierFunction(1f, 5f, 1f);
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
			boolean spike = generator.run(new float[]{time, time+dt}, new float[]{c1, c2});
			
			if (spike) {
				spikeTimes[spikes] = time+dt;
				spikes++;
			}
		}

		final float[] spikeTimesTrimmed = new float[spikes];
		System.arraycopy(spikeTimes, 0, spikeTimesTrimmed, 0, spikes);
		
		SpikePattern pattern = new SpikePattern() {
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

}
