package ca.neo.math.impl;

import ca.neo.math.Function;
import ca.neo.model.Units;
import ca.neo.plot.Plotter;
import ca.neo.util.MU;
import ca.neo.util.impl.TimeSeries1DImpl;
import junit.framework.TestCase;

/**
 * Unit tests for GradientDescentApproximator. 
 * 
 * TODO: this is written as a functional test ... translate to unit test
 * 
 * @author Bryan Tripp
 */
public class GradientDescentApproximatorTest extends TestCase {

	public GradientDescentApproximatorTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testFindCoefficients() {
		float[] frequencies = new float[]{1, 5, 8};
		float[] amplitudes = new float[]{.1f, .2f, .3f};
		float[] phases = new float[]{0, -1, 1};
		
		float[][] evalPoints = new float[100][];
		for (int i = 0; i < evalPoints.length; i++) {
			evalPoints[i] = new float[]{(float) i / (float) evalPoints.length};
		}
		
		Function target = new FourierFunction(frequencies, amplitudes, phases);
		float[][] values = new float[frequencies.length][];
		for (int i = 0; i < frequencies.length; i++) {
			Function component = new FourierFunction(new float[]{frequencies[i]}, new float[]{1}, new float[]{phases[i]});
			values[i] = new float[evalPoints.length];
			for (int j = 0; j < evalPoints.length; j++) {
				values[i][j] = component.map(evalPoints[j]);
			}
		}
		
		GradientDescentApproximator.Constraints constraints = new GradientDescentApproximator.Constraints() {
			public boolean correct(float[] coefficients) {
				boolean allCorrected = true;
				for (int i = 0; i < coefficients.length; i++) {
					if (coefficients[i] < 0) {
						coefficients[i] = 0;
					} else {
						allCorrected = false;
					}
				}
				return allCorrected;
			}
		};
		
		GradientDescentApproximator approximator = new GradientDescentApproximator(evalPoints, values, constraints, true);
		float[] coefficients = approximator.findCoefficients(target);
		
//		float[] estimate = MU.prod(MU.transpose(values), coefficients);
//		Plotter.plot(target, 0, .01f, .99f, "Ideal");
//		Plotter.plot(new TimeSeries1DImpl(MU.prod(evalPoints, new float[]{1}), estimate, Units.UNK), "Estimate");
//		
//		try {
//			Thread.sleep(1000*15);
//		} catch (InterruptedException e) {}
	}

}
