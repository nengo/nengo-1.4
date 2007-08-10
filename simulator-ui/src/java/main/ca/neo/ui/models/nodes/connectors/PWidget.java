package ca.neo.ui.models.nodes.connectors;

import ca.neo.ui.models.PModelConfigurable;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.util.PopupMenuBuilder;

/**
 * Widgets are models such as Terminations and Origins which can be attached to
 * a PNeoNode
 * 
 * @author Shu
 * 
 */
public abstract class PWidget extends PModelConfigurable {

	private boolean isPermenantlyAttached = true;
	private PNeoNode nodeParent;

	public PWidget(PNeoNode nodeParent) {
		super();
		init(nodeParent);
	}

	public PWidget(PNeoNode nodeParent, Object model) {
		super(model);
		init(nodeParent);
	}

	@Override
	protected TooltipBuilder constructTooltips() {

		TooltipBuilder tooltips = new TooltipBuilder(getName() + "("
				+ getTypeName() + ") attached to " + nodeParent.getName());

		return tooltips;

	}

	@Override
	protected PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = super.constructMenu();

		if (isWidgetVisible()) {
			menu.addAction(new HideWidgetAction("Hide " + getTypeName()));
		} else {
			menu.addAction(new ShowWidgetAction("Show " + getTypeName()));
		}
		return menu;
	}

	public PNeoNode getNodeParent() {
		return nodeParent;
	}

	public boolean isWidgetVisible() {
		return isPermenantlyAttached;
	}

	/**
	 * 
	 * 
	 * @param isPermenantlyAttached
	 *            Whether the user has marked this widget as hidden
	 */
	public void setPermenantlyAttached(boolean isPermenantlyAttached) {
		this.isPermenantlyAttached = isPermenantlyAttached;

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
			setPermenantlyAttached(false);
		}

		@Override
		protected void undo() throws ActionException {
			setPermenantlyAttached(true);

		}
	}

	class ShowWidgetAction extends ReversableAction {

		private static final long serialVersionUID = 1L;

		public ShowWidgetAction(String actionName) {
			super("Showing " + getTypeName(), actionName);
		}

		@Override
		protected void action() throws ActionException {
			setPermenantlyAttached(true);
		}

		@Override
		protected void undo() throws ActionException {
			setPermenantlyAttached(false);

		}
	}

}
