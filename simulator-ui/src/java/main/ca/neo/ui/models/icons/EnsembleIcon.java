package ca.neo.ui.models.icons;

import ca.neo.ui.models.nodes.PEnsemble;

public class EnsembleIcon extends NodeContainerIcon {
	private static final long serialVersionUID = 1L;

	public EnsembleIcon(PEnsemble parent) {

		super(parent, new IconImage("images/EnsembleIcon.gif"));

	}

	@Override
	public int getNodeCountNormalization() {
		return 1000;
	}

}
