package ca.shu.ui.lib.util;

import javax.swing.JComponent;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.actions.StandardAction;

/**
 * A menu builder for Swing
 * 
 * @author Shu
 * 
 */
public abstract class AbstractMenuBuilder {
	private boolean applyStyle;

	public AbstractMenuBuilder(boolean applyCustomStyle) {
		this.applyStyle = applyCustomStyle;

	}

	public abstract void addAction(StandardAction action);

	public abstract AbstractMenuBuilder createSubMenu(String label);

	public boolean isApplyCustomStyle() {
		return applyStyle;
	}

	public void style(JComponent item) {
		if (applyStyle)
			Style.applyStyleToComponent(item);
	}
}
