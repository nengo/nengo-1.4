/*
 * Created on 24-May-07
 */
package ca.neo.model.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.neo.dynamics.DynamicalSystem;
import ca.neo.dynamics.Integrator;
import ca.neo.dynamics.impl.EulerIntegrator;
import ca.neo.dynamics.impl.SimpleLTISystem;
import ca.neo.math.Function;
import ca.neo.math.PDF;
import ca.neo.math.impl.GaussianPDF;
import ca.neo.model.Configuration;
import ca.neo.model.Noise;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.Configuration.Property;
import ca.neo.plot.Plotter;
import ca.neo.util.MU;
import ca.neo.util.TimeSeries;
import ca.neo.util.impl.TimeSeriesImpl;

/**
 * Default additive Noise implementations.
 * 
 * TODO: unit tests
 * 
 * @author Bryan Tripp
 */
public class NoiseFactory {

	/**
	 * @param frequency Frequency (in simulation time) with which new noise values are drawn from the PDF
	 * @param pdf PDF from which new noise values are drawn. The dimension must equal 
	 * 		the input dimension of the dynamics. 
	 */
	public static Noise makeRandomNoise(float frequency, PDF pdf) {
		return new NoiseImplPDF(frequency, pdf, null, null);
	}

	/**
	 * @param frequency Frequency (in simulation time) with which new noise values are drawn from the PDF
	 * @param pdf PDF from which new noise values are drawn. The dimension must equal 
	 * 		the input dimension of the dynamics. 
	 * @param dynamics Dynamics through which raw noise values pass before they are combined with non-noise.
	 * 		The output dimension must equal the dimension of expected input to getValues(). 
	 * @param integrator Integrator used to solve dynamics 
	 */
	public static Noise makeRandomNoise(float frequency, PDF pdf, DynamicalSystem dynamics, Integrator integrator) {
		return new NoiseImplPDF(frequency, pdf, dynamics, integrator);
	}

	/**
	 * @param dimension Dimension of the Noise
	 * @return Zero additive Noise
	 */
	public static Noise makeNullNoise(int dimension) {
		return new NoiseImplNull(dimension);
	}
	
	/**
	 * @param functions A list of Noise Functions (one for each noise dimension) 
	 * @return Additive Noise where values are given explicit functions of time
	 */
	public static Noise makeExplicitNoise(Function[] functions) {
		return new NoiseImplFunction(functions);
	}
	
	private static class NoiseImplFunction implements Noise {
				
//		private Function[] myFunctions;
		private List<Function> myFunctions;
		private ConfigurationImpl myConfiguration;
		
		public NoiseImplFunction(Function[] functions) {
			myFunctions = Arrays.asList(functions);
			myConfiguration = new ConfigurationImpl(this);
			myConfiguration.defineSingleValuedProperty(DIMENSION_PROPERTY, Integer.class, false);
//			Property dimProp = new ConfigurationImpl.SingleValuedProperty(myConfiguration, DIMENSION_PROPERTY, Integer.class, true) {
//				@Override
//				public void setValue(int index, Object value) throws StructuralException {
//					if (index == 0 && value instanceof Integer) {
//						setDimension(((Integer) value).intValue());
//					}
//				}				
//			};
			myConfiguration.defineProperty(new ConfigurationImpl.ListProperty(myConfiguration, "functions", Function.class, myFunctions));
//			Property funProp = new ConfigurationImpl.MultiValuedProperty(myConfiguration, "functions", Function.class, true) {
//				
//			};
		}

		public NoiseImplFunction() {
			myFunctions = new ArrayList<Function>(10);
		}
		
		/**
		 * @see ca.neo.model.Configurable#getConfiguration()
		 */
		public Configuration getConfiguration() {
			return myConfiguration;
		}

		/**
		 * @see ca.neo.model.Noise#getDimension()
		 */
		public int getDimension() {
			return myFunctions.size();
		}
		
//		private void setDimension(int dimension) {
//			Function[] f = new Function[dimension];
//			System.arraycopy(myFunctions, 0, f, 0, Math.min(f.length, myFunctions.length));
//			myFunctions = f;
//		}

		/**
		 * @see ca.neo.model.Noise#getValues(float, float, float[])
		 */
		public float[] getValues(float startTime, float endTime, float[] input) {
			float[] result = new float[myFunctions.size()];
			
			for (int i = 0; i < result.length; i++) {
				result[i] = myFunctions.get(i).map(new float[]{startTime});
			}
			
			return result;
		}
		
	}
	
	private static class NoiseImplNull implements Noise {
		
		private int myDimension;
		private ConfigurationImpl myConfiguration;
		
		public NoiseImplNull(int dimension) {
			myDimension = dimension;
			myConfiguration = new ConfigurationImpl(this);
			myConfiguration.defineSingleValuedProperty(DIMENSION_PROPERTY, Integer.class, true);
		}
		
		public NoiseImplNull() {
			this(1);
		}

		/**
		 * @see ca.neo.model.Configurable#getConfiguration()
		 */
		public Configuration getConfiguration() {
			return myConfiguration;
		}

		/**
		 * @see ca.neo.model.Noise#getDimension()
		 */
		public int getDimension() {
			return myDimension;
		}
		
		/**
		 * @see getDimension()
		 */
		public void setDimension(int dimension) {
			myDimension = dimension;
		}

		/**
		 * @see ca.neo.model.Noise#getValues(float, float, float[])
		 */
		public float[] getValues(float startTime, float endTime, float[] input) {
			return input;
		}
		
	}

