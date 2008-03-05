package ca.neo.ui.models.nodes;

import ca.neo.model.Ensemble;
import ca.neo.ui.models.icons.EnsembleIcon;
import ca.neo.ui.models.nodes.widgets.UISpikeProbe;
import ca.neo.ui.models.viewers.EnsembleViewer;
import ca.neo.ui.models.viewers.NodeViewer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.util.menus.AbstractMenuBuilder;

/**
 * UI Wrapper for an Ensemble
 * 
 * @author Shu
 */
public class UIEnsemble extends NodeContainer {

	private static final long serialVersionUID = 1L;

	private UISpikeProbe spikeCollector;

	public UIEnsemble(Ensemble model) {
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
	protected void constructDataCollectionMenu(AbstractMenuBuilder menu) {
		super.constructDataCollectionMenu(menu);

		if (spikeCollector == null || spikeCollector.isDestroyed()) {
			menu.addAction(new StartCollectSpikes());
		} else {
			menu.addAction(new StopCollectSpikes());
		}

	}

	@Override
	protected NodeViewer createViewerInstance() {
		return new EnsembleViewer(this);
	}

	public void collectSpikes(boolean collect) {
		if (collect) {
			if (spikeCollector == null || spikeCollector.isDestroyed()) {
				spikeCollector = new UISpikeProbe(this);
				newProbeAdded(spikeCollector);
			}
		} else {
			if (spikeCollector != null) {
				spikeCollector.destroy();
				spikeCollector = null;
			}
		}
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
	public void modelUpdated() {
		super.modelUpdated();
		if (getModel().isCollectingSpikes()) {
			collectSpikes(true);
		}
	}

	/**
	 * Action to enable Spike Collection
	 * 
	 * @author Shu Wu
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
				collectSpikes(true);
		}

		@Override
		protected void undo() {
			collectSpikes(false);

		}

	}

	/**
	 * Action to Stop Collecting Spikes
	 * 
	 * @author Shu Wu
	 */
	class StopCollectSpikes extends ReversableAction {
		private static final long serialVersionUID = 1L;

		public StopCollectSpikes() {
			super("Stop Collecting Spikes");
		}

		@Override
		protected void action() throws ActionException {
			if (!getModel().isCollectingSpikes())
				throw new ActionException("Already not collecting spikes");
			else
				collectSpikes(false);

		}

		@Override
		protected void undo() throws ActionException {
			collectSpikes(true);
		}

	}
}
