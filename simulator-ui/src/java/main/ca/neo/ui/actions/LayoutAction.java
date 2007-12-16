package ca.neo.ui.actions;

import java.util.Enumeration;

import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.viewers.NodeLayout;
import ca.neo.ui.models.viewers.NodeViewer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;

public abstract class LayoutAction extends ReversableAction {
	private static final long serialVersionUID = 1L;

	private NodeLayout savedLayout;

	NodeViewer nodeViewer;

	public LayoutAction(NodeViewer nodeViewer, String description,
			String actionName) {
		super(description, actionName);
		this.nodeViewer = nodeViewer;
	}

	@Override
	protected void action() throws ActionException {
		savedLayout = new NodeLayout("", nodeViewer);
		applyLayout();
	}

	protected abstract void applyLayout();

	protected void restoreNodePositions() {

		Enumeration<UINeoNode> en = nodeViewer.getNeoNodes().elements();

		while (en.hasMoreElements()) {
			UINeoNode node = en.nextElement();

			node.setOffset(savedLayout.getPosition(node.getName()));

		}
	}

	protected void setSavedLayout(NodeLayout savedLayout) {
		this.savedLayout = savedLayout;
	}

	@Override
	protected void undo() throws ActionException {
		restoreNodePositions();
	}

}
