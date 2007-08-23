package ca.neo.ui.models.tooltips;

import java.util.Collection;
import java.util.Vector;

public class TooltipBuilder {
	String name;

	Vector<TooltipPart> tooltipParts;

	public TooltipBuilder(String name) {
		super();
		this.name = name;
		tooltipParts = new Vector<TooltipPart>(10);
	}

	public void addPart(TooltipPart part) {
		tooltipParts.add(part);

	}

	protected String getName() {
		return name;
	}

	protected Collection<TooltipPart> getParts() {
		return tooltipParts;
	}

}
