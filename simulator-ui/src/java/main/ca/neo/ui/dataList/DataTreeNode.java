package ca.neo.ui.dataList;

import javax.swing.tree.DefaultMutableTreeNode;

import ca.neo.ui.actions.PlotSpikePattern;
import ca.neo.ui.actions.PlotTimeSeries;
import ca.neo.ui.actions.PlotTimeSeriesTau;
import ca.neo.util.SpikePattern;
import ca.neo.util.TimeSeries;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;

/**
 * Tree Node with NEO Data
 * 
 * @author Shu Wu
 */
public abstract class DataTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	public DataTreeNode(Object userObject) {
		super(userObject);
	}

	public abstract StandardAction getDefaultAction();
	public abstract void constructPopupMenu(PopupMenuBuilder menu);
	public abstract String toString();
}

/**
 * Node containing a spike pattern
 * 
 * @author Shu WU
 */
class SpikePatternNode extends DataTreeNode {

	private static final long serialVersionUID = 1L;

	public SpikePatternNode(SpikePattern spikePattern) {
		super(spikePattern);
	}
	
	@Override
	public SpikePattern getUserObject() {
		return (SpikePattern)super.getUserObject();
	}

	@Override
	public StandardAction getDefaultAction() {
		return new PlotSpikePattern((SpikePattern) getUserObject());
	}

	public void constructPopupMenu(PopupMenuBuilder menu) {
		// PopupMenuBuilder menuBuilder = new PopupMenuBuilder("Spike Pattern");
		menu.addAction(getDefaultAction());
	}

	public String toString() {
		return "Spike Pattern";
	}

}

/**
 * Node containing time series data
 * 
 * @author Shu Wu
 */
class TimeSeriesNode extends DataTreeNode {
	private static final long serialVersionUID = 1L;

	private String stateName;

	public TimeSeriesNode(TimeSeries userObject, String stateName) {
		super(userObject);
		this.stateName = stateName;
	}

	@Override
	public StandardAction getDefaultAction() {
		return new PlotTimeSeries((TimeSeries) getUserObject(), "Probe data: "
				+ stateName);
	}

	public void constructPopupMenu(PopupMenuBuilder menu) {
		menu.addAction(getDefaultAction());
		menu.addAction(new PlotTimeSeriesTau(
				(TimeSeries) getUserObject(), "Probe data: " + stateName));
	}
	
	@Override
	public TimeSeries getUserObject() {
		return (TimeSeries)super.getUserObject();
	}

	@Override
	public String toString() {
		return stateName + " (Probe data)";
	}

}