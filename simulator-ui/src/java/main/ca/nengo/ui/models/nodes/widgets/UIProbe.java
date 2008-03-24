package ca.nengo.ui.models.nodes.widgets;

import java.awt.Color;

import ca.nengo.ui.models.UINeoNode;
import ca.nengo.ui.models.icons.ProbeIcon;
import ca.shu.ui.lib.objects.models.ModelObject;

public abstract class UIProbe extends ModelObject {

	private static final long serialVersionUID = 1L;
	private ProbeIcon myIcon;
	private UINeoNode nodeAttachedTo;

	public UIProbe(UINeoNode nodeAttachedTo, Object probeModel) {
		super(probeModel);
		init(nodeAttachedTo);
	}

	private void init(UINeoNode nodeAttachedTo) {
		setSelectable(true);
		this.nodeAttachedTo = nodeAttachedTo;
		myIcon = new ProbeIcon(this);
		myIcon.configureLabel(false);
		myIcon.setLabelVisible(false);
		setIcon(myIcon);
	}

	@Override
	public void dragOffset(double dx, double dy) {
		return; // not movable
	}

	public UINeoNode getProbeParent() {
		return nodeAttachedTo;
	}

	@Override
	public abstract String getTypeName();

	public void setProbeColor(Color color) {
		myIcon.setColor(color);
	}

	@Override
	protected void prepareForDestroy() {
		super.prepareForDestroy();
		getProbeParent().removeProbe(this);
	}

}
