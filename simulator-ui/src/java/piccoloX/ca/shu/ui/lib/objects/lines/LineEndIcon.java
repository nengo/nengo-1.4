package ca.shu.ui.lib.objects.lines;

import java.awt.geom.Point2D;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.objects.GEdge;

public class LineEndIcon extends LineEndWellIcon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	LineEnd parent;

	public LineEndIcon(LineEnd parent) {
		super();
		this.parent = parent;
		addChild(new PointerTriangle(this));
	}

}

class PointerTriangle extends GEdge {

	@Override
	public void updateEdge() {
		updatePointer();
	}

	static final double TRIANGLE_EDGE_LENGTH = 20;

	private static final long serialVersionUID = 1L;

	public PointerTriangle(LineEndIcon lineEndIcon) {
		super(lineEndIcon.parent.getWell(), lineEndIcon.parent);
		setPaint(Style.COLOR_FOREGROUND);

		setOffset(lineEndIcon.getWidth() / 2, lineEndIcon.getHeight() / 2);
		setBounds(-TRIANGLE_EDGE_LENGTH / 2, -TRIANGLE_EDGE_LENGTH / 2,
				TRIANGLE_EDGE_LENGTH, TRIANGLE_EDGE_LENGTH);
	}

	protected void updatePointer() {

		/*
		 * Find the angle between well and end
		 */
		// LineEndWell well = getStartNode();
		Point2D startPosition = getStartNode().localToGlobal(
				getStartNode().getBounds().getOrigin());
		Point2D endPosition = getEndNode().localToGlobal(
				getEndNode().getBounds().getOrigin());

		double deltaX = endPosition.getX() - startPosition.getX();
		double deltaY = endPosition.getY() - startPosition.getY();

		double angle = getAngle(deltaX, deltaY);

		double x = Math.cos(angle + Math.PI) * LineEndWellIcon.ICON_RADIUS;
		double y = Math.sin(angle + Math.PI) * LineEndWellIcon.ICON_RADIUS;
		Point2D point0 = new Point2D.Double(x, y);

		// System.out.println("angle: " + angle);
		x += Math.cos(angle + Math.PI * (5d / 6d)) * TRIANGLE_EDGE_LENGTH;
		y += Math.sin(angle + Math.PI * (5d / 6d)) * TRIANGLE_EDGE_LENGTH;
		Point2D point1 = new Point2D.Double(x, y);

		x += Math.cos(angle + Math.PI * (3d / 2d)) * TRIANGLE_EDGE_LENGTH;
		y += Math.sin(angle + Math.PI * (3d / 2d)) * TRIANGLE_EDGE_LENGTH;
		Point2D point2 = new Point2D.Double(x, y);

		Point2D[] path = { point0, point1, point2 };

		// this.setPaint(Color.black);
		this.setPathToPolyline(path);
		this.closePath();

	}

	public static double getAngle(double x, double y) {
		if (x == 0) {
			if (y < 0) {
				return Math.PI / 4;
			} else {
				return -Math.PI / 4;
			}
		} else if (x < 0) {
			return Math.atan(y / x) + Math.PI;
		} else {
			return Math.atan(y / x);
		}
	}
}
