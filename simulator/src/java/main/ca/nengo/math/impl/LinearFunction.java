package ca.nengo.math.impl;

import ca.nengo.math.Function;
import ca.nengo.util.MU;

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
	
	/**
	 * @return map A 1Xn matrix that defines a map from input onto one dimension
	 * 		(i.e. f(x) = m'x, where m is the map)
	 */
	public float[] getMap() {
		return myMap;
	}
	
	/**
	 * @param map map A 1Xn matrix that defines a map from input onto one dimension
	 * 		(i.e. f(x) = m'x, where m is the map)
	 */
	public void setMap(float[] map) {
		myMap = map;
	}
	
	/**
	 * @return Bias to add to result
	 */
	public float getBias() {
		return myBias;
	}
	
	/**
	 * @param bias Bias to add to result
	 */
	public void setBias(float bias) {
		myBias = bias;
	}
	
	/**
	 * @return If true, result is rectified (set to 0 if less than 0)
	 */
	public boolean getRectified() {
		return myRectified;
	}
	
	/**
	 * @param rectified If true, result is rectified (set to 0 if less than 0)
	 */
	public void setRectified(boolean rectified) {
		myRectified = rectified;
	}

	@Override
	public float map(float[] from) {
		float result = MU.prod(from, myMap) + myBias;
		return (myRectified && result < 0) ? 0 : result;
	}

	@Override
	public Function clone() throws CloneNotSupportedException {
		LinearFunction result = (LinearFunction) super.clone();
		result.setMap(this.getMap().clone());
		return result;
	}

}
