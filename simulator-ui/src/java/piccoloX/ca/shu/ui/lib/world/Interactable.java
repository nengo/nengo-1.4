package ca.shu.ui.lib.world;

import javax.swing.JPopupMenu;

/**
 * Objects which can be interacted with through context menus
 * 
 * @author Shu
 */
public interface Interactable {

	/**
	 * @param event
	 *            The input event triggering the context menu
	 * @return context menu associated to the Named Object
	 */
	public JPopupMenu getContextMenu();

}
