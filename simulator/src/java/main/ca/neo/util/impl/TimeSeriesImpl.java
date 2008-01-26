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
public class TimeSeriesImpl implements TimeSeries {

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
		this(times, values, units, getDefaultLabels(units.length));
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
	
	private static String[] getDefaultLabels(int n) {
		String[] result = new String[n];
		for (int i = 0; i < n; i++) {
			result[i] = String.valueOf(i+1);
		}
		return result;
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
	
//	private void setTimes(float[] times) {
//		myTimes = times;
//	}

	/**
	 * @see ca.neo.util.TimeSeries1D#getValues()
	 */
	public float[][] getValues() {
		return myValues;
	}
	
//	private void setValues(float[][] values) {
//		myValues = values;
//	}

	/**
	 * @see ca.neo.util.TimeSeries1D#getUnits()
	 */
	public Units[] getUnits() {
		return myUnits;
	}
	
	/**
	 * @param index Index of dimension for which to change units 
	 * @param units New units for given dimension
	 */
	public void setUnits(int index, Units units) {
		myUnits[index] = units;
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

	/**
	 * @param index Index of dimension for which to change label
	 * @param label New label for given dimension
	 */
	public void setLabel(int index, String label) {
		myLabels[index] = label;
	}
	
}
