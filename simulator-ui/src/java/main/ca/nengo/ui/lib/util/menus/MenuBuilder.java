package ca.nengo.ui.lib.util.menus;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import ca.nengo.ui.lib.Style.NengoStyle;
import ca.nengo.ui.lib.actions.StandardAction;

public class MenuBuilder extends AbstractMenuBuilder {
	boolean isFirstSection = true;

	JMenu menu;

	public MenuBuilder(String label) {
		this(label, false);
	}

	public MenuBuilder(String label, boolean applyCustomStyle) {
		super(applyCustomStyle);
		menu = new JMenu(label);
		style(menu, true);
	}

	@Override
	public void addAction(StandardAction action) {
		addAction(action, -1, null);
	}

	public void addAction(StandardAction action, KeyStroke accelorator) {
		addAction(action, -1, accelorator);
	}

	public void addAction(StandardAction action, int mnemonic, KeyStroke accelorator) {
		JMenuItem item = new JMenuItem(action.toSwingAction());
		if (accelorator != null) {
			item.setAccelerator(accelorator);
		}
		if (mnemonic != -1) {
			item.setMnemonic(mnemonic);
		}

		style(item);
		menu.add(item);

	}

	/**
	 * @param action
	 *            Action to add
	 * @param mnemonic
	 *            Mnemonic to assign to this action
	 */
	public void addAction(StandardAction action, int mnemonic) {
		addAction(action, mnemonic, null);
	}

	@Override
	public void addLabel(String msg) {
		JLabel item = new JLabel(msg);
		style(item);
		menu.add(item);
	}

	public void addSection(String name) {
		addSection(name, NengoStyle.FONT_BOLD);
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

	@Override
	public MenuBuilder addSubMenu(String label) {
		MenuBuilder mb = new MenuBuilder(label, isCustomStyle());
		getJMenu().add(mb.getJMenu());
		return mb;

	}

	public JMenu getJMenu() {
		return menu;
	}

	/**
	 * removes all elements to start over
	 */
	public void reset() {
		menu.removeAll();
	}

}
