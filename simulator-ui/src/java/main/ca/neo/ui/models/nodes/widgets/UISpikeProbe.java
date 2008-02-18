package ca.neo.ui.models.nodes.widgets;

import ca.neo.model.Ensemble;
import ca.neo.ui.actions.PlotSpikePattern;
import ca.neo.ui.models.nodes.UIEnsemble;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;

public class UISpikeProbe extends UIProbe {

	private static final long serialVersionUID = 1L;

	public UISpikeProbe(UIEnsemble nodeAttachedTo) {
		super(nodeAttachedTo, nodeAttachedTo.getModel());

		getProbeParent().showPopupMessage("Collecting spikes on " + getProbeParent().getName());
		getModel().collectSpikes(true);

		// setProbeColor(ProbeIcon.SPIKE_PROBE_COLOR);
	}

	@Override
	protected void constructTooltips(TooltipBuilder tooltips) {
		super.constructTooltips(tooltips);
		tooltips.addProperty("Attached to", getModel().getName());
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

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);

		if (getModel().getSpikePattern() != null) {
			menu.addAction(new PlotSpikePattern(getModel().getSpikePattern()));
		}
	}

	@Override
	protected void prepareToDestroyModel() {
		getModel().collectSpikes(false);

		getProbeParent().showPopupMessage(
				"Spike collection stopped on " + getProbeParent().getName());

		super.prepareToDestroyModel();
	}

	@Override
	public void doubleClicked() {
		(new PlotSpikePattern(getModel().getSpikePattern())).doAction();
	}
}
