package ca.neo.ui.dataList;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.MutableTreeNode;

import ca.neo.plot.Plotter;
import ca.neo.util.SpikePattern;
import ca.neo.util.TimeSeries;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;

public class PlotNodesAction extends StandardAction {

	private static final long serialVersionUID = 1L;

	List<MutableTreeNode> nodes;

	public PlotNodesAction(List<MutableTreeNode> nodes) {
		super("Plot data together");
		this.nodes = nodes;
	}

	@Override
	protected void action() throws ActionException {
		/*
		 * Get nodes to be plotted together
		 */
		List<TimeSeries> timeSeries = new ArrayList<TimeSeries>(nodes.size());
		List<SpikePattern> spikePatterns = new ArrayList<SpikePattern>(nodes
				.size());

		for (MutableTreeNode node : nodes) {
			if (node instanceof SpikePatternNode) {
				spikePatterns.add(((SpikePatternNode) node).getUserObject());
			} else if (node instanceof TimeSeriesNode) {
				timeSeries.add(((TimeSeriesNode) node).getUserObject());
			}

		}

		Plotter.plot(timeSeries, spikePatterns, "Data Plot");
	}
}
