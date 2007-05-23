/*
 * Created on 19-Apr-07
 */
package ca.neo.math.impl;

import java.util.Arrays;

import ca.neo.math.CurveFitter;
import ca.neo.math.Function;

/**
 * <p>Interpolates linearly between example points. Outside the range of examples, 
 * the last interval is extrapolated.</p>
 * 
 * <p>Inputs x must be sorted from lowest to highest.<p>  
 * 
 * TODO: sort inputs
 * 
 * @author Bryan Tripp
 */
public class LinearCurveFitter implements CurveFitter {

	/**
	 * Note that inputs x must be sorted from lowest to highest.
	 * 
	 * @see ca.neo.math.CurveFitter#fit(float[], float[])
	 */
	public Function fit(float[] x, float[] y) {
		return new InterpolatedFunction(x, y);
	}

	/**
	 * A 1-D Function based on interpolation between known points.  
	 * 
	 * @author Bryan Tripp
	 */
	public static class InterpolatedFunction extends AbstractFunction {

		private static final long serialVersionUID = 1L;
		
		private float[] myX; 
		private float[] myY;

		/**
	 * @param x Example x points 
	 * @param y Example y points (must be same length as x)
		 */
		public InterpolatedFunction(float[] x, float[] y) {
			super(1);
			myX = x;
			myY = y;
			
			if (x.length != y.length) {
				throw new IllegalArgumentException("Arrays x and y must have the same length; we take it that y = f(x)");
			}					
		}

		/**
		 * @see ca.neo.math.impl.AbstractFunction#map(float[])
		 */
		public float map(float[] from) {
			float x = from[0];
			
			//find index of lowest myX that is >= x
			int index = Arrays.binarySearch(myX, x);
			if (index < 0) index = - index - 1; 		
			
			//use last interval on each end for extrapolation
			if (index == 0) index = 1; 
			if (index == myX.length) index = index - 1;
			
			float dx = x - myX[index-1];
			return myY[index-1] + (myY[index] - myY[index-1]) * dx / (myX[index] - myX[index-1]); 
		}
	}
		
}
