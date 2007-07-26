package ca.shu.ui.lib.util;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class MenuBuilder {
	JMenu menu;

	public MenuBuilder(String label) {
		menu = new JMenu(label);
	}
	
	public JMenu getJMenu() {
		return menu;
	}
	
	public void addAction(AbstractAction action) {
		JMenuItem item;
		item = new JMenuItem(action);
		menu.add(item);

	}
}
