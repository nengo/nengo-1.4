package ca.shu.ui.lib.util.menus;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.StandardAction;

/**
 * Used to build a popup menu. The created menu can later be converted to a
 * Swing component.
 * 
 * @author Shu Wu
 */
public class PopupMenuBuilder extends AbstractMenuBuilder {

	boolean isFirstSection = true;

	JPopupMenu menu;

	public PopupMenuBuilder(String label) {
		super(false);
		menu = new JPopupMenu(label);
		style(menu);

		if (label != null && label.compareTo("") != 0) {
			addSection(label, Style.FONT_LARGE);
		}
	}

	@Override
	public void addAction(StandardAction standardAction) {
		JMenuItem item;
		item = new JMenuItem(standardAction.toSwingAction());
		style(item);
		menu.add(item);

	}

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

	@Override
	public MenuBuilder addSubMenu(String label) {
		MenuBuilder mb = new MenuBuilder(label, isCustomStyle());
		toJPopupMenu().add(mb.getJMenu());
		return mb;

	}

	/**
	 * @return Swing component
	 */
	public JPopupMenu toJPopupMenu() {
		return menu;
	}

	@Override
	public void addLabel(String msg) {
		JLabel item = new JLabel(msg);
		style(item);
		menu.add(item);
	}

}
