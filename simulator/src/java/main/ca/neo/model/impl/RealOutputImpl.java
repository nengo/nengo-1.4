/*
 * Created on 23-May-2006
 */
package ca.neo.model.impl;

import ca.neo.model.RealOutput;
import ca.neo.model.Units;

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
	 * @see ca.neo.model.RealOutput#getValues()
	 */
	public float[] getValues() {
		return myValues;
	}

	/**
	 * @see ca.neo.model.InstantaneousOutput#getUnits()
	 */
	public Units getUnits() {
		return myUnits;
	}

	/**
	 * @see ca.neo.model.InstantaneousOutput#getDimension()
	 */
	public int getDimension() {
		return myValues.length;
	}

	/**
	 * @see ca.neo.model.InstantaneousOutput#getTime()
	 */
	public float getTime() {
		return myTime;
	}

}
