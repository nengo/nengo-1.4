/*
 * Created on 12-May-07
 */
package ca.nengo.math.impl;

import ca.nengo.math.Function;
import ca.nengo.plot.Plotter;

/**
 * A numerical convolution of two one-dimensional functions.
 * 
 * TODO: unit tests
 * 
 * @author Bryan Tripp
 */
public class Convolution extends AbstractFunction {

	private static final long serialVersionUID = 1L;
	
	private Function myOne;
	private Function myTwo;
	private float myStepSize;
	private float myWindow;
	
	/**
	 * @param one First of two functions to convolve together
	 * @param two Second of two functions to convolve together
	 * @param stepSize Step size at which to numerically evaluate convolution integral
	 * @param window Window over which to evaluate convolution integral
	 */
	public Convolution(Function one, Function two, float stepSize, float window) {
		super(1);		
		setFunctionOne(one);
		setFunctionTwo(two);		
		myStepSize = stepSize;
		myWindow = window;
	}
	
	/**
	 * @return First of two functions to convolve together
	 */
	public Function getFunctionOne() {
		return myOne;
	}
	
	/**
	 * @param function First of two functions to convolve together
	 */
	public void setFunctionOne(Function function) {
		checkDimension(function);
		myOne = function;
	}
	
	/**
	 * @return Second of two functions to convolve together
	 */
	public Function getFunctionTwo() {
		return myTwo;
	}

	/**
	 * @param function Second of two functions to convolve together
	 */
	public void setFunctionTwo(Function function) {
		checkDimension(function);
		myTwo = function;
	}

	/**
	 * @return Step size at which to numerically evaluate convolution integral
	 */
	public float getStepSize() {
		return myStepSize;
	}

	/**
	 * @param stepSize Step size at which to numerically evaluate convolution integral
	 */
	public void setStepSize(float stepSize) {
		myStepSize = stepSize;
	}

	/**
	 * @return Window over which to evaluate convolution integral
	 */
	public float getWindow() {
		return myWindow;
	}

	/**
	 * @param window Window over which to evaluate convolution integral
	 */
	public void setWindow(float window) {
		myWindow = window;
	}
	
	private static void checkDimension(Function function) {
		if (function.getDimension() != 1) {
			throw new IllegalArgumentException("Functions for convolution must be one-dimensional");
		}		
	}

	/**
	 * @see ca.nengo.math.impl.AbstractFunction#map(float[])
	 */
	public float map(float[] from) {		
		float result = 0;
		
		float time = from[0];
		float tau = 0;
		while (tau <= myWindow) {
			result += myOne.map(new float[]{time - tau}) * myTwo.map(new float[]{tau}) * myStepSize;
			tau += myStepSize;
		}
		
		return result;
	}
	
	@Override
	public Function clone() throws CloneNotSupportedException {
		return new Convolution(myOne.clone(), myTwo.clone(), myStepSize, myWindow);
	}

	//functional test
	public static void main(String[] args) {
		Function one = new PiecewiseConstantFunction(new float[]{0.1f}, new float[]{0, 1});
		Function two = new AbstractFunction(1) {
			private static final long serialVersionUID = 1L;
			public float map(float[] from) {
				float t = from[0];
				float tau = .05f;
				return (1 - t/tau) * (float) Math.exp(-t/tau);
			}
		};
		
		Function conv = new Convolution(one, two, .0001f, .5f);
		
		Plotter.plot(conv, 0, .001f, 1f, "convolution of step and differentiator impulse response");
	}

}
