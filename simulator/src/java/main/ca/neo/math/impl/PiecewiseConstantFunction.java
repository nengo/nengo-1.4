/*
 * Created on 31-Jan-2007
 */
package ca.neo.math.impl;

import ca.neo.math.Function;
import ca.neo.plot.Plotter;

/**
 * A one-dimensional function for which the output is constant between a finite number of 
 * discontinuities.
 * 
 * TODO: unit test
 * 
 * @author Bryan Tripp
 */
public class PiecewiseConstantFunction extends AbstractFunction {

	private static final long serialVersionUID = 1L;
	
	private final float[] myDiscontinuities;
	private final float[] myValues;
	
	/**
	 * @param discontinuities Ordered points x at which the function is y = f(x) is discontinuous 
	 * @param values Values y below x1 and above x1..xn
	 */
	public PiecewiseConstantFunction(float[] discontinuities, float[] values) {
		super(1);
		
		if ( discontinuities.length != (values.length - 1) ) {
			throw new IllegalArgumentException("There must be one more value than point of discontinuity");
		}
		
		myDiscontinuities = discontinuities;
		myValues = values;
	}

	/**
	 * @see ca.neo.math.Function#map(float[])
	 */
	public float map(float[] from) {
		float y = 0;
		float x = from[0];
		
		if (x <= myDiscontinuities[0]) {
			y = myValues[0];
			
		} else if (x >= myDiscontinuities[myDiscontinuities.length-1]) {
			y = myValues[myValues.length-1];
			
		} else {

			int low = 0;
			int high = myDiscontinuities.length;

			while (high-low > 1) {
				int middle = Math.round((low + high) / 2f);
				float xMiddle = myDiscontinuities[middle];
				
				if (xMiddle > x) {
					high = middle;
				} else {
					low = middle;
				}
			}
			
			y = myValues[high];
		}
		
		return y;
	}
	
	public static void main(String args[]) {
//		Function f = new PiecewiseConstantFunction(new float[]{0, 1, 3, 7}, new float[]{5, 2});
		Function f = new PiecewiseConstantFunction(new float[]{0, 1, 3, 7}, new float[]{5, 2, -3, 6, 7});
		Plotter.plot(f, -1, .01f, 10, "");
	}

}
