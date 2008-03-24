package ca.nengo.ui.dataList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.tree.MutableTreeNode;

import ca.nengo.plot.Plotter;
import ca.nengo.util.SpikePattern;
import ca.nengo.util.TimeSeries;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;

public class PlotNodesAction extends StandardAction {

	private static final long serialVersionUID = 1L;

	Collection<DataTreeNode> nodes;

	public PlotNodesAction(Collection<DataTreeNode> nodes) {
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
			} else {
				throw new UnsupportedOperationException(
						"This type of data node is not supported for plotting together");
			}

		}

		Plotter.plot(timeSeries, spikePatterns, "Data Plot");
	}
}
