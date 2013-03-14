package ca.nengo.ui.lib.world.piccolo.objects.icons;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

public class SaveIcon extends LayoutIconBase {
    public SaveIcon(int size) {
        super(size);
    }

    @Override
    protected void paintIcon(Graphics2D g2) {
        int rectangleSize = getSize() - PADDING * 2;

        // Line
        g2.drawLine(PADDING, rectangleSize + PADDING,
                rectangleSize + PADDING, rectangleSize + PADDING);

        // Arrow
        GeneralPath path = new GeneralPath();
        path.moveTo(rectangleSize / 2.0 + PADDING, PADDING);
        path.lineTo(rectangleSize / 2.0 + PADDING, rectangleSize);

        // left tick
        path.lineTo(PADDING * 2, (getSize() / 2.0) - 1);

        // right tick
        path.moveTo(rectangleSize / 2.0 + PADDING, rectangleSize);
        path.lineTo(rectangleSize, (getSize() / 2.0) - 1);
        g2.draw(path);
    }
}