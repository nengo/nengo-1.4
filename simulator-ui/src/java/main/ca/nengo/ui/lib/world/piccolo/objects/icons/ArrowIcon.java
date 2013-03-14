package ca.nengo.ui.lib.world.piccolo.objects.icons;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

public class ArrowIcon extends LayoutIconBase {
    public ArrowIcon(int size) {
        super(size);
    }

    @Override
    protected void paintIcon(Graphics2D g2) {
        int rectangleSize = getSize() - PADDING * 2;
        GeneralPath path = new GeneralPath();
        path.moveTo(PADDING, rectangleSize / 2.0 + PADDING);
        path.lineTo(rectangleSize + PADDING, rectangleSize / 2.0 + PADDING);

        // up tick
        path.lineTo(rectangleSize / 1.5 + PADDING, PADDING * 2);

        // down tick
        path.moveTo(rectangleSize + PADDING, rectangleSize / 2.0 + PADDING);
        path.lineTo(rectangleSize / 1.5 + PADDING, rectangleSize);
        g2.draw(path);
    }
}