/*
 * Created on May 4, 2006
 */
package ca.nengo.util;

import ca.nengo.model.Units;

/**
 * A TimeSeries that consists of 1-dimensional values, with convenience methods 
 * for accessing 1D values and units.   
 * 
 * @author Bryan Tripp
 */
public interface TimeSeries1D extends TimeSeries {

	/**
	 * @return Values at getTimes()
	 */
	public float[] getValues1D();
	
	/**
	 * @return Units in which values are expressed 
	 */	
	public Units getUnits1D();
	
}
