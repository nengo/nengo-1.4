package ca.neo.ui.models.tooltips;

import java.util.Collection;
import java.util.Iterator;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.objects.GText;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Shu
 * 
 */
public class GTooltip extends WorldObject {
	private static final long serialVersionUID = 1L;

	TooltipBuilder tooltipBuilder;
	double tooltipWidth = 400;

	public GTooltip(TooltipBuilder tooltipBuilder) {
		super();

		this.tooltipBuilder = tooltipBuilder;

		init();

	}

	public void init() {
		PText tag = new PText(tooltipBuilder.getName());
		tag.setTextPaint(Style.COLOR_FOREGROUND);
		tag.setFont(Style.FONT_LARGE);
		tag.setWidth(tooltipWidth);

		addChild(tag);

		Collection<TooltipPart> parts = tooltipBuilder.getParts();

		Iterator<TooltipPart> it = parts.iterator();

		/*
		 * Builds the tooltip string
		 */
		StringBuilder strBd = new StringBuilder(200);
		while (it.hasNext()) {
			TooltipPart part = it.next();

			strBd.append(part.toString() + "\n");
		}
		GText propertyText = new GText(strBd.toString());
		propertyText.setOffset(0, tag.getHeight() + 10);
		propertyText.setWidth(tooltipWidth);
		addChild(propertyText);

		setBounds(parentToLocal(getFullBounds()));
	}

}
