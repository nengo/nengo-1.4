package ca.nengo.ui.actions;

import ca.nengo.model.Node;
import ca.nengo.ui.NengoGraphics;
import ca.nengo.ui.models.NodeContainer;
import ca.nengo.ui.models.NodeContainer.ContainerException;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;

public class PasteAction extends StandardAction {

	private static final long serialVersionUID = 1L;

	private NodeContainer nodeContainer;

	public PasteAction(String description, NodeContainer nodeContainer) {
		super(description);

		this.nodeContainer = nodeContainer;
	}

	@Override
	protected void action() throws ActionException {
		Node node = NengoGraphics.getInstance().getClipboard().getContents();
		if (node != null) {
			try {
				CreateModelAction.ensureNonConflictingName(node, nodeContainer);
				nodeContainer.addNodeModel(node);
			} catch (ContainerException e) {
				throw new ActionException(e);
			}
		} else {
			throw new ActionException("Clipboard is empty");
		}
	}
}
