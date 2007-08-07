package ca.neo.ui.models.viewers;

import java.awt.Dimension;
import java.util.Enumeration;
import java.util.Hashtable;

import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.ui.models.PModel;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.nodes.PNetwork;
import ca.neo.ui.models.nodes.PNodeContainer;
import ca.neo.ui.models.nodes.connectors.PModelWidget;
import ca.shu.ui.lib.handlers.Interactable;
import ca.shu.ui.lib.handlers.StatusBarHandler;
import ca.shu.ui.lib.objects.widgets.TrackedActivity;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.NamedObject;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.impl.WorldImpl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * NodeViewer visualizes the nodes within a PNodeContainer such as an Ensemble
 * or Network
 * 
 * @author Shu
 * 
 */
public abstract class NodeViewer extends WorldImpl implements NamedObject,
		Interactable {
	private static final long serialVersionUID = 1L;
	static final Dimension DEFAULT_BOUNDS = new Dimension(1000, 1000);
	static final String LAYOUT_MANAGER_KEY = "layout/manager";
	private Dimension layoutBounds = DEFAULT_BOUNDS;

	Hashtable<String, PNeoNode> nodesUI = new Hashtable<String, PNeoNode>();

	PNodeContainer parentOfViewer;

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

		addInputEventListener(new NodeHoverHandler(this));

		(new TrackedActivity("Building network UI") {

			@Override
			public void doActivity() {
				constructChildrenNodes();
				applyDefaultLayout();
			}

		}).startThread(true);
	}

	public void addNodeToUI(PNeoNode nodeProxy) {
		addNodeToUI(nodeProxy, true, true);
	}

	/**
	 * 
	 * @param nodeProxy
	 *            node to be added
	 * @param updateModel
	 *            if true, the network model is updated. this may be false, if
	 *            it is known that the network model already contains this node
	 */
	public void addNodeToUI(PNeoNode nodeProxy, boolean updateModel,
			boolean dropInCenterOfCamera) {

		/**
		 * Moves the camera to where the node is positioned, if it's not dropped
		 * in the center of the camera
		 */
		if (!dropInCenterOfCamera) {
			setCameraCenterPosition(nodeProxy.getOffset().getX(), nodeProxy
					.getOffset().getY());
		}

		nodesUI.put(nodeProxy.getName(), nodeProxy);
		getGround().catchObject(nodeProxy, dropInCenterOfCamera);
	}

	public abstract void applyDefaultLayout();

	public void applySquareLayout() {
		/*
		 * basic rectangle layout variables
		 */
		double x = 0;
		double y = 0;

		Enumeration<PNeoNode> em = nodesUI.elements();
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

	public Network getNetworkParent() {
		PNode node = parentOfViewer;
		while (node != null) {
			if (node instanceof PNetwork) {
				return ((PNetwork) node).getModel();
			}

			node = node.getParent();
		}

		return null;
	}

	public NodeLayoutManager getNodeLayoutManager() {
		NodeLayoutManager layoutManager = null;
		try {
			Object obj = getNetworkParent().getMetaData(getLayoutManagerKey());
			if (obj != null)
				layoutManager = (NodeLayoutManager) obj;
		} catch (Throwable e) {
			Util.Error("Could not access layout manager, creating a new one");
			// catch all exceptions
		}

		if (layoutManager == null) {
			layoutManager = new NodeLayoutManager();
			getNetworkParent()
					.setMetaData(getLayoutManagerKey(), layoutManager);
		}
		return (NodeLayoutManager) layoutManager;

	}

	public PNodeContainer getViewerParent() {
		return parentOfViewer;
	}

	/**
	 * Saves layout
	 * 
	 * @param name
	 */
	public void saveNodeLayout(String name) {

		NodeLayoutManager layouts = getNodeLayoutManager();
		NodeLayout nodeLayout = new NodeLayout(name);

		Enumeration<PNeoNode> en = nodesUI.elements();

		while (en.hasMoreElements()) {
			PNeoNode node = en.nextElement();
			nodeLayout.addPosition(node.getName(), node.getOffset());

		}

		layouts.addLayout(nodeLayout);
	}

	public void setLayoutBounds(Dimension layoutBounds) {
		this.layoutBounds = layoutBounds;
	}

	/**
	 * 
	 * @return The Key used to access Metadata containing layout information
	 *         from the Network node
	 */
	private String getLayoutManagerKey() {
		if (parentOfViewer instanceof PNetwork) {
			return LAYOUT_MANAGER_KEY;

		} else {
			return getName() + "/" + LAYOUT_MANAGER_KEY;
		}
	}

	protected abstract void constructChildrenNodes();

	/**
	 * 
	 * @return Layout bounds to be used by Layout algorithms
	 */
	protected Dimension getLayoutBounds() {
		return layoutBounds;
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

class NodeHoverHandler extends PBasicInputEventHandler {
	NodeViewer parent;
	PNeoNode selectedNode = null;

	public NodeHoverHandler(NodeViewer parent) {
		super();
		this.parent = parent;
	}

	@Override
	public void mouseEntered(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseEntered(event);
		PNeoNode node = (PNeoNode) Util.getNodeFromPickPath(event,
				PNeoNode.class);

		/*
		 * If no node is picked, find if the mouse is hovering over a widget
		 */
		if (node == null) {

			PModelWidget widget = (PModelWidget) Util.getNodeFromPickPath(
					event, PModelWidget.class);
			if (widget != null)
				node = widget.getNodeParent();

		}

		if (selectedNode != node) {
			if (selectedNode != null)
				selectedNode.setHoveredOver(false);
			if (node != null) {
				node.setHoveredOver(true);
			}
			selectedNode = node;
		}

	}

	@Override
	public void mouseExited(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseExited(event);
		// parent.setHoveredOver(false);
	}

}