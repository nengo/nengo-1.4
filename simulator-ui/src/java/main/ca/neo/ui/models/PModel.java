package ca.neo.ui.models;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.handlers.Interactable;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.event.PInputEvent;

public abstract class PModel extends WorldObjectImpl implements Interactable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Object model;

	WorldObjectImpl icon;

	// boolean modelCreationCancelled = false;
	public static final String PROPERTY_MODEL = "neoModel";

	/**
	 * Fires when the model has changed
	 */
	public void fireModelPropertyChanged() {
		firePropertyChange(0, PROPERTY_MODEL, null, null);
	}

	public boolean isContextMenuEnabled() {
		return true;
	}

	/**
	 * Default constructor, model is constructed internally
	 */
	public PModel() {
		super();
		// setName(name);
	}

	public abstract String getTypeName();

	/**
	 * @param model
	 *            Model is constructed externally
	 */
	public PModel(Object model) {

		setModel(model);

	}

	public PopupMenuBuilder constructMenu() {

		PopupMenuBuilder menu = new PopupMenuBuilder("Model: " + getName());

		MenuBuilder objectMenu = menu.createSubMenu("Object");

		objectMenu.addAction(new RemoveModelAction("Remove " + getTypeName()));

		return menu;
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

	public WorldObjectImpl getIcon() {
		return icon;
	}

	public Object getModel() {
		return model;
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
			Util.Warning("Model is not configured yet");
			return null;
		} else {
			JPopupMenu menu = constructMenu().getJPopupMenu();

			return menu;
		}
	}

	protected void setIcon(WorldObjectImpl icon) {
		if (this.icon != null) {

			this.icon.removeFromParent();
		}

		this.icon = icon;

		this.addChild(icon);
		icon.setDraggable(false);
		// icon.setPickable(false);

		setBounds(getFullBounds());

	}

}
