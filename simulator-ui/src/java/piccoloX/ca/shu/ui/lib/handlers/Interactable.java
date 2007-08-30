package ca.shu.ui.lib.handlers;

import javax.swing.JPopupMenu;

import ca.shu.ui.lib.world.INamedObject;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Objects which can be interacted with through context menus
 * 
 * @author Shu
 * 
 */
public interface Interactable extends INamedObject {

	/**
	 * @return whether the Context Menu is enabled
	 */
	public boolean isContextMenuEnabled();

	/**
	 * @param event
	 *            The input event triggering the context menu
	 * @return context menu associated to the Named Object
	 */
	public JPopupMenu showContextMenu(PInputEvent event);

}
