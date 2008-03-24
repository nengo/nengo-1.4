/*
 * Created on 14-May-07
 */
package ca.nengo.math.impl;

import ca.nengo.math.Function;
import ca.nengo.model.Units;
import ca.nengo.plot.Plotter;
import ca.nengo.util.InterpolatorND;
import ca.nengo.util.TimeSeries;
import ca.nengo.util.TimeSeries1D;
import ca.nengo.util.impl.LinearInterpolatorND;
import ca.nengo.util.impl.TimeSeries1DImpl;

/**
 * <p>A Function based on interpolation of a TimeSeries.</p>
 * 
 * <p>A TimeSeriesFunction can be used to apply the results of a simulation as 
 * input to other simulations. </p>
 * 
 * TODO: unit tests
 * TODO: this could be made more efficient for n-D series by wrapping them in 1D, 
 * 		so interpolation is only done as needed
 * 
 * @author Bryan Tripp
 */
public class TimeSeriesFunction extends AbstractFunction {

	private static final long serialVersionUID = 1L;
	
	private int myDimension;
	private TimeSeries myTimeSeries;
	private InterpolatorND myInterpolator;
	
	/**
	 * @param series TimeSeries from which to obtain Function of time 
	 * @param dimension Dimension of series on which to base Function output
	 */
	public TimeSeriesFunction(TimeSeries series, int dimension) {
		super(1);
		setTimeSeries(series);
		setSeriesDimension(dimension);
	}
	
	/**
	 * @return TimeSeries from which to obtain Function of time
	 */
	public TimeSeries getTimeSeries() {
		return myTimeSeries;
	}
	
	/**
	 * @param series TimeSeries from which to obtain Function of time
	 */
	public void setTimeSeries(TimeSeries series) {
		myTimeSeries = series;
		myInterpolator = new LinearInterpolatorND(series);
	}

	/**
	 * @return Dimension of series on which to base Function output
	 */
	public int getSeriesDimension() {
		return myDimension;
	}

	/**
	 * @param dim Dimension of series on which to base Function output
	 */
	public void setSeriesDimension(int dim) {
		if (dim < 0 || dim >= myTimeSeries.getDimension()) {
			throw new IllegalArgumentException("Dimension must be between 0 and " + (myTimeSeries.getDimension() -1));
		}
		myDimension = dim;
	}

	/**
	 * @see ca.nengo.math.impl.AbstractFunction#map(float[])
	 */
	public float map(float[] from) {
		return myInterpolator.interpolate(from[0])[myDimension];
	}

	/**
	 * @param function A 1-dimensional Function
	 * @param start Start time 
	 * @param increment Time step
	 * @param end End time
	 * @param units Units of Function output
	 * @return A TimeSeries consisting of values output by the given function over the given time range 
	 */
	public static TimeSeries1D makeSeries(Function function, float start, float increment, float end, Units units) {
		int steps = (int) Math.ceil((end - start) / increment);
		float[] times = new float[steps];
		float[] values = new float[steps];
		for (int i = 0; i < steps; i++) {
			times[i] = start + (float) i * increment;
			values[i] = function.map(new float[]{times[i]});
		}
		return new TimeSeries1DImpl(times, values, units);
	}
	
	//functional test
	public static void main(String[] args) {
		Function f = new SineFunction(10);
		TimeSeries series = makeSeries(f, 0, .001f, 1, Units.UNK);
		Plotter.plot(f, 0, .001f, 1, "function output");
		Plotter.plot(series, "time series");
		
		Function f2 = new TimeSeriesFunction(series, 0);
		Plotter.plot(f2, 0, .0005f, 1, "derived function");
	}

}
