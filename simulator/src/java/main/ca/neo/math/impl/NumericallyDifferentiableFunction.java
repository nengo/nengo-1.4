/*
 * Created on 1-Dec-2006
 */
package ca.neo.math.impl;

import ca.neo.math.DifferentiableFunction;
import ca.neo.math.Function;

/**
 * A wrapper around any Function that provides a numerical approximation of its derivative, 
 * so that it can be used as a DifferentiableFunction. A Function should provide its 
 * exact derivative if available, rather than forcing callers to rely on this wrapper.  
 * 
 * TODO: test 
 * 
 * @author Bryan Tripp
 */
public class NumericallyDifferentiableFunction implements DifferentiableFunction {

	private static final long serialVersionUID = 1L;
	
	private Function myFunction;
	private Function myDerivative;
	
	/**
	 * @param function An underlying Function
	 * @param derivativeDimension The dimension along which the derivative is to be calculated 
	 * 		(note that the gradient of a multi-dimensional Function consists of multiple DifferentiableFunctions) 
	 * @param delta Derivative approximation of f(x) is [f(x+delta)-f(x-delta)]/[2*delta]
	 */
	public NumericallyDifferentiableFunction(Function function, int derivativeDimension, float delta) {
		myFunction = function;
		myDerivative = new NumericalDerivative(myFunction, derivativeDimension, delta);
	}

	/**
	 * @return A numerical approximation of the derivative
	 * @see ca.neo.math.DifferentiableFunction#getDerivative()
	 */
	public Function getDerivative() {
		return myDerivative;
	}

	/**
	 * Passed through to underlying Function.
	 * 
	 * @see ca.neo.math.Function#getDimension()
	 */
	public int getDimension() {
		return myFunction.getDimension();
	}

	/**
	 * Passed through to underlying Function.
	 * 
	 * @see ca.neo.math.Function#map(float[])
	 */
	public float map(float[] from) {
		return myFunction.map(from);
	}

	/**
	 * Passed through to underlying Function.
	 * 
	 * @see ca.neo.math.Function#multiMap(float[][])
	 */
	public float[] multiMap(float[][] from) {
		return myFunction.multiMap(from);
	}

	/**
	 * @author Bryan Tripp
	 */
	public static class NumericalDerivative implements Function {

		private static final long serialVersionUID = 1L;
		
		private Function myFunction;
		private int myDerivativeDimension;
		private float myDelta;
		
		/**
		 * @param function The Function of which the derivative is to be approximated
		 * @param derivativeDimension The dimension along which the derivative is to be calculated
		 * @param delta Derivative approximation of f(x) is [f(x+delta)-f(x-delta)]/[2*delta]
		 */
		public NumericalDerivative(Function function, int derivativeDimension, float delta) {
			myFunction = function;
			myDerivativeDimension = derivativeDimension;
			myDelta = delta;
		}

		/**
		 * @see ca.neo.math.Function#getDimension()
		 */
		public int getDimension() {
			return myFunction.getDimension();
		}

		/**
		 * @return An approximation of the derivative of the underlying Function
		 *  
		 * @see ca.neo.math.Function#map(float[])
		 */
		public float map(float[] from) {
			from[myDerivativeDimension] = from[myDerivativeDimension] + myDelta;
			float forward = myFunction.map(from);
			from[myDerivativeDimension] = from[myDerivativeDimension] - 2*myDelta;
			float backward = myFunction.map(from);
			
			return (forward - backward) / (2*myDelta);
		}

		/**
		 * @return Approximations of the derivative of the underlying Function at multiple points
		 * 
		 * @see ca.neo.math.Function#multiMap(float[][])
		 */
		public float[] multiMap(float[][] from) {
			float[] result = new float[from.length];
			
			for (int i = 0; i < from.length; i++) {
				result[i] = map(from[i]);
			}
			
			return result;
		}
		
	}

}
