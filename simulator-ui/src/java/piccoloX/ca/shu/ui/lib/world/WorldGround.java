package ca.shu.ui.lib.world;

import java.awt.geom.Point2D;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.shu.ui.lib.objects.DirectedEdge;
import ca.shu.ui.lib.util.Util;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Layer within a world which is zoomable and pannable. It contains world
 * objects.
 * 
 * @author Shu Wu
 */
public class WorldGround extends WorldObject implements IWorldLayer {

	private static final long serialVersionUID = 1L;

	private ChildFilter myChildFilter;

	/**
	 * World this layer belongs to
	 */
	private World world;

	private PNode myEdgeHolder;

	public List<DirectedEdge> getEdges() {
		myEdgeHolder.getChildrenCount();

		ArrayList<DirectedEdge> edges = new ArrayList<DirectedEdge>(
				myEdgeHolder.getChildrenCount());

		Iterator<?> it = myEdgeHolder.getChildrenIterator();
		while (it.hasNext()) {
			edges.add((DirectedEdge) it.next());
		}
		return edges;
	}

	public boolean containsEdge(DirectedEdge edge) {
		if (edge.getParent() == myEdgeHolder) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Create a new ground layer
	 * 
	 * @param world
	 *            World this layer belongs to
	 */
	public WorldGround(World world, PLayer layer) {
		super();
		this.world = world;
		myEdgeHolder = new PNode();
		myEdgeHolder.setPickable(false);
		layer.addChild(myEdgeHolder);
		this.setSelectable(false);
	}

	public void addEdge(DirectedEdge edge) {
		myEdgeHolder.addChild(edge);
	}

	@Override
	public void addChild(int index, PNode child) {
		if (child instanceof WorldObject) {
			if (myChildFilter != null
					&& (!myChildFilter.acceptChild((WorldObject) child))) {
				throw new InvalidParameterException();
			}
		} else {
			throw new InvalidParameterException();
		}
		super.addChild(index, child);
	}

	/**
	 * Removes and destroys children
	 */
	public void destroyAndClearChildren() {
		List<?> childrenList = getChildrenReference();

		PNode[] children = childrenList.toArray(new PNode[0]);

		for (PNode node : children) {
			if (node instanceof WorldObject) {
				((WorldObject) node).destroy();
			} else {
				Util.Assert(false, "Non-WorldObject in the world");
			}
		}
	}

	/**
	 * Adds a child object. Like addChild, but with more pizzaz.
	 * 
	 * @param wo
	 *            Object to add to the layer
	 */
	public void addObject(WorldObject wo) {
		addObject(wo, true);

	}

	/**
	 * Adds a little pizzaz when adding new objects
	 * 
	 * @param wo
	 *            Object to be added
	 * @param centerCameraPosition
	 *            whether the object's position should be changed to appear at
	 *            the center of the camera
	 */
	public void addObject(WorldObject wo, boolean centerCameraPosition) {
		addChild(wo);

		Point2D finalPosition;
		if (centerCameraPosition) {
			PBounds fullBounds = wo.getFullBounds();

			finalPosition = world.skyToGround(new Point2D.Double(world
					.getWidth() / 2, world.getHeight() / 2));
			/*
			 * The final position is at the center of the full bounds of the
			 * object to be added.
			 */
			finalPosition = new Point2D.Double(finalPosition.getX()
					- (fullBounds.getX() - wo.getOffset().getX())
					- (fullBounds.getWidth() / 2d), finalPosition.getY()
					- (fullBounds.getY() - wo.getOffset().getY())
					- (fullBounds.getHeight() / 2d));
		} else {
			finalPosition = wo.getOffset();

		}
		wo.setScale(1 / getGroundScale());

		wo.setOffset(finalPosition.getX(), finalPosition.getY()
				- (100 / getGroundScale()));

		wo.animateToPositionScaleRotation(finalPosition.getX(), finalPosition
				.getY(), 1, 0, 500);
	}

	/**
	 * @return The scale of the ground in relation to the sky
	 */
	public double getGroundScale() {
		return world.getSky().getViewScale();
	}

	@Override
	public World getWorld() {
		return world;
	}

	public void setChildFilter(ChildFilter childFilter) {
		myChildFilter = childFilter;
	}

	public static interface ChildFilter {
		public boolean acceptChild(WorldObject obj);
	}

	@Override
	protected void prepareForDestroy() {
		myEdgeHolder.removeFromParent();
		super.prepareForDestroy();
	}
}
