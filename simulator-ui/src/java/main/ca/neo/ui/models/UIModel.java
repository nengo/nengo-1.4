package ca.neo.ui.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import ca.neo.ui.models.tooltips.ModelTooltip;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.Interactable;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * A UI Object which wraps a NEO Model
 * 
 * @author Shu Wu
 * 
 */
public abstract class UIModel extends WorldObject implements Interactable {

	private static final long serialVersionUID = 1L;

	/**
	 * The property name that identifies a change in this node's NEO Model
	 */
	public static final String PROPERTY_MODEL = "neoModel";

	/**
	 * Icon for this model
	 */
	private WorldObject icon;

	/**
	 * Property Listener which listens to changes of the Icon's bounds and
	 * updates this node bounds accordingly
	 */
	private PropertyChangeListener iconPropertyChangeListener;

	/**
	 * NEO Model
	 */
	private Object model;

	/**
	 * Creates a hollow wrapper without any NEO model
	 */
	public UIModel() {
		super();
	}

	/**
	 * Create a UI Wrapper around a NEO Model
	 * 
	 * @param model
	 *            NEO Model
	 */
	public UIModel(Object model) {

		setModel(model);

	}

	/**
	 * @return Constructed Context Menu
	 */
	protected PopupMenuBuilder constructMenu() {

		PopupMenuBuilder menu = new PopupMenuBuilder("Model: " + getName());

		menu.addAction(new RemoveModelAction("Remove " + getTypeName()));

		return menu;
	}

	/**
	 * @return Constructed Tooltip
	 */
	protected abstract TooltipBuilder constructTooltips();

	/**
	 * @param newIcon
	 *            New Icon
	 */
	protected void setIcon(WorldObject newIcon) {
		if (icon != null) {
			icon.removePropertyChangeListener(PROPERTY_BOUNDS,
					iconPropertyChangeListener);
			icon.removeFromParent();
		}

		icon = newIcon;

		addChild(icon);
		icon.setSelectable(false);

		iconPropertyChangeListener = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent arg0) {
				setBounds(icon.getBounds());
			}

		};
		setBounds(icon.getBounds());

		icon.addPropertyChangeListener(PROPERTY_BOUNDS,
				iconPropertyChangeListener);

	}

	/**
	 * Fires when the NEO model has changed
	 */
	public void fireModelPropertyChanged() {
		firePropertyChange(0, PROPERTY_MODEL, null, null);
	}

	/**
	 * @return Icon of this node
	 */
	public WorldObject getIcon() {
		return icon;
	}

	/**
	 * @return NEO Model
	 */
	public Object getModel() {
		return model;
	}

	@Override
	public final WorldObject getTooltip() {
		TooltipBuilder tooltips = constructTooltips();
		if (tooltips == null) {
			return null;
		} else {
			return new ModelTooltip(tooltips);
		}
	}

	/**
	 * @return What this type of NEO Model is called
	 */
	public abstract String getTypeName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.handlers.Interactable#isContextMenuEnabled()
	 */
	public boolean isContextMenuEnabled() {
		return true;
	}

	/**
	 * @return Whether the NEO Model exists
	 */
	public boolean isModelExists() {
		return (model != null);
	}

	/**
	 * @param model
	 *            New NEO Model
	 */
	public void setModel(Object model) {
		this.model = model;
		fireModelPropertyChanged();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.handlers.Interactable#showContextMenu(edu.umd.cs.piccolo.event.PInputEvent)
	 */
	public JPopupMenu showContextMenu(PInputEvent event) {
		if (!isModelExists()) {
			Util.UserWarning("Model is not configured yet");
			return null;
		} else {
			JPopupMenu menu = constructMenu().getJPopupMenu();

			return menu;
		}
	}

	/**
	 * Action for removing this UI Wrapper and the model
	 * 
	 * @author Shu Wu
	 * 
	 */
	class RemoveModelAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public RemoveModelAction(String actionName) {
			super("Remove " + getTypeName(), actionName);
		}

		@Override
		protected void action() throws ActionException {
			int response = JOptionPane.showConfirmDialog(UIEnvironment
					.getInstance(),
					"Once an object has been removed, it cannot be undone.",
					"Are you sure?", JOptionPane.YES_NO_OPTION);
			if (response == 0) {
				destroy();
			} else {
				throw new ActionException("Action cancelled by user", false);
			}
		}

	}
}
