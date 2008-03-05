package ca.neo.math.impl;

import ca.neo.util.MU;

/**
 * A linear map into one dimension. Optionally, the result can be biased and/or 
 * rectified. 
 *  
 * @author Bryan Tripp
 */
public class LinearFunction extends AbstractFunction {

	private static final long serialVersionUID = 1L;
	
	private float[] myMap;
	private float myBias;
	private boolean myRectified;

	/**
	 * @param map A 1Xn matrix that defines a map from input onto one dimension
	 * 		(i.e. f(x) = m'x, where m is the map)
	 * @param bias Bias to add to result
	 * @param rectified If true, result is rectified (set to 0 if less than 0) 
	 */
	public LinearFunction(float[] map, float bias, boolean rectified) {
		super(map.length);
		myMap = map;
		myBias = bias;
		myRectified = rectified;
	}

	@Override
	public float map(float[] from) {
		float result = MU.prod(from, myMap) + myBias;
		return (myRectified && result < 0) ? 0 : result;
	}

}
