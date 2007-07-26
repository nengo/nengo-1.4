package ca.shu.ui.lib.objects.lines;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import ca.neo.ui.style.Style;
import edu.umd.cs.piccolo.util.PPaintContext;

public class LineIn extends LineHolder implements ILineAcceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final int LINE_IN_HEIGHT = 30;

	static final int LINE_IN_WIDTH = 30;

	Color color = Style.LINEIN_COLOR;

	LineEnd lineEnd;

	public LineIn() {
		super();
		this.setBounds(0, 0, LINE_IN_WIDTH, LINE_IN_HEIGHT);
		this.setDraggable(false);

	}

	public boolean connect(LineEnd lineEnd) {
		if (this.lineEnd == null) {

			this.lineEnd = lineEnd;

			Point2D position = lineEnd.localToGlobal(new Point2D.Double(0, 0));
			globalToLocal(position);

			lineEnd.removeFromParent();
			addChild(lineEnd);
			lineEnd.setOffset(position.getX(), position.getY());

			lineEnd.animateToPosition(0, 0, 500);

			return true;
		} else {
			// There is already a lineEnd connected
			return false;
		}
	}

	public void disconnect() {
		
		
		lineEnd = null;		
	}

	public Color getColor() {
		return color;
	}

	@Override
	public void layoutEdges() {
		// TODO Auto-generated method stub
		super.layoutEdges();

		if (lineEnd != null)
			lineEnd.layoutEdges();
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
