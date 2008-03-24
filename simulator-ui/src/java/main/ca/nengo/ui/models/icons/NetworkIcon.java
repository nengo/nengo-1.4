package ca.nengo.ui.models.icons;

import ca.nengo.ui.models.nodes.UINetwork;

/**
 * Icon for a Network
 * 
 * @author Shu Wu
 */
public class NetworkIcon extends NodeContainerIcon {
	private static final long serialVersionUID = 1L;

	public NetworkIcon(UINetwork parent) {
		super(parent, new IconImage(
				"images/nengoIcons/NetworkIcon.gif"));
	}

	@Override
	protected int getNodeCountNormalization() {
		return 20;
	}

}
