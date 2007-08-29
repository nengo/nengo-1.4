package ca.neo.ui.models.viewers;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import ca.neo.model.Node;
import ca.neo.ui.actions.LayoutAction;
import ca.neo.ui.actions.SaveNodeContainerAction;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.UIModel;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.nodes.UINodeContainer;
import ca.shu.ui.lib.handlers.Interactable;
import ca.shu.ui.lib.handlers.StatusBarHandler;
import ca.shu.ui.lib.objects.widgets.TrackedStatusMsg;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.NamedObject;
import ca.shu.ui.lib.world.World;
import edu.umd.cs.piccolo.activities.PActivity;
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

	static final double SQUARE_LAYOUT_NODE_SPACING = 150;

	private Dimension layoutBounds = DEFAULT_BOUNDS;

	private final Hashtable<String, UINeoNode> neoNodesChildren = new Hashtable<String, UINeoNode>();

	private final UINodeContainer parentOfViewer;

	/**
	 * @param nodeContainer
	 *            UI Object containing the Node model
	 */
	public NodeViewer(UINodeContainer nodeContainer) {
		super("");
		this.parentOfViewer = nodeContainer;

		setStatusBarHandler(new ModelStatusBarHandler(this));

		setFrameVisible(false);

		setName(getModel().getName());

		// addInputEventListener(new HoverHandler(this));

		TrackedStatusMsg msg = new TrackedStatusMsg("Building nodes in Viewer");

		init();

		updateViewFromModel();
		if (getNeoNodes().size() > 0) {
			applyDefaultLayout();
		}

		msg.finished();

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
	protected void addNeoNode(UINeoNode nodeProxy, boolean updateModel,
			boolean dropInCenterOfCamera, boolean moveCameraToNode) {

		/**
		 * Moves the camera to where the node is positioned, if it's not dropped
		 * in the center of the camera
		 */
		if (moveCameraToNode) {
			getWorld().setCameraCenterPosition(nodeProxy.getOffset().getX(),
					nodeProxy.getOffset().getY());
		}

		neoNodesChildren.put(nodeProxy.getName(), nodeProxy);

		if (dropInCenterOfCamera) {
			getGround().catchObject(nodeProxy, dropInCenterOfCamera);
		} else {
			getGround().addWorldObject(nodeProxy);
		}
	}

	/**
	 * 
	 * @return Layout bounds to be used by Layout algorithms
	 */
	protected Dimension getLayoutBounds() {
		return layoutBounds;
	}

	protected void init() {

	}

	protected void initLayoutMenu(MenuBuilder layoutMenu) {
		MenuBuilder sortMenu = layoutMenu.createSubMenu("Sort by");

		sortMenu.addAction(new SortNodesAction(SortMode.BY_NAME));
		sortMenu.addAction(new SortNodesAction(SortMode.BY_TYPE));

	}

	protected void removeAllNeoNodes() {
		/*
		 * Removes all existing nodes from this viewer
		 */
		Enumeration<UINeoNode> enumeration = getNeoNodes().elements();
		while (enumeration.hasMoreElements()) {
			enumeration.nextElement().destroy();
		}
	}

	protected abstract void updateViewFromModel();;

	public void addNeoNode(UINeoNode node) {
		addNeoNode(node, true, true, false);

	}

	public abstract void applyDefaultLayout();

	@SuppressWarnings("unchecked")
	public void applySortLayout(SortMode sortMode) {
		ArrayList<UINeoNode> nodes = new ArrayList(getNeoNodes().size());

		Enumeration<UINeoNode> em = getNeoNodes().elements();

		while (em.hasMoreElements()) {
			nodes.add(em.nextElement());
		}

		switch (sortMode) {

		case BY_NAME:
			Collections.sort(nodes, new Comparator<UINeoNode>() {

				public int compare(UINeoNode o1, UINeoNode o2) {
					return (o1.getName().compareToIgnoreCase(o2.getName()));

				}

			});

			break;
		case BY_TYPE:
			Collections.sort(nodes, new Comparator<UINeoNode>() {

				public int compare(UINeoNode o1, UINeoNode o2) {
					if (o1.getClass() != o2.getClass()) {

						return o1.getClass().getSimpleName()
								.compareToIgnoreCase(
										o2.getClass().getSimpleName());
					} else {
						return (o1.getName().compareToIgnoreCase(o2.getName()));
					}

				}

			});

			break;
		}

		/*
		 * basic rectangle layout variables
		 */
		double x = 0;
		double y = 0;

		Iterator<UINeoNode> it = nodes.iterator();
		int numberOfNodes = getNeoNodes().size();
		int numberOfColumns = (int) Math.sqrt(numberOfNodes);
		int columnCounter = 0;

		PTransformActivity moveNodeActivity = null;
		while (it.hasNext()) {
			UINeoNode node = it.next();

			moveNodeActivity = node.animateToPositionScaleRotation(x, y, node
					.getScale(), node.getRotation(), 1000);

			x += 150;

			if (++columnCounter > numberOfColumns) {
				x = 0;
				y += SQUARE_LAYOUT_NODE_SPACING;
				columnCounter = 0;
			}

		}

		if (moveNodeActivity != null) {

			(new ZoomToFitActivity()).startAfter(moveNodeActivity);

		}

	}

	@Override
	public PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = super.constructMenu();

		initLayoutMenu(menu.createSubMenu("Layout"));

		/*
		 * File menu
		 */
		// menu.addSection("File");
		menu.addAction(new SaveNodeContainerAction("Save "
				+ getViewerParent().getTypeName() + " to file",
				getViewerParent()));
		return menu;
	}

	/**
	 * @return Neo Model of the Node Container
	 */
	public Node getModel() {
		return parentOfViewer.getModel();
	}

	@Override
	public String getName() {
		return getViewerParent().getTypeName() + " Viewer: "
				+ getViewerParent().getName();
	}

	public Dictionary<String, UINeoNode> getNeoNodes() {
		return neoNodesChildren;
	}

	/**
	 * 
	 * @param name
	 *            of Node
	 * @return Node UI object
	 */
	public UINeoNode getNode(String name) {
		return getNeoNodes().get(name);
	}

	public UINodeContainer getViewerParent() {
		return parentOfViewer;
	}

	public void hideAllWidgets() {
		Enumeration<UINeoNode> enumeration = getNeoNodes().elements();
		while (enumeration.hasMoreElements()) {
			UINeoNode node = enumeration.nextElement();
			node.hideAllWidgets();
		}
	}

	public void removeNeoNode(UINeoNode node) {
		neoNodesChildren.remove(node.getName());
	}

	public void setLayoutBounds(Dimension layoutBounds) {
		this.layoutBounds = layoutBounds;
	}

	public void showAllWidgets() {
		Enumeration<UINeoNode> enumeration = getNeoNodes().elements();
		while (enumeration.hasMoreElements()) {
			UINeoNode node = enumeration.nextElement();
			node.showAllWidgets();
		}
	}

	public static enum SortMode {
		BY_NAME("Name"), BY_TYPE("Type");

		private String name;

		SortMode(String name) {
			this.name = name;
		}

		protected String getName() {
			return name;
		}
	}

	class SortNodesAction extends LayoutAction {

		private static final long serialVersionUID = 1L;
		SortMode sortMode;

		public SortNodesAction(SortMode sortMode) {
			super(NodeViewer.this, "Sort nodes by " + sortMode.getName(),
					sortMode.getName());
			this.sortMode = sortMode;
		}

		@Override
		protected void applyLayout() {
			applySortLayout(sortMode);
		}

	}

	class ZoomToFitActivity extends PActivity {

		public ZoomToFitActivity() {
			super(0);
			addActivity(this);
		}

		@Override
		protected void activityStarted() {
			zoomToFit();
		}

	}

}

// class HoverHandler extends NodePickerHandler {
//
// public HoverHandler(NodeViewer world) {
// super(world);
// }
//
// @Override
// public void eventUpdated(PInputEvent event) {
// PNeoNode node = (PNeoNode) Util.getNodeFromPickPath(event,
// PNeoNode.class);
//
// setSelectedNode(node);
//
// }
//
// @Override
// protected int getKeepPickDelay() {
// return 1500;
// }
//
// @Override
// protected int getPickDelay() {
// return 0;
// }
//
// // @Override
// // protected void nodePicked() {
// // ((PNeoNode) getPickedNode()).setHoveredOver(true);
// //
// // }
// //
// // @Override
// // protected void nodeUnPicked() {
// // ((PNeoNode) getPickedNode()).setHoveredOver(false);
// //
// // }
//
// }

class ModelStatusBarHandler extends StatusBarHandler {

	public ModelStatusBarHandler(World world) {
		super(world);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getStatusStr(PInputEvent event) {
		UIModel wo = (UIModel) Util.getNodeFromPickPath(event, UIModel.class);

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
