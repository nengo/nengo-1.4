package ca.shu.ui.lib.handlers;

import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;

/*
 * Consume all events in the network viewer window.
 * whose behavior is like an internal frame
 */
public class EventConsumer implements PInputEventListener {
	public void processEvent(PInputEvent aEvent, int type) {
		aEvent.setHandled(true);
	}
}

