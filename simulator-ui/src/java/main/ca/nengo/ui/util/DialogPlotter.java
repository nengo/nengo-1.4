/*
 * Created on 15-Jun-2006
 */
package ca.nengo.ui.util;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import ca.nengo.plot.impl.DefaultPlotter;

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

		JDialog dialog = new JDialog(parent, title);
		dialog.getContentPane().add(panel, BorderLayout.CENTER);

		dialog.pack();
		dialog.setVisible(true);
	}

}
