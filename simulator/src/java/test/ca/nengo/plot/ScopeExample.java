/*
 * Created on 11-Feb-08
 */
package ca.nengo.plot;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Color;
import java.awt.geom.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.*;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ca.nengo.util.*;
import ca.nengo.math.Function;
import ca.nengo.math.FunctionBasis;
import ca.nengo.math.impl.FunctionBasisImpl;
import ca.nengo.math.impl.GaussianPDF;
import ca.nengo.math.impl.PostfixFunction;
import ca.nengo.math.impl.SigmoidFunction;
import ca.nengo.model.Network;
import ca.nengo.model.SimulationException;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Units;
import ca.nengo.model.impl.FunctionInput;
import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.plot.Plotter;
import ca.nengo.util.Probe;

/**
 * Example data for scope display. 
 * 
 * @author Bryan Tripp
 */
public class ScopeExample {

	public static void main(String[] args) throws Exception {
		
		try {
			Network network = new NetworkImpl();
			
			//this "function input" is Probeable ... 		
			Function f1 = new PostfixFunction("sin(x0)", 1);
			Function f2 = new PostfixFunction("sin(x0)^2", 1);
			String name = "functions of time";
			FunctionInput fi = new FunctionInput(name, new Function[]{f1, f2}, Units.uAcm2);
			network.addNode(fi);
			
			//we can add a probe to it and run the simulator ... 
			Probe p = network.getSimulator().addProbe(name, FunctionInput.STATE_NAME, true);			
			network.run(0, 10);
			
			animPlotVector(p.getData(), "vector plot", true, 30);

			//... and plot the probed data from the simulation ... 
			//Plotter.plot(p.getData(), "function output");
			
			//now here are a couple of function bases ... 
			Function g1 = new GaussianPDF(0, 1);
			Function g2 = new GaussianPDF(0.5f, 1);			
			FunctionBasis gaussianBasis = new FunctionBasisImpl(new Function[]{g1, g2});
			
			//here is a plot of the probed vector X the gaussian basis (value at time 4.5s) ... 			
			gaussianBasis.setCoefficients(p.getData().getValues()[4500]);
			//Plotter.plot(gaussianBasis, -3, .001f, 3, "gaussian basis plot");
			
			
			Function s1 = new SigmoidFunction(0, 1, 0, 1);
			Function s2 = new SigmoidFunction(0.5f, -1, 0, 1);
			FunctionBasis sigmoidBasis = new FunctionBasisImpl(new Function[]{s1, s2});
			
			//here is a plot of the probed vector X the sigmoid basis (value at time 0.5s) ... 						
			sigmoidBasis.setCoefficients(p.getData().getValues()[500]);
			//Plotter.plot(sigmoidBasis, -3, .001f, 3, "sigmoid basis plot");
			
		} catch (StructuralException e) {
			e.printStackTrace();
		} catch (SimulationException e) {
			e.printStackTrace();
		}
	}
	
	private static JFrame makeAnimPlotFrame(String title) throws IOException {
		JFrame r = new JFrame(title);
		Image image = ImageIO.read(ScopeExample.class.getClassLoader().getResource("ca/nengo/plot/spikepattern-grey.png"));
		r.setIconImage(image);
        r.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        r.setVisible(true);
        return r;
	}
	
	private static List<ChartPanel> makeAnimPlotVectorChartPanels(TimeSeries timeSeries, 
			boolean scaleRangAxisConstant, int trailLength) {
        List<ChartPanel> r = new Vector<ChartPanel>();
        float[][] values = timeSeries.getValues();
        final int timeInc = 100;
        for(int curTime=0; curTime<values.length; curTime+=timeInc) {
	        XYSeriesCollection dataset = new XYSeriesCollection();
	        for(int trailPosTime=curTime; 
	        		trailPosTime>=curTime-trailLength*timeInc && trailPosTime>=0; 
	        		trailPosTime-=timeInc) {
				XYSeries series = new XYSeries("trail "+trailPosTime);
				for(int vecDim=0; vecDim<values[trailPosTime].length; ++vecDim) {
					series.add(trailPosTime, values[trailPosTime][vecDim]);
				}
				dataset.addSeries(series);
	        }
			JFreeChart chart = ChartFactory.createScatterPlot("Function", "Input", "Output",
					dataset, PlotOrientation.VERTICAL, false, false, false);
			animPlotVectorSetupRenderer(chart.getXYPlot().getRenderer(), trailLength);
			r.add(new ChartPanel(chart));
        }
        if(scaleRangAxisConstant) {
        	scaleChartPanelsRangeAxisToConstant(r);
        }
        return r;
	}
	
	private static void animPlotVectorSetupRenderer(XYItemRenderer renderer, int trailLength) {
		for(int i=0; i<=trailLength; ++i) {
			int c = 255*i/trailLength;
			renderer.setSeriesPaint(i, new Color(c, c, c));
			renderer.setSeriesShape(i, new Ellipse2D.Float(-3, -3, 6, 6));
		}
	}
	
	private static void scaleChartPanelsRangeAxisToConstant(List<ChartPanel> chartPanels) {
		double min=Double.MAX_VALUE, max=Double.MIN_VALUE;
        for(Iterator<ChartPanel> it=chartPanels.iterator(); it.hasNext(); ) {
        	final ChartPanel curPanel = it.next();
        	Range curRange = curPanel.getChart().getXYPlot().getRangeAxis().getRange();
        	if(min > curRange.getLowerBound()) {
        		min = curRange.getLowerBound();
        	}
        	if(max < curRange.getUpperBound()) {
        		max = curRange.getUpperBound();
        	}
        }
        for(Iterator<ChartPanel> it=chartPanels.iterator(); it.hasNext(); ) {
        	final ChartPanel curPanel = it.next();
        	curPanel.getChart().getXYPlot().getRangeAxis().setRange(min, max);
        }        
	}
	
	private static void animPlotShowChartPanels(final JFrame frame, List<ChartPanel> chartPanels) {
        int i=0;
        for(Iterator<ChartPanel> it=chartPanels.iterator(); it.hasNext(); ) {
        	final ChartPanel panel = it.next();
        	final boolean firstTimeAround = i==0; 
        	SwingUtilities.invokeLater(new Runnable() { public void run() {
				frame.getContentPane().removeAll();
				frame.getContentPane().add(panel, BorderLayout.CENTER);
				if(firstTimeAround) {
					frame.pack();
				} else {
					frame.validate();
				}
				//System.out.println(panel.getChart().getXYPlot().getRenderer().getSeriesPaint(2)); 
        	}});
			try {
				Thread.sleep(100);
			} catch(InterruptedException e) {}
        }
	}
	
	private static void animPlotVector(TimeSeries timeSeries, String title, 
			boolean scaleRangAxisConstant, int trailLength) throws IOException {
		assert trailLength >= 0;
		List<ChartPanel> chartPanels = makeAnimPlotVectorChartPanels(timeSeries, 
				scaleRangAxisConstant, trailLength);
		animPlotShowChartPanels(makeAnimPlotFrame(title), chartPanels);
	}
	
	private static Vector v(float[] a_) {
		return new Vector(Arrays.asList(a_));
	}
}
