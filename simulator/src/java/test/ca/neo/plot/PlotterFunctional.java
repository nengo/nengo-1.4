/*
 * Created on 15-Jun-2006
 */
package ca.neo.plot;

import ca.neo.model.Units;
import ca.neo.util.TimeSeries;
import ca.neo.util.TimeSeries1D;
import ca.neo.util.impl.TimeSeries1DImpl;
import ca.neo.util.impl.TimeSeriesImpl;

/**
 * Functional test of Plotter
 * @author Bryan Tripp
 */
public class PlotterFunctional {

	public static void main(String[] args) {
		float[] times = new float[]{1f, 2f, 3f, 4f, 5f};
		float[] v1 = new float[]{1f, 2f, 3f, 2f, 1f};
		float[][] v3 = new float[][]{
			new float[]{1f, 2f, 3f}, 
			new float[]{2f, 3f, 4f}, 
			new float[]{3f, 4f, 5f}, 
			new float[]{2f, 3f, 4f}, 
			new float[]{1f, 2f, 3f}			
		};
		
		TimeSeries1D s1 = new TimeSeries1DImpl(times, v1, Units.UNK);		
		Plotter.plot(s1, "test1");
		
		TimeSeries s3 = new TimeSeriesImpl(times, v3, new Units[]{Units.UNK, Units.UNK, Units.UNK}); 
		Plotter.plot(s3, "test2");
	}

}
