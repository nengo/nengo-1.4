package ca.neo.ui.actions;

import ca.neo.model.Node;
import ca.neo.ui.NengoGraphics;
import ca.neo.ui.models.UINeoNode;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;

public class CutAction extends StandardAction {
	private UINeoNode nodeUI;

	public CutAction(String description, UINeoNode nodeUI) {
		super(description);
		this.nodeUI = nodeUI;
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected final void action() throws ActionException {
		Node node = nodeUI.getModel();

		/*
		 * This removes the node from it's parent and externalities
		 */
		nodeUI.destroyModel();

		NengoGraphics.getInstance().getClipboard().setContents(node);
	}

}
