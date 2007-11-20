package ca.neo.ui.actions;

import ca.neo.plot.Plotter;
import ca.neo.util.TimeSeries;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;

/**
 * Action for Plotting
 * 
 * @author Shu Wu
 */
public class PlotTimeSeries extends StandardAction {

	private static final long serialVersionUID = 1L;
	private TimeSeries data;
	private String plotName;

	public PlotTimeSeries(TimeSeries data, String plotName) {
		super("Plot");
		this.data = data;
		this.plotName = plotName;
	}

	@Override
	protected void action() throws ActionException {

		Plotter.plot(data, plotName);

	}

}