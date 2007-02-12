package ca.neo.math.impl;

import ca.neo.math.DifferentiableFunction;
import ca.neo.math.Function;
//import ca.neo.plot.Plotter;

/**
 * A one-dimensional sigmoid function with configurable high and low 
 * values, slope, and inflection point.
 * 
 * TODO: unit tests
 * 
 * @author Bryan Tripp
 */
public class SigmoidFunction extends AbstractFunction implements DifferentiableFunction {

	private float myLow;
	private float myHigh;
	private float myInflection;
	private float myMultiplier;
	private Function myDerivative;
	
	/**
	 * Default parameters (inflection=0; slope=1/4; low=0; high=1). 
	 */
	public SigmoidFunction() {
		super(1);
		
		myLow = 0;
		myHigh = 1;
		myInflection = 0;
		myMultiplier = 1;
		myDerivative = new SigmoidDerivative(myHigh-myLow, myInflection, myMultiplier);
	}

	/**
	 * @param inflection Inflection point
	 * @param slope Slope at inflection point (usually 1/4)
	 * @param low Result for inputs much lower than inflection point 
	 * @param high Result for inputs much higher than inflection point
	 */
	public SigmoidFunction(float inflection, float slope, float low, float high) {
		super(1);
		
		myLow = low;
		myHigh = high;
		myInflection = inflection;
		myMultiplier = slope * 4f; //usual slope is 1/4
		myDerivative = new SigmoidDerivative(myHigh-myLow, myInflection, myMultiplier);
	}
	
	private static final long serialVersionUID = 1L;

	/**
	 * @see ca.neo.math.DifferentiableFunction#getDerivative()
	 */
	public Function getDerivative() {
		return myDerivative;
	}

	/**
	 * @see ca.neo.math.Function#map(float[])
	 */
	public float map(float[] from) {	
		return myLow + (myHigh-myLow) * ( 1f / (1f + (float) Math.exp(-myMultiplier*(from[0]-myInflection))) ) ;
	}
	
	/**
	 * Derivative of a sigmoid. 
	 * 
	 * @author Bryan Tripp
	 */
	private static class SigmoidDerivative extends AbstractFunction {

		private static final long serialVersionUID = 1L;
		
		private float myScale;
		private float myInflection;
		private float myMultiplier;
		
		public SigmoidDerivative(float scale, float inflection, float multiplier) {
			super(1);
			myScale = scale;
			myInflection = inflection;
			myMultiplier = multiplier;			
		}
		
		public float map(float[] from) {
			float sigmoidResult = 1f / (1f + (float) Math.exp(-myMultiplier*(from[0]-myInflection)));
			return myScale * myMultiplier * sigmoidResult * (1 - sigmoidResult);
		}
		
	}
	
//	public static void main(String[] args) {
//		//DifferentiableFunction f = new SigmoidFunction();
//		DifferentiableFunction f = new SigmoidFunction(1, 1f, 1, -1);
//		Plotter.plot(f, -6, .01f, 6, "sigmoid");
//		Plotter.plot(f.getDerivative(), -6, .01f, 6, "derivative");
//	}

}
