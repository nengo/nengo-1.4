package ca.neo.ui.models.tooltips;

/**
 * Tooltip Part describing properties
 * 
 * @author Shu Wu
 * 
 */
public class TooltipProperty implements ITooltipPart {
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
	 * @see ca.neo.ui.models.tooltips.ITooltipPart#getTooltipString()
	 */
	public String getTooltipString() {
		return propertyName + ": " + propertyValue;
	}

}