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
	private NumericalDerivative myDerivative;
	
	/**
	 * @param function An underlying Function
	 * @param derivativeDimension The dimension along which the derivative is to be calculated 
	 * 		(note that the gradient of a multi-dimensional Function consists of multiple DifferentiableFunctions) 
	 * @param delta Derivative approximation of f(x) is [f(x+delta)-f(x-delta)]/[2*delta]
	 */
	public NumericallyDifferentiableFunction(Function function, int derivativeDimension, float delta) {
		set(function, derivativeDimension, delta);
	}
	
	/**
	 * Uses dummy parameters to allow setting after construction.  
	 */
	public NumericallyDifferentiableFunction() {
		this(new IdentityFunction(1,0), 0, .01f);
	}
	
	private void set(Function function, int derivativeDimension, float delta) {
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
	 * @return The underlying Function
	 */
	public Function getFunction() {
		return myFunction;
	}
	
	/**
	 * @param function A new underlying Function
	 */
	public void setFunction(Function function) {
		set(function, getDerivativeDimension(), getDelta());
	}
	
	/**
	 * @return The dimension along which the derivative is to be calculated 
	 */
	public int getDerivativeDimension() {
		return myDerivative.getDerivativeDimension();
	}
	
	/**
	 * @return Delta in derivative approximation [f(x+delta)-f(x-delta)]/[2*delta]
	 */
	public float getDelta() {
		return myDerivative.getDelta();
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
		 * @return The Function of which the derivative is to be approximated
		 */
		public Function getFunction() {
			return myFunction;
		}
		
		/**
		 * @return The dimension along which the derivative is to be calculated
		 */
		public int getDerivativeDimension() {
			return myDerivativeDimension;
		}
		
		/**
		 * @param dim The dimension along which the derivative is to be calculated 
		 */
		public void setDerivativeDimension(int dim) {
			if (dim < 0 || dim >= myFunction.getDimension()) {
				throw new IllegalArgumentException("Derivative dimension must be between 0 and " 
						+ (myFunction.getDimension()-1));
			}
			myDerivativeDimension = dim;
		}
		
		/**
		 * @return The variable delta in derivative approximation [f(x+delta)-f(x-delta)]/[2*delta]
		 */
		public float getDelta() {
			return myDelta;
		}
		
		/**
		 * @param delta The variable delta in derivative approximation [f(x+delta)-f(x-delta)]/[2*delta]
		 */
		public void setDelta(float delta) {
			System.out.println("setting delta to " + delta);
			myDelta = delta;
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
