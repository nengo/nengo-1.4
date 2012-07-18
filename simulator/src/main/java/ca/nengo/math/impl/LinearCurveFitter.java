/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "LinearCurveFitter.java". Description: 
"Interpolates linearly between example points"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU 
Public License license (the GPL License), in which case the provisions of GPL 
License are applicable  instead of those above. If you wish to allow use of your 
version of this file only under the terms of the GPL License and not to allow 
others to use your version of this file under the MPL, indicate your decision 
by deleting the provisions above and replace  them with the notice and other 
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
*/

/*
 * Created on 19-Apr-07
 */
package ca.nengo.math.impl;

import java.util.Arrays;

import ca.nengo.config.ConfigUtil;
import ca.nengo.config.Configuration;
import ca.nengo.config.impl.ConfigurationImpl;
import ca.nengo.config.impl.SingleValuedPropertyImpl;
import ca.nengo.math.CurveFitter;
import ca.nengo.math.Function;

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
	 * @see ca.nengo.math.CurveFitter#fit(float[], float[])
	 */
	public Function fit(float[] x, float[] y) {
		return new InterpolatedFunction(x, y);
	}
	
	@Override
	public CurveFitter clone() throws CloneNotSupportedException {
		return (CurveFitter) super.clone();
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
		 * @param x Known function-argument points to interpolate between 
		 * @param y Known function-output points to interpolate between (must be same length as x)
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
		
		/**
		 * @return Custom configuration
		 */
		public Configuration getConfiguration() {
			ConfigurationImpl result = ConfigUtil.defaultConfiguration(this);
			result.defineProperty(SingleValuedPropertyImpl.getSingleValuedProperty(result, "numPoints", Integer.TYPE));
			return result;
		}

		/**
		 * @return Number of points between which this function interpolates
		 */
		public int getNumPoints() {
			return myX.length;
		}
		
		/**
		 * If this method is used to increase the number of interpolation points, new points 
		 * are set to equal what was previously the last point.
		 *   
		 * @param num New number of points between which this function interpolates
		 */
		public void setNumPoints(int num) {
			if (num < 2) {
				throw new IllegalArgumentException("At least two points are needed");
			}
			
			float[] newX = new float[num];
			float[] newY = new float[num];
			System.arraycopy(myX, 0, newX, 0, Math.min(num, myX.length));
			System.arraycopy(myY, 0, newY, 0, Math.min(num, myY.length));
			
			for (int i = myX.length; i < newX.length; i++) {
				newX[i] = myX[myX.length-1];
				newY[i] = myY[myY.length-1];
			}
			
			myX = newX;
			myY = newY;
		}
		
		/**
		 * @return Known function-argument points to interpolate between
		 */
		public float[] getX() {
			return copy(myX);
		}

		/**
		 * @param x Known function-argument points to interpolate between 
		 */
		public void setX(float[] x) {
			if (x.length != myX.length) {
				throw new IllegalArgumentException("Expected " + myX.length + " values.");
			}
			myX = copy(x);
		}

		/**
		 * @return Known function-output points to interpolate between 
		 */
		public float[] getY() {
			return copy(myY);
		}
		
		/**
		 * @param y Known function-output points to interpolate between 
		 */
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
		 * @see ca.nengo.math.impl.AbstractFunction#map(float[])
		 */
		public float map(float[] from) {
			return interpolate(myX, myY, from[0]);
//			float x = from[0];
//			
//			//find index of lowest myX that is >= x
//			int index = Arrays.binarySearch(myX, x);
//			if (index < 0) index = - index - 1; 		
//			
//			//use last interval on each end for extrapolation
//			if (index == 0) index = 1; 
//			if (index == myX.length) index = index - 1;
//			
//			float dx = x - myX[index-1];
//			return myY[index-1] + (myY[index] - myY[index-1]) * dx / (myX[index] - myX[index-1]); 
		}
		
		/**
		 * @param xs List of x values
		 * @param ys List of y values that x values map onto 
		 * @param x An x value at which to interpolate this mapping
		 * @return The interpolated y value
		 */
		public static float interpolate(float[] xs, float[] ys, float x) {
			//find index of lowest xs that is >= x
			int index = Arrays.binarySearch(xs, x);
			if (index < 0) index = - index - 1; 		
			
			//use last interval on each end for extrapolation
			if (index == 0) index = 1; 
			if (index == xs.length) index = index - 1;
			
			float dx = x - xs[index-1];
			return ys[index-1] + (ys[index] - ys[index-1]) * dx / (xs[index] - xs[index-1]); 			
		}

		@Override
		public Function clone() throws CloneNotSupportedException {
			return new InterpolatedFunction(myX.clone(), myY.clone());
		}
		
	}
	
}
