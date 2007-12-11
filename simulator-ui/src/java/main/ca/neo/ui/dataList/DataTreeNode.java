package ca.neo.ui.dataList;

import javax.swing.tree.DefaultMutableTreeNode;

import ca.neo.ui.actions.PlotAdvanced;
import ca.neo.ui.actions.PlotSpikePattern;
import ca.neo.ui.actions.PlotTimeSeries;
import ca.neo.util.DataUtils;
import ca.neo.util.SpikePattern;
import ca.neo.util.TimeSeries;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;

/**
 * Tree Node with NEO Data
 * 
 * @author Shu Wu
 */
public abstract class DataTreeNode extends SortableMutableTreeNode {

	private static final long serialVersionUID = 1L;

	public DataTreeNode(Object userObject) {
		super(userObject);
	}

	public abstract void constructPopupMenu(PopupMenuBuilder menu,
			SimulatorDataModel dataModel);

	public abstract StandardAction getDefaultAction();

	public abstract boolean includeInExport();

	public abstract String toString();
}

/**
 * Node containing probe data
 * 
 * @author Shu Wu
 */
class ProbeDataNode extends TimeSeriesNode {

	private static final long serialVersionUID = 1L;

	public ProbeDataNode(TimeSeries userObject, String stateName) {
		super(userObject, stateName);
	}

	@Override
	public boolean includeInExport() {
		return true;
	}

	@Override
	public String toString() {
		return name + " (Probe data " + getUserObject().getDimension() + "D)";
	}

}

/**
 * Contains one-dimensional expanded data from a Probe
 * 
 * @author Shu Wu
 */
class ProbeDataExpandedNode extends TimeSeriesNode {

	private static final long serialVersionUID = 1L;

	public ProbeDataExpandedNode(TimeSeries userObject, int dim) {
		super(userObject, "" + dim);
	}

	@Override
	public boolean includeInExport() {
		return false;
	}

	@Override
	public String toString() {
		return name;
	}
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

	public void constructPopupMenu(PopupMenuBuilder menu,
			SimulatorDataModel dataModel) {
		menu.addAction(getDefaultAction());
	}

	@Override
	public StandardAction getDefaultAction() {
		return new PlotSpikePattern((SpikePattern) getUserObject());
	}

	@Override
	public SpikePattern getUserObject() {
		return (SpikePattern) super.getUserObject();
	}

	@Override
	public boolean includeInExport() {
		return true;
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
abstract class TimeSeriesNode extends DataTreeNode {
	private static final long serialVersionUID = 1L;

	protected String name;

	public TimeSeriesNode(TimeSeries userObject, String name) {
		super(userObject);
		this.name = name;
	}

	public void constructPopupMenu(PopupMenuBuilder menu,
			SimulatorDataModel dataModel) {
		menu.addAction(getDefaultAction());
		menu.addAction(new PlotAdvanced((TimeSeries) getUserObject(),
				"Probe data: " + name));
		menu.addAction(new ExtractDimenmsions(dataModel));
	}

	class ExtractDimenmsions extends StandardAction {

		private static final long serialVersionUID = 1L;
		private SimulatorDataModel dataModel;

		public ExtractDimenmsions(SimulatorDataModel dataModel) {
			super("Extract dimensions");
			this.dataModel = dataModel;
		}

		@Override
		protected void action() throws ActionException {
			extractDimensions();
			dataModel.nodeStructureChanged(TimeSeriesNode.this);
		}

		public void extractDimensions() {
			TimeSeries probeData = getUserObject();
			/*
			 * Extract dimensions
			 */
			for (int dimCount = 0; dimCount < probeData.getDimension(); dimCount++) {
				TimeSeries oneDimData = DataUtils.extractDimension(probeData,
						dimCount);

				DefaultMutableTreeNode stateDimNode = new ProbeDataExpandedNode(
						oneDimData, dimCount);
				add(stateDimNode);
			}
		}
	}

	@Override
	public StandardAction getDefaultAction() {
		return new PlotTimeSeries(getUserObject(), "Probe data: " + name);
	}

	@Override
	public TimeSeries getUserObject() {
		return (TimeSeries) super.getUserObject();
	}

	@Override
	public abstract String toString();

}