package ca.neo.ui.models.nodes.widgets;

import ca.neo.model.Ensemble;
import ca.neo.ui.actions.PlotSpikePattern;
import ca.neo.ui.models.nodes.UIEnsemble;
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;

public class UISpikeProbe extends UIProbe {

	private static final long serialVersionUID = 1L;

	public UISpikeProbe(UIEnsemble nodeAttachedTo) {
		super(nodeAttachedTo, nodeAttachedTo.getModel());

		getProbeParent().popupTransientMsg(
				"Collecting spikes on " + getProbeParent().getName());
		getModel().collectSpikes(true);

		// setProbeColor(ProbeIcon.SPIKE_PROBE_COLOR);
	}

	@Override
	protected void constructTooltips(TooltipBuilder tooltips) {
		super.constructTooltips(tooltips);
		tooltips.addPart(new PropertyPart("Attached to", getModel().getName()));
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

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);

		if (getModel().getSpikePattern() != null) {
			menu.addAction(new PlotSpikePattern(getModel().getSpikePattern()));
		}
	}
}
