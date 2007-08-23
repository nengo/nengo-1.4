package ca.neo.ui.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import ca.neo.ui.models.tooltips.GTooltip;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.handlers.Interactable;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Shu
 * 
 */
public abstract class PModel extends WorldObject implements Interactable {

	public static final String PROPERTY_MODEL = "neoModel";

	private static final long serialVersionUID = 1L;

	private Object model;

	WorldObject icon;

	PropertyChangeListener iconPropertyChangeListener;

	/**
	 * Default constructor, model is constructed internally
	 */
	public PModel() {
		super();
		// setName(name);
	}

	/**
	 * @param model
	 *            Model is constructed externally
	 */
	public PModel(Object model) {

		setModel(model);

	}

	/**
	 * Fires when the model has changed
	 */
	public void fireModelPropertyChanged() {
		firePropertyChange(0, PROPERTY_MODEL, null, null);
	}

	public WorldObject getIcon() {
		return icon;
	}

	public Object getModel() {
		return model;
	}

	@Override
	public final WorldObject getTooltip() {
		TooltipBuilder tooltips = constructTooltips();
		if (tooltips == null) {
			return null;
		} else {
			return new GTooltip(tooltips);
		}
	}

	public abstract String getTypeName();

	public boolean isContextMenuEnabled() {
		return true;
	}

	public boolean isModelCreated() {
		return (model != null);
	}

	public void setModel(Object model) {
		this.model = model;
		fireModelPropertyChanged();

	}

	public JPopupMenu showContextMenu(PInputEvent event) {
		if (!isModelCreated()) {
			Util.UserWarning("Model is not configured yet");
			return null;
		} else {
			JPopupMenu menu = constructMenu().getJPopupMenu();

			return menu;
		}
	}

	private void updateBounds() {
		PModel.this.setBounds(icon.getBounds());
	}

	protected PopupMenuBuilder constructMenu() {

		PopupMenuBuilder menu = new PopupMenuBuilder("Model: " + getName());

		// MenuBuilder objectMenu = menu.createSubMenu("Object");

		menu.addAction(new RemoveModelAction("Remove " + getTypeName()));

		return menu;
	}

	protected abstract TooltipBuilder constructTooltips();

	protected void setIcon(WorldObject newIcon) {
		if (icon != null) {
			icon.removePropertyChangeListener(PROPERTY_BOUNDS,
					iconPropertyChangeListener);
			icon.removeFromParent();
		}

		icon = newIcon;

		addChild(icon);
		icon.setDraggable(false);
		// icon.setPickable(false);

		iconPropertyChangeListener = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent arg0) {
				updateBounds();
			}

		};
		updateBounds();
		icon.addPropertyChangeListener(PROPERTY_BOUNDS,
				iconPropertyChangeListener);

	}

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
