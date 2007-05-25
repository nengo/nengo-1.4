/*
 * Created on 24-May-07
 */
package ca.neo.model.impl;

import ca.neo.dynamics.DynamicalSystem;
import ca.neo.dynamics.Integrator;
import ca.neo.dynamics.impl.EulerIntegrator;
import ca.neo.dynamics.impl.SimpleLTISystem;
import ca.neo.math.PDF;
import ca.neo.math.impl.GaussianPDF;
import ca.neo.model.Noise;
import ca.neo.model.Units;
import ca.neo.plot.Plotter;
import ca.neo.util.MU;
import ca.neo.util.TimeSeries;
import ca.neo.util.impl.TimeSeriesImpl;

/**
 * Default implementation of additive Noise.
 * 
 * TODO: unit tests
 * 
 * @author Bryan Tripp
 */
public class NoiseImpl implements Noise {

	private float myPeriod;
	private PDF myPDF;
	private DynamicalSystem myDynamics;
	private Integrator myIntegrator;
	private float myTimeSinceStart;
	private float myLastGenTime = 0;
	private float myLastDynamicsTime = 0;
	private float[] myLastRawNoise;
	private float[] myCurrentRawNoise;
	private Units[] myUnits;
	
	/**
	 * @param frequency Frequency (in simulation time) with which new noise values are drawn from the PDF
	 * @param pdf PDF from which new noise values are drawn (null means no noise). The dimension must equal 
	 * 		the input dimension of the dynamics. 
	 * @param dynamics Dynamics through which raw noise values pass before they are combined with non-noise
	 * 		(null means no dynamics). The output dimension must equal the dimension of expected input to getValues(). 
	 * @param integrator Integrator used to solve dynamics 
	 */
	public NoiseImpl(float frequency, PDF pdf, DynamicalSystem dynamics, Integrator integrator) {
		if (pdf != null && dynamics != null && pdf.getDimension() != dynamics.getInputDimension()) {
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
	 * @see ca.neo.model.Noise#getValues(float, float[])
	 */
	public float[] getValues(float elapsedTime, float[] input) {
		float[] result = null;
		
		myTimeSinceStart += elapsedTime;
		if (myPDF == null) {
			result = input;
		} else {
			myLastRawNoise = myCurrentRawNoise;
			if (myTimeSinceStart >= myLastGenTime + myPeriod) {
				myCurrentRawNoise = myPDF.sample();
				myLastGenTime = myTimeSinceStart;
			}

			if (myDynamics == null) {
				if (myCurrentRawNoise.length != input.length) {
					throw new IllegalArgumentException("Expected input of dimension " + myCurrentRawNoise.length);
				}
				result = MU.sum(input, myCurrentRawNoise);
			} else {
				TimeSeries raw = new TimeSeriesImpl(new float[]{myLastDynamicsTime, myTimeSinceStart}, 
						new float[][]{myLastRawNoise, myCurrentRawNoise}, myUnits);
				float[][] output = myIntegrator.integrate(myDynamics, raw).getValues();
				result = MU.sum(input, output[output.length-1]);
				myLastDynamicsTime = myTimeSinceStart;
			}
		}
		return result;
	}
	
	/**
	 * @see ca.neo.model.Noise#getDimension()
	 */
	public int getDimension() {
		return myDynamics == null ? myPDF.getDimension() : myDynamics.getOutputDimension();
	}
	
	//functional test ... 
	public static void main(String[] args) {
		float tau = .01f;
		DynamicalSystem dynamics = new SimpleLTISystem(new float[]{-1f/tau}, new float[][]{new float[]{1f/tau}}, MU.I(1), new float[1], new Units[]{Units.UNK});
		Integrator integrator = new EulerIntegrator(.0001f);
		Noise noise = new NoiseImpl(1000, new GaussianPDF(0, 1), dynamics, integrator);
		
		float elapsedTime = .001f;
		int steps = 1000;
		float[][] output = new float[steps][];
		for (int i = 0; i < steps; i++) {
			output[i] = noise.getValues(elapsedTime, new float[1]);
		}
		
		Plotter.plot(MU.prod(output, new float[]{1}), "noise");
	}

}
