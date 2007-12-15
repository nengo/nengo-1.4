package ca.neo.ui.models.tooltips;

/**
 * Title Tooltip Part
 * 
 * @author Shu Wu
 * 
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
	public String getTooltipString() {
		return "*** " + titleName + " ***";
	}

}
