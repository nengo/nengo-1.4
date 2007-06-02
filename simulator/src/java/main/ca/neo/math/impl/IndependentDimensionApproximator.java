/*
 * Created on 28-Jul-2006
 */
package ca.neo.math.impl;

import ca.neo.math.ApproximatorFactory;
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

	/**
	 * Factory for IndependentDimensionApproximators. 
	 *  
	 * @author Bryan Tripp
	 */
	public static class Factory implements ApproximatorFactory {
		
		private static final long serialVersionUID = 1L;
		
////		private int myDimensions; 
//		private int myNeuronsPerDim;
//		
//		public Factory(int dimensions, int neurons) {
//			myDimensions = dimensions;
//			myNeuronsPerDim = neurons / dimensions;
//			if (neurons % dimensions != 0) {
//				throw new IllegalArgumentException("Expected # neurons to be evenly divisible by # dimensions");
//			}
//		}

		/**
		 * @see ca.neo.math.ApproximatorFactory#getApproximator(float[][], float[][])
		 */
		public LinearApproximator getApproximator(float[][] evalPoints, float[][] values) {
			int dimensions = evalPoints[0].length;
			int nodes = values[0].length;
			int nodesPerDim = nodes / dimensions;
			if (nodes % dimensions != 0) {
				throw new IllegalArgumentException("Expected # nodes (" + nodes + ") to be evenly divisible by # dimensions (" + dimensions + ")");
			}

			//TODO: this only work with axis-clustered ... is this enforced anywhere? 
			//duplicate point along axis to all dimensions (shouldn't matter because 
			//preferred directions are clustered on axes) 
			float[][] allDimEvalPoints = new float[evalPoints.length][];
			float[] oneDimEvalPoints = new float[evalPoints.length];
			for (int i = 0; i < evalPoints.length; i++) {
				allDimEvalPoints[i] = new float[dimensions];
				for (int j = 0; j < dimensions; j++) {
					allDimEvalPoints[i][j] = evalPoints[i][0];
				}
				oneDimEvalPoints[i] = evalPoints[i][0];
			}
			
			int[] indepDims = new int[values.length];
			for (int i = 0; i < indepDims.length; i++) {
				indepDims[i] = (int) Math.floor((double) i / (double) nodesPerDim);
			}

			Function costWeight = new ConstantFunction(1, 1);
			return new IndependentDimensionApproximator(oneDimEvalPoints, values, indepDims, dimensions, costWeight, .1f);
		}
		
	}
	

}
