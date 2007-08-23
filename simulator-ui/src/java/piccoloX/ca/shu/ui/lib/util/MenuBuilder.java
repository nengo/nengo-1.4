package ca.shu.ui.lib.util;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import ca.shu.ui.lib.actions.StandardAction;

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

	@Override
	public void addAction(StandardAction action) {
		JMenuItem item = new JMenuItem(action.getSwingAction());
		style(item);
		menu.add(item);
	}

	public void addLabel(String msg) {
		JLabel item = new JLabel(msg);
		style(item);
		menu.add(item);
	}

	@Override
	public MenuBuilder createSubMenu(String label) {
		MenuBuilder mb = new MenuBuilder(label, isApplyCustomStyle());
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
