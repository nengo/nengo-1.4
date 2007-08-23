package ca.neo.ui.models.icons;

import ca.neo.ui.models.nodes.PNetwork;

public class NetworkIcon extends NodeContainerIcon {
	private static final long serialVersionUID = 1L;

	public NetworkIcon(PNetwork parent) {
		super(parent, new IconImage("images/NetworkIcon.gif"));

	}

	@Override
	public int getNodeCountNormalization() {
		return 20;
	}

	@Override
	protected void setFullBoundsInvalid(boolean fullBoundsInvalid) {
		super.setFullBoundsInvalid(fullBoundsInvalid);
	}

	@Override
	protected boolean validateFullBounds() {
		return super.validateFullBounds();
	}

}
