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
import ca.shu.ui.lib.handlers.AbstractStatusHandler;
import ca.shu.ui.lib.objects.widgets.TrackedStatusMsg;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.Interactable;
import ca.shu.ui.lib.world.World;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Viewer for looking at NEO Node models
 * 
 * @author Shu
 */
public abstract class NodeViewer extends World implements Interactable,
		INodeContainer {

	/**
	 * Default layout bounds
	 */
	private static final Dimension DEFAULT_LAYOUT_BOUNDS = new Dimension(1000,
			1000);

	private static final long serialVersionUID = 1L;

	/**
	 * Node spacing used in the Square layout
	 */
	private static final double SQUARE_LAYOUT_NODE_SPACING = 150;

	/**
	 * Layout bounds
	 */
	private Dimension layoutBounds = DEFAULT_LAYOUT_BOUNDS;

	/**
	 * Children of NEO nodes
	 */
	private final Hashtable<String, UINeoNode> neoNodesChildren = new Hashtable<String, UINeoNode>();

	/**
	 * Viewer Parent
	 */
	private final UINodeContainer parentOfViewer;

	/**
	 * @param nodeContainer
	 *            UI Object containing the Node model
	 */
	public NodeViewer(UINodeContainer nodeContainer) {
		super("");
		this.parentOfViewer = nodeContainer;

		setStatusBarHandler(new ModelStatusBarHandler(this));

		setName(getModel().getName());

		TrackedStatusMsg msg = new TrackedStatusMsg("Building nodes in Viewer");

		updateViewFromModel();
		if (getNeoNodes().size() > 0) {
			applyDefaultLayout();
		}

		msg.finished();

	}

	/**
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
			getWorld().setSkyPosition(nodeProxy.getOffset().getX(),
					nodeProxy.getOffset().getY());
		}

		neoNodesChildren.put(nodeProxy.getName(), nodeProxy);

		if (dropInCenterOfCamera) {
			getGround().addObject(nodeProxy, dropInCenterOfCamera);
		} else {
			getGround().addChild(nodeProxy);
		}
	}

	/**
	 * @return Layout bounds to be used by Layout algorithms
	 */
	protected Dimension getLayoutBounds() {
		return layoutBounds;
	}

	/**
	 * Creates the layout context menu
	 * 
	 * @param menu
	 *            menu builder
	 */
	protected void constructLayoutMenu(MenuBuilder menu) {
		MenuBuilder sortMenu = menu.createSubMenu("Sort by");

		sortMenu.addAction(new SortNodesAction(SortMode.BY_NAME));
		sortMenu.addAction(new SortNodesAction(SortMode.BY_TYPE));

	}

	/**
	 * Removes all NEO Node children
	 */
	protected void removeAllNeoNodes() {
		/*
		 * Removes all existing nodes from this viewer
		 */
		Enumeration<UINeoNode> enumeration = getNeoNodes().elements();
		while (enumeration.hasMoreElements()) {
			enumeration.nextElement().destroy();
		}
	}

	/**
	 * Called when the model changes. Updates the viewer based on the NEO model.
	 */
	protected abstract void updateViewFromModel();;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.models.INodeContainer#addNeoNode(ca.neo.ui.models.UINeoNode)
	 */
	public void addNeoNode(UINeoNode node) {
		addNeoNode(node, true, true, false);

	}

	/**
	 * Applies the default layout
	 */
	public abstract void applyDefaultLayout();

	/**
	 * Applies a square layout which is sorted
	 * 
	 * @param sortMode
	 *            Type of sort layout to use
	 */
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

		constructLayoutMenu(menu.createSubMenu("Layout"));

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
	 * @return NEO Model represented by the viewer
	 */
	public Node getModel() {
		return parentOfViewer.getModel();
	}

	@Override
	public String getName() {
		return getViewerParent().getTypeName() + " Viewer: "
				+ getViewerParent().getName();
	}

	/**
	 * @return A collection of NEO Nodes contained in this viewer
	 */
	public Dictionary<String, UINeoNode> getNeoNodes() {
		return neoNodesChildren;
	}

	/**
	 * @param name
	 *            Name of Node
	 * @return Node
	 */
	public UINeoNode getNode(String name) {
		return getNeoNodes().get(name);
	}

	/**
	 * @return Parent of this viewer
	 */
	public UINodeContainer getViewerParent() {
		return parentOfViewer;
	}

	/**
	 * Hides all widgets
	 */
	public void hideAllWidgets() {
		Enumeration<UINeoNode> enumeration = getNeoNodes().elements();
		while (enumeration.hasMoreElements()) {
			UINeoNode node = enumeration.nextElement();
			node.hideAllWidgets();
		}
	}

	/**
	 * Removes a node
	 * 
	 * @param node
	 *            Node to remove
	 */
	public void removeNeoNode(UINeoNode node) {
		neoNodesChildren.remove(node.getName());
	}

	/**
	 * @param bounds
	 *            New bounds
	 */
	public void setLayoutBounds(Dimension bounds) {
		this.layoutBounds = bounds;
	}

	/**
	 * Shows all widgets
	 */
	public void showAllWidgets() {
		Enumeration<UINeoNode> enumeration = getNeoNodes().elements();
		while (enumeration.hasMoreElements()) {
			UINeoNode node = enumeration.nextElement();
			node.showAllWidgets();
		}
	}

	/**
	 * Supported types of sorting allowed in layout
	 * 
	 * @author Shu Wu
	 */
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

	/**
	 * Action to apply a sorting layout
	 * 
	 * @author Shu Wu
	 */
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

	/**
	 * Zooms the viewer to optimally fit all nodes
	 * 
	 * @author Shu Wu
	 */
	class ZoomToFitActivity extends PActivity {

		public ZoomToFitActivity() {
			super(0);
			parentOfViewer.addActivity(this);
		}

		@Override
		protected void activityStarted() {
			zoomToFit();
		}

	}

}

/**
 * Handler which updates the status bar of NeoGraphics to display information
 * about the node which the mouse is hovering over.
 * 
 * @author Shu Wu
 */
class ModelStatusBarHandler extends AbstractStatusHandler {

	public ModelStatusBarHandler(World world) {
		super(world);
	}

	@Override
	protected String getStatusMessage(PInputEvent event) {
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
