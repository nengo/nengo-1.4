package ca.neo.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;

import ca.neo.plot.Plotter;
import ca.neo.util.MU;
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

	private Map myData;
	
	public MatlabExporter() {
		myData = new HashMap(10);
	}
	
	/**
	 * @param name Matlab variable name 
	 * @param data Data to be stored in Matlab variable 
	 */
	public void add(String name, TimeSeries data) {
		doAdd(name+"_time", new float[][]{data.getTimes()});
		doAdd(name, data.getValues());
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
		doAdd(name+"_time", new float[][]{filtered.getTimes()});
		doAdd(name+"_data", filtered.getValues());		
	}
	
	private void doAdd(String name, float[][] data) {
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
