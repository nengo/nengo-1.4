package ca.shu.ui.lib.objects.lines;

import java.awt.Color;
import java.awt.Graphics2D;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.util.PPaintContext;

public class LineEndWellIcon extends WorldObject {

	private static final long serialVersionUID = 1L;

	public LineEndWellIcon() {
		super();
		this.setBounds(0, 0, LINE_END_WIDTH, LINE_END_HEIGHT);
	}

	static final int LINE_END_WIDTH = 30;
	static final int LINE_END_HEIGHT = 30;
	static final double ICON_RADIUS = Math
			.sqrt((LINE_END_WIDTH * LINE_END_WIDTH)
					+ (LINE_END_HEIGHT * LINE_END_HEIGHT)) / 2;

	static Color color = Style.COLOR_LINEEND;

	@Override
	protected void paint(PPaintContext paintContext) {
		// TODO Auto-generated method stub
		super.paint(paintContext);
		Graphics2D g2 = (Graphics2D) paintContext.getGraphics();
		Color bright2 = Util.colorAdd(color, new Color(0.4f, 0.4f, 0.4f));
		if (paintContext.getScale() < 0.5) {
			g2.setColor(color);
			g2.fillOval(0, 0, LINE_END_WIDTH, LINE_END_HEIGHT);
		} else {

			// AlphaComposite ac;

			Color color = getColor();

			Color dark = Util.colorAdd(Util.colorTimes(color, 0.65), new Color(
					0.05f, 0.05f, 0.05f));
			Color medium = color;
			Color bright1 = Util
					.colorAdd(color, new Color(0.15f, 0.15f, 0.15f));

			Color hilite = Util.colorAdd(Util.colorTimes(color, 0.05),
					new Color(0.8f, 0.8f, 0.8f));
			// ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			// g2.setComposite(ac);
			g2.setColor(dark);
			g2.fillOval(0, 0, LINE_END_WIDTH, LINE_END_HEIGHT);
			g2.setColor(medium);
			g2.fillOval(LINE_END_WIDTH / 4, 0, LINE_END_WIDTH / 2,
					LINE_END_HEIGHT);
			g2.setColor(bright1);
			g2.fillOval(LINE_END_WIDTH / 6, LINE_END_HEIGHT / 2,
					2 * LINE_END_WIDTH / 3, LINE_END_HEIGHT / 3);
			g2.setColor(bright2);
			g2.fillOval(LINE_END_WIDTH / 6 + 2, LINE_END_HEIGHT / 2 + 2,
					2 * LINE_END_WIDTH / 3 - 4, LINE_END_HEIGHT / 3 - 2);
			// ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f);
			// g2.setComposite(ac);
			g2.setColor(hilite);
			g2.fillOval(LINE_END_WIDTH / 3 - 1, LINE_END_HEIGHT / 6,
					LINE_END_WIDTH / 3 + 2, 3 * LINE_END_HEIGHT / 16);
		}
	}

	public static Color getColor() {
		return color;
	}

	public static void setColor(Color color) {
		LineEndWellIcon.color = color;
	}

}
