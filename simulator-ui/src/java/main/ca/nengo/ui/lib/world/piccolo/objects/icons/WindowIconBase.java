package ca.nengo.ui.lib.world.piccolo.objects.icons;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import ca.nengo.ui.lib.Style.Style;
import ca.nengo.ui.lib.world.PaintContext;
import ca.nengo.ui.lib.world.piccolo.WorldObjectImpl;

public abstract class WindowIconBase extends WorldObjectImpl {

	private int size;
	private static int STROKE_WIDTH = 3;
	public static int PADDING = 6;

	public WindowIconBase(int size) {
		this.size = size;
		this.setBounds(0, 0, size, size);
	}

	public int getSize() {
		return size;
	}

	@Override
	public void paint(PaintContext paintContext) {
		super.paint(paintContext);
		Graphics2D g2 = paintContext.getGraphics();
		g2.setColor(Style.COLOR_FOREGROUND);
		g2.setStroke(new BasicStroke(STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		paintIcon(g2);
	}

	protected abstract void paintIcon(Graphics2D g2);
}