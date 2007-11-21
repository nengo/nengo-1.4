package ca.neo.ui.models.nodes.widgets;

import ca.neo.ui.models.UIModelConfigurable;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;

/**
 * Widgets are models such as Terminations and Origins which can be attached to
 * a PNeoNode
 * 
 * @author Shu Wu
 */
public abstract class Widget extends UIModelConfigurable {

	private boolean isWidgetVisible = true;
	private UINeoNode parent;

	public Widget(UINeoNode nodeParent) {
		super();
		init(nodeParent);
	}

	public Widget(UINeoNode nodeParent, Object model) {
		super(model);
		init(nodeParent);
	}

	private void init(UINeoNode nodeParent) {
		this.setSelectable(false);
		this.parent = nodeParent;
	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);

		if (isWidgetVisible()) {
			menu.addAction(new HideWidgetAction("Hide " + getTypeName()));
		} else {
			menu.addAction(new ShowWidgetAction("Show " + getTypeName()));
		}
	}

	@Override
	protected TooltipBuilder constructTooltips() {

		TooltipBuilder tooltips = new TooltipBuilder(getName() + "("
				+ getTypeName() + ") attached to " + parent.getName());

		return tooltips;

	}

	public UINeoNode getNodeParent() {
		return parent;
	}

	/**
	 * @return Whether this widget is visible on the parent
	 */
	public boolean isWidgetVisible() {
		return isWidgetVisible;
	}

	/**
	 * @param isVisible
	 *            Whether the user has marked this widget as hidden
	 */
	public void setWidgetVisible(boolean isVisible) {
		this.isWidgetVisible = isVisible;

	}

	/**
	 * Action for hiding this widget
	 * 
	 * @author Shu Wu
	 */
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

	/**
	 * Action for showing this widget
	 * 
	 * @author Shu Wu
	 */
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
