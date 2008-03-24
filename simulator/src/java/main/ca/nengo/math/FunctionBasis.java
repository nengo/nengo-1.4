/*
 * Created on May 16, 2006
 */
package ca.nengo.math;

/**
 * <p>A list of orthogonal functions.</p>
 * 
 * <p>Function bases are useful in function representation, because they 
 * make function representation equivalent to vector representation (see 
 * Eliasmith & Anderson, 2003). Essentially, functions in an orthogonal 
 * basis correspond to dimensions in a vector. Cosine tuning curves in a 
 * vector space are equivalent to inner-product tuning curves in the 
 * corresponding function space.</p>
 * 
 * <p>Examples of orthogonal sets of functions include Fourier and wavelet 
 * bases.</p> 
 * 
 * @author Bryan Tripp
 */
public interface FunctionBasis extends Function {

	/**
	 * @return Dimensionality of basis
	 */
	public int getBasisDimension();
	
	/**
	 * @param basisIndex Dimension index
	 * @return Basis function corresponding to given dimension 
	 */
	public Function getFunction(int basisIndex);
	
	/**
	 * @param coefficients Coefficient for summing basis functions 
	 */
	public void setCoefficients(float[] coefficients);
	
}
