/*
 * Created on 2-Jun-2006
 */
package ca.nengo.util;

import java.io.Serializable;

/**
 * A tool for interpolating within a SCALAR time series (see also 
 * InterpolatorND for vector time series').  
 * 
 * @author Bryan Tripp
 */
public interface Interpolator extends Serializable, Cloneable {

	public void setTimeSeries(TimeSeries1D series);
	
	public float interpolate(float time);
	
	public Interpolator clone() throws CloneNotSupportedException;
	
}
