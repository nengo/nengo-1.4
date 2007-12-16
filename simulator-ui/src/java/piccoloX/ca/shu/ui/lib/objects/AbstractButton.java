package ca.shu.ui.lib.objects;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Button which executes an action on click
 * 
 * @author Shu Wu
 */
public abstract class AbstractButton extends WorldObject {

	private static final long serialVersionUID = 1L;

	private Color defaultColor = Style.COLOR_BUTTON_BACKGROUND;

	private Color highlightColor = Style.COLOR_BUTTON_HIGHLIGHT;

	private Runnable myAction;

	private ButtonState myState = ButtonState.DEFAULT;

	private Color selectedColor = Style.COLOR_BUTTON_SELECTED;

	/**
	 * @param action
	 *            Action to execute when the button is pressed
	 */
	public AbstractButton(Runnable action) {
		super();
		this.myAction = action;

		this.setChildrenPickable(false);

		this.addInputEventListener(new HandCursorHandler());
		this.addInputEventListener(new ButtonStateHandler(this));
		setSelectable(false);
	}

	protected void doAction() {
		if (myAction != null) {
			SwingUtilities.invokeLater(myAction);

		}
	}

	protected ButtonState getState() {

		return myState;
	}

	public Runnable getAction() {
		return myAction;
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
		this.myAction = action;
	}

	public void setButtonState(ButtonState pState) {
		myState = pState;
		stateChanged();
	}

	public void setDefaultColor(Color btnDefaultColor) {
		this.defaultColor = btnDefaultColor;
		setButtonState(myState);
	}

	public void setHighlightColor(Color btnHighlightColor) {
		this.highlightColor = btnHighlightColor;
		setButtonState(myState);
	}

	public void setSelectedColor(Color btnSelectedColor) {
		this.selectedColor = btnSelectedColor;
		setButtonState(myState);
	}

	public abstract void stateChanged();

	public static enum ButtonState {
		DEFAULT, HIGHLIGHT, SELECTED
	}

}

/**
 * Changes the button state from mouse events
 * 
 * @author Shu Wu
 */
class ButtonStateHandler extends PBasicInputEventHandler {
	private AbstractButton button;

	public ButtonStateHandler(AbstractButton button) {
		super();
		this.button = button;
	}

	@Override
	public void mouseClicked(PInputEvent event) {
		button.doAction();

	}

	@Override
	public void mouseEntered(PInputEvent event) {
		button.setButtonState(AbstractButton.ButtonState.HIGHLIGHT);

	}

	@Override
	public void mouseExited(PInputEvent event) {
		button.setButtonState(AbstractButton.ButtonState.DEFAULT);
	}

	@Override
	public void mousePressed(PInputEvent event) {
		button.setButtonState(AbstractButton.ButtonState.SELECTED);
	}

	@Override
	public void mouseReleased(PInputEvent event) {
		button.setButtonState(AbstractButton.ButtonState.DEFAULT);
	}

}

/**
 * Changes the mouse cursor to a hand when it enters the object
 * 
 * @author Shu Wu
 */
class HandCursorHandler extends PBasicInputEventHandler {
	private Cursor handCursor;

	@Override
	public void mouseEntered(PInputEvent event) {
		if (handCursor == null) {
			handCursor = new Cursor(Cursor.HAND_CURSOR);
			event.getComponent().pushCursor(handCursor);
		}
	}

	@Override
	public void mouseExited(PInputEvent event) {
		if (handCursor != null) {
			handCursor = null;
			event.getComponent().popCursor();
		}
	}

}
