package ca.shu.ui.lib.objects;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

public abstract class Button extends WorldObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ButtonState buttonState = ButtonState.DEFAULT;

	protected Color defaultColor = Color.white;

	protected Color highlightColor = Color.lightGray;

	protected Color selectedColor = Color.darkGray;

	Runnable action;

	public Button(Runnable action) {
		super();
		this.action = action;

		this.setChildrenPickable(false);

		this.addInputEventListener(new HandCursorHandler());
		this.addInputEventListener(new ButtonHandler(this));
		setSelectable(false);
	}

	public abstract void buttonStateChanged();

	public void doAction() {
		if (action != null) {
			SwingUtilities.invokeLater(action);

			// (new Thread(action)).start();
			// SwingUtilities.invokeLater(action);
		}
	}

	public Runnable getAction() {
		return action;
	}

	public Color getDefaultColor() {
		return defaultColor;
	}

	public Color getHighlightColor() {
		return highlightColor;
	}

	public Color getSelectedColor() {
		return selectedColor;
	}

	public void setAction(Runnable action) {
		this.action = action;
	}

	public void setButtonState(ButtonState pState) {
		buttonState = pState;
		buttonStateChanged();
	}

	public void setDefaultColor(Color btnDefaultColor) {
		this.defaultColor = btnDefaultColor;
		setButtonState(buttonState);
	}

	public void setHighlightColor(Color btnHighlightColor) {
		this.highlightColor = btnHighlightColor;
		setButtonState(buttonState);
	}

	public void setSelectedColor(Color btnSelectedColor) {
		this.selectedColor = btnSelectedColor;
		setButtonState(buttonState);
	}

	protected ButtonState getButtonState() {
		return buttonState;
	}

	public static enum ButtonState {
		DEFAULT, HIGHLIGHT, SELECTED
	}

}

class HandCursorHandler extends PBasicInputEventHandler {
	Cursor handCursor;

	@Override
	public void mouseEntered(PInputEvent event) {
		super.mouseEntered(event);

		if (handCursor == null) {
			handCursor = new Cursor(Cursor.HAND_CURSOR);
			event.getComponent().pushCursor(handCursor);
		}
	}

	@Override
	public void mouseExited(PInputEvent event) {
		super.mouseExited(event);

		if (handCursor != null) {
			handCursor = null;
			event.getComponent().popCursor();
		}
	}

}

class ButtonHandler extends PBasicInputEventHandler {
	Button button;

	public ButtonHandler(Button button) {
		super();
		this.button = button;
	}

	@Override
	public void mouseClicked(PInputEvent event) {
		super.mouseClicked(event);
		button.doAction();

	}

	@Override
	public void mouseEntered(PInputEvent event) {
		super.mouseEntered(event);
		button.setButtonState(GTextButton.ButtonState.HIGHLIGHT);

	}

	@Override
	public void mouseExited(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseExited(event);
		button.setButtonState(GTextButton.ButtonState.DEFAULT);

	}

	@Override
	public void mousePressed(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mousePressed(event);
		button.setButtonState(GTextButton.ButtonState.SELECTED);

	}

	@Override
	public void mouseReleased(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseReleased(event);
		button.setButtonState(GTextButton.ButtonState.HIGHLIGHT);
	}

	@Override
	public void processEvent(PInputEvent event, int type) {
		// TODO Auto-generated method stub
		super.processEvent(event, type);
		// event.setHandled(true);
	}
}
