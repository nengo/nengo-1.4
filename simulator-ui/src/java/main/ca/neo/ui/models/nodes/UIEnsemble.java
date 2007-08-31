package ca.neo.ui.models.nodes;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.neo.model.Ensemble;
import ca.neo.model.Node;
import ca.neo.plot.Plotter;
import ca.neo.ui.NeoGraphics;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.models.icons.EnsembleIcon;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.ui.models.viewers.EnsembleViewer;
import ca.neo.ui.models.viewers.NodeViewer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;

/**
 * UI Wrapper for an Ensemble
 * 
 * @author Shu
 * 
 */
public class UIEnsemble extends UINodeContainer {

	private static final long serialVersionUID = 1L;

	/**
	 * File name for saving this ensemble
	 */
	private String fileName = getName() + "."
			+ NeoGraphics.ENSEMBLE_FILE_EXTENSION;

	public UIEnsemble() {
		super();
		init();
	}

	public UIEnsemble(Node model) {
		super(model);
		init();
	}

	/**
	 * Initializes this instance
	 */
	private void init() {
		setIcon(new EnsembleIcon(this));
	}

	@Override
	protected Object configureModel(ConfigParam configuredProperties)
			throws ConfigException {
		throw new NotImplementedException();
	}

	@Override
	protected PopupMenuBuilder constructMenu() {

		PopupMenuBuilder menu = super.constructMenu();
		menu.addSection("Ensemble");
		MenuBuilder spikesMenu = menu.createSubMenu("Spikes");

		if (getModel().isCollectingSpikes())
			spikesMenu.addAction(new StopCollectSpikes());
		else
			spikesMenu.addAction(new StartCollectSpikes());

		spikesMenu.addAction(new PlotSpikePattern("Plot spikes"));
		return menu;
	}

	@Override
	protected TooltipBuilder constructTooltips() {
		TooltipBuilder tooltips = super.constructTooltips();

		return tooltips;
	}

	@Override
	protected NodeViewer createViewerInstance() {
		return new EnsembleViewer(this);
	}

	@Override
	public ConfigParamDescriptor[] getConfigSchema() {
		UserMessages.showError("Ensemble has not been implemented yet");
		return null;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public Ensemble getModel() {
		return (Ensemble) super.getModel();
	}

	@Override
	public int getNodesCount() {
		if (getModel() != null) {
			return getModel().getNodes().length;
		} else
			return 0;
	}

	@Override
	public String getTypeName() {

		return "Ensemble";
	}

	@Override
	public void saveContainerConfig() {
		/*
		 * Do nothing here. Ensemble configuration cannot be saved yet
		 */
	}

	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Action for Plotting the Spike Pattern
	 * 
	 * @author Shu Wu
	 * 
	 */
	class PlotSpikePattern extends StandardAction {

		private static final long serialVersionUID = 1L;

		public PlotSpikePattern(String actionName) {
			super("Plot spike pattern", actionName);
		}

		@Override
		protected void action() throws ActionException {
			if (!getModel().isCollectingSpikes()) {
				UserMessages.showWarning("Ensemble is not set to collect spikes.");
			}
			Plotter.plot(getModel().getSpikePattern());

		}

	}

	/**
	 * Action to enable Spike Collection
	 * 
	 * @author Shu Wu
	 * 
	 */
	class StartCollectSpikes extends ReversableAction {

		private static final long serialVersionUID = 1L;

		public StartCollectSpikes() {
			super("Collect Spikes");
		}

		@Override
		protected void action() throws ActionException {
			if (getModel().isCollectingSpikes())
				throw new ActionException("Already collecting spikes");
			else
				getModel().collectSpikes(true);
		}

		@Override
		protected void undo() {
			getModel().collectSpikes(false);

		}

	}

	/**
	 * Action to Stop Collecting Spikes
	 * 
	 * @author Shu Wu
	 * 
	 */
	class StopCollectSpikes extends ReversableAction {
		private static final long serialVersionUID = 1L;

		public StopCollectSpikes() {
			super("Stop Collecting Spikes");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void action() throws ActionException {
			if (!getModel().isCollectingSpikes())
				throw new ActionException("Already not collecting spikes");
			else
				getModel().collectSpikes(false);

		}

		@Override
		protected void undo() throws ActionException {
			getModel().collectSpikes(true);

		}

	}
}
