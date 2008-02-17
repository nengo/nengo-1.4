package ca.neo.ui.models.viewers;

import java.awt.geom.Point2D;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.JOptionPane;

import ca.neo.config.ClassRegistry;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.Probeable;
import ca.neo.model.Projection;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.ui.actions.CreateModelAction;
import ca.neo.ui.actions.CreateModelAdvancedAction;
import ca.neo.ui.actions.OpenNeoFileAction;
import ca.neo.ui.actions.RunSimulatorAction;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.constructors.Constructable;
import ca.neo.ui.models.constructors.ModelFactory;
import ca.neo.ui.models.nodes.UINetwork;
import ca.neo.ui.models.nodes.widgets.UIOrigin;
import ca.neo.ui.models.nodes.widgets.UITermination;
import ca.neo.util.Probe;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import edu.uci.ics.jung.visualization.contrib.KKLayout;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Viewer for peeking into a Network
 * 
 * @author Shu Wu
 */
public class NetworkViewer extends NodeViewer implements INodeContainer {
	private static final boolean ELASTIC_LAYOUT_ENABLED_DEFAULT = false;

	private static final long serialVersionUID = -3018937112672942653L;

	/**
	 * Name given to the current layout by default, saved when the viewer is
	 * closed
	 */
	public static final String DEFAULT_NODE_LAYOUT_NAME = "AutoSaved";

	/**
	 * @param pNetwork
	 *            Parent Network UI wrapper
	 */
	public NetworkViewer(UINetwork pNetwork) {
		super(pNetwork);

	}

	@Override
	protected boolean canRemoveChildModel(Node node) {
		return true;
	}

	@Override
	protected void constructLayoutMenu(MenuBuilder menu) {
		super.constructLayoutMenu(menu);
		menu.addSection("File");
		menu.addAction(new SaveLayout("Save"));

		MenuBuilder restoreLayout = menu.addSubMenu("Restore");

		String[] layoutNames = getConfig().getLayoutNames();

		if (layoutNames.length > 0) {
			for (String element : layoutNames) {
				restoreLayout.addAction(new RestoreLayout(element));
			}
		} else {
			restoreLayout.addLabel("none");
		}

		MenuBuilder deleteLayout = restoreLayout.addSubMenu("Delete");

		if (layoutNames.length > 0) {
			for (String element : layoutNames) {
				deleteLayout.addAction(new DeleteLayout(element));
			}
		} else {
			deleteLayout.addLabel("none");
		}

	}

	@Override
	protected void initialize() {
		super.initialize();
		updateSimulatorProbes();
	}

	@Override
	protected void prepareForDestroy() {

		saveLayoutAsDefault();
		super.prepareForDestroy();
	}