	private static class NoiseImplPDF implements Noise {
		
		private float myPeriod;
		private PDF myPDF;
		private DynamicalSystem myDynamics;
		private Integrator myIntegrator;
		private float myLastGenTime = 0;
		private float myLastDynamicsTime = 0;
		private float[] myLastRawNoise;
		private float[] myCurrentRawNoise;
		private Units[] myUnits;
		private ConfigurationImpl myConfiguration;
		
		/**
		 * @param frequency Frequency (in simulation time) with which new noise values are drawn from the PDF
		 * @param pdf PDF from which new noise values are drawn. The dimension must equal 
		 * 		the input dimension of the dynamics. 
		 * @param dynamics Dynamics through which raw noise values pass before they are combined with non-noise.
		 * 		The output dimension must equal the dimension of expected input to getValues(). 
		 * @param integrator Integrator used to solve dynamics 
		 */
		public NoiseImplPDF(float frequency, PDF pdf, DynamicalSystem dynamics, Integrator integrator) {
			if (dynamics != null && pdf.getDimension() != dynamics.getInputDimension()) {
				throw new IllegalArgumentException("PDF dimension (" + pdf.getDimension() + ") must equal dynamics input dimension (" 
						+ dynamics.getInputDimension() + ")");
			}
			
			setFrequency(frequency);
			setPDF(pdf);
			myDynamics = dynamics;		
			myIntegrator = integrator;
			
			myConfiguration = new ConfigurationImpl(this);
			myConfiguration.defineSingleValuedProperty("frequency", Float.class, true);
			myConfiguration.defineSingleValuedProperty("PDF", PDF.class, true);
			myConfiguration.defineSingleValuedProperty("dynamics", DynamicalSystem.class, true);
			myConfiguration.defineSingleValuedProperty("integrator", Integrator.class, true);
		}
		
		public NoiseImplPDF() {
			this(1, null, null, null);
			//TODO: reasonable defaults / Configuration constructor; deal with null dynamics and integrator in getter
		}
		
		/**
		 * @see ca.neo.model.Configurable#getConfiguration()
		 */
		public Configuration getConfiguration() {
			return myConfiguration;
		}

		public float getFrequency() {
			return 1f /myPeriod;
		}
		
		public void setFrequency(float frequency) {
			myPeriod = 1f / frequency;
		}
		
		public PDF getPDF() {
			return myPDF;
		}
		
		public void setPDF(PDF pdf) {
			myPDF = pdf;
			myCurrentRawNoise = myPDF.sample();
			myUnits = Units.uniform(Units.UNK, myCurrentRawNoise.length);
		}
		
		public DynamicalSystem getDynamics() {
			return myDynamics;
		}
		
		public void setDynamics(DynamicalSystem dynamics) {
			myDynamics = dynamics;
		}
		
		public Integrator getIntegrator() {
			return myIntegrator;
		}
		
		public void setIntegrator(Integrator integrator) {
			myIntegrator = integrator;
		}
		
		/**
		 * @see ca.neo.model.Noise#getValues(float, float, float[])
		 */
		public float[] getValues(float startTime, float endTime, float[] input) {
			float[] result = null;
			
			myLastRawNoise = myCurrentRawNoise;
			if (endTime >= myLastGenTime + myPeriod) {
				myCurrentRawNoise = myPDF.sample();
				myLastGenTime = endTime;
			}

			if (myDynamics == null) {
				if (myCurrentRawNoise.length != input.length) {
					throw new IllegalArgumentException("Expected input of dimension " + myCurrentRawNoise.length);
				}
				result = MU.sum(input, myCurrentRawNoise);
			} else {
				TimeSeries raw = new TimeSeriesImpl(new float[]{myLastDynamicsTime, endTime}, 
						new float[][]{myLastRawNoise, myCurrentRawNoise}, myUnits);
				float[][] output = myIntegrator.integrate(myDynamics, raw).getValues();
				result = MU.sum(input, output[output.length-1]);
				myLastDynamicsTime = endTime;
			}

			return result;
		}
		
		/**
		 * @see ca.neo.model.Noise#getDimension()
		 */
		public int getDimension() {
			return myDynamics == null ? myPDF.getDimension() : myDynamics.getOutputDimension();
		}
		
	}
	
	//functional test ... 
	public static void main(String[] args) {
		float tau = .01f;
		DynamicalSystem dynamics = new SimpleLTISystem(new float[]{-1f/tau}, new float[][]{new float[]{1f/tau}}, MU.I(1), new float[1], new Units[]{Units.UNK});
		Integrator integrator = new EulerIntegrator(.0001f);
//		Noise noise = NoiseFactory.makeRandomNoise(1000, new GaussianPDF(0, 1));		
		Noise noise = NoiseFactory.makeRandomNoise(1000, new GaussianPDF(0, 1), dynamics, integrator);		
//		Noise noise = NoiseFactory.makeNullNoise(1);		
//		Noise noise = NoiseFactory.makeExplicitNoise(new Function[]{new FourierFunction(1, 10, 1, -1)});
		
		float elapsedTime = .001f;
		int steps = 1000;
		float[][] output = new float[steps][];
		for (int i = 0; i < steps; i++) {
			output[i] = noise.getValues(i*elapsedTime, (i+1)*elapsedTime, new float[1]);
		}
		
		Plotter.plot(MU.prod(output, new float[]{1}), "noise");
	}

}
