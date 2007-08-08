package ca.shu.ui.lib.objects;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.nodes.PPath;

public class GEdge extends PPath implements PropertyChangeListener {

	// protected PNode arrow;

	private static final long serialVersionUID = 1L;

	private Color defaultColor = Style.COLOR_LINE;

	private Color highlightColor = Style.COLOR_LINE_HIGHLIGHT;

	private State state;

	protected WorldObjectImpl endNode;

	protected WorldObjectImpl startNode;

	boolean hideByDefault;

	public GEdge(WorldObjectImpl startNode, WorldObjectImpl endNode) {
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

	public WorldObjectImpl getEndNode() {
		return endNode;
	}

	public Color getHighlightColor() {
		return highlightColor;
	}

	public WorldObjectImpl getStartNode() {
		return startNode;
	}

	public State getState() {
		return state;
	}

	public boolean isHideByDefault() {
		return hideByDefault;
	}

	public void setDefaultColor(Color defaultColor) {
		this.defaultColor = defaultColor;
	}

	/*
	 * Hides the edge in its default state (ie. mouse is not over the connecting
	 * node)
	 */
	public void setHideByDefault(boolean hideByDefault) {
		if (hideByDefault)
			this.setVisible(false);
		this.hideByDefault = hideByDefault;
	}

	public void setHighlightColor(Color highlightColor) {
		this.highlightColor = highlightColor;
	}

	public final void setState(State state) {
		if (this.state != state) {
			this.state = state;

			stateChanged0();
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
		Point2D bound1 = toLocal(startNode, startNode.getBounds().getCenter2D());
		Point2D bound2 = toLocal(endNode, endNode.getBounds().getCenter2D());

		this.moveTo((float) bound1.getX(), (float) bound1.getY());
		this.lineTo((float) bound2.getX(), (float) bound2.getY());

		// arrow.setOffset(bound2);
	}

	private void stateChanged0() {
		if (isHideByDefault() && state == State.DEFAULT)
			setVisible(false);
		else
			setVisible(true);

		switch (state) {
		case DEFAULT:
			this.setStrokePaint(defaultColor);
			this.setPaint(defaultColor);
			// arrow.setPaint(defaultColor);

			break;
		case HIGHLIGHT:
			this.setStrokePaint(highlightColor);
			this.setPaint(highlightColor);
			// arrow.setPaint(highlightColor);
			break;
		}
		this.repaint();
	}

	protected Point2D toLocal(WorldObjectImpl node, double x, double y) {
		return toLocal(node, new Point2D.Double(x, y));
	}

	protected Point2D toLocal(WorldObjectImpl node, Point2D point) {
		return this.globalToLocal(node.localToGlobal(point));
	}

	public static enum State {
		DEFAULT, HIGHLIGHT
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
}
