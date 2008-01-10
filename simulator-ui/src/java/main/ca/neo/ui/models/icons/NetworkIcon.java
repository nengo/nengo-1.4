package ca.neo.ui.models.icons;

import ca.neo.ui.models.nodes.UINetwork;

/**
 * Icon for a Network
 * 
 * @author Shu Wu
 */
public class NetworkIcon extends NodeContainerIcon {
	private static final long serialVersionUID = 1L;

	public NetworkIcon(UINetwork parent) {
		super(parent, new IconImage("images/neoIcons/NetworkIcon.gif"));
	}

	@Override
	protected int getNodeCountNormalization() {
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
