package ca.nengo.ui.lib.misc;

import ca.nengo.ui.lib.actions.StandardAction;

public class ShortcutKey {

	private StandardAction action;
	private int keyCode;
	private int modifiers;

	public ShortcutKey(int modifiers, int keyCode, StandardAction action) {
		this.modifiers = modifiers;
		this.keyCode = keyCode;
		this.action = action;
	}

	public StandardAction getAction() {
		return action;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public int getModifiers() {
		return modifiers;
	}

}