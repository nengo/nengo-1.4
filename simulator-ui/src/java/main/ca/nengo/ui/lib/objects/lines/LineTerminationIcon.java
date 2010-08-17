package ca.nengo.ui.lib.objects.lines;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import ca.nengo.ui.lib.Style.Style;
import ca.nengo.ui.lib.world.PaintContext;
import ca.nengo.ui.lib.world.piccolo.WorldObjectImpl;

/**
 * Standard Icon for a line end holder
 * 
 * @author Shu Wu
 */
public class LineTerminationIcon extends WorldObjectImpl {

	private static final long serialVersionUID = 1L;

	static final int LINE_IN_HEIGHT = 30;

	static final int LINE_IN_WIDTH = 30;

	private Color myColor = Style.COLOR_LINEIN;

	public LineTerminationIcon() {
		super();
		this.setBounds(0, 0, LINE_IN_WIDTH, LINE_IN_HEIGHT);
	}

	@Override
	public void paint(PaintContext paintContext) {
		super.paint(paintContext);

		Area a1 = new Area(new Ellipse2D.Double(0, 0, LINE_IN_WIDTH,
				LINE_IN_HEIGHT));
		a1.exclusiveOr(new Area(new Ellipse2D.Double(5.0, 5.0,
				LINE_IN_WIDTH - 10.0, LINE_IN_HEIGHT - 10.0)));
		Graphics2D g2 = paintContext.getGraphics();
		g2.setColor(getColor());
		g2.fill(a1);
	}

	public Color getColor() {
		return myColor;
	}

	public void setColor(Color color) {
		this.myColor = color;
	}

}
