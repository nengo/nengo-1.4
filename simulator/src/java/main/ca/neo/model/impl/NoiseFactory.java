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

	/**
	 * Note: there are no public setters here for the same reason as in NoiseImplPDF. 
	 * 
	 * @author Bryan Tripp
	 *
	 */
	public static class NoiseImplFunction implements Noise {
				
		private static final long serialVersionUID = 1L;
		
		private Function myFunction;
		
		/**
		 * @param function A function of time that explicitly defines the noise
		 */
		public NoiseImplFunction(Function function) {
			myFunction = function;
		}

		/**
		 * Default zero noise.  
		 */
		public NoiseImplFunction() {
			myFunction = new ConstantFunction(1, 0);
		}
		
		/**
		 * @return The function of time that explicitly defines the noise
		 */
		public Function getFunction() {
			return myFunction;
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
				NoiseImplFunction result = (NoiseImplFunction) super.clone();
				result.myFunction = myFunction.clone();
				return result;
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			} 
		}

		/**
		 * @see ca.neo.model.Resettable#reset(boolean)
		 */
		public void reset(boolean randomize) {
		}
		
	}
	
	public static class NoiseImplNull implements Noise {
		
		private static final long serialVersionUID = 1L;

		/**
		 * @see ca.neo.model.Noise#getValue(float, float, float)
		 */
		public float getValue(float startTime, float endTime, float input) {
			return input;
		}

		@Override
		public Noise clone() {
			return this; //allows sharing between dimensions
		}

		/**
		 * @see ca.neo.model.Resettable#reset(boolean)
		 */
		public void reset(boolean randomize) {
		}
		
	}

	/**
	 * Note: setters are private, because Origins typically make copies for each output dimension, 
	 * which would then not be updated with changes to the original. So to change noise properties 
	 * the Noise object must be replaced. 
	 * 
	 * @author Bryan Tripp
	 */
	public static class NoiseImplPDF implements Noise {
		
		private static final long serialVersionUID = 1L;
		
		private float myPeriod;
		private PDF myPDF;
		private DynamicalSystem myDynamics;
		private Integrator myIntegrator;
		private float myLastGenTime = 0;
		private float myLastDynamicsTime = 0;
		private float[] myLastRawNoise;
		private float[] myCurrentRawNoise;
		private Units[] myUnits;
		private float[] myInitialState;
		
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
		
		/**
		 * @return Frequency (in simulation time) with which new noise values are drawn from the PDF
		 */
		public float getFrequency() {
			return 1f /myPeriod;
		}
		
		/**
		 * @param frequency Frequency (in simulation time) with which new noise values are drawn from the PDF
		 */
		private void setFrequency(float frequency) {
			myPeriod = 1f / frequency;
		}
		
		/**
		 * @return PDF from which new noise values are drawn. The dimension of the space over which the PDF is defined  
		 * 		must equal the input dimension of the dynamics.
		 */
		public PDF getPDF() {
			return myPDF;
		}
		
		/**
		 * @param pdf PDF from which new noise values are drawn. The dimension of the space over which the PDF is defined  
		 * 		must equal the input dimension of the dynamics.
		 */
		private void setPDF(PDF pdf) {
			if (myDynamics == null && pdf.getDimension() != 1) {
				throw new IllegalArgumentException("With null dynamics, the PDF must be defined over one dimension.");	
			}
			
			myPDF = pdf;
			myCurrentRawNoise = myPDF.sample();
			myUnits = Units.uniform(Units.UNK, myCurrentRawNoise.length);
		}
		
		/**
		 * @return Dynamics through which raw noise values pass before they are combined with non-noise.
		 * 		The input dimension must match the PDF and the output dimension must equal one. Can be null in which 
		 * 		case the PDF must be one-dimensional.
		 */
		public DynamicalSystem getDynamics() {
			return myDynamics;
		}
		
		/**
		 * @param dynamics Dynamics through which raw noise values pass before they are combined with non-noise.
		 * 		The input dimension must match the PDF and the output dimension must equal one. Can be null in which 
		 * 		case the PDF must be one-dimensional.
		 */
		private void setDynamics(DynamicalSystem dynamics) {
			if (dynamics != null && dynamics.getOutputDimension() != 1) {
				throw new IllegalArgumentException("The output of the dynamics must be one-dimensional");
			}
			
			myDynamics = dynamics;
			if (myDynamics != null) myInitialState = dynamics.getState();
		}
		
		/**
		 * @return Integrator used to solve dynamics. Can be null if dynamics is null.
		 */
		public Integrator getIntegrator() {
			return myIntegrator;
		}
		
		/**
		 * @see ca.neo.model.Noise#getValue(float, float, float)
		 */
		public float getValue(float startTime, float endTime, float input) {
			float result = input;
			
			myLastRawNoise = myCurrentRawNoise;
			if (endTime >= myLastGenTime + myPeriod || endTime < myLastGenTime) {
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
			//must return an independent copy of this Noise since there may be a DynamicalSystem with state
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

		/**
		 * @see ca.neo.model.Resettable#reset(boolean)
		 */
		public void reset(boolean randomize) {
			if (myDynamics != null) myDynamics.setState(myInitialState);
			myLastDynamicsTime = 0;
			myLastGenTime = 0;
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
