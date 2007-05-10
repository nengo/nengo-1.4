/*
 * Created on 15-Jun-2006
 */
package ca.neo.plot;

import ca.neo.dynamics.Integrator;
import ca.neo.dynamics.impl.EulerIntegrator;
import ca.neo.dynamics.impl.LTISystem;
import ca.neo.dynamics.impl.SimpleLTISystem;
import ca.neo.math.Function;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.plot.impl.DefaultPlotter;
import ca.neo.util.SpikePattern;
import ca.neo.util.TimeSeries;

/** 
 * Factory for frequently-used plots. 
 * 
 * @author Bryan Tripp
 */
public abstract class Plotter {
	
	private static Plotter ourInstance;
	
	private int myOpenPlots;
	
	private synchronized static Plotter getInstance() {
		if (ourInstance == null) {
			//TODO: this can be made configurable if we get more plotters
			ourInstance = new DefaultPlotter(); 
		}
		
		return ourInstance;
	}
	
	/**
	 * Plotters should call this method when they are opening a new plot
	 */
	public void openingPlot() {
		myOpenPlots++;
	}
	
	/**
	 * Plots should call this method when they are closed by the user. If there are 
	 * no plots remaining, the JVM is closed.  
	 */
	public void closingPlot() {
		myOpenPlots--;
		
		if (myOpenPlots <= 0) {
			System.exit(0); //we'll want to turn this off if running in a UI
		}
	}
	
	
	/**
	 * Static convenience method for producing a TimeSeries plot.
	 *  
	 * @param series TimeSeries to plot
	 * @param title Plot title
	 */
	public static void plot(TimeSeries series, String title) {
		getInstance().doPlot(series, title);
	}
	
	/**
	 * As plot(TimeSeries) but series is filtered before plotting. This is useful when plotting 
	 * NEFEnsemble output (which may consist of spikes) in a manner more similar to the way it would 
	 * appear within post-synaptic neurons. 
	 * 
	 * @param series TimeSeries to plot
	 * @param tauFilter Time constant of display filter (s) 
	 * @param title Plot title
	 */
	public static void plot(TimeSeries series, float tauFilter, String title) {
		series = filter(series, tauFilter);
		getInstance().doPlot(series, title);
	}
	
	/**
	 * @param series A TimeSeries to which to apply a 1-D linear filter
	 * @param tauFilter Filter time constant
	 * @return Filtered TimeSeries
	 */
	public static TimeSeries filter(TimeSeries series, float tauFilter) {
		Integrator integrator = new EulerIntegrator(.0005f);
		
		int dim = series.getDimension();
		float[] A = new float[dim];
		float[][] B = new float[dim][];
		float[][] C = new float[dim][];
		for (int i = 0; i < dim; i++) {
			A[i] = -1f / tauFilter;
			B[i] = new float[dim];
			B[i][i] = 1f;
			C[i] = new float[dim];
			C[i][i] = 1f / tauFilter;
		}		
		LTISystem filter = new SimpleLTISystem(A, B, C, new float[dim], series.getUnits());
		
		return integrator.integrate(filter, series);		
	}

	/**
	 * Plots ideal and actual TimeSeries' together. 
	 *  
	 * @param ideal Ideal time series 
	 * @param actual Actual time series
	 * @param title Plot title
	 */
	public static void plot(TimeSeries ideal, TimeSeries actual, String title) {
		getInstance().doPlot(ideal, actual, title);
	}

	/**
	 * Plots ideal and actual TimeSeries' together, with each series filtered before plotting. 
	 * 
	 * @param ideal Ideal time series 
	 * @param actual Actual time series
	 * @param tauFilter Time constant of display filter (s) 
	 * @param title Plot title
	 */
	public static void plot(TimeSeries ideal, TimeSeries actual, float tauFilter, String title) {
		//ideal = filter(ideal, tauFilter);
		actual = filter(actual, tauFilter);
		getInstance().doPlot(ideal, actual, title);
	}
	
	/**
	 * @param series TimeSeries to plot
	 * @param title Plot title
	 */
	public abstract void doPlot(TimeSeries series, String title);
	
	/**
	 * @param ideal Ideal time series 
	 * @param actual Actual time series 
	 * @param title Plot title
	 */
	public abstract void doPlot(TimeSeries ideal, TimeSeries actual, String title);
	
	/**
	 * Static convenience method for producing a decoding error plot of an NEFEnsemble origin. 
	 * 
	 * @param ensemble NEFEnsemble from which origin arises
	 * @param origin Name of origin (must be a DecodedOrigin, not one derived from a combination of 
	 * 		neuron origins)
	 */
	public static void plot(NEFEnsemble ensemble, String origin) {
		getInstance().doPlot(ensemble, origin);
	}
	
	/**
	 * @param ensemble NEFEnsemble from which origin arises
	 * @param origin Name of origin (must be a DecodedOrigin, not one derived from a combination of 
	 * 		neuron origins)
	 */
	public abstract void doPlot(NEFEnsemble ensemble, String origin);
	
	/**
	 * Static convenience method for producing a plot of CONSTANT_RATE responses over range 
	 * of inputs. 
	 *  
	 * @param ensemble An NEFEnsemble  
	 */
	public static void plot(NEFEnsemble ensemble) {
		getInstance().doPlot(ensemble);
	}
	
	/**
	 * @param ensemble An NEFEnsemble  
	 */
	public abstract void doPlot(NEFEnsemble ensemble);
	
	/**
	 * Static convenience method for plotting a spike raster. 
	 * 
	 * @param pattern SpikePattern to plot
	 */
	public static void plot(SpikePattern pattern) {
		getInstance().doPlot(pattern);
	}
	
	/**
	 * @param pattern A SpikePattern for which to plot a raster
	 */
	public abstract void doPlot(SpikePattern pattern);

	/**
	 * Static convenience method for plotting a Function. 
	 * 
	 * @param function Function to plot
	 * @param start Minimum of input range 
	 * @param increment Size of incrememnt along input range 
	 * @param end Maximum of input range
	 * @param title Display title of plot
	 */
	public static void plot(Function function, float start, float increment, float end, String title) {
		getInstance().doPlot(function, start, increment, end, title);
	}
	
	/**
	 * @param function Function to plot
	 * @param start Minimum of input range 
	 * @param increment Size of incrememnt along input range 
	 * @param end Maximum of input range
	 * @param title Display title of plot
	 */
	public abstract void doPlot(Function function, float start, float increment, float end, String title);
	
	/**
	 * Static convenience method for plotting a vector. 
	 * 
	 * @param vector Vector of points to plot
	 * @param title Display title of plot
	 */
	public static void plot(float[] vector, String title) {
		getInstance().doPlot(vector, title);
	}
	
	/**
	 * @param vector Vector of points to plot
	 * @param title Display title of plot
	 */
	public abstract void doPlot(float[] vector, String title);
}
