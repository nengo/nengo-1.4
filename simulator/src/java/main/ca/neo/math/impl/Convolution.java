/*
 * Created on 12-May-07
 */
package ca.neo.math.impl;

import ca.neo.config.ConfigUtil;
import ca.neo.config.Configuration;
import ca.neo.math.Function;
import ca.neo.plot.Plotter;

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
	private float myTimeStep;
	private float myWindow;
	
	private Configuration myConfiguration;
	
	public Convolution(Function one, Function two, float timeStep, float window) {
		super(1);		
		setFunctionOne(one);
		setFunctionTwo(two);		
		myTimeStep = timeStep;
		myWindow = window;
		
//		myConfiguration = ConfigUtil.defaultConfiguration(this);
	}
	
	public Convolution() {
		this(new ConstantFunction(1, 1), new ConstantFunction(1, 1), .001f, 1f);
	}
	
//	@Override
//	public Configuration getConfiguration() {
//		return myConfiguration;
//	}

	public Function getFunctionOne() {
		return myOne;
	}
	
	public void setFunctionOne(Function function) {
		checkDimension(function);
		myOne = function;
	}
	
	public Function getFunctionTwo() {
		return myTwo;
	}
	
	public void setFunctionTwo(Function function) {
		checkDimension(function);
		myTwo = function;
	}
	
	public float getTimeStep() {
		return myTimeStep;
	}
	
	public void setTimeStep(float timeStep) {
		myTimeStep = timeStep;
	}
	
	public float getWindow() {
		return myWindow;
	}
	
	public void setWindow(float window) {
		myWindow = window;
	}
	
	private static void checkDimension(Function function) {
		if (function.getDimension() != 1) {
			throw new IllegalArgumentException("Functions for convolution must be one-dimensional");
		}		
	}

	/**
	 * @see ca.neo.math.impl.AbstractFunction#map(float[])
	 */
	public float map(float[] from) {		
		float result = 0;
		
		float time = from[0];
		float tau = 0;
		while (tau <= myWindow) {
			result += myOne.map(new float[]{time - tau}) * myTwo.map(new float[]{tau}) * myTimeStep;
			tau += myTimeStep;
		}
		
		return result;
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
