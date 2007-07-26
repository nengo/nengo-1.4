package ca.shu.ui.lib.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import ca.neo.ui.style.Style;

public class PopupMenuBuilder {

	JPopupMenu menu;

	/*
	 * @return unwraps the JPopupMenu
	 */
	public JPopupMenu getJPopupMenu() {
		return menu;
	}

	public PopupMenuBuilder(String label) {
		super();
		this.menu = new JPopupMenu(label);
	}

	public void addAction(AbstractAction action) {
		JMenuItem item;
		item = new JMenuItem(action);
		menu.add(item);

	}

	boolean isFirstSection = true;

	/*
	 * Creates a new section in the Popup menu
	 * 
	 * @param name the name of the new section
	 */
	public void addSection(String name) {
		if (isFirstSection) {
			isFirstSection = false;
		} else {
			menu.add(new JSeparator());
		}

		JLabel label = new JLabel(name);
		label.setLocation(4, 4);
		label.setFont(Style.FONT_BIG);
		menu.add(label);
	}

	public MenuBuilder addSubMenu(String label) {
		MenuBuilder mb = new MenuBuilder(label);
		getJPopupMenu().add(mb.getJMenu());
		return mb;

	}

}
