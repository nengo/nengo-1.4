/*
 * Created on 15-Jun-2006
 */
package ca.neo.ui.util;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import ca.neo.plot.Plotter;
import ca.neo.plot.impl.DefaultPlotter;

/**
 * Plotter uses dialog rather than frames to support parent-child relationship
 * with NeoGraphics components.
 * 
 * @author Shu Wu
 */
public class DialogPlotter extends DefaultPlotter {

	private JDialog parent;

	public DialogPlotter(JDialog parentPanel) {
		super();
		this.parent = parentPanel;
	}

	@Override
	protected void showChart(JFreeChart chart, String title) {
		
		JPanel panel = new ChartPanel(chart);

		openingPlot();
		JDialog dialog = new JDialog(parent, title);
		dialog.getContentPane().add(panel, BorderLayout.CENTER);

		final Plotter plotter = this;
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				plotter.closingPlot();
			}
		});

		dialog.pack();
		dialog.setVisible(true);
	}

}
