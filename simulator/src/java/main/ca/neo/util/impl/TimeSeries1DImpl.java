/*
 * Created on May 4, 2006
 */
package ca.neo.util.impl;

import java.io.Serializable;

import ca.neo.model.Units;
import ca.neo.util.TimeSeries1D;

/**
 * Default implementation of TimeSeries.  
 * 
 * @author Bryan Tripp
 */
public class TimeSeries1DImpl implements TimeSeries1D, Serializable {

	private static final long serialVersionUID = 1L;
	
	private float[] myTimes;
	private float[] myValues;
	private Units myUnits;
	private String myLabel;
	
	/**
	 * @param times @see ca.bpt.cn.util.TimeSeries#getTimes()
	 * @param values @see ca.bpt.cn.util.TimeSeries#getValues()
	 * @param units @see ca.bpt.cn.util.TimeSeries#getUnits()
	 */	 
	public TimeSeries1DImpl(float[] times, float[] values, Units units) {
		if (times.length != values.length) {
			throw new IllegalArgumentException(times.length + " times were given with " + values.length + " values");
		}
		
		this.myTimes = times;
		this.myValues = values;
		this.myUnits = units;
		this.myLabel = "1";
	}

	/**
	 * @see ca.neo.util.TimeSeries1D#getTimes()
	 */
	public float[] getTimes() {
		return myTimes;		
	}

	/**
	 * @see ca.neo.util.TimeSeries1D#getValues1D()
	 */
	public float[] getValues1D() {
		return myValues;
	}

	/**
	 * @see ca.neo.util.TimeSeries1D#getUnits1D()
	 */
	public Units getUnits1D() {
		return myUnits;
	}

	/**
	 * @see ca.neo.util.TimeSeries#getDimension()
	 */
	public int getDimension() {
		return 1;
	}

	/**
	 * @see ca.neo.util.TimeSeries#getValues()
	 */
	public float[][] getValues() {
		float[][] result = new float[myValues.length][];
		
		for (int i = 0; i < myValues.length; i++) {
			result[i] = new float[]{myValues[i]};
		}
		
		return result;
	}

	/**
	 * @see ca.neo.util.TimeSeries#getUnits()
	 */
	public Units[] getUnits() {
		return new Units[]{myUnits};
	}
	
	/**
	 * @see ca.neo.util.TimeSeries#getLabels()
	 */
	public String[] getLabels() {
		return new String[]{myLabel};
	}
	
	
}
