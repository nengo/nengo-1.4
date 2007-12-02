/*
 * Created on 15-Jun-2006
 */
package ca.neo.plot;

import java.util.ArrayList;
import java.util.List;

import ca.neo.model.Units;
import ca.neo.util.MU;
import ca.neo.util.SpikePattern;
import ca.neo.util.TimeSeries;
import ca.neo.util.TimeSeries1D;
import ca.neo.util.impl.SpikePatternImpl;
import ca.neo.util.impl.TimeSeries1DImpl;
import ca.neo.util.impl.TimeSeriesImpl;

/**
 * Functional test of Plotter
 * @author Bryan Tripp
 */
public class PlotterFunctional {

	public static void basicTest() {
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
	
	public static void multiTest() {
		List<TimeSeries> series = new ArrayList<TimeSeries>(10);
		float[] times = MU.makeVector(1, 1, 10);
		float[][] values1 = new float[][]{MU.makeVector(.1f, .1f, 1), MU.makeVector(.2f, .1f, 1.1f)};
		float[][] values2 = new float[][]{MU.makeVector(1.1f, .1f, 2), MU.makeVector(1.2f, .1f, 2.1f)};
		float[][] values3 = new float[][]{MU.makeVector(2.1f, .1f, 3), MU.makeVector(2.2f, .1f, 3.1f)};
		Units[] units = Units.uniform(Units.UNK, 2);
		series.add(new TimeSeriesImpl(times, MU.transpose(values1), units));
		series.add(new TimeSeriesImpl(times, MU.transpose(values2), units));
		series.add(new TimeSeriesImpl(times, MU.transpose(values3), units));
		
		List<SpikePattern> patterns = new ArrayList<SpikePattern>(10);
		SpikePatternImpl p1 = new SpikePatternImpl(2);
		p1.addSpike(0, 1);
		p1.addSpike(1, 2);
		patterns.add(p1);
		SpikePatternImpl p2 = new SpikePatternImpl(3);
		p2.addSpike(0, 5);
		p2.addSpike(1, 6);
		p2.addSpike(2, 7);
		patterns.add(p2);
		
		Plotter.plot(series, patterns, "multi series");
	}
	
	public static void main(String[] args) {
		multiTest();
	}

}
