package ca.shu.ui.lib.objects;

import java.awt.Font;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class GTextButton extends Button {
	private static final int BORDER_HEIGHT = 3;

	private static final int BORDER_WIDTH = 5;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8982670127440624596L;

	private final PPath frame;

	private final PText pText;

	public GTextButton(String value, Runnable action) {
		super(action);

		pText = new PText("");
		pText.setOffset(BORDER_WIDTH, BORDER_HEIGHT);

		frame = PPath.createRectangle(0, 0, 100, 100);

		// pBorder.setBounds(0f, 0f,
		// (float) (pText.getWidth() + 2 * BORDER_WIDTH), (float) (pText
		// .getHeight() + 2 * BORDER_HEIGHT));

		addChild(frame);
		addChild(pText);

		this.setText(value);
		buttonStateChanged();
	}

	@Override
	public void buttonStateChanged() {
		ButtonState buttonState = getButtonState();

		switch (buttonState) {
		case DEFAULT:
			frame.setPaint(defaultColor);

			break;
		case HIGHLIGHT:
			frame.setPaint(highlightColor);
			break;
		case SELECTED:
			frame.setPaint(selectedColor);
			break;
		}
	}

	public PPath getFrame() {
		return frame;
	}

	public PText getText() {
		return pText;
	}

	public void recomputeBounds() {

		frame.setBounds(0f, 0f, (float) (pText.getWidth() + 2 * BORDER_WIDTH),
				(float) (pText.getHeight() + 2 * BORDER_HEIGHT));
		this.setBounds(frame.getBounds());

		// setBounds(getFullBounds());
	}

	public void setFont(Font font) {
		pText.setFont(font);
	}

	public void setText(String str) {
		pText.setText(str);

		recomputeBounds();

	}
}
