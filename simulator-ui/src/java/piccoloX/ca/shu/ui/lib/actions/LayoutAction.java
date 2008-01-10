package ca.shu.ui.lib.actions;

import java.util.Enumeration;

import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.viewers.WorldLayout;
import ca.neo.ui.models.viewers.NodeViewer;

public abstract class LayoutAction extends ReversableAction {
	private static final long serialVersionUID = 1L;

	private WorldLayout savedLayout;

	NodeViewer nodeViewer;

	public LayoutAction(NodeViewer nodeViewer, String description,
			String actionName) {
		super(description, actionName);
		this.nodeViewer = nodeViewer;
	}

	@Override
	protected void action() throws ActionException {
		savedLayout = new WorldLayout("", nodeViewer, false);
		applyLayout();
	}

	protected abstract void applyLayout();

	protected void restoreNodePositions() {

		Enumeration<UINeoNode> en = nodeViewer.getNeoNodes().elements();

		while (en.hasMoreElements()) {
			UINeoNode node = en.nextElement();

			node.setOffset(savedLayout.getPosition(node));

		}
	}

	protected void setSavedLayout(WorldLayout savedLayout) {
		this.savedLayout = savedLayout;
	}

	@Override
	protected void undo() throws ActionException {
		restoreNodePositions();
	}

}
