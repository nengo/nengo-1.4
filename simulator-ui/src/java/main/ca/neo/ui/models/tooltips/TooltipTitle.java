package ca.neo.ui.models.tooltips;

import ca.shu.ui.lib.world.IWorldObject;
import ca.shu.ui.lib.world.piccolo.primitives.Text;

/**
 * Title Tooltip Part
 * 
 * @author Shu Wu
 */
public class TooltipTitle implements ITooltipPart {
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
	public IWorldObject toWorldObject(double width) {
		Text propertyText = new Text("*** " + titleName + " ***");

		propertyText.setConstrainWidthToTextWidth(false);
		propertyText.setWidth(width);
		propertyText.recomputeLayout();

		return propertyText;

	}

}
