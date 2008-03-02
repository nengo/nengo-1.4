package ca.neo.ui.actions;

import ca.neo.model.Node;
import ca.neo.ui.NengoGraphics;
import ca.neo.ui.models.UINeoNode;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;

public class CopyAction extends StandardAction {

	private static final long serialVersionUID = 1L;
	private UINeoNode nodeUI;

	public CopyAction(String description, UINeoNode nodeUI) {
		super(description);
		this.nodeUI = nodeUI;
	}

	@Override
	protected final void action() throws ActionException {
		Node node = nodeUI.getModel();

		NengoGraphics.getInstance().getClipboard().setContents(node);
	}

	protected void processNodeUI(UINeoNode nodeUI) {

	}

}
