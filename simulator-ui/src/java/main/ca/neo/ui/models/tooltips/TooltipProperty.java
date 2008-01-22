package ca.neo.ui.models.tooltips;

import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.piccolo.primitives.Text;

/**
 * Tooltip Part describing properties
 * 
 * @author Shu Wu
 */
class TooltipProperty implements ITooltipPart {
	private String propertyName;
	private String propertyValue;

	public TooltipProperty(String propertyName, String propertyValue) {
		super();
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.models.tooltips.ITooltipPart#toWorldObject()
	 */
	public WorldObject toWorldObject(double width) {
		Text propertyText = new Text(propertyName + ": " + propertyValue);

		propertyText.setConstrainWidthToTextWidth(false);
		propertyText.setWidth(width);
		propertyText.recomputeLayout();

		return propertyText;
	}

}