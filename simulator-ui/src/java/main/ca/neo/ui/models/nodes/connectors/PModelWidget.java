package ca.neo.ui.models.nodes.connectors;

import ca.neo.ui.models.PModelConfigurable;
import ca.neo.ui.models.PNeoNode;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.util.PopupMenuBuilder;

public abstract class PModelWidget extends PModelConfigurable {
	private boolean isWidgetVisible = true;
	private PNeoNode nodeParent;

	public PModelWidget(PNeoNode nodeParent) {
		super();
		init(nodeParent);
	}

	public PModelWidget(PNeoNode nodeParent, Object model) {
		super(model);
		init(nodeParent);
	}

	@Override
	public PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = super.constructMenu();

		if (isWidgetVisible()) {
			menu.addAction(new HideWidgetAction("Hide " + getTypeName()));
		} else {
			menu.addAction(new ShowWidgetAction("Show " + getTypeName()));
		}
		return menu;
	}

	@Override
	protected void prepareForDestroy() {

		nodeParent.removeWidget(this);
		super.prepareForDestroy();
	}

	public PNeoNode getNodeParent() {
		return nodeParent;
	}

	public boolean isWidgetVisible() {
		return isWidgetVisible;
	}

	/**
	 * 
	 * 
	 * @param isWidgetVisible
	 *            Whether the user has marked this widget as hidden
	 */
	public void setWidgetVisible(boolean isWidgetVisible) {
		this.isWidgetVisible = isWidgetVisible;

		nodeParent.layoutWidgets();

	}

	private void init(PNeoNode nodeParent) {
		this.setDraggable(false);
		this.nodeParent = nodeParent;
	}

	class HideWidgetAction extends ReversableAction {

		private static final long serialVersionUID = 1L;

		public HideWidgetAction(String actionName) {
			super("Hiding " + getTypeName(), actionName);
		}

		@Override
		protected void action() throws ActionException {
			setWidgetVisible(false);
		}

		@Override
		protected void undo() throws ActionException {
			setWidgetVisible(true);

		}
	}

	class ShowWidgetAction extends ReversableAction {

		private static final long serialVersionUID = 1L;

		public ShowWidgetAction(String actionName) {
			super("Showing " + getTypeName(), actionName);
		}

		@Override
		protected void action() throws ActionException {
			setWidgetVisible(true);
		}

		@Override
		protected void undo() throws ActionException {
			setWidgetVisible(false);

		}
	}

}
