package ca.neo.ui.models.nodes.widgets;

import ca.neo.model.Ensemble;
import ca.neo.plot.Plotter;
import ca.neo.ui.models.nodes.UIEnsemble;
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;

public class UISpikeProbe extends UIProbe {

	private static final long serialVersionUID = 1L;

	public UISpikeProbe(UIEnsemble nodeAttachedTo) {
		super(nodeAttachedTo, nodeAttachedTo.getModel());

		getProbeParent().popupTransientMsg(
				"Collecting spikes on " + getProbeParent().getName());
		getModel().collectSpikes(true);

//		setProbeColor(ProbeIcon.SPIKE_PROBE_COLOR);
	}

	@Override
	protected TooltipBuilder constructTooltips() {
		TooltipBuilder tooltips = new TooltipBuilder("Collecting spikes");
		tooltips.addPart(new PropertyPart("Attached to", getModel().getName()));
		return tooltips;
	}

	@Override
	protected void prepareForDestroy() {

		super.prepareForDestroy();

		getModel().collectSpikes(false);

		getProbeParent().popupTransientMsg(
				"Spike collection stopped on " + getProbeParent().getName());

		getProbeParent().removeProbe(this);

	}

	@Override
	public Ensemble getModel() {
		return (Ensemble) super.getModel();
	}

	@Override
	public UIEnsemble getProbeParent() {
		return (UIEnsemble) super.getProbeParent();
	}

	@Override
	public String getTypeName() {
		return "Spike Collector";
	}

	/**
	 * Action for Plotting the Spike Pattern
	 * 
	 * @author Shu Wu
	 */
	class PlotSpikePattern extends StandardAction {

		private static final long serialVersionUID = 1L;

		public PlotSpikePattern(String actionName) {
			super("Plot spike pattern", actionName);
		}

		@Override
		protected void action() throws ActionException {
			if (!getModel().isCollectingSpikes()) {
				UserMessages
						.showWarning("Ensemble is not set to collect spikes.");
			}

			Plotter.plot(getModel().getSpikePattern());
		}

	}

	@Override
	protected PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = super.constructMenu();

		menu.addAction(new PlotSpikePattern("Plot spikes"));
		return menu;
	}

}
