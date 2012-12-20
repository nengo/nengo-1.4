package ca.nengo.math.impl;

import ca.nengo.math.Function;
import ca.nengo.math.LinearApproximator;
import ca.nengo.model.Units;
import ca.nengo.util.MU;
import ca.nengo.util.impl.TimeSeries1DImpl;
import Jama.Matrix;
import static org.junit.Assert.*;
import org.junit.Test;

public class WeightedCostApproximatorTest {
	@Test
	public void testPseudoInverse() {
		WeightedCostApproximator a = new WeightedCostApproximator(new float[][]{new float[]{0f},new float[]{1f},new float[]{2f}}, 
				new float[][]{new float[]{3f,2f,3f},new float[]{1f,2f,3f}}, 
				new ConstantFunction(1,1f), 0.02f, -1);
		double[][] ps = a.pseudoInverse(new double[][]{new double[]{1,2},new double[]{3,4}}, 0f, -1);
		Matrix psM = new Matrix(ps);
		Matrix aM = new Matrix(new double[][]{new double[]{1,2},new double[]{3,4}});
		Matrix apsaM = aM.times(psM.times(aM));
		
		assertEquals(apsaM.get(0,0), aM.get(0,0), 0.0001f);
		assertEquals(apsaM.get(0,1), aM.get(0,1), 0.0001f);
		assertEquals(apsaM.get(1,0), aM.get(1,0), 0.0001f);
		assertEquals(apsaM.get(1,1), aM.get(1,1), 0.0001f);
	}
	
	@Test
	public void testFindCoefficients() {
		float[] frequencies = new float[]{1, 5, 8};
		float[] amplitudes = new float[]{.1f, .2f, .3f};
		float[] phases = new float[]{0, -1, 1};
		
		float[][] evalPoints = new float[100][];
		for (int i = 0; i < evalPoints.length; i++) {
			evalPoints[i] = new float[]{(float) i / (float) evalPoints.length};
		}
		
		//testing with eval points
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
			assertEquals(approx, target.map(evalPoints[j]), 0.0001f);
		}
		
		//testing with eval signals
		TimeSeries1DImpl targetsig = (TimeSeries1DImpl)TimeSeriesFunction.makeSeries(new FourierFunction(frequencies, amplitudes, phases), 0.0f, 0.001f, 1.0f, Units.UNK);
		float[] times = targetsig.getTimes();
		float[][][] valuesig = new float[frequencies.length][1][];
		for (int i = 0; i < frequencies.length; i++) {
			Function component = new FourierFunction(new float[]{frequencies[i]}, new float[]{1}, new float[]{phases[i]});
			valuesig[i][0] = new float[times.length];
			for (int j = 0; j < times.length; j++) {
				valuesig[i][0][j] = component.map(new float[]{times[j]});
			}
		}
		float[][][] evalsigs = new float[1][1][];
		evalsigs[0][0] = times;
		factory = new WeightedCostApproximator.Factory(0f);
		WeightedCostApproximator approximator2 = (WeightedCostApproximator)factory.getApproximator(evalsigs, valuesig);
		coefficients = approximator2.findCoefficients(MU.transpose(targetsig.getValues())[0]);
		
		float[] approxsig = new float[times.length]; 
		for (int j = 0; j < times.length; j++) {
			approxsig[j] = 0f;
			for (int i = 0; i < frequencies.length; i++) {
				approxsig[j] += coefficients[i] * valuesig[i][0][j];
			}
		}
		
		assertEquals(0.0f, MU.sum(MU.difference(approxsig,targetsig.getValues1D())), 0.0001f);
	}
}
