package ca.shu.ui.lib.util;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import ca.neo.ui.style.Style;

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
	
	public void style(JComponent item) {
		if (applyStyle)
			Style.applyStyleToComponent(item);
	}
	
	

	public boolean isApplyCustomStyle() {
		return applyStyle;
	}
	
	
	public abstract void addAction(AbstractAction action);
	public abstract AbstractMenuBuilder createSubMenu(String label);
}
