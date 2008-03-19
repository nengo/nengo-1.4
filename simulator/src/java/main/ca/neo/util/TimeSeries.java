/*
 * Created on May 4, 2006
 */
package ca.neo.util;

import java.io.Serializable;

import ca.neo.model.Units;

/**
 * A series of vector values at ordered points in time. 
 * 
 * @author Bryan Tripp
 */
public interface TimeSeries extends Serializable, Cloneable {

	/**
	 * @return Name of the TimeSeries
	 */
	public String getName();

	/**
	 * @return Times for which values are available
	 */
	public float[] getTimes();
	
	/**
	 * @return dimension of vector values 
	 */
	public int getDimension();
	
	/**
	 * @return Values at getTimes(). Each value is a vector of size getDimension() 
	 */
	public float[][] getValues();
	
	/**
	 * @return Units in which values in each dimension are expressed (length 
	 * 		equals getDimension()) 
	 */	
	public Units[] getUnits();
	
	/**
	 * @return Name of each series (numbered by default)
	 */
	public String[] getLabels();
	
	public TimeSeries clone() throws CloneNotSupportedException;
		
}
