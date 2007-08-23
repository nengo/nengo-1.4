package ca.shu.ui.lib.objects.lines;

import java.awt.Color;
import java.awt.Graphics2D;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.util.PPaintContext;

public class LineEndWellIcon extends WorldObject {

	public static final int _LINE_END_HEIGHT = 30;

	public static final int _LINE_END_WIDTH = 30;

	public static final double ICON_RADIUS = Math
			.sqrt((_LINE_END_WIDTH * _LINE_END_WIDTH)
					+ (_LINE_END_HEIGHT * _LINE_END_HEIGHT)) / 2;
	private static final long serialVersionUID = 1L;
	private Color color = Style.COLOR_FOREGROUND;

	public LineEndWellIcon() {
		super();
		this.setBounds(0, 0, _LINE_END_WIDTH, _LINE_END_HEIGHT);
		setColor(Style.COLOR_LINEENDWELL);

	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	protected void paint(PPaintContext paintContext) {
		// TODO Auto-generated method stub
		super.paint(paintContext);
		Graphics2D g2 = (Graphics2D) paintContext.getGraphics();
		Color bright2 = Util.colorAdd(color, new Color(0.4f, 0.4f, 0.4f));
		if (paintContext.getScale() < 0.5) {
			g2.setColor(color);
			g2.fillOval(0, 0, _LINE_END_WIDTH, _LINE_END_HEIGHT);
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
			g2.fillOval(0, 0, _LINE_END_WIDTH, _LINE_END_HEIGHT);
			g2.setColor(medium);
			g2.fillOval(_LINE_END_WIDTH / 4, 0, _LINE_END_WIDTH / 2,
					_LINE_END_HEIGHT);
			g2.setColor(bright1);
			g2.fillOval(_LINE_END_WIDTH / 6, _LINE_END_HEIGHT / 2,
					2 * _LINE_END_WIDTH / 3, _LINE_END_HEIGHT / 3);
			g2.setColor(bright2);
			g2.fillOval(_LINE_END_WIDTH / 6 + 2, _LINE_END_HEIGHT / 2 + 2,
					2 * _LINE_END_WIDTH / 3 - 4, _LINE_END_HEIGHT / 3 - 2);
			// ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f);
			// g2.setComposite(ac);
			g2.setColor(hilite);
			g2.fillOval(_LINE_END_WIDTH / 3 - 1, _LINE_END_HEIGHT / 6,
					_LINE_END_WIDTH / 3 + 2, 3 * _LINE_END_HEIGHT / 16);
		}
	}

}
