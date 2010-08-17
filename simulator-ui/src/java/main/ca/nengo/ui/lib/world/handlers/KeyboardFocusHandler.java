package ca.nengo.ui.lib.world.handlers;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Focuses the Keyboard handler on the event Pick Path when the mouse enters a a
 * new path.
 * 
 * @author Shu Wu
 */
public class KeyboardFocusHandler extends PBasicInputEventHandler {

	@Override
	public void mouseEntered(PInputEvent event) {
		event.getInputManager().setKeyboardFocus(event.getPath());
	}
}
