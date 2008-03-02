package ca.neo.ui.actions;

import javax.swing.JOptionPane;

import ca.neo.model.impl.MockNode;
import ca.neo.ui.models.INodeContainer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.util.UIEnvironment;

public class PasteAction extends StandardAction {

	private static final long serialVersionUID = 1L;
	private MockNode node;
	private INodeContainer nodeContainer;

	public PasteAction(String description, MockNode node, INodeContainer nodeContainer) {
		super(description);
		this.node = node;
		this.nodeContainer = nodeContainer;
	}

	@Override
	protected void action() throws ActionException {
		String originalName = node.getName();
		String newName = node.getName();
		int i = 0;

		while (nodeContainer.getNodeModel(newName) != null) {
			// Avoid duplicate names
			while (nodeContainer.getNodeModel(newName) != null) {
				i++;
				newName = originalName + " (" + i + ")";

			}
			newName = JOptionPane.showInputDialog(UIEnvironment.getInstance(),
					"Node already exists, please enter a new name", newName);

			if (newName == null || newName.equals("")) {
				throw new UserCancelledException();
			}

		}

		node.setName(newName);
		nodeContainer.addNodeModel(node);
	}
}
