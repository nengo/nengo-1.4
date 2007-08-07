package ca.shu.ui.lib.objects.lines;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.util.PPaintContext;

public class LineInIcon extends WorldObjectImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final int LINE_IN_HEIGHT = 30;

	static final int LINE_IN_WIDTH = 30;

	Color color = Style.COLOR_LINEIN;

	public LineInIcon() {
		super();
		this.setBounds(0, 0, LINE_IN_WIDTH, LINE_IN_HEIGHT);
		this.setDraggable(false);

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
		// super.paintComponent(g);
		Area a1 = new Area(new Ellipse2D.Double(0, 0, LINE_IN_WIDTH,
				LINE_IN_HEIGHT));
		a1.exclusiveOr(new Area(new Ellipse2D.Double(5.0, 5.0,
				LINE_IN_WIDTH - 10.0, LINE_IN_HEIGHT - 10.0)));
		Graphics2D g2 = (Graphics2D) paintContext.getGraphics();
		// g2.setColor(new Color(200,200,240));
		g2.setColor(getColor());
		g2.fill(a1);
		// g2.fillOval(0,0,LINE_IN_WIDTH,LINE_IN_HEIGHT);
	}

}
