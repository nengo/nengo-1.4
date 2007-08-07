package ca.shu.ui.lib.handlers;

import javax.swing.JPopupMenu;

import ca.shu.ui.lib.world.NamedObject;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.event.PInputEvent;

public interface Interactable extends NamedObject, WorldObject {

	/**
	 * @param event
	 *            The input event triggering the context menu
	 * @return context menu associated to the Named Object
	 */
	public JPopupMenu showContextMenu(PInputEvent event);

	/**
	 * @return whether the Context Menu is enabled
	 */
	public boolean isContextMenuEnabled();


}
