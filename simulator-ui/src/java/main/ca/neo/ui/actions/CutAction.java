package ca.neo.ui.actions;

import ca.neo.ui.models.UINeoNode;

public class CutAction extends CopyAction {

	public CutAction(String description, UINeoNode nodeUI) {
		super(description, nodeUI);
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected void processNodeUI(UINeoNode nodeUI) {
		super.processNodeUI(nodeUI);
		nodeUI.destroy();
	}

}
