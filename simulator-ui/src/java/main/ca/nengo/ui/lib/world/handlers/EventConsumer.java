package ca.nengo.ui.lib.world.handlers;

import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;

/**
 * Handler which consumes all events passed to it. Used when an object wants to
 * shield its events from the outside.
 * 
 * @author Shu Wu
 */
public class EventConsumer implements PInputEventListener {
	public void processEvent(PInputEvent aEvent, int type) {
		aEvent.setHandled(true);
	}
}
