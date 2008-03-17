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
		Node originalNode = nodeUI.getModel();

		try {
			Node copiedNode = originalNode.clone();
			NengoGraphics.getInstance().getClipboard().setContents(copiedNode);
			processNodeUI(nodeUI);
		} catch (CloneNotSupportedException e) {
			throw new ActionException("Could not clone node", e);
		}
	}

	protected void processNodeUI(UINeoNode nodeUI) {

	}

}
