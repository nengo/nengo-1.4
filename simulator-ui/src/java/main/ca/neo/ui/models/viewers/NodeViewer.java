package ca.neo.ui.models.viewers;

import java.awt.Dimension;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import ca.neo.model.Node;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.PModel;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.nodes.PNodeContainer;
import ca.shu.ui.lib.handlers.Interactable;
import ca.shu.ui.lib.handlers.NodePickerHandler;
import ca.shu.ui.lib.handlers.StatusBarHandler;
import ca.shu.ui.lib.objects.widgets.TrackedStatusMsg;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.NamedObject;
import ca.shu.ui.lib.world.World;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * NodeViewer visualizes the nodes within a PNodeContainer such as an Ensemble
 * or Network
 * 
 * @author Shu
 * 
 */
public abstract class NodeViewer extends World implements NamedObject,
		Interactable, INodeContainer {
	private static final long serialVersionUID = 1L;

	static final Dimension DEFAULT_BOUNDS = new Dimension(1000, 1000);
	static final String LAYOUT_MANAGER_KEY = "layout/manager";
	private Dimension layoutBounds = DEFAULT_BOUNDS;

	private PNodeContainer parentOfViewer;

	private Hashtable<String, PNeoNode> viewerNodes = new Hashtable<String, PNeoNode>();

	public Enumeration<PNeoNode> getViewedNodesElements() {
		return viewerNodes.elements();
	}

	/**
	 * @param nodeContainer
	 *            UI Object containing the Node model
	 */
	public NodeViewer(PNodeContainer nodeContainer) {
		super("");
		this.parentOfViewer = nodeContainer;

		setStatusBarHandler(new ModelStatusBarHandler(this));

		setFrameVisible(false);

		setName(getModel().getName());

		addInputEventListener(new HoverHandler(this));

		TrackedStatusMsg msg = new TrackedStatusMsg("Building nodes in Viewer");

		updateNodesFromModel();
		if (viewerNodes.size() > 0) {
			applyDefaultLayout();
		}

		msg.finished();

	}

	public void addNeoNode(PNeoNode node) {
		addNeoNode(node, true, true, false);

	}

	/**
	 * 
	 * @param nodeProxy
	 *            node to be added
	 * @param updateModel
	 *            if true, the network model is updated. this may be false, if
	 *            it is known that the network model already contains this node
	 * @param dropInCenterOfCamera
	 *            whether to drop the node in the center of the camera
	 * @param moveCameraToNode
	 *            whether to move the camera to where the node is
	 */
	public void addNeoNode(PNeoNode nodeProxy, boolean updateModel,
			boolean dropInCenterOfCamera, boolean moveCameraToNode) {

		/**
		 * Moves the camera to where the node is positioned, if it's not dropped
		 * in the center of the camera
		 */
		if (moveCameraToNode) {
			setCameraCenterPosition(nodeProxy.getOffset().getX(), nodeProxy
					.getOffset().getY());
		}

		viewerNodes.put(nodeProxy.getName(), nodeProxy);

		if (dropInCenterOfCamera) {
			getGround().catchObject(nodeProxy, dropInCenterOfCamera);
		} else {
			getGround().addWorldObject(nodeProxy);
		}
	}

	/**
	 * 
	 * @param nodeUI
	 *            node to be removed
	 */
	public void removeNeoNode(PNeoNode nodeUI) {
		viewerNodes.remove(nodeUI.getName());

	}

	public abstract void applyDefaultLayout();

	public void applySquareLayout() {

		/*
		 * basic rectangle layout variables
		 */
		double x = 0;
		double y = 0;

		Enumeration<PNeoNode> em = viewerNodes.elements();
		PTransformActivity moveNodeActivity = null;
		while (em.hasMoreElements()) {
			PNeoNode node = em.nextElement();

			moveNodeActivity = node.animateToPositionScaleRotation(x, y, node
					.getScale(), node.getRotation(), 1000);

			x += 150;

			if (x > getLayoutBounds().getWidth()) {
				x = 0;
				y += 130;
			}

		}

		if (moveNodeActivity != null) {
			zoomToFit().startAfter(moveNodeActivity);
		}

	}

	@Override
	public PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = super.constructMenu();

		return menu;
	}

	/**
	 * @return Neo Model of the Node Container
	 */
	public Node getModel() {
		return parentOfViewer.getModel();
	}

	public String getName() {
		return "Network Viewer: " + getViewerParent().getName();
	}

	public PNodeContainer getViewerParent() {
		return parentOfViewer;
	}

	public void hideAllWidgets() {
		Enumeration<PNeoNode> enumeration = viewerNodes.elements();
		while (enumeration.hasMoreElements()) {
			PNeoNode node = enumeration.nextElement();
			node.hideAllWidgets();
		}
	}

	public void setLayoutBounds(Dimension layoutBounds) {
		this.layoutBounds = layoutBounds;
	}

	public void showAllWidgets() {
		Enumeration<PNeoNode> enumeration = viewerNodes.elements();
		while (enumeration.hasMoreElements()) {
			PNeoNode node = enumeration.nextElement();
			node.showAllWidgets();
		}
	}

	/**
	 * 
	 * @return Layout bounds to be used by Layout algorithms
	 */
	protected Dimension getLayoutBounds() {
		return layoutBounds;
	}

	protected abstract void updateNodesFromModel();

	protected Dictionary<String, PNeoNode> getViewerNodes() {
		return viewerNodes;
	}

}

class ModelStatusBarHandler extends StatusBarHandler {

	public ModelStatusBarHandler(World world) {
		super(world);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getStatusStr(PInputEvent event) {
		PModel wo = (PModel) Util.getNodeFromPickPath(event, PModel.class);

		StringBuilder statuStr = new StringBuilder(getWorld().getName() + " | ");

		if (wo != null) {
			statuStr.append("Model name: " + wo.getName() + " ("
					+ wo.getTypeName() + ")");
		} else {
			statuStr.append("No Model Selected");
		}
		return statuStr.toString();
	}
}

class HoverHandler extends NodePickerHandler {

	public HoverHandler(NodeViewer world) {
		super(world);
	}

	@Override
	protected int getKeepPickDelay() {
		return 1500;
	}

	@Override
	protected int getPickDelay() {
		return 0;
	}

	@Override
	public void eventUpdated(PInputEvent event) {
		PNeoNode node = (PNeoNode) Util.getNodeFromPickPath(event,
				PNeoNode.class);

		setSelectedNode(node);

	}

	@Override
	protected void nodePicked() {
		((PNeoNode) getPickedNode()).setHoveredOver(true);

	}

	@Override
	protected void nodeUnPicked() {
		((PNeoNode) getPickedNode()).setHoveredOver(false);

	}

}