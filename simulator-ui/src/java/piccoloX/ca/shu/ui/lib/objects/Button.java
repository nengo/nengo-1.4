package ca.shu.ui.lib.objects;

import java.awt.Color;

import ca.shu.ui.lib.handlers.HandCursorHandler;
import ca.shu.ui.lib.world.impl.WorldObject;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

public class Button extends WorldObject {

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
	}

	public State state = State.DEFAULT;

	public void setState(State pState) {
		state = pState;

	}

	public void doAction() {
		if (action != null) {
			(new Thread(action)).start();
			//SwingUtilities.invokeLater(action);
		}
	}

	public static enum State {
		DEFAULT, HIGHLIGHT, SELECTED
	}

	public Color getDefaultColor() {
		return defaultColor;
	}

	public void setDefaultColor(Color btnDefaultColor) {
		this.defaultColor = btnDefaultColor;
		setState(state);
	}

	public Color getHighlightColor() {
		return highlightColor;
	}

	public void setHighlightColor(Color btnHighlightColor) {
		this.highlightColor = btnHighlightColor;
		setState(state);
	}

	public Color getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(Color btnSelectedColor) {
		this.selectedColor = btnSelectedColor;
		setState(state);
	}

	public Runnable getAction() {
		return action;
	}

	public void setAction(Runnable action) {
		this.action = action;
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
//		event.setHandled(true);
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
		button.setState(GTextButton.State.HIGHLIGHT);

	}

	@Override
	public void mouseExited(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseExited(event);
		button.setState(GTextButton.State.DEFAULT);

	}

	@Override
	public void mousePressed(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mousePressed(event);
		button.setState(GTextButton.State.SELECTED);

	}

	@Override
	public void mouseReleased(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseReleased(event);
		button.setState(GTextButton.State.HIGHLIGHT);
	}
}
