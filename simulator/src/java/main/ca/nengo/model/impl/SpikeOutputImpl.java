/*
 * Created on 23-May-2006
 */
package ca.nengo.model.impl;

import ca.nengo.model.SpikeOutput;
import ca.nengo.model.Units;

/**
 * Default implementation of SpikeOutput. 
 *  
 * @author Bryan Tripp
 */
public class SpikeOutputImpl implements SpikeOutput {

	private static final long serialVersionUID = 1L;
	
	private boolean[] myValues;
	private Units myUnits;
	private float myTime;

	/**
	 * @param values @see #getValues()
	 * @param units @see #getUnits()
	 * @param time @see #getTime()
	 */
	public SpikeOutputImpl(boolean[] values, Units units, float time) {
		myValues = values;
		myUnits = units;
		myTime = time;
	}

	/**
	 * @see ca.nengo.model.SpikeOutput#getValues()
	 */
	public boolean[] getValues() {
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
	public SpikeOutput clone() throws CloneNotSupportedException {
		return new SpikeOutputImpl(myValues.clone(), myUnits, myTime);
	}

}
