/*
 * Created on 14-Nov-07
 */
package ca.neo.util.impl;

import ca.neo.model.neuron.Neuron;
import ca.neo.util.SpikePattern;
import ca.neo.util.TimeSeries;

public class DataUtils {

	public static TimeSeries filter(TimeSeries series, float tau) {
		return null;
	}
	
	public static TimeSeries extractDimension(TimeSeries series, int dim) {
		return null;
	}
	
	public static TimeSeries subsample(TimeSeries series, int maxPoints) {
		//TODO: sample, interpolate, or take mean? 
		return null;
	}
	
	public static SpikePattern subset(SpikePattern pattern, int start, int interval, int end) {
		return null;
	}
	
	public static SpikePattern subset(SpikePattern pattern, float proportion) {
		return null;
	}
	
	public static SpikePattern subset(SpikePattern pattern, int[] indices) {
		return null;
	}
	
	public static SpikePattern sort(SpikePattern pattern, Neuron[] neurons) {
		return null;
	}

}
