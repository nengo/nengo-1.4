package ca.shu.ui.lib.objects;

import java.awt.Color;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.handlers.HandCursorHandler;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

public abstract class Button extends WorldObjectImpl {

	protected Color defaultColor = Color.white;

	protected Color highlightColor = Color.lightGray;

	protected Color selectedColor = Color.darkGray;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Runnable action;

	public Button(Runnable action) {
		super();
		this.action = action;

		this.setChildrenPickable(false);

		this.addInputEventListener(new HandCursorHandler());
		this.addInputEventListener(new ButtonHandler(this));
		// buttonStateChanged();
	}

	private ButtonState buttonState = ButtonState.DEFAULT;

	public void setButtonState(ButtonState pState) {
		buttonState = pState;
		buttonStateChanged();
	}

	public abstract void buttonStateChanged();

	public void doAction() {
		if (action != null) {
			SwingUtilities.invokeLater(action);

			// (new Thread(action)).start();
			// SwingUtilities.invokeLater(action);
		}
	}

	public static enum ButtonState {
		DEFAULT, HIGHLIGHT, SELECTED
	}

	public Color getDefaultColor() {
		return defaultColor;
	}

	public void setDefaultColor(Color btnDefaultColor) {
		this.defaultColor = btnDefaultColor;
		setButtonState(buttonState);
	}

	public Color getHighlightColor() {
		return highlightColor;
	}

	public void setHighlightColor(Color btnHighlightColor) {
		this.highlightColor = btnHighlightColor;
		setButtonState(buttonState);
	}

	public Color getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(Color btnSelectedColor) {
		this.selectedColor = btnSelectedColor;
		setButtonState(buttonState);
	}

	public Runnable getAction() {
		return action;
	}

	public void setAction(Runnable action) {
		this.action = action;
	}

	protected ButtonState getButtonState() {
		return buttonState;
	}

}

class ButtonHandler extends PBasicInputEventHandler {
	Button button;

	public ButtonHandler(Button button) {
		super();
		this.button = button;
	}

	@Override
	public void processEvent(PInputEvent event, int type) {
		// TODO Auto-generated method stub
		super.processEvent(event, type);
		// event.setHandled(true);
	}

	@Override
	public void mouseClicked(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseClicked(event);
		button.doAction();

	}

	@Override
	public void mouseEntered(PInputEvent event) {
		// TODO Auto-generated method stub
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
}
