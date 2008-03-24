/*
 * Created on 23-May-2006
 */
package ca.nengo.model.impl;

import ca.nengo.model.RealOutput;
import ca.nengo.model.Units;

/**
 * Default implementation of RealOutput. 
 * 
 * @author Bryan Tripp
 */
public class RealOutputImpl implements RealOutput {

	private static final long serialVersionUID = 1L;
	
	private float[] myValues;
	private Units myUnits;
	private float myTime;

	/**
	 * @param values @see #getValues()
	 * @param units @see #getUnits()
	 * @param time @see #getTime()
	 */
	public RealOutputImpl(float[] values, Units units, float time) {
		myValues = values;
		myUnits = units;
		myTime = time;
	}

	/**
	 * @see ca.nengo.model.RealOutput#getValues()
	 */
	public float[] getValues() {
		return myValues;
	}

	/**
	 * @see ca.nengo.model.InstantaneousOutput#getUnits()
	 */
	public Units getUnits() {
		return myUnits;
	}

	/**
	 * @see ca.nengo.model.InstantaneousOutput#getDimension()
	 */
	public int getDimension() {
		return myValues.length;
	}

	/**
	 * @see ca.nengo.model.InstantaneousOutput#getTime()
	 */
	public float getTime() {
		return myTime;
	}

	@Override
	public RealOutput clone() throws CloneNotSupportedException {
		return new RealOutputImpl(myValues.clone(), myUnits, myTime);
	}

}
