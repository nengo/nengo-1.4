/*
 * Created on 1-Dec-2006
 */
package ca.neo.math.impl;

import ca.neo.config.ConfigUtil;
import ca.neo.math.DifferentiableFunction;
import ca.neo.math.Function;
import ca.neo.model.Configuration;
import ca.neo.model.StructuralException;
import ca.neo.model.impl.ConfigurationImpl;

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
	
	public static final String DIMENSION_PROPERTY = AbstractFunction.DIMENSION_PROPERTY;
	public static final String FUNCTION_PROPERTY = "function";
	public static final String DERIVATIVE_PROPERTY = "derivative";
	public static final String DERIVATIVE_DIMENSION_PROPERTY = NumericalDerivative.DERIVATIVE_DIMENSION_PROPERTY;
	public static final String DELTA_PROPERTY = NumericalDerivative.DELTA_PROPERTY;
	
	private Function myFunction;
	private NumericalDerivative myDerivative;
	private ConfigurationImpl myConfiguration;
	
	/**
	 * @param function An underlying Function
	 * @param derivativeDimension The dimension along which the derivative is to be calculated 
	 * 		(note that the gradient of a multi-dimensional Function consists of multiple DifferentiableFunctions) 
	 * @param delta Derivative approximation of f(x) is [f(x+delta)-f(x-delta)]/[2*delta]
	 */
	public NumericallyDifferentiableFunction(Function function, int derivativeDimension, float delta) {
		set(function, derivativeDimension, delta);
		
		myConfiguration = new ConfigurationImpl(this);
		myConfiguration.defineSingleValuedProperty(DIMENSION_PROPERTY, Integer.class, false);
		myConfiguration.defineSingleValuedProperty(FUNCTION_PROPERTY, Function.class, true);
		myConfiguration.defineSingleValuedProperty(DERIVATIVE_PROPERTY, Function.class, false);
		myConfiguration.defineSingleValuedProperty(DERIVATIVE_DIMENSION_PROPERTY, Integer.class, true);
		myConfiguration.defineSingleValuedProperty(DELTA_PROPERTY, Float.class, true);
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
	 * @see ca.neo.model.Configurable#getConfiguration()
	 */
	public Configuration getConfiguration() {
		return myConfiguration;
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
	 * @param dim The dimension along which the derivative is to be calculated 
	 */
	public void setDerivativeDimension(int dim) {
		set(myFunction, dim, getDelta());
	}
	
	/**
	 * @return Delta in derivative approximation [f(x+delta)-f(x-delta)]/[2*delta]
	 */
	public float getDelta() {
		return myDerivative.getDelta();
	}
	
	/**
	 * @param delta Delta in derivative approximation [f(x+delta)-f(x-delta)]/[2*delta]
	 */
	public void setDelta(float delta) {
		myDerivative.setDelta(delta);
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
		
		public static final String DIMENSION_PROPERTY = AbstractFunction.DIMENSION_PROPERTY;
		public static final String FUNCTION_PROPERTY = "function";
		public static final String DERIVATIVE_DIMENSION_PROPERTY = "derivativeDimension";
		public static final String DELTA_PROPERTY = "delta";
		
		private Function myFunction;
		private int myDerivativeDimension;
		private float myDelta;
		private ConfigurationImpl myConfiguration;
		
		/**
		 * @param function The Function of which the derivative is to be approximated
		 * @param derivativeDimension The dimension along which the derivative is to be calculated
		 * @param delta Derivative approximation of f(x) is [f(x+delta)-f(x-delta)]/[2*delta]
		 */
		public NumericalDerivative(Function function, int derivativeDimension, float delta) {
			init(function, derivativeDimension, delta);
		}
		
		public NumericalDerivative(Configuration properties) throws StructuralException {
			Function f = (Function) ConfigUtil.get(properties, FUNCTION_PROPERTY, Function.class);
			int d = ((Integer) ConfigUtil.get(properties, DERIVATIVE_DIMENSION_PROPERTY, Function.class)).intValue();
			init(f, d, .01f);
		}
		
		private void init(Function function, int derivativeDimension, float delta) {
			myFunction = function;
			myDerivativeDimension = derivativeDimension;
			myDelta = delta;
			
			myConfiguration = new ConfigurationImpl(this);
			myConfiguration.defineSingleValuedProperty(DIMENSION_PROPERTY, Integer.class, false);
			myConfiguration.defineSingleValuedProperty(FUNCTION_PROPERTY, Function.class, false);
			myConfiguration.defineSingleValuedProperty(DERIVATIVE_DIMENSION_PROPERTY, Integer.class, false);
			myConfiguration.defineSingleValuedProperty(DELTA_PROPERTY, Float.class, true);
		}
		
		/**
		 * @return A construction template
		 */
		public static Configuration getConstructionTemplate() {
			ConfigurationImpl result = new ConfigurationImpl(null);
			result.defineTemplateProperty(FUNCTION_PROPERTY, Function.class, new ConstantFunction(1, 1));
			result.defineTemplateProperty(DERIVATIVE_DIMENSION_PROPERTY, Integer.class, new Integer(0));
			return result;
		}

		/**
		 * @see ca.neo.model.Configurable#getConfiguration()
		 */
		public Configuration getConfiguration() {
			return myConfiguration;
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
