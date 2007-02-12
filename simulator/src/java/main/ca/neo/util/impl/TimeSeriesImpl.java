/*
 * Created on May 4, 2006
 */
package ca.neo.util.impl;

import java.io.Serializable;

import ca.neo.model.Units;
import ca.neo.util.TimeSeries;

/**
 * Default implementation of TimeSeriesND. 
 * 
 * @author Bryan Tripp
 */
public class TimeSeriesImpl implements TimeSeries, Serializable {

	private static final long serialVersionUID = 1L;
	
	private float[] myTimes;
	private float[][] myValues;
	private Units[] myUnits;
	private String[] myLabels;
	
	/**
	 * @param times @see ca.bpt.cn.util.TimeSeries#getTimes()
	 * @param values @see ca.bpt.cn.util.TimeSeries#getValues()
	 * @param units @see ca.bpt.cn.util.TimeSeries#getUnits()
	 */	 
	public TimeSeriesImpl(float[] times, float[][] values, Units[] units) {
		checkDimensions(times, values, units);
		
		myTimes = times;
		myValues = values;
		myUnits = units;
		myLabels = new String[units.length];
		
		for (int i = 0; i < myLabels.length; i++) {
			myLabels[i] = String.valueOf(i+1);
		}
	}
	
	/**
	 * @param times @see ca.neo.util.TimeSeries#getTimes()
	 * @param values @see ca.neo.util.TimeSeries#getValues()
	 * @param units @see ca.neo.util.TimeSeries#getUnits()
	 * @param labels @see ca.neo.util.TimeSeries#getLabels()
	 */	 
	public TimeSeriesImpl(float[] times, float[][] values, Units[] units, String[] labels) {		
		checkDimensions(times, values, units);
		
		myTimes = times;
		myValues = values;
		myUnits = units;
		myLabels = labels;
	}
	
	private void checkDimensions(float[] times, float[][] values, Units[] units) {
		if (times.length != values.length) {
			throw new IllegalArgumentException(times.length + " times were given with " + values.length + " values");
		}
		
		if (values.length > 0 && values[0].length != units.length) {
			throw new IllegalArgumentException("Values have dimension " + values[0].length
					+ " but there are " + units.length + " units");
		}
	}

	/**
	 * @see ca.neo.util.TimeSeries1D#getTimes()
	 */
	public float[] getTimes() {
		return myTimes;		
	}

	/**
	 * @see ca.neo.util.TimeSeries1D#getValues()
	 */
	public float[][] getValues() {
		return myValues;
	}

	/**
	 * @see ca.neo.util.TimeSeries1D#getUnits()
	 */
	public Units[] getUnits() {
		return myUnits;
	}

	/**
	 * @see ca.neo.util.TimeSeries#getDimension()
	 */
	public int getDimension() {
		return myUnits.length;
	}

	/**
	 * @see ca.neo.util.TimeSeries#getLabels()
	 */
	public String[] getLabels() {
		return myLabels;
	}
	
}
