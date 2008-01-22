package ca.neo.ui.models.tooltips;

import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.piccolo.primitives.Text;

/**
 * Title Tooltip Part
 * 
 * @author Shu Wu
 */
class TooltipTitle implements ITooltipPart {
	String titleName;

	public TooltipTitle(String titleName) {
		super();
		this.titleName = titleName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.models.tooltips.ITooltipPart#getTooltipString()
	 */
	public WorldObject toWorldObject(double width) {
		Text propertyText = new Text("*** " + titleName + " ***");

		propertyText.setConstrainWidthToTextWidth(false);
		propertyText.setWidth(width);
		propertyText.recomputeLayout();

		return propertyText;

	}

}
