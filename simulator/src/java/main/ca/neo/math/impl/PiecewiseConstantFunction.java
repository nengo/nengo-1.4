/*
 * Created on 31-Jan-2007
 */
package ca.neo.math.impl;

import java.util.Arrays;

import ca.neo.config.ConfigUtil;
import ca.neo.config.Configuration;
import ca.neo.config.impl.ConfigurationImpl;
import ca.neo.config.impl.SingleValuedPropertyImpl;
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
	
	private float[] myDiscontinuities;
	private float[] myValues;
	
	/**
	 * @param discontinuities Ordered points x at which the function is y = f(x) is discontinuous 
	 * @param values Values y below x1 and above x1..xn
	 */
	public PiecewiseConstantFunction(float[] discontinuities, float[] values) {
		super(1);
		
		myDiscontinuities = discontinuities;
		if ( discontinuities.length != (values.length - 1) ) {
			throw new IllegalArgumentException("There must be one more value than point of discontinuity");
		}
		
		myDiscontinuities = new float[discontinuities.length];
		System.arraycopy(discontinuities, 0, myDiscontinuities, 0, discontinuities.length);
		Arrays.sort(myDiscontinuities);
		
		setValues(values);
	}
	
	/**
	 * @return Custom configuration
	 */
	public Configuration getConfiguration() {
		ConfigurationImpl result = ConfigUtil.defaultConfiguration(this);
		result.defineProperty(SingleValuedPropertyImpl.getSingleValuedProperty(result, "numDiscontinuities", Integer.TYPE));
		return result;
	}
	
	/**
	 * @return Number of discontinuities 
	 */
	public int getNumDiscontinuities() {
		return myDiscontinuities.length;
	}

	/**
	 * @param num New number of discontinuities
	 */
	public void setNumDiscontinuities(int num) {
		float[] nd = new float[num];
		System.arraycopy(myDiscontinuities, 0, nd, 0, Math.min(num, myDiscontinuities.length));
		myDiscontinuities = nd;
		
		float[] nv = new float[num+1];
		System.arraycopy(myValues, 0, nv, 0, Math.min(num+1, myValues.length));
		myValues = nv;
	}
	
	/**
	 * @return Ordered points x at which the function is y = f(x) is discontinuous 
	 */
	public float[] getDiscontinuities() {
		return myDiscontinuities;
	}
	
	/**
	 * @param discontinuities Ordered points x at which the function is y = f(x) is discontinuous
	 */
	public void setDiscontinuities(float[] discontinuities) {
		if (discontinuities.length != myDiscontinuities.length) {
			throw new IllegalArgumentException(
					"Number of discontinuities must be consistent with number of values (use setNumDiscontinuities() to change).");
		}
		myDiscontinuities = new float[discontinuities.length];
		System.arraycopy(discontinuities, 0, myDiscontinuities, 0, discontinuities.length);
		Arrays.sort(myDiscontinuities);
	}

	/**
	 * @return Values y below x1 and above x1..xn
	 */
	public float[] getValues() {
		return myValues;
	}

	/**
	 * @param values Values y below x1 and above x1..xn
	 */
	public void setValues(float[] values) {
		if (values.length - 1 != myDiscontinuities.length) {
			throw new IllegalArgumentException(
				"Number of discontinuities must be consistent with number of values (use setNumDiscontinuities() to change).");			
		}
		myValues = new float[values.length];
		System.arraycopy(values, 0, myValues, 0, values.length);
	}
	
	/**
	 * @see ca.neo.math.Function#map(float[])
	 */
	public float map(float[] from) {
		float y = 0;
		float x = from[0];
		
		if (myDiscontinuities.length == 0 || x <= myDiscontinuities[0]) {
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
	
	@Override
	public Function clone() throws CloneNotSupportedException {
		return new PiecewiseConstantFunction(myDiscontinuities.clone(), myValues.clone());
	}

	public static void main(String args[]) {
//		Function f = new PiecewiseConstantFunction(new float[]{0, 1, 3, 7}, new float[]{5, 2});
//		Function f = new PiecewiseConstantFunction(new float[]{0, 1, 3, 7}, new float[]{5, 2, -3, 6, 7});
		Function f = new PiecewiseConstantFunction(new float[0], new float[]{5});
		Plotter.plot(f, -1, .01f, 10, "");
	}

}
