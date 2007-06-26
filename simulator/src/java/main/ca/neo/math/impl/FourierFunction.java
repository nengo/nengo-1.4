/*
 * Created on 6-Jun-2006
 */
package ca.neo.math.impl;

import java.util.Random;

import ca.neo.math.Function;

/**
 * A Function that is composed of a finite number of sinusoids.
 * 
 * @author Bryan Tripp
 */
public class FourierFunction implements Function {

	private static final long serialVersionUID = 1L;
	
	private float[][] myFrequencies;
	private float[] myAmplitudes;
	private float[][] myPhases;
	
	/**
	 * Creates a 1-dimensional function composed of explicitly defined sinusoids. 
	 * 
	 * @param frequencies Explicit list of frequencies of sinusoidal components of the 
	 * 		function (Hz)
	 * @param amplitudes The amplitude of each component 
	 * @param phases The phase lead of each component (from -.5 to .5)
	 */
	public FourierFunction(float[] frequencies, float[] amplitudes, float[] phases) {
		if (frequencies.length != amplitudes.length || frequencies.length != phases.length) {
			throw new IllegalArgumentException("Lists of frequencies, amplitudes, and phases must have same length");
		}
		
		myFrequencies = new float[][]{frequencies};
		myAmplitudes = amplitudes;
		myPhases = new float[][]{phases};
	}
	
	/**
	 * Creates an n-dimensional function composed of explicitly defined sinusoids. 
	 * 
	 * @param frequencies Lists of frequencies (length n; ith members define frequencies of ith component along each dimension)  
	 * @param amplitudes The amplitude of each component
	 * @param phases Lists of phases (length n; ith members define phases of ith component along each dimension)
	 */
	public FourierFunction(float[][] frequencies, float[] amplitudes, float[][] phases) {
		if (frequencies.length != phases.length) {
			throw new IllegalArgumentException("Lists of frequencies and phases must have same dimension");
		}
		
		if (frequencies[0].length != amplitudes.length || phases[0].length != amplitudes.length) {
			throw new IllegalArgumentException("Frequencies, amplitudes, and phases must have same length in each dimension");
		}
		
		myFrequencies = frequencies;
		myAmplitudes = amplitudes;
		myPhases = phases;
	}
	
	/**
	 * Creates a 1-dimensional band-limited pink noise function with specified parameters. 
	 *  
	 * @param fundamental The fundamental frequency (Hz)
	 * @param cutoff The high-frequency limit (Hz)
	 * @param rms The root-mean-squared function amplitude
	 */
	public FourierFunction(float fundamental, float cutoff, float rms, long seed) {
		int n = (int) Math.floor(cutoff / fundamental);
		
		myFrequencies = new float[][]{new float[n]};
		myAmplitudes = new float[n];
		myPhases = new float[][]{new float[n]};
		Random random = new Random(seed); 
		
		for (int i = 0; i < n; i++) {
			myFrequencies[0][i] = fundamental * (i+1);
			myAmplitudes[i] = (float) random.nextFloat() * fundamental / myFrequencies[0][i]; //decreasing amplitude = pink noise
//			myPhases[i] = (float) ( -Math.PI + (2d *  Math.PI * Math.random()) ) ;
			myPhases[0][i] = -.5f + 2f * (float) random.nextFloat();
		}
		
		//find amplitude over one period and rescale to specified amplitude 
		int samplePoints = 500;
		float dx = (1f / fundamental) / samplePoints;
		double sumSquared = 0;
		for (int i = 0; i < samplePoints; i++) {
			float val = getValue(new float[]{i*dx}, myFrequencies, myAmplitudes, myPhases);
			sumSquared += val * val;
		}
		double unscaledRMS = Math.sqrt(sumSquared / samplePoints);
		
		for (int i = 0; i < n; i++) {
			myAmplitudes[i] = myAmplitudes[i] * rms / (float) unscaledRMS;
		}
	}

	/**
	 * @return 1 
	 * @see ca.neo.math.Function#getDimension()
	 */
	public int getDimension() {
		return myFrequencies.length;
	}

	/**
	 * @see ca.neo.math.Function#map(float[])
	 */
	public float map(float[] from) {
		return getValue(from, myFrequencies, myAmplitudes, myPhases);
	}
	
	/**
	 * @see ca.neo.math.Function#multiMap(float[][])
	 */
	public float[] multiMap(float[][] from) {
		float[] result = new float[from.length];
		
		for (int i = 0; i < from.length; i++) {
			result[i] = getValue(from[i], myFrequencies, myAmplitudes, myPhases);
		}
		
		return result;
	}

	private static float getValue(float[] x, float[][] f, float[] a, float[][] p) {
		float result = 0f;
		
		for (int i = 0; i < f[0].length; i++) {
			float component = 1;
			for (int j = 0; j < x.length; j++) {
				component = component * (float) Math.sin(2d * Math.PI * (f[j][i] * x[j] + p[j][i]));
			}
			result += a[i] * component;
		}
		
		return result;
	}
	
}
