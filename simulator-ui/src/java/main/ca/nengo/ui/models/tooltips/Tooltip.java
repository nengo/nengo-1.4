package ca.nengo.ui.models.tooltips;

import java.util.Collection;
import java.util.Iterator;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * UI Object which builds itself from a ToolTipBuilder
 * 
 * @author Shu Wu
 */
public class Tooltip extends WorldObjectImpl {
	private static final long serialVersionUID = 1L;

	private TooltipBuilder tooltipBuilder;
	private double tooltipWidth;
	public static final double DEFAULT_WIDTH = 250;

	public Tooltip(TooltipBuilder tooltipBuilder) {
		this(tooltipBuilder, DEFAULT_WIDTH);
	}

	public Tooltip(TooltipBuilder tooltipBuilder, double width) {
		super();

		this.tooltipBuilder = tooltipBuilder;
		this.tooltipWidth = width;
		init();
	}

	private void init() {
		PText tag = new PText(tooltipBuilder.getName());
		tag.setConstrainWidthToTextWidth(false);
		tag.setTextPaint(Style.COLOR_FOREGROUND);
		tag.setFont(Style.FONT_LARGE);
		tag.setWidth(tooltipWidth);
		int layoutY = 0;
		getPiccolo().addChild(tag);

		layoutY += tag.getHeight() + 10;

		Collection<ITooltipPart> parts = tooltipBuilder.getParts();

		Iterator<ITooltipPart> it = parts.iterator();

		/*
		 * Builds the tooltip parts
		 */

		while (it.hasNext()) {
			ITooltipPart part = it.next();
			WorldObject wo = part.toWorldObject(tooltipWidth);

			wo.setOffset(wo.getOffset().getX(), layoutY);

			addChild(wo);

			layoutY += wo.getHeight();
		}

		setBounds(parentToLocal(getFullBounds()));
	}

}
