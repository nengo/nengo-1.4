package ca.shu.ui.lib.handlers;

import javax.swing.JPopupMenu;

import ca.shu.ui.lib.world.NamedObject;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.event.PInputEvent;

public interface IContextMenu extends NamedObject, WorldObject {
	
	/*
	 * @return context menu associated to the Named Object
	 */
	public JPopupMenu showPopupMenu(PInputEvent event);
}
