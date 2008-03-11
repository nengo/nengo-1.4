/**
 * 
 */
package ca.neo.math.impl;

import ca.neo.math.PDF;
import ca.neo.math.PDFTools;

/**
 * A Poisson distribution. 
 * 
 * @author Bryan Tripp
 */
public class PoissonPDF extends AbstractFunction implements PDF {

	private static final long serialVersionUID = 1L;

	private float myRate;

	/**
	 * @param rate The mean & variance of the distribution 
	 */
	public PoissonPDF(float rate) {
		super(1);
		myRate = rate;
	}

	/**
	 * @see ca.neo.math.impl.AbstractFunction#map(float[])
	 */
	@Override
	public float map(float[] from) {
		assert from.length == 0;
		
		float result = 0;
		int observation = (int) Math.floor(from[0]);
		
		result = (float) doMap((double) myRate, observation);
		
		return result;
	}

	/**
	 * @see ca.neo.math.PDF#sample()
	 */
	public float[] sample() {
		double L = Math.exp(-myRate);
		float k = 0;
		double p = 1;

	    do {
	    	k = k + 1;
	    	double u = PDFTools.random();
	    	p = p * u;
	    } while (p >= L);
		
		return new float[]{k-1};
	}

	//this doesn't work ... overflows with large rate or observation; huge roundoff error with small rate & large observation 
//	private static double doMap(double rate, int observation) {
//		return Math.pow(rate, observation) * Math.exp(-rate) / factorial(observation);
//	}
	
//	private static long factorial(long n) {
//		assert n >= 0;
//		return (n == 0) ? 1 : n * factorial(n-1);
//	}

	private static double doMap(double rate, int observation) {
		double result = Math.exp(-rate);
		
		for (int i = 0; i < observation; i++) {
			result = result * rate / (i+1);
		}
		
		return result;
	}
		
	@Override
	public PDF clone() throws CloneNotSupportedException {
		return (PDF) super.clone();
	}
}
