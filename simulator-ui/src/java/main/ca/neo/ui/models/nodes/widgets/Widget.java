package ca.neo.ui.models.nodes.widgets;

import java.awt.Color;

import javax.swing.JOptionPane;

import ca.neo.model.Network;
import ca.neo.ui.models.UIModelConfigurable;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.menus.AbstractMenuBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Widgets are models such as Terminations and Origins which can be attached to
 * a PNeoNode
 * 
 * @author Shu Wu
 */
public abstract class Widget extends UIModelConfigurable {

	private boolean isWidgetVisible = true;
	private ExposedIcon myExposedIcon;

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
		setSelectable(false);
		this.parent = nodeParent;
	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);

		if (isWidgetVisible()) {
			menu.addAction(new HideWidgetAction("Hide this icon"));
		} else {
			menu.addAction(new ShowWidgetAction("Show this icon"));
		}

		menu.addSection(getTypeName());

		if (getExposedName() == null) {
			menu.addAction(new ExposeAction());
		} else {
			menu.addAction(new UnExposeAction());
		}
		constructWidgetMenu(menu);
	}

	@Override
	protected void constructTooltips(TooltipBuilder tooltips) {
		super.constructTooltips(tooltips);
		tooltips.addProperty("Attached to", parent.getName());
		tooltips.addProperty("Exposed as", getExposedName());
	}

	/**
	 * Constructs widget-specific menu
	 * 
	 * @param menu
	 */
	protected void constructWidgetMenu(AbstractMenuBuilder menu) {

	}

	protected abstract void expose(Network network, String exposedName);

	/**
	 * Exposes this origin/termination outside the Network
	 * 
	 * @param exposedName
	 *            Name of the newly exposed origin/termination
	 */
	protected void expose(String exposedName) {
		Network network = getNodeParent().getParentNetwork();
		if (network != null) {
			expose(network, exposedName);
			showPopupMessage(this.getName() + " is exposed as " + exposedName + " on Network: "
					+ network.getName());
			updateModel();
		} else {
			UserMessages.showWarning("Cannot expose because no external network is available");
		}
	}

	protected String getExposedName() {
		if (getNodeParent() != null) {
			Network network = getNodeParent().getParentNetwork();
			if (network != null) {
				String exposedName = getExposedName(network);
				if (exposedName != null) {
					return exposedName;
				}
			}
		}
		return null;
	}

	protected abstract String getExposedName(Network network);

	protected abstract String getModelName();

	/**
	 * UnExposes this origin/termination outside the Network
	 */
	protected void unExpose() {
		Network network = getNodeParent().getParentNetwork();
		if (network != null) {
			unExpose(network);
			showPopupMessage(this.getName() + " is UN-exposed on Network: " + network.getName());
			updateModel();
		} else {
			UserMessages.showWarning("Cannot expose because no external network is available");
		}
	}

	protected abstract void unExpose(Network network);

	public abstract Color getColor();

	public UINeoNode getNodeParent() {
		return parent;
	}

	/**
	 * @return Whether this widget is visible on the parent
	 */
	public boolean isWidgetVisible() {
		return isWidgetVisible;
	}

	@Override
	public void setModel(Object model) {
		super.setModel(model);

		String name = getModelName();

		String exposedName = getExposedName();
		if (exposedName != null) {
			if (myExposedIcon == null) {
				myExposedIcon = new ExposedIcon(getColor());
				getPiccolo().addChild(myExposedIcon);
				myExposedIcon.setOffset(getWidth() + 2,
						(getHeight() - myExposedIcon.getHeight()) / 2);
			}

		} else {
			if (myExposedIcon != null) {
				myExposedIcon.removeFromParent();
			}
		}
		setName(name);

		firePropertyChange(EventType.MODEL_CHANGED);

	}

	/**
	 * @param isVisible
	 *            Whether the user has marked this widget as hidden
	 */
	public void setWidgetVisible(boolean isVisible) {
		this.isWidgetVisible = isVisible;

		firePropertyChange(EventType.WIDGET);

		setVisible(isVisible);
		getPiccolo().invalidateFullBounds();
	}

	class ExposeAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public ExposeAction() {
			super("Expose outside Network");
		}

		@Override
		protected void action() throws ActionException {

			String name = JOptionPane.showInputDialog(UIEnvironment.getInstance(),
					"Please enter the name to expose this as: ");

			if (name != null && name.compareTo("") != 0) {
				expose(name);
			} else {
				throw new ActionException("Invalid name");
			}

		}
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

	class UnExposeAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public UnExposeAction() {
			super("Un-expose as " + getExposedName());
		}

		@Override
		protected void action() throws ActionException {
			unExpose();
		}
	}
}

class ExposedIcon extends PText {

	private static final long serialVersionUID = 1L;

	public ExposedIcon(Color color) {
		super("E");

		setTextPaint(color);
		setFont(Style.FONT_XLARGE);
	}

}
