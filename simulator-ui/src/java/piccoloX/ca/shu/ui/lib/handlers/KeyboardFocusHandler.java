package ca.shu.ui.lib.handlers;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Shu Focuses the Keyboard handler on the event Pick Path after the
 *         mouse is over it
 * 
 */
public class KeyboardFocusHandler extends PBasicInputEventHandler {

	@Override
	public void mouseEntered(PInputEvent event) {
		event.getInputManager().setKeyboardFocus(event.getPath());
	}

}
