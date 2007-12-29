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
				throw new IllegalArgumentException("Arrays x and y must have the same length; y = f(x)");
			}					
			if (x.length < 2) {
				throw new IllegalArgumentException("At least two points are needed");
			}
		}
		
		public InterpolatedFunction() {
			this(new float[]{0, 1}, new float[]{0, 0});
		}
		
		public int getNumPoints() {
			return myX.length;
		}
		
		public void setNumPoints(int num) {
			if (num < 2) {
				throw new IllegalArgumentException("At least two points are needed");
			}
			
			float[] newX = new float[num];
			float[] newY = new float[num];
			System.arraycopy(myX, 0, newX, 0, Math.min(num, myX.length));
			System.arraycopy(myY, 0, newY, 0, Math.min(num, myY.length));
			myX = newX;
			myY = newY;
		}
		
		public float[] getX() {
			return copy(myX);
		}
		
		public void setX(float[] x) {
			if (x.length != myX.length) {
				throw new IllegalArgumentException("Expected " + myX.length + " values.");
			}
			myX = copy(x);
		}
		
		public float[] getY() {
			return copy(myY);
		}
		
		public void setY(float[] y) {
			if (y.length != myY.length) {
				throw new IllegalArgumentException("Expected " + myY.length + " values.");
			}
			myY = copy(y);
		}
		
		private static float[] copy(float[] vector) {
			float[] result = new float[vector.length];
			System.arraycopy(vector, 0, result, 0, vector.length);
			return result;
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
