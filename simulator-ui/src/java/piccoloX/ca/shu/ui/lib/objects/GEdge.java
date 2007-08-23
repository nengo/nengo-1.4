package ca.shu.ui.lib.objects;

import java.awt.Color;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.nodes.PPath;

public class GEdge extends PPath implements PropertyChangeListener {

	// protected PNode arrow;

	private static final long serialVersionUID = 1L;

	private Color defaultColor = Style.COLOR_LINE;

	private Color highlightColor = Style.COLOR_LINE_HIGHLIGHT;

	private State state;

	protected WorldObject endNode;

	protected WorldObject startNode;

	boolean hideByDefault;

	double minArcRadius = 200;

	LineShape myLineType = LineShape.STRAIGHT;

	public GEdge(WorldObject startNode, WorldObject endNode) {
		super();
		this.startNode = startNode;
		this.endNode = endNode;

		setPickable(false);

		// arrow = PPath.createRectangle(0, 0, 100, 100);
		// addChild(arrow);

		startNode.addPropertyChangeListener(WorldObject.PROPERTY_EDGES, this);
		startNode.addPropertyChangeListener(WorldObject.PROPERTY_STATE, this);

		endNode.addPropertyChangeListener(WorldObject.PROPERTY_EDGES, this);
		endNode.addPropertyChangeListener(WorldObject.PROPERTY_STATE, this);

		setState(State.DEFAULT);
	}

	public void destroy() {
		removeFromParent();
		startNode
				.removePropertyChangeListener(WorldObject.PROPERTY_EDGES, this);
		startNode
				.removePropertyChangeListener(WorldObject.PROPERTY_STATE, this);

		endNode.removePropertyChangeListener(WorldObject.PROPERTY_EDGES, this);
		endNode.removePropertyChangeListener(WorldObject.PROPERTY_STATE, this);
	}

	public Color getDefaultColor() {
		return defaultColor;
	}

	public WorldObject getEndNode() {
		return endNode;
	}

	public Color getHighlightColor() {
		return highlightColor;
	}

	public WorldObject getStartNode() {
		return startNode;
	}

	public State getState() {
		return state;
	}

	public boolean isHideByDefault() {
		return hideByDefault;
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		updateEdge();

		if (startNode.getState() != WorldObject.State.DEFAULT
				|| endNode.getState() != WorldObject.State.DEFAULT) {
			setState(State.HIGHLIGHT);
		} else {
			setState(State.DEFAULT);
		}

	}

	public void setDefaultColor(Color defaultColor) {
		this.defaultColor = defaultColor;
		stateChanged();
	}

	/*
	 * Hides the edge in its default state (ie. mouse is not over the connecting
	 * node)
	 */
	public void setHideByDefault(boolean hideByDefault) {
		if (hideByDefault)
			this.setVisible(false);
		this.hideByDefault = hideByDefault;
	};

	public void setHighlightColor(Color highlightColor) {
		this.highlightColor = highlightColor;
		stateChanged();
	}

	public void setLineShape(LineShape lineShape) {
		this.myLineType = lineShape;
		updateEdge();
	}

	public void setMinArcRadius(double minArcRadius) {
		this.minArcRadius = minArcRadius;
	}

	public final void setState(State state) {
		if (this.state != state) {
			this.state = state;

			stateChanged();
		}

	}

	public void updateEdge() {
		reset();

		/**
		 * Remove this object if both start and endNodes are destroyed
		 */
		if (startNode.isDestroyed() || endNode.isDestroyed()) {
			destroy();
			return;
		}

		// Note that the node's "FullBounds" must be used (instead of just the
		// "Bound")
		// because the nodes have non-identity transforms which must be included
		// when
		// determining their position.
		Point2D startBounds = toLocal(startNode, startNode.getBounds()
				.getCenter2D());
		Point2D endBounds = toLocal(endNode, endNode.getBounds().getCenter2D());

		switch (myLineType) {
		case STRAIGHT:
			this.moveTo((float) startBounds.getX(), (float) startBounds.getY());
			this.lineTo((float) endBounds.getX(), (float) endBounds.getY());
			break;
		case UPWARD_ARC:
			createArc(startBounds, endBounds, true);

			break;
		case DOWNWARD_ARC:
			createArc(startBounds, endBounds, false);
			break;
		}

	}

