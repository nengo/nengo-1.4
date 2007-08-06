package ca.shu.ui.lib.world;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PBounds;

public interface WorldObject extends NamedObject {
	public static final String PROPERTY_STATE = "state";

	/**
	 * Called when the user wants to remove the Model
	 */
	public void destroy();

	public void addChild(PNode child);

	public static final String PROPERTY_EDGES = "edges";
	public static final String PROPERTY_NAME = "objectName";

	public void addChildW(WorldObject child);

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener);

	public PTransformActivity animateToPositionScaleRotation(double x,
			double y, double scale, double theta, long duration);

	public void endDrag();

	public Collection<WorldObject> getChildrenAtBounds(Rectangle2D bounds);

	public PBounds getBounds();

	public PBounds getFullBounds();

	public Point2D getOffset();

	public PNode getParent();

	public World getWorld();

	public WorldLayer getWorldLayer();

	public Dimension2D globalToLocal(Dimension2D globalDimension);

	public Point2D globalToLocal(Point2D globalPoint);

	public Rectangle2D globalToLocal(Rectangle2D globalRectangle);

	public boolean isDraggable();

	/*
	 * Called when the object has been dragged and dropped
	 */
	public void justDropped();

	// public void addedToWorld();

	public Dimension2D localToGlobal(Dimension2D localDimension);

	public Point2D localToGlobal(Point2D localPoint);

	public Rectangle2D localToGlobal(Rectangle2D localRectangle);

	public Dimension2D localToParent(Dimension2D localDimension);

	public Point2D localToParent(Point2D localPoint);

	public Rectangle2D localToParent(Rectangle2D localRectangle);

	public Dimension2D parentToLocal(Dimension2D parentDimension);

	public Point2D parentToLocal(Point2D parentPoint);

	public Rectangle2D parentToLocal(Rectangle2D parentRectangle);

	public void popState(State state);

	public void pushState(State state);

	// public void removedFromWorld();

	public void removePropertyChangeListener(PropertyChangeListener listener);

	public boolean setBounds(double x, double y, double width, double height);

	public void setDraggable(boolean isDraggable);

	public void setOffset(double x, double y);

	public void doubleClicked();
	
	public void setOffset(Point2D point);

	public void setScale(double scale);

	public void startDrag();

	public static enum State {
		DEFAULT, IN_DRAG, SELECTED
	}
}
