package ca.shu.ui.lib.objects;

import ca.neo.ui.style.Style;


public class GButton extends GTextButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 773422993714826750L;

	@Override
	public void setState(State pState) {
		// TODO Auto-generated method stub
		super.setState(pState);
		
		switch (state) {
		case DEFAULT:
			getFrame().setPaint(Style.BACKGROUND_COLOR);
			

			break;
		case HIGHLIGHT:
			getFrame().setPaint(Style.BUTTON_HIGHLIGHT_COLOR);
			break;
		case SELECTED:
			getFrame().setPaint(Style.BUTTON_SELECTED_COLOR);
			break;
		}
	}

	public GButton(String value, Runnable action) {
		super(value, action);
		
		
		getText().setFont(Style.FONT_BUTTONS);
		getText().setTextPaint(Style.FOREGROUND_COLOR);
		getFrame().setPaint(Style.BACKGROUND_COLOR);
		getFrame().setStrokePaint(Style.BUTTON_BORDER_COLOR);

		recomputeBounds();
	}

}
