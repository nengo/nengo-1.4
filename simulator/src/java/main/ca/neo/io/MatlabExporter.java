package ca.neo.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;

import ca.neo.plot.Plotter;
import ca.neo.util.MU;
import ca.neo.util.SpikePattern;
import ca.neo.util.TimeSeries;

/**
 * <p>A tool for exporting data to Matlab .mat files. Use like this:</p> 
 * 
 * <p><code>
 * MatlabExport me = new MatlabExport();<br>
 * me.add("series1", series1);<br>
 * ... <br>
 * me.add("series1", series1);<br>
 * me.write(new File("c:\\foo.mat"));<br> 
 * </code></p>
 *  
 * @author Bryan Tripp
 */
public class MatlabExporter {

	private Map<String, MLArray> myData;
	
	public MatlabExporter() {
		myData = new HashMap<String, MLArray>(10);
	}
	
	/**
	 * @param name Matlab variable name 
	 * @param data Data to be stored in Matlab variable 
	 */
	public void add(String name, TimeSeries data) {
		add(name+"_time", new float[][]{data.getTimes()});
		add(name, data.getValues());
	}
	
	/**
	 * Filters TimeSeries data with given time constant (this is usually a good 
	 * idea for spike output, which is a sum of impulses).
	 * 
	 * TODO: this filter is prohibitively slow for large datasets
	 * 
	 * @param name Matlab variable name 
	 * @param data Data to be stored in Matlab variable 
	 * @param tau Time constant of filter to apply to data
	 */
	public void add(String name, TimeSeries data, float tau) {
		TimeSeries filtered = Plotter.filter(data, tau);
		add(name+"_time", new float[][]{filtered.getTimes()});
		add(name+"_data", filtered.getValues());		
	}
	
	/**
	 * @param name Matlab variable name
	 * @param pattern Spike times for a group of neurons
	 */
	public void add(String name, SpikePattern pattern) {
		int n = pattern.getNumNeurons();
		int maxSpikes = 0;
		for (int i = 0; i < n; i++) {
			float[] times = pattern.getSpikeTimes(i);
			if (times.length > maxSpikes) maxSpikes = times.length;
		}
		float[][] timesMatrix = new float[n][]; 
		for (int i = 0; i < n; i++) {
			timesMatrix[i] = new float[maxSpikes];
			float[] times = pattern.getSpikeTimes(i);
			System.arraycopy(times, 0, timesMatrix[i], 0, times.length);
		}
		add(name, timesMatrix);
	}
	
	/**
	 * @param name Matlab variable name
	 * @param data A matrix
	 */
	public void add(String name, float[][] data) {
		if (!MU.isMatrix(data)) {
			throw new IllegalArgumentException("Data must be a matrix (same number of columns in each row)");
		}
		MLDouble mld = new MLDouble(name, MU.convert(data));	
		myData.put(name, mld);
	}
	
	/**
	 * Clears all variables 
	 */
	public void removeAll() {
		myData.clear();
	}
		
	/**
	 * Writes to given destination the data that have been added to this exporter. 
	 * 
	 * @param destination File to which data are to be written (should have extension .mat)
	 * @throws IOException 
	 */
	public void write(File destination) throws IOException {
		new MatFileWriter(destination, myData.values());
	}

}
