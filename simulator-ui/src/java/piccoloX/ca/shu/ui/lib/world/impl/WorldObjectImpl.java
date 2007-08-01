package ca.shu.ui.lib.world.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Stack;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.activities.Fader;
import ca.shu.ui.lib.objects.GContextButton;
import ca.shu.ui.lib.objects.GText;
import ca.shu.ui.lib.objects.LayoutManager;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldLayer;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PNodeFilter;

/*
 * TODO: Clean up class, move non-core functionality to child objects
 */
public class WorldObjectImpl extends PNode implements WorldObject {
	private static final long serialVersionUID = 1L;

	private PPath frame = null;

	private String name;

	protected State state = State.DEFAULT;

	PActivity animation = null;

	boolean autoPositionCloseButton = true;

	PPath tempFrame = null;

	GContextButton closeButton = null;

	boolean draggable = true;

	Stack<PNode> frames;

	boolean isAlive = true;

	boolean isFrameVisible = false;

	LayoutManager layoutManager;

	boolean selected;

	Stack<State> states;

	boolean tangible = true;

	GText titleBar;

	@Override
	public void signalBoundsChanged() {
		// TODO Auto-generated method stub
		super.signalBoundsChanged();
		signalEdgesChanged();

	}

	// int i = 0;
	public void signalEdgesChanged() {
		// System.out.println("Signal edges changed " + i++);
		firePropertyChange(0, PROPERTY_EDGES, null, null);

		/*
		 * Updates children edges
		 */
		int count = getChildrenCount();
		for (int i = 0; i < count; i++) {
			PNode each = (PNode) getChildrenReference().get(i);

			if (each instanceof WorldObjectImpl) {
				WorldObjectImpl wo = (WorldObjectImpl) each;

				wo.signalEdgesChanged();
			}
		}

	}

	public WorldObjectImpl() {
		this("");

	}

	public WorldObjectImpl(String name) {
		super();

		this.name = name;

		this.setFrameVisible(false);
		this.setDraggable(true);

		// this.setBounds(0, 0, 25, 25);

		layoutManager = new LayoutManager();

		if (name.compareTo("") != 0) {

			titleBar = new GText(name);
			titleBar.setConstrainWidthToTextWidth(true);
			titleBar.setFont(new Font("Arial", Font.PLAIN, 20));
			titleBar.setOffset(3, 3);

			addChild(titleBar);
			getLayoutManager().translate(0, titleBar.getHeight() + 3);
		}
	}

	public void addChildFancy(PNode node) {
		addChildFancy(node, 1, 500);
	}

	/*
	 * TODO: hmmm, maybe addToWorld has to be made thread safe
	 * 
	 */
	public void addChildFancy(PNode node, float scale, long duration) {
		this.addChild(node);
		node.setScale(scale);

		addActivity(new Fader(node, duration, true));
	}

