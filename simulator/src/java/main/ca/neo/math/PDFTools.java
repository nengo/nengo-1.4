/*
 * Created on May 16, 2006
 */
package ca.neo.math;

import java.util.Random;

/**
 * Convenience methods for using PDFs. 
 * 
 * @author Bryan Tripp
 */
public class PDFTools {
	
	private static final Random ourRandom = new Random();

	/**
	 * Note: PDF treated as univariate (only first dimension considered). 
	 * 
	 * @param pdf The PDF from which to sample
	 * @return Sample from PDF rounded to nearest integer 
	 */
	public static int sampleInt(PDF pdf) {
		return Math.round(pdf.sample()[0]);
	}

	/**
	 * Note: PDF treated as univariate (only first dimension considered).
	 *  
	 * @param pdf The PDF from which to sample
	 * @return True iff sample from PDF is > 1
	 */
	public static boolean sampleBoolean(PDF pdf) {
		return pdf.sample()[0] > 1;
	}
	
	/**
	 * Note: PDF treated as univariate (only first dimension considered).
	 *  
	 * @param pdf The PDF from which to sample
	 * @return Sample from PDF (this is a convenience method for getting 1st 
	 * 		dimension of sample() result)
	 */
	public static float sampleFloat(PDF pdf) {
		return pdf.sample()[0];
	}
	
	/**
	 * Use this rather than Math.random(), to allow user to reproduce random results
	 * by setting the seed. 
	 * 
	 * @return A random sample between 0 and 1
	 */
	public static double random() {
		return ourRandom.nextDouble();
	}
	
	/**
	 * @param seed New random seed for random()
	 */
	public static void setSeed(long seed) {
		ourRandom.setSeed(seed);
	}
	
}
