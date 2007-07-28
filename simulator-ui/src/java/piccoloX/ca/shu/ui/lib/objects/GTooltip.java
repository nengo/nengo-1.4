package ca.shu.ui.lib.objects;

import ca.shu.ui.lib.world.impl.WorldObjectImpl;

public class GTooltip extends WorldObjectImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GTooltip() {
		this("");
	}

	public GTooltip(String titleStr) {
		
		super(titleStr);
		setWidth(600);
		getLayoutManager().setLeftPadding(0);
		getLayoutManager().setVerticalPadding(0);

		setDraggable(false);
		setFrameVisible(false);
	}

}
