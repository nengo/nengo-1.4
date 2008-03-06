/*
 * Created on 5-Feb-2007
 */
package ca.neo.math.impl;

import ca.neo.math.Function;
import ca.neo.math.LinearApproximator;

/**
 * <p>A LinearApproximator that approximates multi-dimensional functions as sums of 
 * lower-dimensional functions. Each lower-dimensional function is approximated by
 * a component approximator, which is provided in the constructor. The resulting 
 * approximation is the sum of approximations produced by each component.</p> 
 * 
 * <p>CompositeApproximator is similar to the simpler IndependentDimensionApproximator, 
 * but more general because dimensions can be handled either independently or in 
 * arbitrary groups.</p> 
 * 
 * <p>CompositeApproximator is useful for low-dimensionally non-linear functions
 * of high-dimensional vectors, eg x1*x2 + x3*x4 - x5*x6.</p>
 * 
 * <p>It is also useful for creating accurate, high-dimensional ensembles of neurons  
 * with a little overlap between dimensions.</p>
 * 
 * TODO: should LinearApproximator have getDimension()? would be possible to get rid of 2nd constructor arg then 
 * TODO: test
 * 
 * @author Bryan Tripp
 */
public class CompositeApproximator implements LinearApproximator {

	private static final long serialVersionUID = 1L;
	
	private LinearApproximator[] myComponents;
	private int[][] myDimensions;
	private int myDimension;
	
	public CompositeApproximator(LinearApproximator[] components, int[][] dimensions) {
		if (components.length != dimensions.length) {
			throw new IllegalArgumentException("Length of dimensions list must equal number of components (" 
					+ dimensions.length + " vs " + components.length + ")");
		}
		
		myComponents = components;
		myDimensions = dimensions;
		
		myDimension = 0;
		for (int i = 0; i < dimensions.length; i++) {
			myDimension += dimensions[i].length;
		}
	}

	/**
	 * @see ca.neo.math.LinearApproximator#findCoefficients(ca.neo.math.Function)
	 */
	public float[] findCoefficients(Function target) {
		float[] result = new float[0];
		
		for (int i = 0; i < myDimensions.length; i++) {
			Function f = new FunctionWrapper(target, myDimensions[i]);
			float[] compCoeff = myComponents[i].findCoefficients(f);
			
			float[] newResult = new float[result.length + compCoeff.length];
			System.arraycopy(result, 0, newResult, 0, result.length);
			System.arraycopy(compCoeff, 0, newResult, result.length, compCoeff.length);
			result = newResult;
		}
		
		return result;
	}
	
	private static class FunctionWrapper extends AbstractFunction {

		private static final long serialVersionUID = 1L;
		
		private Function myFunction;
		private int[] myDimensions;
		
		public FunctionWrapper(Function function, int[] dimensions) {
			super(dimensions.length);
			myFunction = function;
			myDimensions = dimensions;
		}
		
		public float map(float[] from) {
			assert from.length == myDimensions.length;
			float[] projection = new float[myFunction.getDimension()];
			
			for (int i = 0; i < from.length; i++) {
				projection[myDimensions[i]] = from[i];
			}
			
			return myFunction.map(projection);
		}

		@Override
		public Function clone() throws CloneNotSupportedException {
			return new FunctionWrapper(myFunction.clone(), myDimensions.clone());
		}

	}

}
