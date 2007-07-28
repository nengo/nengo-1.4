package ca.shu.ui.lib.util;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class MenuBuilder extends AbstractMenuBuilder {
	JMenu menu;

	public MenuBuilder(String label) {
		this(label, true);
	}

	public MenuBuilder(String label, boolean applyCustomStyle) {
		super(applyCustomStyle);

		menu = new JMenu(label);

		style(menu);
	}

	public JMenu getJMenu() {
		return menu;
	}

	public void addAction(AbstractAction action) {
		JMenuItem item = new JMenuItem(action);
		style(item);
		menu.add(item);
	}

	public MenuBuilder createSubMenu(String label) {
		MenuBuilder mb = new MenuBuilder(label, isApplyCustomStyle());
		getJMenu().add(mb.getJMenu());
		return mb;

	}

}
