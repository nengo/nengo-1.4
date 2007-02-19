/*
 * Created on 2-Jun-2006
 */
package ca.neo.util.impl;

import ca.neo.util.IndexFinder;
import ca.neo.util.InterpolatorND;
import ca.neo.util.TimeSeries;

/**
 * Interpolates linearly between adjacent values of a vector time series. 
 * 
 * TODO: test
 *   
 * @author Bryan Tripp
 */
public class LinearInterpolatorND implements InterpolatorND {

	private static final long serialVersionUID = 1L;
	
	private TimeSeries mySeries;
	private IndexFinder myFinder;
	private float[] myTimes;
	
	/**
	 * @param series Series to interpolate
	 */
	public LinearInterpolatorND(TimeSeries series) {
		setTimeSeries(series);
	}

	/**
	 * @see ca.neo.util.InterpolatorND#setTimeSeries(ca.neo.util.TimeSeries)
	 */
	public void setTimeSeries(TimeSeries series) {
		mySeries = series;
		myFinder = getFinder(series.getTimes());
		myTimes = series.getTimes();
	}

	/**
	 * @see ca.neo.util.InterpolatorND#interpolate(float)
	 */
	public float[] interpolate(float time) {
		float[] result = null;
		
		if (myTimes[0] >= time) {
			result = mySeries.getValues()[0];
		} else if (myTimes[myTimes.length-1] <= time) {
			result = mySeries.getValues()[myTimes.length-1];
		} else {
			int below = myFinder.findIndexBelow(time);
			
			float prop = (time - myTimes[below]) / (myTimes[below+1] - myTimes[below]);
			float[] low = mySeries.getValues()[below];
			float[] high = mySeries.getValues()[below+1];
			
			result = new float[low.length];
			for (int i = 0; i < low.length; i++) {
				result[i] = low[i] + prop * (high[i] - low[i]);
			}
		}
		
		return result;
	}
	
	/**
	 * Uses a StatefulIndexFinder by default. Override to change this. 
	 *  
	 * @param times Times of time series 
	 * @return IndexFinder on times 
	 */
	public IndexFinder getFinder(float[] times) {
		return new StatefulIndexFinder(times);
	}

}
