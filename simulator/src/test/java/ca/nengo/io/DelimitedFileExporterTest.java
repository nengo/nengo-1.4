package ca.nengo.io;

import ca.nengo.model.Units;
import ca.nengo.plot.Plotter;
import ca.nengo.util.TimeSeries;
import ca.nengo.util.impl.SpikePatternImpl;
import ca.nengo.util.impl.TimeSeriesImpl;
import java.io.File;
import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.Test;

public class DelimitedFileExporterTest {
	private DelimitedFileExporter myExporter = new DelimitedFileExporter();
	private File myFile= new File("./delimited_file_exporter_test.txt");
	
	@Test
	public void testExportTimeSeriesFile() throws IOException {
		TimeSeries ts = new TimeSeriesImpl(new float[]{1, 2, 3}, 
				new float[][]{new float[]{4, 7}, new float[]{5, 8}, new float[]{6, 9}}, 
				Units.uniform(Units.UNK, 2));
		myExporter.export(ts, myFile);
		float[][] imported = myExporter.importAsMatrix(myFile);
		assertEquals(imported[0][0], 1, .0001f);
		assertEquals(imported[1][0], 2, .0001f);
		assertEquals(imported[0][1], 4, .0001f);
		assertEquals(imported[0][2], 7, .0001f);
	}

	@Test
	public void testExportTimeSeriesFileFloat() throws IOException {
		TimeSeries ts = new TimeSeriesImpl(new float[]{1, 2, 3}, 
				new float[][]{new float[]{4, 7}, new float[]{5, 8}, new float[]{6, 9}}, 
				Units.uniform(Units.UNK, 2));
		myExporter.export(ts, myFile, .5f);
		TimeSeries filtered = Plotter.filter(ts, .5f);
		float[][] imported = myExporter.importAsMatrix(myFile);
		assertEquals(imported[0][0], filtered.getTimes()[0], .0001f);
		assertEquals(imported[1][0], filtered.getTimes()[1], .0001f);
		assertEquals(imported[0][1], filtered.getValues()[0][0], .0001f);
		assertEquals(imported[0][2], filtered.getValues()[0][1], .0001f);
	}

	@Test
	public void testExportSpikePatternFile() throws IOException {
		SpikePatternImpl pattern = new SpikePatternImpl(2);
		pattern.addSpike(0, 1);
		pattern.addSpike(0, 2);
		pattern.addSpike(1, 3);
		pattern.addSpike(1, 4);
		pattern.addSpike(1, 5);

		myExporter.export(pattern, myFile);
		float[][] imported = myExporter.importAsMatrix(myFile);
		assertEquals(imported[0][0], 1, .0001f);
		assertEquals(imported[0][1], 2, .0001f);
		assertEquals(imported[1][0], 3, .0001f);
		assertEquals(imported[1][1], 4, .0001f);
		assertEquals(imported[1][2], 5, .0001f);
	}

	@Test
	public void testExportFloatArrayArrayFile() throws IOException {
		float[][] matrix = new float[][]{new float[]{1}, new float[]{-1.2f, .0000000001f}};
		myExporter.export(matrix, myFile);
		float[][] imported = myExporter.importAsMatrix(myFile);
		assertEquals(matrix[0][0], imported[0][0], .0000000000001f);
		assertEquals(matrix[1][0], imported[1][0], .0000000000001f);
		assertEquals(matrix[1][1], imported[1][1], .0000000000001f);
	}
}
