package ca.neo.ui.actions;

import javax.swing.JOptionPane;

import ca.neo.plot.Plotter;
import ca.neo.util.TimeSeries;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Action for Plotting with a Tau Filter
 * 
 * @author Shu Wu
 */
public class PlotTimeSeriesTau extends StandardAction {

	private static final long serialVersionUID = 1L;
	private TimeSeries timeSeries;
	private String plotName;

	public PlotTimeSeriesTau(TimeSeries timeSeries, String plotName) {
		super("Plot time series w/ tau filter", "Plot w/ filter");
		this.timeSeries = timeSeries;
		this.plotName = plotName;
	}

	@Override
	protected void action() throws ActionException {
		try {
			float tauFilter = new Float(JOptionPane
					.showInputDialog("Time constant of display filter (s): "));
			Plotter.plot(timeSeries, tauFilter, plotName);
		} catch (java.lang.NumberFormatException exception) {
			UserMessages.showWarning("Could not parse number");
		}

	}
}