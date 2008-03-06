/*
 * Created on May 4, 2006
 */
package ca.neo.util.impl;

import java.io.Serializable;
import java.lang.reflect.Method;

import ca.neo.config.ConfigUtil;
import ca.neo.config.Configuration;
import ca.neo.config.SingleValuedProperty;
import ca.neo.config.impl.ConfigurationImpl;
import ca.neo.config.impl.SingleValuedPropertyImpl;
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
	 * @return Custom Configuration (to more cleanly handle properties in 1D) 
	 */
	public Configuration getConfiguration() {
		ConfigurationImpl result = ConfigUtil.defaultConfiguration(this);
		result.removeProperty("units");
		result.removeProperty("units1D");
		result.removeProperty("values");
		result.removeProperty("values1D");
		result.removeProperty("labels");
		
		try {
			Method unitsGetter = this.getClass().getMethod("getUnits1D", new Class[0]);
			Method unitsSetter = this.getClass().getMethod("setUnits", new Class[]{Units.class});
			result.defineProperty(new SingleValuedPropertyImpl(result, "units", Units.class, unitsGetter, unitsSetter));
			
			Method valuesGetter = this.getClass().getMethod("getValues1D", new Class[0]);
			result.defineProperty(new SingleValuedPropertyImpl(result, "values", float[].class, valuesGetter));

			final Method labelGetter = this.getClass().getMethod("getLabels", new Class[0]);
			Method labelSetter = this.getClass().getMethod("setLabel", new Class[]{String.class});
			SingleValuedProperty labelProp = new SingleValuedPropertyImpl(result, "label", String.class, labelGetter, labelSetter) {

				@Override
				public Object getValue() {
					Object result = null;
					try {
						Object configurable = getConfiguration().getConfigurable();
						String[] labels = (String[]) labelGetter.invoke(configurable, new Object[0]);
						result = labels[0];
					} catch (Exception e) {
						throw new RuntimeException("Can't get label value", e);
					}
					return result;
				}
				
			};
			result.defineProperty(labelProp);
		} catch (SecurityException e) {
			throw new RuntimeException("Can't access getter/setter -- this is a bug", e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Can't access getter/setter -- this is a bug", e);
		}
		return result;
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
	
//	private void setValues(float[] values) {
//		myValues = values;
//	}

	/**
	 * @see ca.neo.util.TimeSeries#getUnits()
	 */
	public Units[] getUnits() {
		return new Units[]{myUnits};
	}
	
	/**
	 * @param units New Units
	 */
	public void setUnits(Units units) {
		myUnits = units;
	}
	
	/**
	 * @see ca.neo.util.TimeSeries#getLabels()
	 */
	public String[] getLabels() {
		return new String[]{myLabel};
	}
	
	/**
	 * @param label New label
	 */
	public void setLabel(String label) {
		myLabel = label;
	}

	@Override
	public TimeSeries1D clone() throws CloneNotSupportedException {
		return (TimeSeries1D) super.clone();
	}
	
}
