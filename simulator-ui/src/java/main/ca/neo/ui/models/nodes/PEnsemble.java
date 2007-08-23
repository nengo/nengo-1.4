package ca.neo.ui.models.nodes;

import java.io.File;
import java.io.IOException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.neo.io.FileManager;
import ca.neo.model.Ensemble;
import ca.neo.model.Node;
import ca.neo.plot.Plotter;
import ca.neo.ui.NeoGraphics;
import ca.neo.ui.configurable.managers.PropertySet;
import ca.neo.ui.configurable.struct.PropDescriptor;
import ca.neo.ui.exceptions.ModelConfigurationException;
import ca.neo.ui.models.icons.EnsembleIcon;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.ui.models.viewers.EnsembleViewer;
import ca.neo.ui.models.viewers.NodeViewer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;

public class PEnsemble extends PNodeContainer {

	private static final long serialVersionUID = 1L;

	public PEnsemble() {
		super();
		init();
	}

	public PEnsemble(Node model) {
		super(model);
		init();
	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		Util.UserError("Ensemble has not been implemented yet");
		return null;
	}

	/*
	 * @return Ensemble Model
	 */
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

	public void saveModel(File file) throws IOException {
		FileManager fm = new FileManager();

		fm.save(getModel(), file);
	}

	private void init() {
		setIcon(new EnsembleIcon(this));
	}

	@Override
	protected Object configureModel(PropertySet configuredProperties)
			throws ModelConfigurationException {
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
	protected NodeViewer createNodeViewerInstance() {
		return new EnsembleViewer(this);
	}

	class PlotSpikePattern extends StandardAction {

		private static final long serialVersionUID = 1L;

		public PlotSpikePattern(String actionName) {
			super("Plot spike pattern", actionName);
		}

		@Override
		protected void action() throws ActionException {
			if (!getModel().isCollectingSpikes()) {
				Util.UserWarning("Ensemble is not set to collect spikes.");
			}
			Plotter.plot(getModel().getSpikePattern());

		}

	}

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

	String fileName = getName() + "." + NeoGraphics.ENSEMBLE_FILE_EXTENSION;

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
