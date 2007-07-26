package ca.shu.ui.lib.handlers;

import javax.swing.JPopupMenu;

import ca.shu.ui.lib.world.INamedObject;
import ca.shu.ui.lib.world.IWorldObject;
import edu.umd.cs.piccolo.event.PInputEvent;

public interface IContextMenu extends INamedObject, IWorldObject {
	
	/*
	 * @return context menu associated to the Named Object
	 */
	public JPopupMenu showPopupMenu(PInputEvent event);
}
