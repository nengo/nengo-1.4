package ca.shu.ui.lib.util.menus;

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
		style(menu, true);
	}

	@Override
	public void addAction(StandardAction action) {
		addAction(action, -1);
	}

	/**
	 * @param action
	 *            Action to add
	 * @param mnemonic
	 *            Mnemonic to assign to this action
	 */
	public void addAction(StandardAction action, int mnemonic) {
		JMenuItem item = new JMenuItem(action.toSwingAction());
		if (mnemonic != -1) {
			item.setMnemonic(mnemonic);
		}

		style(item);
		menu.add(item);
	}

	@Override
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
