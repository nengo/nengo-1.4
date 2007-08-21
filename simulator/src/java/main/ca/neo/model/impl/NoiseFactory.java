/*
 * Created on 24-May-07
 */
package ca.neo.model.impl;

import ca.neo.dynamics.DynamicalSystem;
import ca.neo.dynamics.Integrator;
import ca.neo.dynamics.impl.EulerIntegrator;
import ca.neo.dynamics.impl.SimpleLTISystem;
import ca.neo.math.Function;
import ca.neo.math.PDF;
import ca.neo.math.impl.GaussianPDF;
import ca.neo.model.Noise;
import ca.neo.model.Units;
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
		
		private Function[] myFunctions;
		
		public NoiseImplFunction(Function[] functions) {
			myFunctions = functions;
		}

		/**
		 * @see ca.neo.model.Noise#getDimension()
		 */
		public int getDimension() {
			return myFunctions.length;
		}

		/**
		 * @see ca.neo.model.Noise#getValues(float, float, float[])
		 */
		public float[] getValues(float startTime, float endTime, float[] input) {
			float[] result = new float[myFunctions.length];
			
			for (int i = 0; i < result.length; i++) {
				result[i] = myFunctions[i].map(new float[]{startTime});
			}
			
			return result;
		}
		
	}
	
	private static class NoiseImplNull implements Noise {
		
		private int myDimension;
		
		public NoiseImplNull(int dimension) {
			myDimension = dimension;
		}

		/**
		 * @see ca.neo.model.Noise#getDimension()
		 */
		public int getDimension() {
			return myDimension;
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
			
			myPeriod = 1f / frequency;
			myPDF = pdf;
			myDynamics = dynamics;		
			myIntegrator = integrator;
			myCurrentRawNoise = myPDF.sample();
			myUnits = Units.uniform(Units.UNK, myCurrentRawNoise.length);
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
