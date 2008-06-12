package ca.shu.ui.lib.world.piccolo.objects;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.world.piccolo.primitives.Image;
import ca.shu.ui.lib.world.piccolo.primitives.Path;

/**
 * A button which uses an image as an representation
 * 
 * @author Shu Wu
 */
public class ImageButton extends AbstractButton {

	private static final long serialVersionUID = 1L;

	private Path buttonCover;

	public ImageButton(String imgPath, Runnable action) {
		super(action);

		Image buttonImg = new Image(imgPath);
		addChild(buttonImg);

		buttonCover = Path.createRectangle(0f, 0f, (float) buttonImg
				.getWidth(), (float) buttonImg.getHeight());
		buttonCover.setPaint(Style.COLOR_FOREGROUND);
		addChild(buttonCover);

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
