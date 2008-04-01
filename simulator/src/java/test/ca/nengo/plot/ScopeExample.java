/*
 * Created on 11-Feb-08
 */
package ca.nengo.plot;

import java.awt.BorderLayout;
import java.awt.Image;
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
			
			//... and plot the probed data from the simulation ... 
			//Plotter.plot(p.getData(), "function output");
			
			//now here are a couple of function bases ... 
			Function g1 = new GaussianPDF(0, 1);
			Function g2 = new GaussianPDF(0.5f, 1);			
			FunctionBasis gaussianBasis = new FunctionBasisImpl(new Function[]{g1, g2});
			
			animPlot(p.getData(), gaussianBasis, -3, .001f, 3, "gaussian basis plot");

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
	
	private static void animPlot(TimeSeries timeSeries, 
			FunctionBasis functionBasis, float start, float increment, float end, String title) 
			throws Exception {
		if (functionBasis.getDimension() > 1) {
			throw new IllegalArgumentException("Only 1-D functions can be plotted with this method.  "
					+"There may or may not be a good reason for this.");
		}
		
		final JFrame frame = new JFrame(title);
		
		try {
			Image image = ImageIO.read(ScopeExample.class.getClassLoader().getResource("ca/nengo/plot/spikepattern-grey.png"));
			frame.setIconImage(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        List<ChartPanel> chartPanels = new Vector<ChartPanel>();
        for(int i=0; i<timeSeries.getValues().length; i+=100) {
	        XYSeriesCollection dataset = new XYSeriesCollection();
			XYSeries series = new XYSeries("Function");
			functionBasis.setCoefficients(timeSeries.getValues()[i]);
			
			float x = start;
			while (x <= end) {
				float y = functionBasis.map(new float[]{x});
				series.add(x, y);
				x += increment;
			}
			
			dataset.addSeries(series);
			
			JFreeChart chart = ChartFactory.createXYLineChart("Function", "Input", "Output", 
					dataset, PlotOrientation.VERTICAL, false, false, false);
			
			chartPanels.add(new ChartPanel(chart));
        }
        
        long t0 = System.currentTimeMillis();
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
        	}});
			try {
				Thread.sleep(0);
			} catch(InterruptedException e) {}
        	System.out.println(i++);
        }
        System.out.println(System.currentTimeMillis()-t0);


	}
}
