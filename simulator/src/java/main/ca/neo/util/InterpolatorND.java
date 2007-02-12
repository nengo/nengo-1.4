/*
 * Created on 2-Jun-2006
 */
package ca.neo.util;

import java.io.Serializable;

/**
 * A tool for interpolating within a VECTOR time series (see also 
 * Interpolator for scalar time series').  
 * 
 * @author Bryan Tripp
 */
public interface InterpolatorND extends Serializable {

	public void setTimeSeries(TimeSeries series);
	
	public float[] interpolate(float time);
	
}
