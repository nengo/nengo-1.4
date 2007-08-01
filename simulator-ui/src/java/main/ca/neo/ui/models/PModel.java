package ca.neo.ui.models;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import ca.shu.ui.lib.handlers.IContextMenu;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.event.PInputEvent;

public abstract class PModel extends WorldObjectImpl implements IContextMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Object model;

	WorldObjectImpl icon;

	// boolean modelCreationCancelled = false;

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

		objectMenu.addAction(new AbstractAction("Remove") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				removeModel();
			}
		});

		return menu;
	}

	/**
	 * Called when the user wants to remove the Model
	 */
	public void removeModel() {
		removeFromParent();
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

	}

	public JPopupMenu showPopupMenu(PInputEvent event) {
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
