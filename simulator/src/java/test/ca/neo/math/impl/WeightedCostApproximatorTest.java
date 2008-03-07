/*
 * Created May 30, 2007
 */
package ca.neo.math.impl;

import ca.neo.math.Function;
import ca.neo.math.LinearApproximator;
import Jama.Matrix;
import ca.neo.TestUtil;
//import ca.neo.model.Units;
//import ca.neo.plot.Plotter;
//import ca.neo.util.MU;
//import ca.neo.util.impl.TimeSeries1DImpl;
import junit.framework.TestCase;

/**
 * Unit tests for WeightedCostApproximator. 
 */
public class WeightedCostApproximatorTest extends TestCase {

	/*
	 * Test method for 'ca.neo.math.impl.WeightedCostApproximator.pseudoInverse()'
	 */
	public void testPseudoInverse() {
		WeightedCostApproximator a = new WeightedCostApproximator(new float[][]{new float[]{0f},new float[]{1f},new float[]{2f}}, 
				new float[][]{new float[]{3f,2f,3f},new float[]{1f,2f,3f}}, 
				new ConstantFunction(1,1f), 0.02f, -1);
		double[][] ps = a.pseudoInverse(new double[][]{new double[]{1,2},new double[]{3,4}}, 0f, -1);
		Matrix psM = new Matrix(ps);
		Matrix aM = new Matrix(new double[][]{new double[]{1,2},new double[]{3,4}});
		Matrix apsaM = aM.times(psM.times(aM));
		
		TestUtil.assertClose((float)apsaM.get(0,0), (float)aM.get(0,0), 0.0001f );
		TestUtil.assertClose((float)apsaM.get(0,1), (float)aM.get(0,1), 0.0001f );
		TestUtil.assertClose((float)apsaM.get(1,0), (float)aM.get(1,0), 0.0001f );
		TestUtil.assertClose((float)apsaM.get(1,1), (float)aM.get(1,1), 0.0001f );
	}
	
	/* 
	 * Test method for 'ca.neo.math.impl.WeightedCostApproximator.findCoefficients()'
	 */
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
		
		WeightedCostApproximator.Factory factory = new WeightedCostApproximator.Factory(0f);
		LinearApproximator approximator = factory.getApproximator(evalPoints, values);
		float[] coefficients = approximator.findCoefficients(target);
		
		float approx;
		for (int j = 0; j < evalPoints.length; j++) {
			approx = 0f;
			for (int i = 0; i < frequencies.length; i++) {
				approx += coefficients[i] * values[i][j];
			}
			TestUtil.assertClose(approx, target.map(evalPoints[j]), 0.0001f);
		}
	}

}
