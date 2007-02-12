/*
 * Created on 4-Jun-2006
 */
package ca.neo.util.impl;

import ca.neo.math.impl.GaussianPDF;
import ca.neo.util.VectorGenerator;

/**
 * Generates random vectors distributed on or in a hypersphere. 
 * 
 * TODO: Reference Deak, Muller
 * 
 * @author Bryan Tripp
 */
public class RandomHypersphereVG implements VectorGenerator {

	private boolean mySurface;
	private float myRadius;
	private boolean myAllOnAxes; //true if vectors are all to lie on an axis
	private float myAxisRatio; //ratio of vector density between cluster-centre axis to other axes
	
	/**
	 * @param surface If true, vectors are generated on surface of hypersphere; if false, throughout
	 * 		volume of hypersphere
	 * @param radius Radius of hypersphere
	 * @param axisClusterFactor Value between 0 and 1, with higher values indicating greater clustering
	 * 		of vectors around axes. 0 means even distribution; 1 means all vectors on axes.  
	 */
	public RandomHypersphereVG(boolean surface, float radius, float axisClusterFactor) {
		if (radius <= 0) {
			throw new IllegalArgumentException(radius + " is not a valid radius (must be > 0)");
		}
		if (axisClusterFactor < 0 || axisClusterFactor > 1) {
			throw new IllegalArgumentException(axisClusterFactor + " is not a valid cluster factor (must be between 0 and 1)");
		}
		
		mySurface = surface;
		myRadius = radius;
		
		if (axisClusterFactor > .999) {
			myAllOnAxes = true;
		} else {
			myAllOnAxes = false;
			myAxisRatio = (float) Math.tan( (.5 + axisClusterFactor/2) * (Math.PI / 2) );
		}
	}

	/**
	 * @see ca.neo.util.VectorGenerator#genVectors(int, int)
	 */
	public float[][] genVectors(int number, int dimension) {
		float[][] result = new float[number][]; //we'll generate from a unit sphere then scale to radius
		
		for (int i = 0; i < number; i++) {
			float[] vector = null;
			
			if (dimension == 1) {
				vector = new float[]{ genScalar(myRadius, mySurface) };
			} else if (myAllOnAxes) {
				vector = genOnAxes(dimension, myRadius, mySurface);
			} else {
				vector = genOffAxes(dimension, myRadius, mySurface);
			}
			
			result[i] = vector;
		}
		
		return result;
	}
	
	private static float genScalar(float radius, boolean surface) {
		if (surface) {
			return Math.random() > .5 ? radius : -radius;
		} else {
			return 2f * (float) (Math.random() - .5) * radius;
		}		
	}
	
	private static float[] genOnAxes(int dimension, float radius, boolean surface) {
		float[] result = new float[dimension];			
		int axis = (int) Math.floor(Math.random() * dimension); 

		for (int i = 0; i < dimension; i++) {
			result[i] = (i == axis) ? genScalar(radius, surface) : 0f;
		}
		
		return result;
	}
	
	private float[] genOffAxes(int dimension, float radius, boolean surface) {
		float[] result = new float[dimension];			
		int axis = (int) Math.floor(Math.random() * dimension); 
		
		float scale = mySurface ? 1f : (float) Math.pow(Math.random(), 1d / ((double) dimension));

		for (int i = 0; i < dimension; i = i + 2) { //note the increment by 2 
			float[] samples = GaussianPDF.doSample();
			result[i] = samples[0] * myRadius;
			if (i < dimension - 1) result[i+1] = samples[1] * myRadius;
		}				
		result[axis] = result[axis] * myAxisRatio;
						
		float normSquared = 0f;
		for (int i = 0; i < dimension; i++) {
			normSquared += result[i] * result[i];
		}
		
		float norm = (float) Math.pow(normSquared, .5); 
		for (int i = 0; i < dimension; i++) {
			result[i] = result[i] * scale / norm;
		}

		return result;
	}
	
//	private float[][] genRegularScalars(int number, float radius) {
//	float[][] result = new float[number][];
//	
//	int n1 = Math.round((float) number / 2f);
//	
//	for (int i = 0; i < number; i++) {
//		result[i] = new float[]{ i < n1 ? radius : -radius };
//	}
//	
//	return result;
//}

}
