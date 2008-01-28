/*
 * Created on May 16, 2006
 */
package ca.neo.math.impl;

import ca.neo.math.PDF;

/**
 * Univariate Gaussian probability density function. 
 * 
 * @author Bryan Tripp
 */
public class GaussianPDF implements PDF {

	private static final long serialVersionUID = 1L;
	
	private float myMean;
	private float myVariance;
	private float mySD;
	private float nextNormal;
	private boolean nextAvailable;
	private float myPeak;
	private boolean myScalePeakWithVariance;
	
	/**
	 * @param mean Mean of the distribution 
	 * @param variance Variance of the distribution
	 */
	public GaussianPDF(float mean, float variance) {
		this(mean, variance, 1f / (float) Math.pow(variance * 2f * Math.PI, .5));
		myScalePeakWithVariance = true;
	}

	/**
	 * Constructs a scaled Gaussian with the given peak value. 
	 *  
	 * @param mean Mean of the distribution 
	 * @param variance Variance of the distribution
	 * @param peak Maximum value of scaled Gaussian 
	 */
	public GaussianPDF(float mean, float variance, float peak) {
		myMean = mean;
		myVariance = variance;
		mySD = (float) Math.sqrt(variance);
		myPeak = peak;
		nextNormal = 0;
		nextAvailable = false;		
		myScalePeakWithVariance = false;
	}

	/**
	 * Instantiates with default mean=0 and variance=1
	 */
	public GaussianPDF() {
		this(0, 1);
	}

	/**
	 * @param mean Mean of the distribution 
	 */
	public void setMean(float mean) {
		myMean = mean;
		nextAvailable = false;
	}
	
	/**
	 * @return Mean of the distribution 
	 */
	public float getMean() {
		return myMean;
	}
	
	/**
	 * @param variance Variance of the distribution
	 */
	public void setVariance(float variance) {
		myVariance = variance;
		nextAvailable = false;
		if (myScalePeakWithVariance) {
			myPeak = 1f / (float) Math.pow(variance * 2f * Math.PI, .5);
		}
	}
	
	/**
	 * @return Variance of the distribution
	 */
	public float getVariance() {
		return myVariance;
	}
	
	/**
	 * @param peak Maximum value of scaled Gaussian 
	 */
	public void setPeak(float peak) {
		myPeak = peak;
		nextAvailable = false;
		myScalePeakWithVariance = false;
	}
	
	/**
	 * @return Maximum value of scaled Gaussian 
	 */
	public float getPeak() {
		return myPeak;
	}
	
	/**
	 * @param scale If true, the peak of the distribution scales automatically so that the integral is 1
	 */
	public void setScalePeakWithVariance(boolean scale) {
		myScalePeakWithVariance = scale;
	}
	
	/**
	 * @return If true, the peak of the distribution scales automatically so that the integral is 1
	 */
	public boolean getScalePeakWithVariance() {
		return myScalePeakWithVariance;
	}

	/**
	 * @see ca.neo.math.PDF#sample()
	 */
	public float[] sample() {
		float normal = 0;
		
		// 2 are generated at a time
		if (nextAvailable) {
			normal = nextNormal;
			nextAvailable = false;
		} else {
			float[] newSamples = doSample();
			normal = newSamples[0];
			nextNormal = newSamples[1];
			nextAvailable = true;
		}
		
		return new float[] {normal * mySD + myMean};
	}

	/**
	 * This method is publically exposed because normal deviates are often needed, 
	 * and static access allows the compiler to inline the call, which brings a 
	 * small performance advantage.  
	 *    
	 * @return Two random samples from a normal distribution (mean 0; variance 1) 
	 */
	public static float[] doSample() {
		//see http://www.taygeta.com/random/gaussian.html
		
		float x1, x2, w, y1, y2;
		 
		do {
			x1 = 2f * (float) Math.random() - 1f;
        	x2 = 2f * (float) Math.random() - 1f;
        	w = x1 * x1 + x2 * x2;
        } while ( w >= 1f );

        w = (float) Math.sqrt( (-2.0 * Math.log( w ) ) / w );
        y1 = x1 * w;
        y2 = x2 * w;

        return new float[] {y1, y2};
	}

	/**
	 * @return 1
	 * @see ca.neo.math.Function#getDimension()
	 */
	public int getDimension() {
		return 1;
	}

	/**
	 * @see ca.neo.math.Function#map(float[])
	 */
	public float map(float[] from) {
		return doMap(from, myMean, myVariance, myPeak);
	}

	/**
	 * @see ca.neo.math.Function#multiMap(float[][])
	 */
	public float[] multiMap(float[][] from) {
		float[] result = new float[from.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = doMap(from[i], myMean, myVariance, myPeak);
		}
		return result;
	}
	
	private static float doMap(float[] from, float mean, float variance, float scale) {
		if (from.length != 1) {
			throw new IllegalArgumentException("Argument must have dimension 1");
		}
		
		float d = from[0] - mean;
		
		double result = scale * Math.pow(Math.E, -(d*d) / (2*variance));
		
		return (float) result;
	}

}
