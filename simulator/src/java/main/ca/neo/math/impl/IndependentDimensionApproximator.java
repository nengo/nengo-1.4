/*
 * Created on 28-Jul-2006
 */
package ca.neo.math.impl;

import ca.neo.math.Function;
import ca.neo.math.LinearApproximator;
import ca.neo.util.MU;

/**
 * A LinearApproximator for functions with no multidimensional nonlinearities. Each of the source functions 
 * is assumed to be a function of one dimension. Consequently, only functions of one dimension can be decoded
 * directly. Linear functions of multiple dimensions can obtained later by combining weights of one-dimensional 
 * functions.  
 * 
 * @author Bryan Tripp
 */
public class IndependentDimensionApproximator implements LinearApproximator {

	private static final long serialVersionUID = 1L;
	
	private LinearApproximator[] myApproximators; //for each dimension
	private int[][] myIndices; //values indices for each dimension  
	private int[] myDimensions; //dimension for each neuron
	
	/**
	 * @param evaluationPoints Points of evaluation of source functions, in the dimension along which they vary
	 * @param values Values of each source function at each point
	 * @param dimensions The dimension along which each function varies 
	 * @param dimension Dimension of the space from which source functions map 
	 * @param costFunction As in WeightedCostApproximator, but in dimension along which functions vary  
	 * @param noise Proportion of noise to add 
	 */
	public IndependentDimensionApproximator(float[] evaluationPoints, float[][] values, int[] dimensions, int dimension, Function costFunction, float noise) {
		assert MU.isMatrix(values);
		assert evaluationPoints.length == values[0].length;
		assert values.length == dimensions.length;
		
		myIndices = new int[dimension][];
		int[] dimCount = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			myIndices[i] = new int[values.length];
		}
		
		for (int i = 0; i < dimensions.length; i++) {
			int dim = dimensions[i];
			myIndices[dim][dimCount[dim]++] = i;		
		}
		
		float[][] ep = new float[evaluationPoints.length][];
		for (int i = 0; i < ep.length; i++) {
			ep[i] = new float[]{evaluationPoints[i]};
		}
		
		myApproximators = new LinearApproximator[dimension];
		for (int i = 0; i < dimension; i++) {
			float[][] dimValues = new float[dimCount[i]][];
			for (int j = 0; j < dimCount[i]; j++) {
				dimValues[j] = values[myIndices[i][j]];
			}
			myApproximators[i] = new WeightedCostApproximator(ep, dimValues, costFunction, noise);			
		}
		
		myDimensions = dimensions;
	}

	/**
	 * @see ca.neo.math.LinearApproximator#findCoefficients(ca.neo.math.Function)
	 */
	public float[] findCoefficients(Function target) {
		if ( !(target instanceof IdentityFunction) ) {
			throw new IllegalArgumentException("Only IdentityFunction supported");
			//TODO: could generalize to any function that maps from a single dimension by wrapping below instead of new IdentityFunction  
		}
		
		int dim = ((IdentityFunction) target).getIdentityDimension();
		
		float[] weights = myApproximators[dim].findCoefficients(new IdentityFunction(1, 0));
		float[] result = new float[myDimensions.length];
		
		for (int i = 0; i < weights.length; i++) {
			result[myIndices[dim][i]] = weights[i];
		}
		
		return result;
	}
	

}
