package ca.shu.ui.lib.handlers;

import java.awt.Cursor;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

public class HandCursorHandler extends PBasicInputEventHandler {
	Cursor handCursor;

	@Override
	public void mouseEntered(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseEntered(event);

		if (handCursor == null) {
			handCursor = new Cursor(Cursor.HAND_CURSOR);
			event.getComponent().pushCursor(handCursor);
		}
	}

	@Override
	public void mouseExited(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseExited(event);

		if (handCursor != null) {
			handCursor = null;
			event.getComponent().popCursor();
		}
	}

}
