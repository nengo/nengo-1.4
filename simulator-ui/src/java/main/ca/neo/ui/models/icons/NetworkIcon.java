package ca.neo.ui.models.icons;

import ca.neo.ui.models.nodes.PNetwork;

public class NetworkIcon extends NodeContainerIcon {
	@Override
	protected void setFullBoundsInvalid(boolean fullBoundsInvalid) {
		super.setFullBoundsInvalid(fullBoundsInvalid);
	}

	private static final long serialVersionUID = 1L;

	public NetworkIcon(PNetwork parent) {
		super(parent, new IconImage("images/NetworkIcon.gif"));

	}

	@Override
	protected boolean validateFullBounds() {
		return super.validateFullBounds();
	}

	@Override
	public int getNodeCountNormalization() {
		return 20;
	}

}
