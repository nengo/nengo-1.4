package ca.nengo.ui.lib.world.piccolo.objects;

import java.awt.Font;
import java.awt.Paint;

import ca.nengo.ui.lib.Style.Style;
import ca.nengo.ui.lib.world.piccolo.primitives.Path;
import ca.nengo.ui.lib.world.piccolo.primitives.Text;

/**
 * A Button whose represntation is a text label
 * 
 * @author Shu Wu
 */
public class TextButton extends AbstractButton {

	private static final int BORDER_HEIGHT = 3;

	private static final int BORDER_WIDTH = 5;

	private static final long serialVersionUID = -8982670127440624596L;

	private final Path frame;

	private final Text myTextNode;

	/**
	 * @param textLabel
	 *            Button label
	 * @param action
	 *            Action to execute when the button is pressed
	 */
	public TextButton(String textLabel, Runnable action) {
		super(action);

		myTextNode = new Text("");
		myTextNode.setOffset(BORDER_WIDTH, BORDER_HEIGHT);
		myTextNode.setFont(Style.FONT_BUTTONS);
		myTextNode.setTextPaint(Style.COLOR_FOREGROUND);

		frame = Path.createRectangle(0, 0, 100, 100);
		frame.setStrokePaint(Style.COLOR_BUTTON_BORDER);

		addChild(frame);
		addChild(myTextNode);

		setText(textLabel);

		stateChanged();
	}

	public Path getFrame() {
		return frame;
	}

	public Text getText() {
		return myTextNode;
	}

	public void updateBounds() {

		frame.setBounds(0f, 0f,
				(float) (myTextNode.getWidth() + 2 * BORDER_WIDTH),
				(float) (myTextNode.getHeight() + 2 * BORDER_HEIGHT));
		setBounds(frame.getBounds());
	}

	public void setFont(Font font) {
		myTextNode.setFont(font);
		updateBounds();
	}

	public void setText(String textLabel) {
		myTextNode.setText(textLabel);
		updateBounds();
	}

	@Override
	public void stateChanged() {
		ButtonState buttonState = getState();

		switch (buttonState) {
		case DEFAULT:
			frame.setPaint(getDefaultColor());
			break;
		case HIGHLIGHT:
			frame.setPaint(getHighlightColor());
			break;
		case SELECTED:
			frame.setPaint(getSelectedColor());
			break;
		}
		repaint();
	}

	public void setStrokePaint(Paint paint) {
		frame.setStrokePaint(paint);
	}
}
