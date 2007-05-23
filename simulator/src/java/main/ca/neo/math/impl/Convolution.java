/*
 * Created on 12-May-07
 */
package ca.neo.math.impl;

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
	
	public Convolution(Function one, Function two, float timeStep, float window) {
		super(1);		
		myOne = one;
		myTwo = two;		
		myTimeStep = timeStep;
		myWindow = window;
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