	private void createArc(Point2D startBounds, Point2D endBounds,
			boolean isUpward) {
		/*
		 * Find center of arc to connect starting and ending nodes This
		 * algorithm was derived on paper, no explanation provided
		 */

		double deltaX = endBounds.getX() - startBounds.getX();
		double deltaY = endBounds.getY() - startBounds.getY();

		double distance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));

		double arcRadius = minArcRadius;

		if (arcRadius < (distance / 2d)) {
			arcRadius = (distance / 2d);
		}

		double ang1 = Math.atan2(-deltaY, deltaX);

		double ang1b = Math.acos(distance / (2d * arcRadius));
		double vertical = Math.sin(ang1b) * (2d * arcRadius);

		double ang2 = Math.atan2(vertical, distance);

		double ang3;

		if (isUpward) {
			if (deltaX >= 0) {
				ang3 = ang1 + ang2;
			} else {
				ang3 = ang1 - ang2;
			}
		} else {
			if (deltaX < 0) {
				ang3 = ang1 + ang2;
			} else {
				ang3 = ang1 - ang2;
			}
		}

		double deltaToCenterX = (Math.cos(ang3) * arcRadius);
		double deltaToCenterY = -(Math.sin(ang3) * arcRadius);

		double circleCenterX = startBounds.getX() + deltaToCenterX;
		double circleCenterY = startBounds.getY() + deltaToCenterY;

		double x = circleCenterX - arcRadius;
		double y = circleCenterY - arcRadius;

		double startXFromCenter = startBounds.getX() - circleCenterX;
		double startYFromCenter = startBounds.getY() - circleCenterY;

		double endXFromCenter = endBounds.getX() - circleCenterX;
		double endYFromCenter = endBounds.getY() - circleCenterY;
		double start = Math.toDegrees(Math.atan2(-startYFromCenter,
				startXFromCenter));

		double end = Math
				.toDegrees(Math.atan2(-endYFromCenter, endXFromCenter));

		if (isUpward) {
			if (deltaX > 0) {
				double oldEnd = end;
				end = start;
				start = oldEnd;

			}

		} else {
			if (deltaX < 0) {
				double oldEnd = end;
				end = start;
				start = oldEnd;

			}

		}
		double extent = end - start;
		if (extent <= 0) {
			extent = 360 + extent;

		}
		Arc2D arc = new Arc2D.Double(x, y, arcRadius * 2, arcRadius * 2, start,
				extent, Arc2D.OPEN);

		append(arc, false);
	}

	private void stateChanged() {

		if (isHideByDefault() && state == State.DEFAULT)
			setVisible(false);
		else
			setVisible(true);

		switch (state) {
		case DEFAULT:
			this.setStrokePaint(defaultColor);
			// this.setPaint(defaultColor);
			// arrow.setPaint(defaultColor);

			break;
		case HIGHLIGHT:
			this.setStrokePaint(highlightColor);
			// this.setPaint(highlightColor);
			// arrow.setPaint(highlightColor);
			break;

		}
		this.repaint();
	}

	protected Point2D toLocal(WorldObject node, double x, double y) {
		return toLocal(node, new Point2D.Double(x, y));
	}

	protected Point2D toLocal(WorldObject node, Point2D point) {
		return this.globalToLocal(node.localToGlobal(point));
	}

	public enum LineShape {
		DOWNWARD_ARC, STRAIGHT, UPWARD_ARC
	}

	public static enum State {
		DEFAULT, HIGHLIGHT
	}
}
