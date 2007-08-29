package ca.neo.ui.models.tooltips;

import java.util.Collection;
import java.util.Vector;

/**
 * Builds tooltips from parts
 * 
 * @author Shu Wu
 * 
 */
public class TooltipBuilder {
	private String name;

	private Vector<ITooltipPart> tooltipParts;

	/**
	 * @param name
	 *            Name of this tooltip
	 */
	public TooltipBuilder(String name) {
		super();
		this.name = name;
		tooltipParts = new Vector<ITooltipPart>(10);
	}

	/**
	 * @return Name of this tooltip
	 */
	protected String getName() {
		return name;
	}

	/**
	 * @return Collection of tooltip parts
	 */
	protected Collection<ITooltipPart> getParts() {
		return tooltipParts;
	}

	/**
	 * @param part
	 *            Tooltip part to add
	 */
	public void addPart(ITooltipPart part) {
		tooltipParts.add(part);

	}

}
