/*
 * Created on May 16, 2006
 */
package ca.neo.math;

/**
 * Convenience methods for using PDFs. 
 * 
 * @author Bryan Tripp
 */
public class PDFTools {

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
	
}
