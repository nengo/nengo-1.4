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
import ca.neo.math.impl.ConstantFunction;
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
	 * @return Zero additive Noise
	 */
	public static Noise makeNullNoise() {
		return new NoiseImplNull();
	}
	
	/**
	 * @param function A function of time  
	 * @return Additive Noise where values are given explicit functions of time
	 */
	public static Noise makeExplicitNoise(Function function) {
		return new NoiseImplFunction(function);
	}
	
	public static class NoiseImplFunction implements Noise {
				
		private Function myFunction;
		
		public NoiseImplFunction(Function function) {
			myFunction = function;
		}

		public NoiseImplFunction() {
			myFunction = new ConstantFunction(1, 0);
		}
		
		/**
		 * @see ca.neo.model.Noise#getValue(float, float, float)
		 */
		public float getValue(float startTime, float endTime, float input) {
			return input + myFunction.map(new float[]{startTime});
		}

		@Override
		public Noise clone() {
			try {
				return (Noise) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	public static class NoiseImplNull implements Noise {
		
		/**
		 * @see ca.neo.model.Noise#getValue(float, float, float)
		 */
		public float getValue(float startTime, float endTime, float input) {
			return input;
		}

		@Override
		public Noise clone() {
			try {
				return (Noise) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
		
	}

	public static class NoiseImplPDF implements Noise {
		
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
		 * @param pdf PDF from which new noise values are drawn. The dimension of the space over which the PDF is defined  
		 * 		must equal the input dimension of the dynamics. 
		 * @param dynamics Dynamics through which raw noise values pass before they are combined with non-noise.
		 * 		The input dimension must match the PDF and the output dimension must equal one. Can be null in which 
		 * 		case the PDF must be one-dimensional. 
		 * @param integrator Integrator used to solve dynamics. Can be null if dynamics is null.  
		 */
		public NoiseImplPDF(float frequency, PDF pdf, DynamicalSystem dynamics, Integrator integrator) {
			if (dynamics != null && pdf.getDimension() != dynamics.getInputDimension()) {
				throw new IllegalArgumentException("PDF dimension (" + pdf.getDimension() + ") must equal dynamics input dimension (" 
						+ dynamics.getInputDimension() + ")");
			}
									
			setFrequency(frequency);
			setPDF(pdf);
			setDynamics(dynamics);		
			myIntegrator = integrator;			
		}
		
		public NoiseImplPDF() {
			this(1, new GaussianPDF(0, 1), null, null);
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
			if (myDynamics == null && pdf.getDimension() != 1) {
				throw new IllegalArgumentException("With null dynamics, the PDF must be defined over one dimension.");	
			}
			
			myPDF = pdf;
			myCurrentRawNoise = myPDF.sample();
			myUnits = Units.uniform(Units.UNK, myCurrentRawNoise.length);
		}
		
		public DynamicalSystem getDynamics() {
			return myDynamics;
		}
		
		public void setDynamics(DynamicalSystem dynamics) {
			if (dynamics != null && dynamics.getOutputDimension() != 1) {
				throw new IllegalArgumentException("The output of the dynamics must be one-dimensional");
			}
			
			myDynamics = dynamics;
		}
		
		public Integrator getIntegrator() {
			return myIntegrator;
		}
		
		public void setIntegrator(Integrator integrator) {
			myIntegrator = integrator;
		}
		
		/**
		 * @see ca.neo.model.Noise#getValue(float, float, float)
		 */
		public float getValue(float startTime, float endTime, float input) {
			float result = input;
			
			myLastRawNoise = myCurrentRawNoise;
			if (endTime >= myLastGenTime + myPeriod) {
				myCurrentRawNoise = myPDF.sample();
				myLastGenTime = endTime;
			}

			if (myDynamics == null) {
				result = input + myCurrentRawNoise[0];
			} else {
				TimeSeries raw = new TimeSeriesImpl(new float[]{myLastDynamicsTime, endTime}, 
						new float[][]{myLastRawNoise, myCurrentRawNoise}, myUnits);
				float[][] output = myIntegrator.integrate(myDynamics, raw).getValues();
				result = input + output[output.length-1][0];
				myLastDynamicsTime = endTime;
			}

			return result;
		}

		@Override
		public Noise clone() {
			try {
				NoiseImplPDF result = (NoiseImplPDF) super.clone();
				if (myDynamics != null) {
					result.setDynamics((DynamicalSystem) myDynamics.clone());					
				}
				return result;				
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
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
		float[] output = new float[steps];
		for (int i = 0; i < steps; i++) {
			output[i] = noise.getValue(i*elapsedTime, (i+1)*elapsedTime, 1);
		}
		
		Plotter.plot(output, "noise");
	}

}
