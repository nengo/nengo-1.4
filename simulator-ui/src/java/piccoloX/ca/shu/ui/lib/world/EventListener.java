package ca.shu.ui.lib.world;

import ca.shu.ui.lib.world.IWorldObject.EventType;

/**
 * Listener interface used by WorldObjects
 * 
 * @author Shu Wu
 */
public interface EventListener {
	public void propertyChanged(EventType event);
}
