/*
 * Created June 3, 2007
 */
package ca.neo.math.impl;

import ca.neo.math.Function;
import ca.neo.math.LinearApproximator;
import ca.neo.TestUtil;
//import ca.neo.model.Units;
//import ca.neo.plot.Plotter;
//import ca.neo.util.MU;
//import ca.neo.util.impl.TimeSeries1DImpl;
import junit.framework.TestCase;

/**
 * Unit tests for IndependentDimensionApproximator. 
 */
public class IndependentDimensionApproximatorTest extends TestCase {
	
	/* 
	 * Test method for 'ca.neo.math.impl.IndependentDimensionApproximator.findCoefficients()'
	 */
	public void testFindCoefficients() {
		float[][] polyCoeffs = new float[][]{{5,5},{1,-2},{-2,3}};
		
		float[] evalPoints = new float[100];
		for (int i = 0; i < evalPoints.length; i++) {
			evalPoints[i] = (float) -10 + 20 * (float)i / (float)evalPoints.length;
		}
		
		Function target = new IdentityFunction(1,0);
		
		float[][] values = new float[polyCoeffs.length][];
		for (int i = 0; i < values.length; i++) {
			Function component = new Polynomial(polyCoeffs[i]);
			values[i] = new float[evalPoints.length];
			for (int j = 0; j < evalPoints.length; j++) {
				values[i][j] = component.map(new float[]{evalPoints[j]});
			}
		}
		
		LinearApproximator approximator = new IndependentDimensionApproximator(evalPoints, values, new int[]{0,1,0}, 2, new ConstantFunction(1,1f), 0f);
		float[] coefficients = approximator.findCoefficients(target);
		
		float approx;
		for (int j = 0; j < evalPoints.length; j++) {
			approx = 0f;
			for (int i = 0; i < polyCoeffs.length; i++) {
				approx += coefficients[i] * values[i][j];
			}
			TestUtil.assertClose(approx, target.map(new float[]{evalPoints[j]}), 0.0001f);
		}
	}

}
