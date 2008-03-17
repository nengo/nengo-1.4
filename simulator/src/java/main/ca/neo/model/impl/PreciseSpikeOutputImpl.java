package ca.neo.model.impl;

import ca.neo.model.PreciseSpikeOutput;
import ca.neo.model.SpikeOutput;
import ca.neo.model.Units;

public class PreciseSpikeOutputImpl implements PreciseSpikeOutput {

	private static final long serialVersionUID = 1L;
	
	private boolean[] myValues;
	private float[] mySpikeTimes;
	private Units myUnits;
	private float myTime;

	/**
	 * @param values @see #getValues()
	 * @param units @see #getUnits()
	 * @param time @see #getTime()
	 */
	public PreciseSpikeOutputImpl(float[] spikeTimes, Units units, float time) {
		mySpikeTimes = spikeTimes;
		myValues = new boolean[spikeTimes.length];
		for (int i=0; i<spikeTimes.length; i++) myValues[i]=spikeTimes[i]>=0;
		myUnits = units;
		myTime = time;
	}

	/**
	 * @see ca.neo.model.PreciseSpikeOutput#getSpikeTimes()
	 */
	public float[] getSpikeTimes() {
		return mySpikeTimes;
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

	/**
	 * @see ca.neo.model.InstantaneousOutput#getTime()
	 */
	public float getTime() {
		return myTime;
	}

	@Override
	public PreciseSpikeOutput clone() throws CloneNotSupportedException {
		return new PreciseSpikeOutputImpl(mySpikeTimes.clone(), myUnits, myTime);
	}

}
