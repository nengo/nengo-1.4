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
			getFrame().setPaint(Style.COLOR_BACKGROUND);
			

			break;
		case HIGHLIGHT:
			getFrame().setPaint(Style.COLOR_BUTTON_HIGHLIGHT);
			break;
		case SELECTED:
			getFrame().setPaint(Style.COLOR_BUTTON_SELECTED);
			break;
		}
	}

	public GButton(String value, Runnable action) {
		super(value, action);
		
		
		getText().setFont(Style.FONT_BUTTONS);
		getText().setTextPaint(Style.COLOR_FOREGROUND);
		getFrame().setPaint(Style.COLOR_BACKGROUND);
		getFrame().setStrokePaint(Style.COLOR_BUTTON_BORDER);

		recomputeBounds();
	}

}
