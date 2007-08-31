package ca.shu.ui.lib.objects;

import ca.shu.ui.lib.Style.Style;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * A button which uses an image as an representation
 * 
 * @author Shu Wu
 */
public class ImageButton extends AbstractButton {

	private static final long serialVersionUID = 1L;

	private PPath buttonCover;

	public ImageButton(String imgPath, Runnable action) {
		super(action);

		PImage buttonImg = new PImage(imgPath);
		addChild(buttonImg);

		buttonCover = PPath.createRectangle(0f, 0f, (float) buttonImg
				.getWidth(), (float) buttonImg.getHeight());
		buttonCover.setPaint(Style.COLOR_FOREGROUND);

		initDefaultState();
		this.setWidth(buttonImg.getWidth());
		this.setHeight(buttonImg.getHeight());
	}

	private void initDefaultState() {
		buttonCover.setTransparency(0f);

	}

	@Override
	public void stateChanged() {
		ButtonState state = getState();

		switch (state) {
		case DEFAULT:
			initDefaultState();
			break;
		case HIGHLIGHT:
			buttonCover.setTransparency(0.2f);
			break;
		case SELECTED:
			buttonCover.setTransparency(0.4f);
			break;
		}

	}

}
