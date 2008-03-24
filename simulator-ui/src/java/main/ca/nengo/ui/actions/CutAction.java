package ca.nengo.ui.actions;

import ca.nengo.model.Node;
import ca.nengo.ui.NengoGraphics;
import ca.nengo.ui.models.UINeoNode;
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
