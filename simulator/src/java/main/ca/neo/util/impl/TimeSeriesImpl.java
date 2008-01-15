/*
 * Created on May 4, 2006
 */
package ca.neo.util.impl;

import java.io.Serializable;

import ca.neo.config.ConfigUtil;
import ca.neo.config.Configuration;
import ca.neo.config.ListProperty;
import ca.neo.config.Property;
import ca.neo.config.impl.ConfigurationImpl;
import ca.neo.config.impl.FixedCardinalityProperty;
import ca.neo.config.impl.SingleValuedPropertyImpl;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.util.MU;
import ca.neo.util.TimeSeries;

/**
 * Default implementation of TimeSeriesND. 
 * 
 * @author Bryan Tripp
 */
public class TimeSeriesImpl implements TimeSeries, Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final String DIMENSION_PROPERTY = "dimension";
	private static final String LENGTH_PROPERTY = "length";
	private static final String TIMES_PROPERTY = "times";
	private static final String VALUES_PROPERTY = "values";
	private static final String UNITS_PROPERTY = "units";
	private static final String LABELS_PROPERTY = "labels";
	
	private float[] myTimes;
	private float[][] myValues;
	private Units[] myUnits;
	private String[] myLabels;
	
	private ConfigurationImpl myConfiguration;
	
	/**
	 * @param times @see ca.bpt.cn.util.TimeSeries#getTimes()
	 * @param values @see ca.bpt.cn.util.TimeSeries#getValues()
	 * @param units @see ca.bpt.cn.util.TimeSeries#getUnits()
	 */	 
	public TimeSeriesImpl(float[] times, float[][] values, Units[] units) {
		init(times, values, units, getDefaultLabels(units.length));
	}
	
	/**
	 * @param times @see ca.neo.util.TimeSeries#getTimes()
	 * @param values @see ca.neo.util.TimeSeries#getValues()
	 * @param units @see ca.neo.util.TimeSeries#getUnits()
	 * @param labels @see ca.neo.util.TimeSeries#getLabels()
	 */	 
	public TimeSeriesImpl(float[] times, float[][] values, Units[] units, String[] labels) {		
		init(times, values, units, labels);
	}
	
	public TimeSeriesImpl(Configuration properties) throws StructuralException {
		if (properties.getPropertyNames().contains(DIMENSION_PROPERTY)) { //from user
			int dim = ((Integer) ConfigUtil.get(properties, DIMENSION_PROPERTY, Integer.class)).intValue();
			int length = ((Integer) ConfigUtil.get(properties, LENGTH_PROPERTY, Integer.class)).intValue();
			init(new float[length], MU.zero(length, dim), Units.uniform(Units.UNK, dim), getDefaultLabels(dim));
		} else { //from file
			float[] times = ((float[]) ConfigUtil.get(properties, TIMES_PROPERTY, float[].class));
			float[][] values = ((float[][]) ConfigUtil.get(properties, VALUES_PROPERTY, float[][].class));			
			Units[] units = new Units[((ListProperty) properties.getProperty(UNITS_PROPERTY)).getNumValues()];
			String[] labels = new String[units.length];
			for (int i = 0; i < units.length; i++) {
				units[i] = (Units) ((ListProperty) properties.getProperty(UNITS_PROPERTY)).getValue(i);
				labels[i] = (String) ((ListProperty) properties.getProperty(LABELS_PROPERTY)).getValue(i);
			}
			init(times, values, units, labels);
		}
	}
	
	private void init(float[] times, float[][] values, Units[] units, String[] labels) {
		checkDimensions(times, values, units);
		
		myTimes = times;
		myValues = values;
		myUnits = units;
		myLabels = labels;		
		
		myConfiguration = new ConfigurationImpl(this);
		Property tp = new SingleValuedPropertyImpl(myConfiguration, TIMES_PROPERTY, float[].class, true) {
			@Override
			public void setValue(Object value) throws StructuralException {
				setTimes((float[]) value);
			}
		};
		myConfiguration.defineProperty(tp);

		Property vp = new SingleValuedPropertyImpl(myConfiguration, VALUES_PROPERTY, float[][].class, true) {
			@Override
			public void setValue(Object value) throws StructuralException {
				setValues((float[][]) value);
			}
		};
		myConfiguration.defineProperty(vp);

		Property up = new FixedCardinalityProperty(myConfiguration, UNITS_PROPERTY, Units.class, true) {
			@Override
			public Object doGetValue(int index) throws StructuralException {
				return getUnits()[index];
			}

			@Override
			public void doSetValue(int index, Object value) throws IndexOutOfBoundsException, StructuralException {
				setUnits(index, (Units) value);
			}

			@Override
			public int getNumValues() {
				return getUnits().length;
			}
		};
		myConfiguration.defineProperty(up);
		
		Property lp = new FixedCardinalityProperty(myConfiguration, LABELS_PROPERTY, String.class, true) {
			@Override
			public Object doGetValue(int index) throws StructuralException {
				return getLabels()[index];
			}

			@Override
			public void doSetValue(int index, Object value) throws IndexOutOfBoundsException, StructuralException {
				setLabel(index, (String) value);
			}

			@Override
			public int getNumValues() {
				return getLabels().length;
			}
		};
		myConfiguration.defineProperty(lp);
	}
	
	/**
	 * @return Properties for contruction in UI
	 */
	public static Configuration getUserConstructionTemplate() {
		ConfigurationImpl properties = new ConfigurationImpl(null);
		properties.defineTemplateProperty(DIMENSION_PROPERTY, Integer.class, new Integer(1));
		properties.defineTemplateProperty(LENGTH_PROPERTY, Integer.class, new Integer(1));
		return properties;
	}
	
	/**
	 * @return Properties for contruction from file 
	 */
	public static Configuration getConstructionTemplate() {
		ConfigurationImpl properties = new ConfigurationImpl(null);
		properties.defineTemplateProperty(TIMES_PROPERTY, float[].class, new float[0]);
		properties.defineTemplateProperty(VALUES_PROPERTY, float[][].class, new float[0][]);
//		properties.defineTemplateProperty(UNITS_PROPERTY, Units.class, true, false);
//		properties.defineTemplateProperty(LABELS_PROPERTY, String.class, true, false);
		return properties;
	}
	
	/**
	 * @see ca.neo.config.Configurable#getConfiguration()
	 */
	public Configuration getConfiguration() {
		return myConfiguration;
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
	
	private void setTimes(float[] times) {
		myTimes = times;
	}

	/**
	 * @see ca.neo.util.TimeSeries1D#getValues()
	 */
	public float[][] getValues() {
		return myValues;
	}
	
	private void setValues(float[][] values) {
		myValues = values;
	}

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
