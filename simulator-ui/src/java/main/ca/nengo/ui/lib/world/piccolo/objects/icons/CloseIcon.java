package ca.nengo.ui.lib.world.piccolo.objects.icons;

import java.awt.Graphics2D;

public class CloseIcon extends WindowIconBase {
	public CloseIcon(int size) {
		super(size);
	}

	@Override
	protected void paintIcon(Graphics2D g2) {
		int rectangleSize = getSize() - PADDING;
		g2.drawLine(PADDING, PADDING, rectangleSize, rectangleSize);
		g2.drawLine(PADDING, rectangleSize, rectangleSize, PADDING);
	}
}