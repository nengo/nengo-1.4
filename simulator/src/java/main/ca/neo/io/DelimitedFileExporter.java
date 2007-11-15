/*
 * Created on 14-Nov-07
 */
package ca.neo.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import ca.neo.plot.Plotter;
import ca.neo.util.MU;
import ca.neo.util.SpikePattern;
import ca.neo.util.TimeSeries;

/**
 * Exports TimeSeries, SpikePattern, and float[][] data to delimited text files. 
 *  
 * @author Bryan Tripp
 */
public class DelimitedFileExporter {

	private String myColumnDelim;
	private String myRowDelim;
	
	/**
	 * Uses default column delimiter ", " and row delimiter "\r\n". 
	 */
	public DelimitedFileExporter() {
		myColumnDelim = ", ";
		myRowDelim = "\r\n";
	}

	/**
	 * @param columnDelim String used to delimit items within a matrix row 
	 * @param rowDelim String used to delimit rows of a matrix
	 */
	public DelimitedFileExporter(String columnDelim, String rowDelim) {
		myColumnDelim = columnDelim;
		myRowDelim = rowDelim;
	}
	
	/**
	 * Exports a TimeSeries with times in the first column and data from each dimension in subsequent columns. 
	 * 
	 * @param series TimeSeries to export
	 * @param file File to which to export the TimeSeries 
	 * @throws IOException
	 */
	public void export(TimeSeries series, File file) throws IOException {
		float[][] values = MU.transpose(series.getValues());
		float[][] timesAndValues = new float[values.length + 1][];
		timesAndValues[0] = series.getTimes();
		System.arraycopy(values, 0, timesAndValues, 1, values.length);
		
		export(MU.transpose(timesAndValues), file);
	}
	
	/**
	 * Exports a TimeSeries as a matrix with times in the first column and data from each dimension 
	 * in subsequent rows. 
	 * 
	 * @param series TimeSeries to export
	 * @param file File to which to export the TimeSeries
	 * @param tau Time constant with which to filter data 
	 * @throws IOException
	 */
	public void export(TimeSeries series, File file, float tau) throws IOException {
		TimeSeries filtered = Plotter.filter(series, tau);
		
		float[][] values = MU.transpose(filtered.getValues());		
		float[][] timesAndValues = new float[values.length + 1][];
		timesAndValues[0] = filtered.getTimes();
		System.arraycopy(values, 0, timesAndValues, 1, values.length);
		
		export(MU.transpose(timesAndValues), file);
	}
	
	/**
	 * Exports a SpikePattern as a matrix with spikes times of each neuron in a different row.
	 *  
	 * @param pattern SpikePattern to export
	 * @param file File to which to export the SpikePattern 
	 * @throws IOException
	 */
	public void export(SpikePattern pattern, File file) throws IOException {
		int n = pattern.getNumNeurons();
		float[][] times = new float[n][]; 
		for (int i = 0; i < n; i++) {
			times[i] = pattern.getSpikeTimes(i);
		}
		export(times, file);
	}
	
	/**
	 * Exports a matrix with rows and columns delimited as specified in the constructor. 
	 *   
	 * @param matrix The matrix to export
	 * @param file File to which to export the matrix 
	 * @throws IOException
	 */
	public void export(float[][] matrix, File file) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				writer.write(String.valueOf(matrix[i][j]));
				if (j < matrix[i].length - 1) writer.write(myColumnDelim);
			}
			writer.write(myRowDelim);
		}
		
		writer.flush();
		writer.close();
	}
	
	/**
	 * Imports a delimited file as a matrix. Assumes that rows are delimited as lines, and 
	 * items in a row are delimited with one or more of the following: comma, colon, semicolon, 
	 * space, tab.  
	 *  
	 * @param file File from which to load matrix
	 * @return Matrix from file
	 * @throws IOException
	 */
	public float[][] importAsMatrix(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));

		List<float[]> rows = new ArrayList<float[]>(100);
		for (String line; (line = reader.readLine()) != null; ) {
			StringTokenizer tok = new StringTokenizer(line, ",;: \t", false);
			float[] row = new float[tok.countTokens()];
			for (int i = 0; i < row.length; i++) {
				row[i] = Float.parseFloat(tok.nextToken());
			}
			rows.add(row);
		}
		
		return (float[][]) rows.toArray(new float[0][]);
	}
	
	
}
