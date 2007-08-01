package ca.shu.ui.lib.util;

import java.awt.Font;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import ca.neo.ui.style.Style;

public class PopupMenuBuilder extends AbstractMenuBuilder {

	JPopupMenu menu;

	/*
	 * @return unwraps the JPopupMenu
	 */
	public JPopupMenu getJPopupMenu() {
		return menu;
	}

	public PopupMenuBuilder(String label) {
		super(false);
		menu = new JPopupMenu(label);
		style(menu);

		addSection(label, Style.FONT_LARGE);

	}

	public void addAction(AbstractAction action) {
		JMenuItem item;
		item = new JMenuItem(action);
		style(item);
		menu.add(item);

	}

	boolean isFirstSection = true;

	public void addSection(String name) {
		addSection(name, Style.FONT_BOLD);
	}

	/**
	 * Creates a new section in the Popup menu
	 * 
	 * @param name
	 *            the name of the new section
	 * @param fontStyle
	 *            style of font for the subsection label
	 */
	public void addSection(String name, Font fontStyle) {
		if (isFirstSection) {
			isFirstSection = false;
		} else {
			menu.add(new JSeparator());
		}

		JLabel label = new JLabel(name);
		label.setLocation(4, 4);
		label.setFont(fontStyle);
		style(label);
		menu.add(label);
	}

	public MenuBuilder createSubMenu(String label) {
		MenuBuilder mb = new MenuBuilder(label, isApplyCustomStyle());
		getJPopupMenu().add(mb.getJMenu());
		return mb;

	}

}
