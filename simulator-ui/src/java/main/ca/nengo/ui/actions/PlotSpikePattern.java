package ca.nengo.ui.actions;

import ca.nengo.plot.Plotter;
import ca.nengo.util.SpikePattern;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;

/**
 * Action for Plotting the Spike Pattern
 * 
 * @author Shu Wu
 */
public class PlotSpikePattern extends StandardAction {

	private static final long serialVersionUID = 1L;
	SpikePattern spikePattern;

	public PlotSpikePattern(SpikePattern spikePattern) {
		super("Plot spike pattern", "Plot spikes");
		this.spikePattern = spikePattern;
	}

	@Override
	protected void action() throws ActionException {
		Plotter.plot(spikePattern);
	}

}