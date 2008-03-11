package ca.neo.model.neuron.impl;

import ca.neo.math.Function;
import ca.neo.math.PDF;
import ca.neo.math.PDFTools;
import ca.neo.math.impl.FourierFunction;
import ca.neo.math.impl.IndicatorPDF;
import ca.neo.math.impl.LinearFunction;
import ca.neo.math.impl.PoissonPDF;
import ca.neo.math.impl.SigmoidFunction;
import ca.neo.math.impl.SineFunction;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.SpikeOutput;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.model.impl.NodeFactory;
import ca.neo.model.impl.RealOutputImpl;
import ca.neo.model.impl.SpikeOutputImpl;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.neo.model.neuron.Neuron;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.model.neuron.SynapticIntegrator;
import ca.neo.plot.Plotter;
import ca.neo.util.MU;
import ca.neo.util.Probe;
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

	/**
	 * @param rateFunction Maps input current to Poisson spiking rate
	 */
	public PoissonSpikeGenerator(Function rateFunction) {
		setRateFunction(rateFunction);
		myMode = SimulationMode.DEFAULT;
		mySupportedModes = new SimulationMode[]{SimulationMode.DEFAULT, SimulationMode.CONSTANT_RATE, SimulationMode.RATE};
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
		} else if (myMode.equals(SimulationMode.RATE)) {
			float totalTimeSpan = time[time.length-1] - time[0];
			float ratePerSecond = myRateFunction.map(new float[]{MU.mean(current)});	
			float ratePerStep = totalTimeSpan * ratePerSecond;
			float numSpikes = new PoissonPDF(ratePerStep).sample()[0];
			
			result = new RealOutputImpl(new float[]{numSpikes / totalTimeSpan}, Units.SPIKES_PER_S, time[time.length-1]);
		} else {
			boolean spike = false;
			for (int i = 0; i < time.length - 1 && !spike; i++) {
				float timeSpan = time[i+1] - time[i];
				
				float rate = myRateFunction.map(new float[]{current[i]});						
				double probNoSpikes = Math.exp(-rate*timeSpan);
				spike = (PDFTools.random() > probNoSpikes);
			}
			
			result = new SpikeOutputImpl(new boolean[]{spike}, Units.SPIKES, time[time.length-1]); 
		}
		
		return result;
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
	
	/**
	 * Creates PoissonSpikeGenerators with linear response functions. 
	 * 
	 * @author Bryan Tripp
	 */
	public static class LinearFactory implements SpikeGeneratorFactory {

		private static final long serialVersionUID = 1L;
		
		PDF myMaxRate;
		PDF myIntercept;
		boolean myRectified;
		
		public LinearFactory() {
			myMaxRate = new IndicatorPDF(200, 400);
			myIntercept = new IndicatorPDF(-.9f, .9f);
			myRectified = true;
		}
		
		/**
		 * @return Firing rate of produced SpikeGenerators when input current is 1
		 */
		public PDF getMaxRate() {
			return myMaxRate;
		}
		
		/**
		 * @param maxRate Firing rate of produced SpikeGenerators when input current is 1
		 */
		public void setMaxRate(PDF maxRate) {
			myMaxRate = maxRate;
		}
		
		/**
		 * @return Input current at which firing rate is zero
		 */
		public PDF getIntercept() {
			return myIntercept;
		}
		
		/**
		 * @param intercept Input current at which firing rate is zero
		 */
		public void setIntercept(PDF intercept) {
			myIntercept = intercept;
		}
		
		/**
		 * @return If true, response functions will be rectified (firing rates > 0)
		 */
		public boolean getRectified() {
			return myRectified;
		}
		
		/**
		 * @param rectified If true, response functions will be rectified (firing rates > 0)
		 */
		public void setRectified(boolean rectified) {
			myRectified = rectified;
		}
		
		/**
		 * @see ca.neo.model.neuron.impl.SpikeGeneratorFactory#make()
		 */
		public SpikeGenerator make() {
			float maxRate = myMaxRate.sample()[0];
			float intercept = myIntercept.sample()[0];
			float slope = maxRate / (1-intercept);
			float bias = - slope * intercept;
			Function line = new LinearFunction(new float[]{slope}, bias, myRectified);
			return new PoissonSpikeGenerator(line);
		}
		
	}
	
	/**
	 * A factory for neurons with linear or rectified linear response functions. 
	 * 
	 * @author bryan
	 *
	 */
	public static class LinearNeuronFactory implements NodeFactory {

		private static final long serialVersionUID = 1L;
		
		private static float ourMaxTimeStep = .0005f;
		private static Units ourCurrentUnits = Units.ACU;

		private LinearFactory mySpikeGeneratorFactory;
		
		public LinearNeuronFactory(PDF maxRate, PDF intercept, boolean rectified) {
			mySpikeGeneratorFactory = new LinearFactory();
			mySpikeGeneratorFactory.setIntercept(intercept);
			mySpikeGeneratorFactory.setMaxRate(maxRate);
			mySpikeGeneratorFactory.setRectified(rectified);
		}
		
		/**
		 * @see ca.neo.model.impl.NodeFactory#make(java.lang.String)
		 */
		public Node make(String name) throws StructuralException {
			SynapticIntegrator integrator = new LinearSynapticIntegrator(ourMaxTimeStep, ourCurrentUnits);
			SpikeGenerator generator = mySpikeGeneratorFactory.make();			
			return new PlasticExpandableSpikingNeuron(integrator, generator, 1, 0, name);		
		}

		/**
		 * @see ca.neo.model.impl.NodeFactory#getTypeDescription()
		 */
		public String getTypeDescription() {
			return "Linear Neuron";
		}
		
	}
	
	public static class SigmoidFactory implements SpikeGeneratorFactory {

		private static final long serialVersionUID = 1L;
		
		private PDF mySlope;
		private PDF myInflection;
		private PDF myMaxRate;
		
		public SigmoidFactory() {
			mySlope = new IndicatorPDF(1, 10);
			myInflection = new IndicatorPDF(-1f, 1f);
			myMaxRate = new IndicatorPDF(200, 400);
		}
		
		/**
		 * @return Distribution of slopes of the sigmoid functions that describe current-firing rate relationships
		 * 		before scaling to maxRate (slope at inflection point = slope*maxRate)
		 */
		public PDF getSlope() {
			return mySlope;
		}
		
		/**
		 * @param slope Distribution of slopes of the sigmoid functions that describe current-firing rate relationships
		 * 		before scaling to maxRate (slope at inflection point = slope*maxRate)
		 */
		public void setSlope(PDF slope) {
			mySlope = slope;
		}
		
		/**
		 * @return Distribution of inflection points of the sigmoid functions that describe current-firing rate relationships
		 */
		public PDF getInflection() {
			return myInflection;
		}
		
		/**
		 * @param inflection Distribution of inflection points of the sigmoid functions that describe current-firing rate relationships
		 */
		public void setInflection(PDF inflection) {
			myInflection = inflection;
		}
		
		/**
		 * @return Distribution of maximum firing rates
		 */
		public PDF getMaxRate() {
			return myMaxRate;
		}
		
		/**
		 * @param maxRate Distribution of maximum firing rates
		 */
		public void setMaxRate(PDF maxRate) {
			myMaxRate = maxRate;
		}

		/**
		 * @see ca.neo.model.neuron.impl.SpikeGeneratorFactory#make()
		 */
		public SpikeGenerator make() {
			Function sigmoid = new SigmoidFunction(myInflection.sample()[0], mySlope.sample()[0], 0, myMaxRate.sample()[0]);
			return new PoissonSpikeGenerator(sigmoid);
		}
		
	}
	
	/**
	 * A factory for neurons with sigmoid response functions. 
	 * 
	 * @author Bryan Tripp
	 */
	public static class SigmoidNeuronFactory implements NodeFactory {
		
		private static final long serialVersionUID = 1L;
		
		private static float ourMaxTimeStep = .0005f;
		private static Units ourCurrentUnits = Units.ACU;

		private SigmoidFactory mySigmoidFactory;
		
		/**
		 * Neurons from this factory will have Poisson firing rates that are sigmoidal functions 
		 * of current. The constructor arguments parameterize the sigmoid function. 
		 *   
		 * @param slope Distribution of slopes of the sigmoid functions that describe current-firing rate relationships, 
		 * 		before scaling to maxRate (slope at inflection point = slope*maxRate)
		 * @param inflection Distribution of inflection points of the sigmoid functions that describe current-firing rate relationships
		 * @param maxRate Distribution of maximum firing rates
		 */
		public SigmoidNeuronFactory(PDF slope, PDF inflection, PDF maxRate) {
			mySigmoidFactory = new SigmoidFactory();
			mySigmoidFactory.setSlope(slope);
			mySigmoidFactory.setInflection(inflection);
			mySigmoidFactory.setMaxRate(maxRate);
		}
		
		/**
		 * @see ca.neo.model.impl.NodeFactory#make(java.lang.String)
		 */
		public Neuron make(String name) throws StructuralException {			
			SynapticIntegrator integrator = new LinearSynapticIntegrator(ourMaxTimeStep, ourCurrentUnits);
			SpikeGenerator generator = mySigmoidFactory.make();			
			return new PlasticExpandableSpikingNeuron(integrator, generator, 1, 0, name);		
		}

		/**
		 * @see ca.neo.model.impl.NodeFactory#getTypeDescription()
		 */
		public String getTypeDescription() {
			return "Sigmoid Neuron";
		}

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
		
//		SigmoidNeuronFactory snf = new SigmoidNeuronFactory(new IndicatorPDF(-10, 10), new IndicatorPDF(-1, 1), new IndicatorPDF(100, 200));
		LinearNeuronFactory lnf = new LinearNeuronFactory(new IndicatorPDF(200, 400), new IndicatorPDF(-1, 1), true);
		NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();
		ef.setNodeFactory(lnf);
		
		try {
			NEFEnsemble ensemble = ef.make("test", 20, 1);
			ensemble.addDecodedTermination("input", MU.I(1), .01f, false);						
			Plotter.plot(ensemble);
			
			Network network = new NetworkImpl();
			network.addNode(ensemble);
			FunctionInput input = new FunctionInput("input", new Function[]{new SineFunction(3)}, Units.UNK);
			network.addNode(input);
			network.addProjection(input.getOrigin(FunctionInput.ORIGIN_NAME), ensemble.getTermination("input"));
			
			network.setMode(SimulationMode.RATE);
			Probe rates = network.getSimulator().addProbe("test", "rate", true);
			network.run(0, 2);
//			Plotter.plot(rates.getData(), .05f, "rates");
			Plotter.plot(rates.getData(), "rates");
		} catch (StructuralException e) {
			e.printStackTrace();
		} catch (SimulationException e) {
			e.printStackTrace();
		}
		
	}

}
