package ca.neo.ui.actions;

import javax.swing.JOptionPane;

import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.ui.NengoGraphics;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.INodeContainer.ContainerException;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.util.UIEnvironment;

public class PasteAction extends StandardAction {

	private static final long serialVersionUID = 1L;

	private INodeContainer nodeContainer;

	public PasteAction(String description, INodeContainer nodeContainer) {
		super(description);

		this.nodeContainer = nodeContainer;
	}

	/**
	 * Prompts the user to select a non-conflicting name
	 * 
	 * @param node
	 * @param container
	 * @throws UserCancelledException
	 *             If the user cancels
	 */
	public static void ensureNonConflictingName(Node node, INodeContainer container)
			throws UserCancelledException {
		String originalName = node.getName();
		String newName = node.getName();
		int i = 0;

		while (container.getNodeModel(newName) != null) {
			// Avoid duplicate names
			while (container.getNodeModel(newName) != null) {
				i++;
				newName = originalName + " (" + i + ")";

			}
			newName = JOptionPane.showInputDialog(UIEnvironment.getInstance(),
					"Node already exists, please enter a new name", newName);

			if (newName == null || newName.equals("")) {
				throw new UserCancelledException();
			}
		}

		try {
			node.setName(newName);
		} catch (StructuralException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void action() throws ActionException {
		Node node = NengoGraphics.getInstance().getClipboard().getContents();
		if (node != null) {
			try {
				ensureNonConflictingName(node, nodeContainer);
				nodeContainer.addNodeModel(node);
			} catch (ContainerException e) {
				throw new ActionException(e);
			}
		} else {
			throw new ActionException("Clipboard is empty");
		}
	}
}
