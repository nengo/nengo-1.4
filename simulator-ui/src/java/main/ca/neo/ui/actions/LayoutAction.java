package ca.neo.ui.actions;

import java.util.Enumeration;

import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.viewers.NodeLayout;
import ca.neo.ui.models.viewers.NodeViewer;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;

public abstract class LayoutAction extends ReversableAction {
	NodeViewer nodeViewer;

	public LayoutAction(NodeViewer nodeViewer, String description,
			String actionName) {
		super(description, actionName);
		this.nodeViewer = nodeViewer;
	}

	private static final long serialVersionUID = 1L;

	/**
	 * TODO: Save node positions here, it's safer and won't get deleted by other
	 * layouts
	 */
	private NodeLayout savedLayout;

	@Override
	protected void action() throws ActionException {
		savedLayout = new NodeLayout("", nodeViewer);
		applyLayout();
	}

	protected abstract void applyLayout();

	protected void restoreNodePositions() {

		Enumeration<PNeoNode> en = nodeViewer.getNeoNodes().elements();

		while (en.hasMoreElements()) {
			PNeoNode node = en.nextElement();

			node.setOffset(savedLayout.getPosition(node.getName()));

		}
	}

	@Override
	protected void undo() throws ActionException {
		restoreNodePositions();
	}

	protected void setSavedLayout(NodeLayout savedLayout) {
		this.savedLayout = savedLayout;
	}

}