	public void addChildW(WorldObject child) {
		addChild((PNode) child);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#addToLayout(edu.umd.cs.piccolo.PNode)
	 */
	public void addToLayout(PNode node) {
		addToLayout(node, false);
	}

	/*
	 * Be careful, if the "this" node is removed from its process while an
	 * animation the animation will not complete and the node will not be added.
	 * 
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#addToLayout(edu.umd.cs.piccolo.PNode,
	 *      boolean)
	 */
	public void addToLayout(PNode node, boolean animate) {
		if (animate) {
			addChildFancy(node);
		} else {
			addChild(node);
		}
		getLayoutManager().positionNode(node);

		PBounds bounds = getLayoutManager().getBounds();
		// if (bounds.height < )

		if (animate && (node.getRoot() != null)) {
			animateToBounds(bounds.x, bounds.y, bounds.width, bounds.height,
					1000);
		} else {
			setBounds(bounds);
		}
	}

	public void animateToBounds(Rectangle2D rect, long duration) {

		this.animateToBounds(rect.getX(), rect.getY(), rect.getWidth(), rect
				.getHeight(), duration);

	}

	public PTransformActivity animateToPosition(double x, double y,
			long duration) {

		return animateToPositionScaleRotation(x, y, 1, 0, duration);
	}

	@Override
	public PTransformActivity animateToPositionScaleRotation(double x,
			double y, double scale, double theta, long duration) {
		// TODO Auto-generated method stub
		PTransformActivity activity = super.animateToPositionScaleRotation(x,
				y, scale, theta, duration);
		setAnimation(animation);
		return activity;
	}

	public void animateToScale(double scale, long duration) {
		this.animateToPositionScaleRotation(this.getOffset().getX(), this
				.getOffset().getY(), scale, this.getRotation(), duration);
	}

	public void bringToFront() {
		PNode parent = getParent();
		removeFromParent();
		parent.addChild(this);

	}

	public void endDrag() {

	}

	@SuppressWarnings("unchecked")
	public Collection<WorldObject> getChildrenAtBounds(Rectangle2D bounds) {
		return (Collection<WorldObject>) (this.getAllNodes(new BoundsFilter(
				this, this.localToGlobal(bounds), WorldObjectImpl.class), null));
		
	}

	@SuppressWarnings("unchecked")
	public Collection<PNode> getChildrenAtBounds(Rectangle2D bounds,
			Class classType) {
		return (Collection<PNode>) (this.getAllNodes(new BoundsFilter(this,
				this.localToGlobal(bounds), classType), null));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#getControlDelay()
	 */
	public long getControlDelay() {
		return 400;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#getLayoutManager()
	 */
	public LayoutManager getLayoutManager() {
		return layoutManager;
	}

	public String getName() {
		if (name == null) {
			return "";
		} else
			return name;
	}

	public State getState() {
		return state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#getTitleBarText()
	 */
	public String getTitleBarText() {
		if (titleBar == null) {
			return "";
		}
		return titleBar.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#getControls()
	 */
	public WorldObjectImpl getTooltipObject() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#getWorld()
	 */
	public World getWorld() {
		if (getWorldLayer() != null)
			return getWorldLayer().getWorld();
		else
			return null;
	}

	public WorldLayer getWorldLayer() {
		PNode node = this;

		while (node != null) {
			if (node instanceof WorldLayer)
				return ((WorldLayer) node);

			node = node.getParent();
		}

		return null;

	}

	public void hideContextButton() {
		if (closeButton != null) {
			closeButton.removeFromParent();
			closeButton = null;
		}
	}

	public boolean isAlive() {
		return isAlive;
	}

	public boolean isAncestorOf(WorldObjectImpl node) {
		return isAncestorOf((PNode) node);
	}

	public boolean isContained() {
		PNode parentNode = getParent();
		while (parentNode != null && !(parentNode instanceof WorldGround)) {
			Rectangle2D dBounds = localToGlobal(getBounds());

			Rectangle2D pBounds = parentNode.localToGlobal(parentNode
					.getBounds());
			if (dBounds.intersects(pBounds))
				return true;

			parentNode = parentNode.getParent();
		}
		return false;
	}

	public boolean isDraggable() {
		return draggable;
	}

	public boolean isFrameVisible() {
		return isFrameVisible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#isSelected()
	 */
	public boolean isSelected() {
		return selected;
	}

	public boolean isTangible() {
		return tangible;
	}

	/**
	 * End of a drag and dorp operation
	 */
	public void justDropped() {
		// TODO Auto-generated method stub

	}

	/**
	 * Called when the World the object lives in has changed
	 */
	protected void addedToWorld() {
	}

	// public Object loadStatic(String name) {
	// return Util.loadProperty(getFileNamePrefix() + name);
	// }

	/*
	 * TODO: Perhaps this is not needed
	 */
	public PBounds localToLayer(PBounds bounds) {
		WorldLayer worldLayer = getWorldLayer();

		this.localToGlobal(bounds);
		worldLayer.globalToLocal(bounds);

		return bounds;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#moveOverlappedNodes()
	 */
	public void moveOverlappedNodes() {
		moveOverlappedNodes(null);
	}

	public void pack() {
		boolean frameVisible = isFrameVisible();

		// hide the frame first, so it does not contribute to the bounds
		if (frameVisible) {
			setFrameVisible(false);
		}

		// if (border != null) {
		// border.removeFromParent();
		// }

		Rectangle2D newBounds = parentToLocal(getFullBounds());

		// if (border != null) {
		// border.setVisible(true);
		// }

		if (frameVisible) {
			setFrameVisible(true);
		}

		this.setBounds(newBounds);

	}

	public void popState(State state) {
		states.remove(state);
		if (states.size() == 0)
			setState(State.DEFAULT);
		else
			setState(states.peek());

	}

	public void pushState(State state) {
		if (states == null) {
			states = new Stack<State>();
		}
		states.push(state);

		setState(state);
	}

	protected void removedFromWorld() {
		// TODO Auto-generated method stub

	}

	/*
	 * TODO: implement this
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#removeFromLayout(edu.umd.cs.piccolo.PNode)
	 */
	public void removeFromLayout(PNode node) {

	}

	// public void saveStatic(Object obj, String name) {
	// Util.saveProperty(obj, getFileNamePrefix() + name);
	// }

	/*
	 * 
	 * TODO: Prune inactive animations
	 */
	public void setAnimation(PActivity animation) {
		terminateAnimation();
		this.animation = animation;
	}

	public void setBorder(Color borderColor) {
		if (borderColor == null) {
			if (tempFrame != null)
				tempFrame.removeFromParent();
			tempFrame = null;
			return;
		}

		if (tempFrame == null) {

			tempFrame = PPath.createRectangle((float) getX(), (float) getY(),
					(float) getWidth(), (float) getHeight());
			synchronized (tempFrame) {
				tempFrame.setPaint(null);

				addChild(tempFrame);
			}
		}

		tempFrame.setStrokePaint(borderColor);

	}

	@Override
	public boolean setBounds(double x, double y, double width, double height) {
		boolean rtn = super.setBounds(x, y, width, height);

		boundsChanged();
		// TODO Auto-generated method stub
		return rtn;
	}

	public void setBoundsWithPadding(double x, double y, double width,
			double height, double padding) {
		this.setBounds(x, y, width - (padding * 2), height - (padding * 2));
		this.setOffset(padding, padding);
	}

	public void setBoundsWithPadding(Rectangle2D bounds, double padding) {
		this.setBoundsWithPadding(bounds.getX(), bounds.getY(), bounds
				.getWidth(), bounds.getHeight(), padding);
	}

	public void setDraggable(boolean isDraggable) {
		this.draggable = isDraggable;
	}

	public void setFrameVisible(boolean isVisible) {
		isFrameVisible = isVisible;

		if (isVisible) {
			if (frame == null) {
				frame = PPath.createRectangle(0, 0, 100, 100);

				frame.setPaint(Style.COLOR_BACKGROUND);
				frame.setStrokePaint(Style.COLOR_FOREGROUND);

				// frame.setPickable(true);

				this.addChild(0, frame);
			}
		} else {
			if (frame != null) {
				frame.removeFromParent();
				frame = null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#setLayoutManager(ca.sw.graphics.basics.LayoutManager)
	 */
	public void setLayoutManager(LayoutManager layoutManager) {
		this.layoutManager = layoutManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setParent(PNode newParent) {
		boolean worldChanged = false;

		World newWorld = null;

		if (newParent instanceof WorldObject) {
			newWorld = ((WorldObject) newParent).getWorld();
		}

		if (newWorld != getWorld()) {
			worldChanged = true;
		}

		super.setParent(newParent);

		if (worldChanged) {
			// World has disappeared
			if (newWorld == null) {

				removedFromWorld();

			}
			// Is in a new world
			else {
				addedToWorld();

				ListIterator it = getChildrenIterator();

				while (it.hasNext()) {
					PNode node = (PNode) it.next();

					if (node instanceof WorldObjectImpl) {
						((WorldObjectImpl) node).addedToWorld();
					}
				}
			}
		}
		signalEdgesChanged();
	}

	/*
	 * @param tangible Whether physical rules of the world apply to this node
	 */
	public void setTangible(boolean tangible) {
		this.tangible = tangible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#setTitleBarText(java.lang.String)
	 */
	public void setTitleBarText(String str) {

		if (titleBar != null) {
			titleBar.setText(str);
		}
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see ca.sw.graphics.nodes.WorldO#springBack()
	// */
	// public boolean springBack() {
	//
	// if (springX == 0 && springY == 0)
	// return true;
	//
	// return false;
	// }
	//
	// /*
	// * translate to location and spring back
	// */
	// /*
	// * (non-Javadoc)
	// *
	// * @see ca.sw.graphics.nodes.WorldO#springTranslate(double, double)
	// */
	// public void springTranslate(double dx, double dy) {
	// springX = dx;
	// springY = dy;
	//
	// translate(dx, dy);
	//
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#showContextButton()
	 */
	public PNode showContextButton() {
		return showContextButton(autoPositionCloseButton);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#showContextButton(boolean)
	 */
	public PNode showContextButton(boolean autoPosition) {
		autoPositionCloseButton = autoPosition;
		if (closeButton == null) {
			closeButton = new GContextButton(this);

			this.addChildFancy(closeButton);

		}
		boundsChanged();
		return closeButton;
	}

	public void startDrag() {

	}

	public void terminateAnimation() {
		if (animation != null) {
			animation.terminate(PActivity.TERMINATE_AND_FINISH);
		}
		animation = null;
	}

	private void setState(State newState) {
		State oldState = state;
		state = newState;

		firePropertyChange(0, PROPERTY_STATE, oldState, newState);

		stateChanged();
	}

	protected void boundsChanged() {

		if (titleBar != null) {
			titleBar.setWidth(this.getWidth());
			// titleBar.recomputeLayout()
		}

		if ((closeButton != null) && autoPositionCloseButton) {
			closeButton.setOffset(getWidth() - closeButton.getWidth() - 10, 10);

		}
	}

	protected Collection<WorldObject> getChildrenAtBounds(double x, double y,
			double width, double height) {

		return getChildrenAtBounds(new PBounds(x, y, width, height));

	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();

		if (frame != null) {
			frame.setBounds(getBounds());
		}

		if (tempFrame != null) {
			tempFrame.setBounds(getBounds());
		}
	}

	/*
	 * Moves nodes which overlap
	 * 
	 */
	protected void moveOverlappedNodes(WorldObjectImpl callingNode) {
		if (!isTangible())
			return;

		Rectangle2D dBounds = localToGlobal(getBounds());

		/*
		 * Move nodes which are close to it
		 * 
		 */

		WorldLayer world = getWorldLayer();
		if (world == null)
			return;

		world.globalToLocal(dBounds);

		Collection<WorldObject> intersectingNodes = world
				.getChildrenAtBounds(dBounds);

		// find intersecting nodes
		Iterator<WorldObject> it = intersectingNodes.iterator();
		while (it.hasNext()) {
			WorldObject node = it.next();

			if (node != this && node != callingNode) {
				WorldObjectImpl gNode = (WorldObjectImpl) node;

				if (gNode.isDraggable() && gNode.isTangible()) {
					Rectangle2D bounds = gNode.localToGlobal(gNode.getBounds());

					double translateX = Double.MAX_VALUE;

					double translateY = 0;

					translateX = dBounds.getMaxX() - bounds.getMinX();
					double trX = dBounds.getMinX() - bounds.getMaxX();
					if (Math.abs(trX) < Math.abs(translateX))
						translateX = trX;

					translateY = dBounds.getMaxY() - bounds.getMinY();
					double trY = dBounds.getMinY() - bounds.getMaxY();
					if (Math.abs(trY) < Math.abs(translateY))
						translateY = trY;

					if (Math.abs(translateX) < Math.abs(translateY)) {
						gNode.translate(translateX, 0);
					} else
						gNode.translate(0, translateY);

					gNode.moveOverlappedNodes(this);
				}

			}

		}

	}

	protected void stateChanged() {

		if (state == State.DEFAULT) {
			setBorder(null);
		} else if (state == State.IN_DRAG) {
			setBorder(Style.COLOR_BORDER_DRAGGED);
		} else if (state == State.SELECTED) {
			setBorder(Style.COLOR_SELECTED);
		}
	}

	public void setName(String name) {
		String oldName = this.name;
		this.name = name;

		firePropertyChange(0, PROPERTY_NAME, oldName, this.name);

	}

	@Override
	public void setOffset(double x, double y) {
		// TODO Auto-generated method stub
		super.setOffset(x, y);
		signalEdgesChanged();
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see ca.sw.graphics.nodes.WorldO#setSelected(boolean)
	// */
	// public void setSelected(boolean isSelected) {
	// this.selected = isSelected;
	// if (isSelected) {
	// setFrameVisible(true);
	//
	// } else {
	// setFrameVisible(isFrameVisible());
	// getFrame().setStrokePaint(GDefaults.FOREGROUND_COLOR);
	// }
	//
	// }

}

class BoundsFilter implements PNodeFilter {
	Rectangle2D bounds;

	@SuppressWarnings("unchecked")
	Class classType;

	PBounds localBounds = new PBounds();

	PNode node;

	@SuppressWarnings("unchecked")
	protected BoundsFilter(PNode node, Rectangle2D bounds, Class classType) {
		this.bounds = bounds;
		this.node = node;
		this.classType = classType;

	}

	public boolean accept(PNode node) {
		if (this.node == node) // rejects the parent node... we only want the
			// children
			return false;

		if (!classType.isInstance(node) || node instanceof WorldLayer)
			return false;

		localBounds.setRect(bounds);
		node.globalToLocal(localBounds);

		boolean boundsIntersects = node.intersects(localBounds);

		return boundsIntersects;
	}

	public boolean acceptChildrenOf(PNode node) {
		if (this.node == node) {
			return true;
		} else {
			return false;
		}
	}

}