/*
 * Created on 23-May-2006
 */
package ca.nengo.math.impl;

import java.lang.reflect.Method;

import ca.nengo.config.ConfigUtil;
import ca.nengo.config.Configuration;
import ca.nengo.config.impl.ConfigurationImpl;
import ca.nengo.config.impl.ListPropertyImpl;
import ca.nengo.math.Function;
import ca.nengo.math.FunctionBasis;

/**
 * Default implementation of FunctionBasis. 
 *  
 * @author Bryan Tripp
 */
public class FunctionBasisImpl extends AbstractFunction implements FunctionBasis {

	private static final long serialVersionUID = 1L;
	
	private Function[] myFunctions;
	private float[] myCoefficients;
	
	/**
	 * @param functions Ordered list of functions composing this basis (all must have same dimension)
	 */
	public FunctionBasisImpl(Function[] functions) {
		super(functions[0].getDimension());

		for (int i = 1; i < functions.length; i++) {
			if (functions[i].getDimension() != getDimension()) {
				throw new IllegalArgumentException("Functions must all have same dimension");
			}
		}
		
		myFunctions = functions;
		myCoefficients = new float[functions.length];
	}
	
	/**
	 * @return Custom configuration 
	 */
	public Configuration getConfiguration() {
		ConfigurationImpl result = ConfigUtil.defaultConfiguration(this);
		result.removeProperty("basisDimension");
		try {
			Method getter = this.getClass().getMethod("getFunction", new Class[]{Integer.TYPE});
			Method countGetter = this.getClass().getMethod("getBasisDimension", new Class[0]);
			result.defineProperty(new ListPropertyImpl(result, "functions", Function.class, getter, countGetter));			
		} catch (Exception e) {
			throw new RuntimeException("Can't find function-related methods (this is a bug)", e);
		}
		return result;
	}

	/**
	 * @see ca.nengo.math.FunctionBasis#getBasisDimension()
	 */
	public int getBasisDimension() {
		return myFunctions.length;
	}

	/**
	 * @see ca.nengo.math.FunctionBasis#getFunction(int)
	 */
	public Function getFunction(int dimension) {
		if (dimension < 0 || dimension >= myFunctions.length) {
			throw new IllegalArgumentException("Dimension " + dimension + " does not exist");
		}
		
		return myFunctions[dimension];
	}

	/**
	 * @see ca.nengo.math.FunctionBasis#setCoefficients(float[])
	 */
	public void setCoefficients(float[] coefficients) {
		if (coefficients.length != myCoefficients.length) {
			throw new IllegalArgumentException(myCoefficients.length + " coefficients are needed");
		}
		myCoefficients = coefficients;
	}
	
	/**
	 * @return Coefficients with which basis functions are combined
	 */
	public float[] getCoefficients() {
		return myCoefficients;
	}

	/**
	 * @see ca.nengo.math.Function#map(float[])
	 */
	public float map(float[] from) {
		float result = 0;
		
		for (int i = 0; i < myFunctions.length; i++) {
			result += myCoefficients[i] * myFunctions[i].map(from);
		}
		
		return result;
	}

	@Override
	public Function clone() throws CloneNotSupportedException {
		Function[] functions = new Function[myFunctions.length];
		for (int i = 0; i < functions.length; i++) {
			functions[i] = myFunctions[i].clone();
		}
		FunctionBasisImpl result = new FunctionBasisImpl(functions);
		result.setCoefficients(myCoefficients.clone());
		return result;
	}

}
