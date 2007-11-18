package ca.neo.ui.models.nodes.widgets;

import java.awt.Color;

import ca.neo.ui.models.UIModel;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.icons.ProbeIcon;
import ca.neo.ui.models.tooltips.TooltipBuilder;

public abstract class UIProbe extends UIModel {

	private static final long serialVersionUID = 1L;
	private ProbeIcon myIcon;
	private UINeoNode nodeAttachedTo;

	protected UIProbe(UINeoNode nodeAttachedTo) {
		super();
		init(nodeAttachedTo);
	}

	public UIProbe(UINeoNode nodeAttachedTo, Object probeModel) {
		super(probeModel);
		init(nodeAttachedTo);
	}

	private void init(UINeoNode nodeAttachedTo) {
//		setTransparency(0.5f);
		this.nodeAttachedTo = nodeAttachedTo;
		setSelectable(false);
		myIcon = new ProbeIcon(this);
		myIcon.configureLabel(false);
		myIcon.setLabelVisible(false);
		setIcon(myIcon);
	}

	@Override
	protected abstract TooltipBuilder constructTooltips();

	public UINeoNode getProbeParent() {
		return nodeAttachedTo;
	}

	@Override
	public abstract String getTypeName();

	public void setProbeColor(Color color) {
		myIcon.setColor(color);
	}

}
