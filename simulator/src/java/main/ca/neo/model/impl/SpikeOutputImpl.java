/*
 * Created on 23-May-2006
 */
package ca.neo.model.impl;

import ca.neo.model.SpikeOutput;
import ca.neo.model.Units;

/**
 * Default implementation of SpikeOutput. 
 *  
 * @author Bryan Tripp
 */
public class SpikeOutputImpl implements SpikeOutput {

	private static final long serialVersionUID = 1L;
	
	private boolean[] myValues;
	private Units myUnits;

	/**
	 * @param values @see #getValues()
	 * @param units @see #getUnits()
	 */
	public SpikeOutputImpl(boolean[] values, Units units) {
		myValues = values;
		myUnits = units;
	}

	/**
	 * @see ca.neo.model.SpikeOutput#getValues()
	 */
	public boolean[] getValues() {
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

}
