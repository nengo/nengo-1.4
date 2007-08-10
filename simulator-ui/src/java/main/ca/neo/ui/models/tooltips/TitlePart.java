package ca.neo.ui.models.tooltips;

public class TitlePart extends TooltipPart {
	String titleName;

	public TitlePart(String titleName) {
		super();
		this.titleName = titleName;
	}

	@Override
	public String toString() {
		return "*** " + titleName + " ***";
	}

}
