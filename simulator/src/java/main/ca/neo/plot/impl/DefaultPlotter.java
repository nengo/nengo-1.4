/*
 * Created on 15-Jun-2006
 */
package ca.neo.plot.impl;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import ca.neo.math.Function;
import ca.neo.model.Origin;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFNode;
import ca.neo.model.nef.impl.DecodedOrigin;
import ca.neo.model.neuron.Neuron;
import ca.neo.plot.Plotter;
import ca.neo.util.SpikePattern;
import ca.neo.util.TimeSeries;
import ca.neo.util.TimeSeries1D;

/**
 * Default Plotter implementation. 
 * 
 * @author Bryan Tripp
 */
public class DefaultPlotter extends Plotter {

	/**
	 * @see ca.neo.plot.Plotter#doPlot(ca.neo.util.TimeSeries, java.lang.String)
	 */
	public void doPlot(TimeSeries series, String title) {
		XYSeriesCollection dataset = getDataset(series);
		
		JFreeChart chart = ChartFactory.createXYLineChart(
				title,
				"Time (s)", 
				"", 
				dataset, 
				PlotOrientation.VERTICAL, 
				(series.getDimension() < 10), false, false
		);
		
		showChart(chart, "Time Series Plot");
	}
	
	/**
	 * @see ca.neo.plot.Plotter#doPlot(ca.neo.util.TimeSeries, ca.neo.util.TimeSeries, java.lang.String)
	 */
	public void doPlot(TimeSeries ideal, TimeSeries actual, String title) {
		XYSeriesCollection idealDataset = getDataset(ideal);
		XYSeriesCollection actualDataset = getDataset(actual);
		
		JFreeChart chart = ChartFactory.createXYLineChart(
				title,
				"Time (s)", 
				"", 
				idealDataset, 
				PlotOrientation.VERTICAL, 
				false, false, false
		);

		XYPlot plot = (XYPlot) chart.getPlot();		
		plot.setDataset(1, actualDataset);

		XYLineAndShapeRenderer idealRenderer = new XYLineAndShapeRenderer(true, false);
		idealRenderer.setDrawSeriesLineAsPath(true);
		idealRenderer.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10f, new float[]{10f, 10f}, 0f));
		plot.setRenderer(plot.indexOf(idealDataset), idealRenderer);

		XYLineAndShapeRenderer actualRenderer = new XYLineAndShapeRenderer(true, false);
		actualRenderer.setDrawSeriesLineAsPath(true);
		//idealRenderer.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10f, new float[]{10f, 10f}, 0f));
		plot.setRenderer(plot.indexOf(actualDataset), actualRenderer);

		showChart(chart, "Time Series Plot");
	}
	
	private XYSeriesCollection getDataset(TimeSeries series) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		float[] times = series.getTimes();
		
		if (times.length > 0) {
			if (series instanceof TimeSeries1D) {
				XYSeries xy = new XYSeries(series.getLabels()[0]);
				
				float[] values = ((TimeSeries1D) series).getValues1D();
				for (int i = 0; i < values.length; i++) {
					xy.add(times[i], values[i]);
				}
				
				dataset.addSeries(xy);
			} else {
				float[][] values = series.getValues();
				
				for (int j = 0; j < values[0].length; j++) {
					XYSeries xy = new XYSeries(series.getLabels()[j]);
					
					for (int i = 0; i < values.length; i++) {
						xy.add(times[i], values[i][j]);
					}
					
					dataset.addSeries(xy);
				}
			}
		}

		return dataset;
	}

	/**
	 * @see ca.neo.plot.Plotter#doPlot(ca.neo.model.nef.NEFEnsemble, java.lang.String)
	 */
	public void doPlot(NEFEnsemble ensemble, String name) {
		try {
			Origin o = ensemble.getOrigin(name);
			
			if ( !(o instanceof DecodedOrigin) ) {
				throw new RuntimeException("Can't plot origin error: Origin must be a DecodedOrigin");
			}
			
			DecodedOrigin origin = (DecodedOrigin) o;
			
			if (ensemble.getDimension() != 1) {
				throw new RuntimeException("Distortion error can not be plotted for multi-dimensional NEFEnsembles");
			}
			
			float[][] encoders = ensemble.getEncoders();

			float[] x = new float[101]; 
			float[][] idealOutput = new float[x.length][];
			float[][] actualOutput = new float[x.length][];

			NEFNode[] nodes = (NEFNode[]) ensemble.getNodes();
			
			SimulationMode mode = ensemble.getMode();
			for (int i = 0; i < x.length; i++) {
				x[i] = -1f + (float) i * (2f / (float) x.length);
								
				ensemble.setMode(SimulationMode.CONSTANT_RATE);
				for (int j = 0; j < nodes.length; j++) {
					((NEFNode) nodes[j]).setRadialInput(x[i]*encoders[j][0]);
					nodes[j].run(0f, 0f);					
				}
				origin.run(null, 0f, 1f);
				actualOutput[i] = ((RealOutput) origin.getValues()).getValues();
				
				ensemble.setMode(SimulationMode.DIRECT);
				origin.run(new float[]{x[i]}, 0f, 1f);
				idealOutput[i] = ((RealOutput) origin.getValues()).getValues();
			}
			ensemble.setMode(mode);
			
			//one plot per output dimension ... 
			for (int i = 0; i < idealOutput[0].length; i++) {
				doPlot(x, idealOutput, actualOutput, i);
			}			
			
		} catch (StructuralException e) {
			throw new RuntimeException("Can't plot origin error", e);
		} catch (SimulationException e) {
			throw new RuntimeException("Can't plot origin error", e);
		}
	}
	
	//used by origin plot
	private void doPlot(float[] x, float[][] ideal, float[][] actual, int dim) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		XYSeries idealSeries = new XYSeries("Ideal");
		for (int i = 0; i < x.length; i++) {
			idealSeries.add(x[i], ideal[i][dim]);
		}
		dataset.addSeries(idealSeries);
		
		XYSeries actualSeries = new XYSeries("Actual");
		for (int i = 0; i < x.length; i++) {
			actualSeries.add(x[i], actual[i][dim]);
		}
		dataset.addSeries(actualSeries);
		
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Distortion",
				"X", 
				"Estimate", 
				dataset, 
				PlotOrientation.VERTICAL, 
				true, false, false
		);
		
		XYSeries errorSeries = new XYSeries("Error");
		for (int i = 0; i < x.length; i++) {
			errorSeries.add(x[i], actual[i][dim] - ideal[i][dim]);
		}
		XYSeriesCollection errorDataset = new XYSeriesCollection();
		errorDataset.addSeries(errorSeries);
		NumberAxis errorAxis = new NumberAxis("Error");
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setRangeAxis(1, errorAxis);
		plot.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);
		plot.setDataset(1, errorDataset);
		plot.mapDatasetToRangeAxis(1, 1);
		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
		plot.setRenderer(1, renderer);
		
		showChart(chart, "Distortion Error Plot");
	}

	/**
	 * @see ca.neo.plot.Plotter#doPlot(ca.neo.model.nef.NEFEnsemble)
	 */
	public void doPlot(NEFEnsemble ensemble) {
		float[][] encoders = ensemble.getEncoders();
		NEFNode[] nodes = (NEFNode[]) ensemble.getNodes();

		float[] x = new float[101];
		for (int i = 0; i < x.length; i++) {
			x[i] = -1f + (float) i * (2f / (float) x.length);
		}
		
		SimulationMode mode = ensemble.getMode();
		ensemble.setMode(SimulationMode.CONSTANT_RATE);
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		for (int i = 0; i < nodes.length; i++) {
			XYSeries series = new XYSeries("Neuron " + i);
			
			for (int j = 0; j < x.length; j++) {
				//plot along preferred direction for multi-dimensional ensembles
				float radialInput = (ensemble.getDimension() == 1) ? x[j]*encoders[i][0] : x[j]; 
				
				((NEFNode) nodes[i]).setRadialInput(radialInput);
				try {
					nodes[i].run(0f, 0f);
					RealOutput output = (RealOutput) nodes[i].getOrigin(Neuron.AXON).getValues();
					series.add(x[j], output.getValues()[0]);
				} catch (SimulationException e) {
					throw new RuntimeException("Can't plot activities: error running neurons", e);
				} catch (ClassCastException e) {
					throw new RuntimeException("Can't plot activities: neurons producing spike output", e);					
				} catch (StructuralException e) {
					throw new RuntimeException("Can't plot activities: error running neurons", e);
				}
			}
			
			dataset.addSeries(series);
		}
		
		ensemble.setMode(mode);
		
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Activities",
				"X", 
				"Firing Rate (spikes/s)", 
				dataset, 
				PlotOrientation.VERTICAL, 
				false, false, false
		);

		showChart(chart, "Activities Plot");
	}

	/**
	 * @see ca.neo.plot.Plotter#doPlot(ca.neo.util.SpikePattern)
	 */
	public void doPlot(SpikePattern pattern) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		for (int i = 0; i < pattern.getNumNeurons(); i++) {
			XYSeries series = new XYSeries("Neuron " + i);
			float[] spikes = pattern.getSpikeTimes(i);
			for (int j = 0; j < spikes.length; j++) {
				series.add(spikes[j], i);
			}
			dataset.addSeries(series);
		}		
		
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Spike Raster",
				"Time (s)", 
				"Neuron #", 
				dataset, 
				PlotOrientation.VERTICAL, 
				false, false, false
		);

		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
		renderer.setShape(ShapeUtilities.createDiamond(1f));
		renderer.setShapesVisible(true);
		renderer.setShapesFilled(true);
		renderer.setLinesVisible(false);
		renderer.setPaint(Color.BLACK);

		showChart(chart, "Spike Raster");
	}

	/**
	 * @see ca.neo.plot.Plotter#doPlot(ca.neo.math.Function, float, float, float, String)
	 */
	public void doPlot(Function function, float start, float increment, float end, String title) {
		if (function.getDimension() > 2) {
			throw new IllegalArgumentException("Only 1-D and 2-D functions can be plotted with this method");
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		if (function.getDimension() == 1) {
			XYSeries series = new XYSeries("Function");

			float x = start;
			while (x <= end) {
				float y = function.map(new float[]{x});
				series.add(x, y);
				x += increment;
			}
			
			dataset.addSeries(series);
		} else if (function.getDimension() == 2) {
			float increment2 = increment * 10f;
			
			float x2 = start;
			while (x2 <= end) {
				XYSeries series = new XYSeries(""+x2);
				float x = start;
				while (x <= end) {
					float y = function.map(new float[]{x, x2});
					series.add(x, y);
					x += increment;
				}				
				dataset.addSeries(series);
				x2 += increment2;
			}
		}

		JFreeChart chart = ChartFactory.createXYLineChart(
				"Function",
				"Input", 
				"Output", 
				dataset, 
				PlotOrientation.VERTICAL, 
				false, false, false
		);
		
		showChart(chart, title);
		
	}

	/**
	 * @see ca.neo.plot.Plotter#doPlot(float[], String)
	 */
	public void doPlot(float[] vector, String title) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries("Vector");

		for (int i = 0; i < vector.length; i++) {
			series.add(i, vector[i]); 
		}

		dataset.addSeries(series);

		JFreeChart chart = ChartFactory.createXYLineChart(
				"Vector",
				"Index", 
				"Value", 
				dataset, 
				PlotOrientation.VERTICAL, 
				false, false, false
		);
		
		showChart(chart, title);
	}

	/**
	 * @see ca.neo.plot.Plotter#doPlot(float[], float[], java.lang.String)
	 */
	public void doPlot(float[] domain, float[] vector, String title) {
		if (domain.length < vector.length) {
			throw new IllegalArgumentException("Not enough domain points (" + domain.length + "given; " + vector.length + "needed)");
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries("Vector");

		for (int i = 0; i < vector.length; i++) {
			series.add(domain[i], vector[i]); 
		}

		dataset.addSeries(series);

		JFreeChart chart = ChartFactory.createXYLineChart(
				"Vector",
				"Index", 
				"Value", 
				dataset, 
				PlotOrientation.VERTICAL, 
				false, false, false
		);
		
		showChart(chart, title);
	}
	
	//shows a chart in a new window 
	private void showChart(JFreeChart chart, String title) {
		JPanel panel = new ChartPanel(chart);

		openingPlot();
		JFrame frame = new JFrame(title);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		final Plotter plotter = this;
        frame.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		  plotter.closingPlot();
            }
        });

        frame.pack();
        frame.setVisible(true);		
	}

}
