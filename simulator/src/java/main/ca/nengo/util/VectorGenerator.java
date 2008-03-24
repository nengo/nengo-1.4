/*
 * Created on 4-Jun-2006
 */
package ca.nengo.util;

/**
 * A tool for generating sets of uniformly or randomly distributed vectors. 
 * 
 * @author Bryan Tripp
 */
public interface VectorGenerator {

	/**
	 * The vector distribution is decided by implementing classes. 
	 *  
	 * @param number Number of vectors to be returned
	 * @param dimension Dimension of the vectors to be returned
	 * @return A List of float[] vectors
	 */
	public float[][] genVectors(int number, int dimension);
		
}
