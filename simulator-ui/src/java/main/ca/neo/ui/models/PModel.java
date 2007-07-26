package ca.neo.ui.models;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.text.SimpleAttributeSet;

import ca.neo.ui.NeoWorld;
import ca.neo.ui.style.Style;
import ca.neo.ui.views.objects.properties.IConfigurable;
import ca.neo.ui.views.objects.properties.PropertiesDialog;
import ca.neo.ui.views.objects.properties.PropertySchema;
import ca.shu.ui.lib.handlers.IContextMenu;
import ca.shu.ui.lib.objects.GText;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.impl.Frame;
import ca.shu.ui.lib.world.impl.WorldObject;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

public abstract class PModel extends WorldObject implements IContextMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Object model;

	WorldObject icon;

	// boolean modelCreationCancelled = false;

	String name;

	/*
	 * Default constructor, model is constructed internally
	 */
	public PModel() {
		super();
		WorldObject icon = createIcon();

		setIcon(icon);
		setName(icon.getName());

	}

	/*
	 * @param model Model is constructed externally
	 */
	public PModel(Object model) {
		this.model = model;

	}

	public PopupMenuBuilder constructPopup() {

		PopupMenuBuilder menu = new PopupMenuBuilder(getName());

		JMenuItem item;

		menu.addSection("Object Actions");

		menu.addAction(new AbstractAction("Remove from world") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				removeFromParent();
			}
		});

		return menu;
	}

	public WorldObject getIcon() {
		return icon;
	}

	public Object getModel() {
		return model;
	}

	public String getName() {
		return name;
	}

	public boolean isModelCreated() {
		return (model != null);
	}

	public void setModel(Object model) {
		this.model = model;

	}

	public void setName(String name) {
		this.name = name;
	}

	public JPopupMenu showPopupMenu(PInputEvent event) {
		if (model == null) {
			Util.Warning("Model is not configured yet");
			return null;
		} else {
			JPopupMenu menu = constructPopup().getJPopupMenu();

			return menu;
		}
	}

	protected abstract WorldObject createIcon();

	protected void setIcon(WorldObject icon) {
		if (this.icon != null) {

			this.icon.removeFromParent();
		}

		this.icon = icon;

		this.addChild(icon);
		icon.setDraggable(false);
		// icon.setPickable(false);

		setBounds(getFullBounds());

	}

	/*
	 * Updates the drawing of the proxyObject
	 */
	protected void updateSymbol() {

	}

}
