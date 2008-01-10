package ca.neo.ui.models.icons;

import ca.neo.ui.models.nodes.UIEnsemble;

/**
 * Icon for an Ensemble
 * 
 * @author Shu Wu
 * 
 */
public class EnsembleIcon extends NodeContainerIcon {
	private static final long serialVersionUID = 1L;

	public EnsembleIcon(UIEnsemble parent) {

		super(parent, new IconImage("images/neoIcons/EnsembleIcon.gif"));

	}

	@Override
	protected int getNodeCountNormalization() {
		return 1000;
	}

}
