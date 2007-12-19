package ca.neo.ui.models.viewers;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JPopupMenu;

import ca.neo.model.Node;
import ca.neo.ui.actions.LayoutAction;
import ca.neo.ui.actions.SaveNodeAction;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.ModelsContextMenu;
import ca.neo.ui.models.UIModel;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.nodes.NodeContainer;
import ca.shu.ui.lib.handlers.AbstractStatusHandler;
import ca.shu.ui.lib.objects.activities.TrackedStatusMsg;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.Interactable;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.elastic.ElasticWorld;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Viewer for looking at NEO Node models
 * 
 * @author Shu
 */
public abstract class NodeViewer extends ElasticWorld implements Interactable,
		INodeContainer {
	/**
	 * Default layout bounds
	 */
	private static final Dimension DEFAULT_LAYOUT_BOUNDS = new Dimension(1000,
			1000);

	private static final long serialVersionUID = 1L;

	/**
	 * Layout bounds
	 */
	private Dimension layoutBounds = DEFAULT_LAYOUT_BOUNDS;

	/**
	 * Children of NEO nodes
	 */
	private final Hashtable<String, UINeoNode> neoNodesChildren = new Hashtable<String, UINeoNode>();;

	/**
	 * Viewer Parent
	 */
	private final NodeContainer parentOfViewer;

	/**
	 * @param nodeContainer
	 *            UI Object containing the Node model
	 */
	public NodeViewer(NodeContainer nodeContainer) {
		super("");
		this.parentOfViewer = nodeContainer;

		setStatusBarHandler(new NodeViewerStatus(this));

		setName(getModel().getName());

		TrackedStatusMsg msg = new TrackedStatusMsg("Building nodes in Viewer");

		updateViewFromModel();

		msg.finished();

	}

	/**
	 * @param node
	 *            node to be added
	 * @param updateModel
	 *            if true, the network model is updated. this may be false, if
	 *            it is known that the network model already contains this node
	 * @param dropInCenterOfCamera
	 *            whether to drop the node in the center of the camera
	 * @param moveCameraToNode
	 *            whether to move the camera to where the node is
	 */
	protected void addNeoNode(UINeoNode node, boolean updateModel,
			boolean dropInCenterOfCamera, boolean moveCameraToNode) {

		/**
		 * Moves the camera to where the node is positioned, if it's not dropped
		 * in the center of the camera
		 */
		if (moveCameraToNode) {
			getWorld().animateToSkyPosition(node.getOffset().getX(),
					node.getOffset().getY());
		}

		neoNodesChildren.put(node.getName(), node);

		if (dropInCenterOfCamera) {
			getGround().addObject(node, dropInCenterOfCamera);
		} else {
			getGround().addChild(node);
		}

		node.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().compareTo(
						WorldObject.PROPERTY_DESTROYED) == 0) {

					removeNeoNode((UINeoNode) evt.getSource());
				}
			}
		});
	}

	/**
	 * Creates the layout context menu
	 * 
	 * @param menu
	 *            menu builder
	 */
	protected void constructLayoutMenu(MenuBuilder menu) {
		MenuBuilder sortMenu = menu.addSubMenu("Sort");

		sortMenu.addAction(new SortNodesAction(SortMode.BY_NAME));
		sortMenu.addAction(new SortNodesAction(SortMode.BY_TYPE));
	}

	protected void constructLayoutAlgorithmsMenu(MenuBuilder menu) {

	}

	/**
	 * @return Layout bounds to be used by Layout algorithms
	 */
	protected Dimension getLayoutBounds() {
		return layoutBounds;
	}

	/**
	 * Called when the model changes. Updates the viewer based on the NEO model.
	 */
	protected abstract void updateViewFromModel();

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
		getGround().setElasticEnabled(false);

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

		if (it.hasNext()) {
			double startX = Double.MAX_VALUE;
			double startY = Double.MAX_VALUE;
			double maxRowHeight = 0;
			double endX = Double.MIN_VALUE;
			double endY = Double.MIN_VALUE;

			while (it.hasNext()) {
				UINeoNode node = it.next();

				node.animateToPositionScaleRotation(x, y, node.getScale(), node
						.getRotation(), 1000);

				if (x < startX) {
					startX = x;
				} else if (x + node.getWidth() > endX) {
					endX = x + node.getWidth();
				}

				if (y < startY) {
					startY = y;
				} else if (y + node.getHeight() > endY) {
					endY = y + node.getHeight();
				}

				if (node.getFullBounds().getHeight() > maxRowHeight) {
					maxRowHeight = node.getFullBounds().getHeight();
				}

				x += node.getFullBounds().getWidth() + 50;

				if (++columnCounter > numberOfColumns) {
					x = 0;
					y += maxRowHeight + 50;
					maxRowHeight = 0;
					columnCounter = 0;
				}

			}

			PBounds fullBounds = new PBounds(startX, startY, endX - startX,
					endY - startY);
			zoomToBounds(fullBounds);

		}

	}

	@Override
	public void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);

		constructLayoutMenu(menu.addSubMenu("Layout"));

		/*
		 * File menu
		 */
		menu.addSection("File");
		menu.addAction(new SaveNodeAction(getViewerParent()));
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
	public NodeContainer getViewerParent() {
		return parentOfViewer;
	}

	/**
	 * Hides all widgets
	 */
	public void hideAllOriginTerminations() {
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
	protected void removeNeoNode(UINeoNode node) {
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
	public void showAllOriginTerminations() {
		Enumeration<UINeoNode> enumeration = getNeoNodes().elements();
		while (enumeration.hasMoreElements()) {
			UINeoNode node = enumeration.nextElement();
			node.showAllWidgets();
		}
	}

	@Override
	public JPopupMenu showSelectionContextMenu() {
		ArrayList<UIModel> models = new ArrayList<UIModel>(getSelection()
				.size());

		for (WorldObject object : getSelection()) {
			if (object instanceof UIModel) {
				models.add((UIModel) object);

			}
		}

		return ModelsContextMenu.getMenu(models);
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
class NodeViewerStatus extends AbstractStatusHandler {

	public NodeViewerStatus(NodeViewer world) {
		super(world);
	}

	@Override
	protected String getStatusMessage(PInputEvent event) {
		UIModel wo = (UIModel) Util.getNodeFromPickPath(event, UIModel.class);

		StringBuilder statusStr = new StringBuilder(200);
		if (getWorld().getGround().isElasticMode()) {
			statusStr.append("Elastic layout enabled | ");
		}
		statusStr.append(getWorld().getViewerParent().getFullName() + " -> ");

		if (getWorld().getSelection().size() > 1) {
			statusStr.append(getWorld().getSelection().size()
					+ " Objects selected");

		} else {

			if (wo != null) {
				statusStr.append(wo.getFullName());
			} else {
				statusStr.append("No Model Selected");
			}
		}
		return statusStr.toString();
	}

	@Override
	protected NodeViewer getWorld() {
		return (NodeViewer) super.getWorld();
	}
}
