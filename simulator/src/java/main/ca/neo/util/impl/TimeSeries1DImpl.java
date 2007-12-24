/*
 * Created on May 4, 2006
 */
package ca.neo.util.impl;

import java.io.Serializable;

import ca.neo.config.ConfigUtil;
import ca.neo.model.Configuration;
import ca.neo.model.StructuralException;
import ca.neo.model.Configuration.Property;
import ca.neo.model.impl.ConfigurationImpl;
import ca.neo.model.Units;
import ca.neo.util.TimeSeries1D;

/**
 * Default implementation of TimeSeries.  
 * 
 * @author Bryan Tripp
 */
public class TimeSeries1DImpl implements TimeSeries1D, Serializable {

	private static final long serialVersionUID = 1L;
	private static final String LENGTH_PROPERTY = "length";
	private static final String TIMES_PROPERTY = "times";
	private static final String VALUES_PROPERTY = "values";
	private static final String UNITS_PROPERTY = "units";
	private static final String LABEL_PROPERTY = "label";	
	
	private float[] myTimes;
	private float[] myValues;
	private Units myUnits;
	private String myLabel;
	private ConfigurationImpl myConfiguration;
	
	/**
	 * @param times @see ca.bpt.cn.util.TimeSeries#getTimes()
	 * @param values @see ca.bpt.cn.util.TimeSeries#getValues()
	 * @param units @see ca.bpt.cn.util.TimeSeries#getUnits()
	 */	 
	public TimeSeries1DImpl(float[] times, float[] values, Units units) {
		init(times, values, units);
	}
	
	public TimeSeries1DImpl(Configuration properties) throws StructuralException {
		if (properties.getPropertyNames().contains(LENGTH_PROPERTY)) { //from user
			int length = ((Integer) ConfigUtil.get(properties, LENGTH_PROPERTY, Integer.class)).intValue();
			init(new float[length], new float[length], Units.UNK);
		} else { //from file
			float[] times = ((float[]) ConfigUtil.get(properties, TIMES_PROPERTY, float[].class));
			float[] values = ((float[]) ConfigUtil.get(properties, VALUES_PROPERTY, float[].class));			
			Units units = (Units) properties.getProperty(UNITS_PROPERTY).getValue();
			init(times, values, units);
			setLabel((String) properties.getProperty(LABEL_PROPERTY).getValue());
		}
	}
	
	/**
	 * @return Properties for contruction in UI
	 */
	public static Configuration getUserConstructionTemplate() {
		ConfigurationImpl properties = new ConfigurationImpl(null);
		properties.defineTemplateProperty(LENGTH_PROPERTY, Integer.class, new Integer(1));
		return properties;
	}
	
	/**
	 * @return Properties for contruction from file 
	 */
	public static Configuration getConstructionTemplate() {
		ConfigurationImpl properties = new ConfigurationImpl(null);
		properties.defineTemplateProperty(TIMES_PROPERTY, float[].class, new float[0]);
		properties.defineTemplateProperty(VALUES_PROPERTY, float[].class, new float[0]);
		properties.defineTemplateProperty(UNITS_PROPERTY, Units.class, Units.UNK);
		properties.defineTemplateProperty(LABEL_PROPERTY, String.class, "label");
		return properties;
	}
	
	private void init(float[] times, float[] values, Units units) {
		if (times.length != values.length) {
			throw new IllegalArgumentException(times.length + " times were given with " + values.length + " values");
		}
		
		this.myTimes = times;
		this.myValues = values;
		this.myUnits = units;
		this.myLabel = "1";		
		
		myConfiguration = new ConfigurationImpl(this);
		Property tp = new ConfigurationImpl.SingleValuedProperty(myConfiguration, TIMES_PROPERTY, float[].class, true) {
			@Override
			public void setValue(Object value) throws StructuralException {
				setTimes((float[]) value);
			}
		};
		myConfiguration.defineProperty(tp);

		Property vp = new ConfigurationImpl.SingleValuedProperty(myConfiguration, VALUES_PROPERTY, float[].class, true) {
			@Override
			public void setValue(Object value) throws StructuralException {
				setValues((float[]) value);
			}
			@Override
			public Object getValue() {
				return myValues;
			}			
		};
		myConfiguration.defineProperty(vp);
		
		Property up = new ConfigurationImpl.SingleValuedProperty(myConfiguration, UNITS_PROPERTY, Units.class, true) {
			@Override
			public Object getValue() {
				return getUnits1D();
			}			
		};
		myConfiguration.defineProperty(up);
		
		Property lp = new ConfigurationImpl.SingleValuedProperty(myConfiguration, LABEL_PROPERTY, String.class, true) {
			@Override
			public Object getValue() {
				return getLabels()[0];
			}
		};
		myConfiguration.defineProperty(lp);
	}

	/**
	 * @see ca.neo.model.Configurable#getConfiguration()
	 */
	public Configuration getConfiguration() {
		return myConfiguration;
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
	
	private void setValues(float[] values) {
		myValues = values;
	}

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
}
