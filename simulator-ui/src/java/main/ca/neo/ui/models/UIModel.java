package ca.neo.ui.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPopupMenu;

import ca.neo.ui.actions.RemoveModelAction;
import ca.neo.ui.models.tooltips.ModelTooltip;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.Interactable;
import ca.shu.ui.lib.world.WorldObject;

/**
 * A UI Object which wraps a NEO Model
 * 
 * @author Shu Wu
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

		menu.addAction(new RemoveModelAction("Remove " + getTypeName(), this));

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

		addChild(0, icon);
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
	public JPopupMenu showContextMenu() {
		if (!isModelExists()) {
			UserMessages.showWarning("Model is not configured yet");
			return null;
		} else {
			JPopupMenu menu = constructMenu().toJPopupMenu();

			return menu;
		}

	}

}
