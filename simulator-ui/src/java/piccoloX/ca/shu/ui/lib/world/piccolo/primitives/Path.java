package ca.shu.ui.lib.world.piccolo.primitives;

import java.awt.Paint;
import java.awt.geom.Point2D;

import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;

public class Path extends WorldObjectImpl {
	private PXPath pathNode;

	private Path(PXPath path) {
		super(path);
		pathNode = (PXPath) getPiccolo();
		setPickable(false);
		setSelectable(false);
	}

	public static Path createRectangle(float x, float y, float width,
			float height) {
		return new Path(PXPath.createRectangle(x, y, width, height));
	}

	public static Path createEllipse(float x, float y, float width, float height) {
		return new Path(PXPath.createEllipse(x, y, width, height));
	}

	public static Path createLine(float x1, float y1, float x2, float y2) {
		return new Path(PXPath.createLine(x1, y1, x2, y2));
	}

	public static Path createPolyline(Point2D[] points) {
		return new Path(PXPath.createPolyline(points));
	}

	public static Path createPolyline(float[] xp, float[] yp) {
		return new Path(PXPath.createPolyline(xp, yp));
	}

	public Paint getStrokePaint() {
		return pathNode.getStrokePaint();
	}

	public void setStrokePaint(Paint paint) {
		pathNode.setStrokePaint(paint);
	}
}
