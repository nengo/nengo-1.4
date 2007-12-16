package ca.neo.ui.models.tooltips;

import java.util.Collection;
import java.util.Iterator;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.objects.PXText;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * UI Object which builds itself from a ToolTipBuilder
 * 
 * @author Shu Wu
 * 
 */
public class Tooltip extends WorldObject {
	private static final long serialVersionUID = 1L;

	private TooltipBuilder tooltipBuilder;
	private double tooltipWidth = 400;

	public Tooltip(TooltipBuilder tooltipBuilder) {
		super();

		this.tooltipBuilder = tooltipBuilder;

		init();

	}

	private void init() {
		PText tag = new PText(tooltipBuilder.getName());
		tag.setTextPaint(Style.COLOR_FOREGROUND);
		tag.setFont(Style.FONT_LARGE);
		tag.setWidth(tooltipWidth);

		addChild(tag);

		Collection<ITooltipPart> parts = tooltipBuilder.getParts();

		Iterator<ITooltipPart> it = parts.iterator();

		/*
		 * Builds the tooltip string
		 */
		StringBuilder strBd = new StringBuilder(200);
		while (it.hasNext()) {
			ITooltipPart part = it.next();

			strBd.append(part.getTooltipString() + "\n");
		}
		PXText propertyText = new PXText(strBd.toString());
		propertyText.setOffset(0, tag.getHeight() + 10);
		propertyText.setWidth(tooltipWidth);
		addChild(propertyText);

		setBounds(parentToLocal(getFullBounds()));
	}

}