	@Override
	protected void removeChildModel(Node node) {
		try {
			getModel().removeNode(node.getName());
		} catch (StructuralException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Construct UI Nodes from the NEO Network model
	 */
	protected void updateViewFromModel(boolean isFirstUpdate) {

		/*
		 * Get the current children and map them
		 */
		HashMap<Node, UINeoNode> currentNodes = new HashMap<Node, UINeoNode>(getGround()
				.getChildrenCount());

		Enumeration<UINeoNode> en = neoNodesChildren.elements();
		while (en.hasMoreElements()) {
			UINeoNode node = en.nextElement();
			if (!node.isDestroyed()) {
				Util.Assert(node.getModel() != null);
				currentNodes.put(node.getModel(), node);
			}
		}
		neoNodesChildren.clear();

		/*
		 * Construct Nodes from the Network model
		 */
		Node[] nodes = getModel().getNodes();

		for (Node node : nodes) {
			if (getUINode(node) == null) {
				UINeoNode nodeUI = currentNodes.get(node);

				if (nodeUI == null) {
					/*
					 * Create UI Wrappers here
					 */
					nodeUI = UINeoNode.createNodeUI(node);

					boolean centerAndNotify = !isFirstUpdate;

					addUINode(nodeUI, centerAndNotify, false);
					if (centerAndNotify) {
						nodeUI.showPopupMessage("Node " + node.getName() + " added to Network");
					}
				} else {
					neoNodesChildren.put(nodeUI.getModel(), nodeUI);
				}

			} else {
				Util.Assert(false, "Trying to add node which already exists");
			}
		}

		/*
		 * Prune existing nodes by deleting them
		 */
		for (Node node : currentNodes.keySet()) {
			// Remove nodes which are no longer referenced by the network model
			if (getUINode(node) == null) {
				UINeoNode nodeUI = currentNodes.get(node);
				nodeUI.showPopupMessage("Node " + nodeUI.getName() + " removed from Network");
				nodeUI.destroy();
			}
		}

		/*
		 * Construct projections
		 */
		Projection[] projections = getModel().getProjections();
		for (Projection projection : projections) {
			Origin origin = projection.getOrigin();
			Termination term = projection.getTermination();

			UINeoNode nodeOrigin = getUINode(origin.getNode());

			UINeoNode nodeTerm = getUINode(term.getNode());

			UIOrigin originUI = nodeOrigin.showOrigin(origin.getName());
			UITermination termUI = nodeTerm.showTermination(term.getName());

			originUI.connectTo(termUI, false);
			if (!isFirstUpdate) {
				termUI.showPopupMessage("NEW Projection to " + termUI.getName() + "." + getName());
			}

		}

	}

	public void addNodeModel(Node node) {
		try {
			getModel().addNode(node);
		} catch (StructuralException e) {
			UserMessages.showWarning(e.toString());
			return;
		}
	}

	@Override
	public void applyDefaultLayout() {
		if (getUINodes().size() != 0) {
			if (restoreNodeLayout(DEFAULT_NODE_LAYOUT_NAME)) {
				return;
			} else {
				applyJungLayout(KKLayout.class);
			}
		}
		if (ELASTIC_LAYOUT_ENABLED_DEFAULT) {
			// enable elastic layout for Jung && when no nodes are loaded.
			getGround().setElasticEnabled(true);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);

		menu.addSection("Simulator");
		menu.addAction(new RunSimulatorAction("Run", getViewerParent()));

		/*
		 * Create new models
		 */
		menu.addSection("Add model");
		MenuBuilder createNewMenu = menu.addSubMenu("Create new");

		// Nodes
		for (Constructable constructable : ModelFactory.getNodeConstructables()) {
			createNewMenu.addAction(new CreateModelAction(this, constructable));
		}

		MenuBuilder createAdvancedMenu = createNewMenu.addSubMenu("Other");
		for (Class<?> element : ClassRegistry.getInstance().getRegisterableTypes()) {
			if (Node.class.isAssignableFrom(element)) {
				createAdvancedMenu.addAction(new CreateModelAdvancedAction(this, element));
			}
		}

		menu.addAction(new OpenNeoFileAction(this));

		/*
		 * Origins & Terminations
		 */
		menu.addSection("Origins and Terminations");
		menu.addAction(new SetOTVisiblityAction("Show all", true));
		menu.addAction(new SetOTVisiblityAction("Hide all", false));

	}

	/**
	 * @param name
	 *            Name of layout to delete
	 */
	public void deleteNodeLayout(String name) {
		NetworkViewerConfig layouts = getConfig();
		layouts.removeLayout(name);
	}

	/**
	 * @return Static settings including saved layouts
	 */
	public NetworkViewerConfig getConfig() {
		return getViewerParent().getSavedConfig();
	}

	@Override
	public Network getModel() {
		return (Network) super.getModel();
	}

	@Override
	public UINetwork getViewerParent() {
		return (UINetwork) super.getViewerParent();
	}

	/**
	 * @return Whether the operation was successful
	 * @param name
	 *            Name of layout to restore
	 */
	public boolean restoreNodeLayout(String name) {

		NetworkViewerConfig config = getConfig();
		NodeLayout layout = config.getLayout(name);

		if (layout == null) {
			return false;
		}
		getGround().setElasticEnabled(false);
		boolean enableElasticMode = layout.elasticModeEnabled();

		double startX = Double.MAX_VALUE;
		double startY = Double.MAX_VALUE;
		double endX = Double.MIN_VALUE;
		double endY = Double.MIN_VALUE;
		boolean foundSavedPosition = false;

		for (UINeoNode node : getUINodes()) {

			Point2D savedPosition = layout.getPosition(node);
			if (savedPosition != null) {
				double x = savedPosition.getX();
				double y = savedPosition.getY();

				if (!enableElasticMode) {
					node.animateToPositionScaleRotation(x, y, 1, 0, 700);
				} else {
					node.setOffset(x, y);
				}

				if (x < startX) {
					startX = x;
				}
				if (x + node.getWidth() > endX) {
					endX = x + node.getWidth();
				}

				if (y < startY) {
					startY = y;
				}
				if (y + node.getHeight() > endY) {
					endY = y + node.getHeight();
				}

				foundSavedPosition = true;
			}

		}

		if (foundSavedPosition) {
			PBounds fullBounds = new PBounds(startX, startY, endX - startX, endY - startY);
			zoomToBounds(fullBounds, 700);
		}

		if (enableElasticMode) {
			getGround().setElasticEnabled(true);
		}

		return true;
	}

	/**
	 * Saves the current layout as the default
	 */
	public void saveLayoutAsDefault() {
		saveNodeLayout(NetworkViewer.DEFAULT_NODE_LAYOUT_NAME);
	}

	/**
	 * @param name
	 *            Name given to the saved layout
	 */
	public void saveNodeLayout(String name) {

		NetworkViewerConfig layouts = getConfig();
		if (layouts != null) {
			NodeLayout nodeLayout = new NodeLayout(name, this, getGround().isElasticMode());

			layouts.addLayout(nodeLayout);
		} else {
			UserMessages.showError("Could not save node layout");
		}
	}

	public void updateSimulatorProbes() {
		/*
		 * Construct probes TODO: Remove unreferenced probes
		 */
		Probe[] probes = getModel().getSimulator().getProbes();

		for (Probe probe : probes) {
			Probeable target = probe.getTarget();

			if (!(target instanceof Node)) {
				UserMessages.showError("Unsupported target type for probe");
			} else {

				if (!probe.isInEnsemble()) {

					Node node = (Node) target;

					UINeoNode nodeUI = getUINode(node);
					if (nodeUI != null) {
						nodeUI.showProbe(probe);
					} else {
						Util.debugMsg("There is a dangling probe in the Simulator");
					}
				}
			}

		}
	}

	/**
	 * Action to delete a layout
	 * 
	 * @author Shu Wu
	 */
	class DeleteLayout extends StandardAction {
		private static final long serialVersionUID = 1L;

		String layoutName;

		public DeleteLayout(String name) {
			super("Delete layout: " + name, name);
			this.layoutName = name;
		}

		@Override
		protected void action() throws ActionException {
			deleteNodeLayout(layoutName);
		}
	}

	/**
	 * Action to restore a layout
	 * 
	 * @author Shu Wu
	 */
	class RestoreLayout extends StandardAction {
		private static final long serialVersionUID = 1L;

		String layoutName;

		public RestoreLayout(String name) {
			super("Restore layout: " + name, name);
			this.layoutName = name;
		}

		@Override
		protected void action() throws ActionException {
			if (!restoreNodeLayout(layoutName)) {
				throw new ActionException("Could not restore layout");
			}
		}
	}

	/**
	 * Action to save a layout
	 * 
	 * @author Shu Wu
	 */
	class SaveLayout extends StandardAction {
		private static final long serialVersionUID = 1L;

		public SaveLayout(String description) {
			super("Save layout", description);
		}

		@Override
		protected void action() throws ActionException {
			String name = JOptionPane.showInputDialog(UIEnvironment.getInstance(), "Name");

			if (name != null) {
				saveNodeLayout(name);
			} else {
				throw new ActionException("Could not get layout name", false);
			}

		}

	}

	/**
	 * Action to hide all widgets
	 * 
	 * @author Shu Wu
	 */
	class SetOTVisiblityAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		private boolean visible;

		public SetOTVisiblityAction(String actionName, boolean visible) {
			super(actionName);
			this.visible = visible;
		}

		@Override
		protected void action() throws ActionException {
			setOriginsTerminationsVisible(visible);
		}

	}
}
