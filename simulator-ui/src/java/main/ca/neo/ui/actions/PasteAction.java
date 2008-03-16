package ca.neo.ui.actions;

import ca.neo.model.Node;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.INodeContainer.ContainerException;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;

public class PasteAction extends StandardAction {

	private static final long serialVersionUID = 1L;
	private Node node;
	private INodeContainer nodeContainer;

	public PasteAction(String description, Node node, INodeContainer nodeContainer) {
		super(description);
		this.node = node;
		this.nodeContainer = nodeContainer;
	}

	@Override
	protected void action() throws ActionException {
		/*
		 * TODO: need ability to rename node here
		 */
		// String originalName = node.getName();
		// String newName = node.getName();
		// int i = 0;
		//
		// while (nodeContainer.getNodeModel(newName) != null) {
		// // Avoid duplicate names
		// while (nodeContainer.getNodeModel(newName) != null) {
		// i++;
		// newName = originalName + " (" + i + ")";
		//
		// }
		// newName = JOptionPane.showInputDialog(UIEnvironment.getInstance(),
		// "Node already exists, please enter a new name", newName);
		//
		// if (newName == null || newName.equals("")) {
		// throw new UserCancelledException();
		// }
		//
		// }
		// node.setName(newName);
		try {
			nodeContainer.addNodeModel(node);
		} catch (ContainerException e) {
			throw new ActionException(e);
		}
	}
}
