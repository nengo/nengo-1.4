package ca.shu.ui.lib.objects;

import ca.shu.ui.lib.world.WorldObject;

public class GTooltip extends WorldObject {

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

		setSelectable(false);

	}

}
