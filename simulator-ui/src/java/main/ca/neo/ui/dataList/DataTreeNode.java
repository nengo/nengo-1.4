package ca.neo.ui.dataList;

import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

import ca.neo.ui.actions.PlotSpikePattern;
import ca.neo.ui.actions.PlotTimeSeries;
import ca.neo.ui.actions.PlotTimeSeriesTau;
import ca.neo.util.SpikePattern;
import ca.neo.util.TimeSeries;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.Interactable;

/**
 * Tree Node with NEO Data
 * 
 * @author Shu Wu
 */
public abstract class DataTreeNode extends DefaultMutableTreeNode implements
		Interactable {

	private static final long serialVersionUID = 1L;

	public DataTreeNode(Object userObject) {
		super(userObject);
	}

	public abstract StandardAction getDefaultAction();

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
	public StandardAction getDefaultAction() {
		return new PlotSpikePattern((SpikePattern) getUserObject());
	}

	public JPopupMenu showContextMenu() {
		PopupMenuBuilder menuBuilder = new PopupMenuBuilder("Spike Pattern");
		menuBuilder.addAction(getDefaultAction());

		return menuBuilder.toJPopupMenu();
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

	public JPopupMenu showContextMenu() {
		PopupMenuBuilder menuBuilder = new PopupMenuBuilder("Probe Data");
		menuBuilder.addAction(getDefaultAction());
		menuBuilder.addAction(new PlotTimeSeriesTau(
				(TimeSeries) getUserObject(), "Probe data: " + stateName));
		return menuBuilder.toJPopupMenu();
	}

	@Override
	public String toString() {
		return stateName + " (Probe data)";
	}

}