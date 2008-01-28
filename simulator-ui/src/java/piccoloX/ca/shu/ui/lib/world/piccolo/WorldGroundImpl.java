package ca.shu.ui.lib.world.piccolo;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.ObjectSet;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldLayer;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.piccolo.primitives.PXEdge;
import ca.shu.ui.lib.world.piccolo.primitives.PXNode;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;

/**
 * Layer within a world which is zoomable and pannable. It contains world
 * objects.
 * 
 * @author Shu Wu
 */
public class WorldGroundImpl extends WorldLayerImpl implements WorldLayer {

	private static final long serialVersionUID = 1L;

	/**
	 * Adds a little pizzaz when adding new objects
	 * 
	 * @param wo
	 *            Object to be added
	 * @param centerCameraPosition
	 *            whether the object's position should be changed to appear at
	 *            the center of the camera
	 */
	protected static void dropObject(World world, WorldObject parent, WorldObject wo,
			boolean centerCameraPosition) {
		parent.addChild(wo);

		Point2D finalPosition;
		if (centerCameraPosition) {
			Rectangle2D fullBounds = wo.getFullBounds();

			finalPosition = world.skyToGround(new Point2D.Double(world.getWidth() / 2, world
					.getHeight() / 2));
			/*
			 * The final position is at the center of the full bounds of the
			 * object to be added.
			 */
			finalPosition = new Point2D.Double(finalPosition.getX()
					- (fullBounds.getX() - wo.getOffset().getX()) - (fullBounds.getWidth() / 2d),
					finalPosition.getY() - (fullBounds.getY() - wo.getOffset().getY())
							- (fullBounds.getHeight() / 2d));
		} else {
			finalPosition = wo.getOffset();

		}
		wo.setScale(1 / world.getSky().getViewScale());

		wo.setOffset(finalPosition.getX(), finalPosition.getY()
				- (100 / world.getSky().getViewScale()));

		wo.animateToPositionScaleRotation(finalPosition.getX(), finalPosition.getY(), 1, 0, 500);
	}

	/*
	 * Convenient storage of all children
	 */
	private ObjectSet<WorldObject> children = new ObjectSet<WorldObject>();

	private GroundNode myLayerNode;

	public WorldGroundImpl() {
		super("Ground", new GroundNode());
		myLayerNode = (GroundNode) getPiccolo();
		myLayerNode.setPickable(false);
	}

	@Override
	protected void prepareForDestroy() {
		myLayerNode.removeFromParent();
		super.prepareForDestroy();
	}

	@Override
	public void addChild(WorldObject wo, int index) {

		super.addChild(wo, index);
	}

	public void addEdge(PXEdge edge) {
		myLayerNode.addEdge(edge);
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

	public void addObject(WorldObject wo, boolean centerCameraPosition) {
		dropObject(world, this, wo, centerCameraPosition);
	}

	@Override
	public void childAdded(WorldObject wo) {
		super.childAdded(wo);

		children.add(wo);
	}

	@Override
	public void childRemoved(WorldObject wo) {
		super.childRemoved(wo);
		if (!children.remove(wo)) {
			Util.Assert(false);
		}
	}

	public boolean containsEdge(PXEdge edge) {
		return myLayerNode.containsEdge(edge);
	}

	@Override
	public Iterable<WorldObject> getChildren() {
		return children;
	}

	public Collection<PXEdge> getEdges() {
		return myLayerNode.getEdges();
	}

	/**
	 * @return The scale of the ground in relation to the sky
	 */
	public double getGroundScale() {
		return world.getSky().getViewScale();
	}

	public static interface ChildFilter {
		public boolean acceptChild(WorldObject obj);
	}
}

class GroundNode extends PXNode {

	private static final long serialVersionUID = 1L;

	private PNode edgeHolder;

	public GroundNode() {
		super();
		this.edgeHolder = new PNode();
	}

	public void addEdge(PXEdge edge) {
		edgeHolder.addChild(edge);
	}

	public boolean containsEdge(PXEdge edge) {
		if (edge.getParent() == edgeHolder) {
			return true;
		} else {
			return false;
		}
	}

	public Collection<PXEdge> getEdges() {
		ArrayList<PXEdge> edges = new ArrayList<PXEdge>(edgeHolder.getChildrenCount());

		Iterator<?> it = edgeHolder.getChildrenIterator();
		while (it.hasNext()) {
			edges.add((PXEdge) it.next());
		}
		return edges;
	}

	@Override
	public void setParent(PNode newParent) {
		if (!(newParent instanceof PLayer)) {
			throw new InvalidParameterException();
		}
		super.setParent(newParent);
		/*
		 * Invoke later, otherwise the edge holder may be added below the
		 * ground. We can't add directly here because this function is called
		 * from also addChild
		 */
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (getParent() != null) {
					getParent().addChild(0, edgeHolder);
				}
			}
		});

	}

}
