package ca.neo.ui.models.viewers;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import javax.swing.JOptionPane;

import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.Probeable;
import ca.neo.model.Projection;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.ui.actions.CreateModelAction;
import ca.neo.ui.actions.LoadEnsembleAction;
import ca.neo.ui.actions.LoadNetworkAction;
import ca.neo.ui.actions.RunSimulatorAction;
import ca.neo.ui.models.PModelClasses;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.actions.SaveNodeContainerAction;
import ca.neo.ui.models.icons.ModelIcon;
import ca.neo.ui.models.nodes.PNetwork;
import ca.neo.ui.models.nodes.connectors.POrigin;
import ca.neo.ui.models.nodes.connectors.PTermination;
import ca.neo.ui.views.objects.configurable.IConfigurable;
import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.neo.ui.views.objects.configurable.managers.UserConfig;
import ca.neo.ui.views.objects.configurable.struct.PTInt;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
import ca.neo.util.Probe;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.widgets.TrackedActivity;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.contrib.CircleLayout;
import edu.uci.ics.jung.visualization.contrib.KKLayout;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;

/**
 * 
 * TODO: Save layout information to Network model TODO: Layout bounds should be
 * saved by Configuration manager
 * 
 * @author Shu Wu
 */
public class NetworkViewer extends NodeViewer {
	public static final String DEFAULT_NODE_LAYOUT_NAME = "AutoSaved";

	private static final long serialVersionUID = -3018937112672942653L;

	@SuppressWarnings("unchecked")
	Class currentLayoutType = null;

	// Network network;

	// PNetwork networkProxy;

	ModelIcon icon;

	/**
	 * 
	 * @param pNetwork
	 * @param root
	 */
	public NetworkViewer(PNetwork pNetwork) {
		super(pNetwork);

	}

	@Override
	public void addedToWorld() {
		// TODO Auto-generated method stub
		super.addedToWorld();

		// this.animateToBounds(0, 0, 400, 300, 1000);
	}

	@Override
	public void addNeoNode(PNeoNode nodeProxy, boolean updateModel,
			boolean dropInCenterOfCamera, boolean moveCamera) {
		if (updateModel) {
			try {

				getNetwork().addNode(nodeProxy.getModel());

			} catch (StructuralException e) {
				Util.Warning(e.toString());
				return;
			}
		}
		super.addNeoNode(nodeProxy, updateModel, dropInCenterOfCamera,
				moveCamera);

		if (updateModel) {
			showTransientMsg("Node " + getName() + " added to Network",
					nodeProxy);

		}
	}

	public void applyDefaultLayout() {
		if (!restoreNodeLayout(DEFAULT_NODE_LAYOUT_NAME)) {
			applyJungLayout(KKLayout.class);
		}
	}

	@SuppressWarnings( { "unchecked" })
	public void applyJungLayout(Class layoutType) {

		Layout layout = null;
		try {
			Class[] ctArgs = new Class[1];
			ctArgs[0] = Graph.class;

			Constructor ct = layoutType.getConstructor(ctArgs);
			Object[] args = new Object[1];
			args[0] = createGraph();

			layout = (Layout) ct.newInstance(args);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		if (layout == null) {
			Util.Error("Could not apply layout");
			return;
		}

		currentLayoutType = layoutType;
		(new JungLayoutActivity(layout)).startThread();

	}

	@Override
	public PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = super.constructMenu();

		menu.addSection("Simulator");
		menu.addAction(new RunSimulatorAction("Run", getNetwork()
				.getSimulator()));

		/*
		 * File menu
		 */
		// menu.addSection("File");
		menu.addAction(new SaveNodeContainerAction("Save network to file",
				getViewerParent()));

		/*
		 * Create new models
		 */
		menu.addSection("Add model to network");
		MenuBuilder createNewMenu = menu.createSubMenu("Create new");

		MenuBuilder nodeContainersMenu = createNewMenu
				.createSubMenu("Node Containers");
		MenuBuilder nodesMenu = createNewMenu.createSubMenu("Nodes");
		MenuBuilder functionsMenu = createNewMenu.createSubMenu("Functions");

		MenuBuilder fromFileMenu = menu.createSubMenu("From file");
		fromFileMenu.addAction(new LoadNetworkAction("Network", this));
		fromFileMenu.addAction(new LoadEnsembleAction("NEFEnsemble / Ensemble",
				this));

		// Nodes
		for (int i = 0; i < PModelClasses.NODE_TYPES.length; i++) {
			nodesMenu.addAction(new CreateModelAction(this,
					PModelClasses.NODE_TYPES[i]));
		}

		// Node Containers
		for (int i = 0; i < PModelClasses.NODE_CONTAINER_TYPES.length; i++) {
			nodeContainersMenu.addAction(new CreateModelAction(this,
					PModelClasses.NODE_CONTAINER_TYPES[i]));
		}

		// Functions
		for (int i = 0; i < PModelClasses.FUNCTION_TYPES.length; i++) {
			functionsMenu.addAction(new CreateModelAction(this,
					PModelClasses.FUNCTION_TYPES[i]));
		}

		/*
		 * Layouts
		 */

		menu.addSection("Layout");

		menu.addAction(new ShowAllWidgetsAction("Show all widgets"));
		menu.addAction(new HideAllWidgetsAction("Hide all widgets"));

		MenuBuilder layoutMenu = menu.createSubMenu("Apply layout");

		MenuBuilder layoutSettings = layoutMenu.createSubMenu("Settings");
		layoutSettings.addAction(new SetLayoutBoundsAction("Set bounds", this));

		layoutMenu.addAction(new SquareLayoutAction());
		layoutMenu.addAction(new LayoutAction(FRLayout.class,
				"Fruchterman-Reingold"));
		layoutMenu.addAction(new LayoutAction(KKLayout.class, "Kamada-Kawai"));
		layoutMenu.addAction(new LayoutAction(CircleLayout.class, "Circle"));

		// MenuBuilder layoutFile = menu.createSubMenu("File");

		menu.addAction(new SaveLayout("Save layout"));
		MenuBuilder savedLayouts = menu.createSubMenu("Restore layout");
		String[] layoutNames = getNodeLayoutManager().getLayoutNames();

		if (layoutNames.length > 0) {
			for (int i = 0; i < layoutNames.length; i++) {
				savedLayouts.addAction(new RestoreLayout(layoutNames[i]));
			}
		} else {
			savedLayouts.addLabel("none");
		}

		/**
		 * TODO: Enable spring layout & ISOM Layout which are continuous
		 */
		// layoutMenu.addAction(new LayoutAction(SpringLayout.class, "Spring"));
		// layoutMenu.addAction(new LayoutAction(ISOMLayout.class, "ISOM"));
		return menu;
	}

	public Network getNetwork() {
		return (Network) getModel();
	}

	/**
	 * 
	 * @param name
	 *            of Node
	 * @return Node UI object
	 */
	public PNeoNode getNode(String name) {
		return getViewerNodes().get(name);
	}

	public NodeLayoutManager getNodeLayoutManager() {
		NodeLayoutManager layoutManager = null;
		try {

			Object obj = getViewerParent().getModel().getMetaData(
					getLayoutManagerKey());
			if (obj != null)
				layoutManager = (NodeLayoutManager) obj;
		} catch (Throwable e) {
			Util.Error("Could not access layout manager, creating a new one");
			// catch all exceptions
		}

		if (layoutManager == null) {
			layoutManager = new NodeLayoutManager();
			getViewerParent().getModel().setMetaData(getLayoutManagerKey(),
					layoutManager);
		}
		return (NodeLayoutManager) layoutManager;

	}

	@Override
	public PNetwork getViewerParent() {
		// TODO Auto-generated method stub
		return (PNetwork) super.getViewerParent();
	}

	@Override
	public void removeNeoNode(PNeoNode nodeUI) {

		try {
			showTransientMsg("Node " + nodeUI.getName()
					+ " removed from Network", nodeUI);
			getNetwork().removeNode(nodeUI.getName());

		} catch (StructuralException e) {
			Util.Warning(e.toString());
			return;
		}
		super.removeNeoNode(nodeUI);

	}

	/**
	 * @return Whether the layout could be restored
	 * @param name
	 *            of layout to restore
	 */
	public boolean restoreNodeLayout(String name) {
		NodeLayoutManager layouts = getNodeLayoutManager();
		NodeLayout layout = layouts.getLayout(name);

		if (layout == null) {
			return false;
		}

		Enumeration<PNeoNode> en = getViewerNodes().elements();

		while (en.hasMoreElements()) {
			PNeoNode node = en.nextElement();

			Point2D savedPosition = layout.getPosition(node.getName());
			if (savedPosition != null) {
				node.animateToPositionScaleRotation(savedPosition.getX(),
						savedPosition.getY(), 1, 0, 700);
			}
		}
		if (layout.getSavedViewBounds() != null) {
			getSky().setViewBounds(layout.getSavedViewBounds());
		}
		return true;
	}

	public void saveLayoutAsDefault() {
		saveNodeLayout(NetworkViewer.DEFAULT_NODE_LAYOUT_NAME);
	}

	/**
	 * Saves layout
	 * 
	 * @param name
	 */
	public void saveNodeLayout(String name) {

		NodeLayoutManager layouts = getNodeLayoutManager();
		if (layouts != null) {
			NodeLayout nodeLayout = new NodeLayout(name, this);

			layouts.addLayout(nodeLayout);
		} else {
			Util.Error("Could not save node layout");
		}
	}

	/**
	 * Construct children UI nodes from the NEO Network model
	 */
	public void updateNodesFromModel() {

		/*
		 * Removes all existing nodes from this viewer
		 */
		Enumeration<PNeoNode> enumeration = getViewerNodes().elements();
		while (enumeration.hasMoreElements()) {
			enumeration.nextElement().removeFromParent();
		}

		/*
		 * Construct Nodes from the Network model
		 */

		Node[] nodes = getNetwork().getNodes();

		for (int i = 0; i < nodes.length; i++) {

			Node node = nodes[i];
			/*
			 * only add nodes if they don't already exist
			 */
			if (getNode(node.getName()) == null) {
				PNeoNode nodeUI = PModelClasses.createUIFromModel(node);
				addNeoNode(nodeUI, false, false, false);
			}
		}

		/*
		 * Construct projections
		 */
		Projection[] projections = getNetwork().getProjections();
		for (int i = 0; i < projections.length; i++) {
			Projection projection = projections[i];

			Origin origin = projection.getOrigin();
			Termination term = projection.getTermination();

			PNeoNode nodeOrigin = getNode(origin.getNode().getName());

			PNeoNode nodeTerm = getNode(term.getNode().getName());

			POrigin originUI = nodeOrigin.showOrigin(origin.getName());
			PTermination termUI = nodeTerm.showTermination(term.getName());

			// modifyModel is false because the connections already exist in the
			// NEO Network model
			originUI.connectTo(termUI, false);
		}

		/*
		 * Construct probes
		 */
		Probe[] probes = getNetwork().getSimulator().getProbes();

		for (int i = 0; i < probes.length; i++) {
			Probe probe = probes[i];
			Probeable target = probe.getTarget();

			if (!(target instanceof Node)) {
				Util.Error("Unsupported target type for probe");
			} else {

				if (probe.isInEnsemble()) {
					Util
							.Error("A probe is inside an ensemble. Displaying this is not supported yet");
				} else {

					Node node = (Node) target;

					PNeoNode nodeUI = getNode(node.getName());
					nodeUI.showProbe(probe);

				}
			}

		}
	}

	/**
	 * 
	 * @return The Key used to access Metadata containing layout information
	 *         from the Network node
	 */
	private String getLayoutManagerKey() {

		return LAYOUT_MANAGER_KEY;

	}

	/**
	 * 
	 * @return The nodes as a graph
	 */
	protected Graph createGraph() {
		DirectedSparseGraph graph = new DirectedSparseGraph();

		Enumeration<PNeoNode> enumeration = getViewedNodesElements();

		/**
		 * Add vertices
		 */
		while (enumeration.hasMoreElements()) {
			PNeoNode node = enumeration.nextElement();
			DirectedSparseVertex vertex = new DirectedSparseVertex();

			node.setVertex(vertex);

			graph.addVertex(vertex);

		}

		/**
		 * Add Directed edges
		 * 
		 */
		Projection[] projections = getNetwork().getProjections();
		for (int i = 0; i < projections.length; i++) {
			Projection projection = projections[i];

			Origin origin = projection.getOrigin();
			Termination term = projection.getTermination();

			PNeoNode nodeOrigin = getNode(origin.getNode().getName());
			PNeoNode nodeTerm = getNode(term.getNode().getName());

			DirectedSparseEdge edge = new DirectedSparseEdge(nodeOrigin
					.getVertex(), nodeTerm.getVertex());
			graph.addEdge(edge);

		}

		return graph;

	}

	@Override
	protected void prepareForDestroy() {

		saveLayoutAsDefault();
		super.prepareForDestroy();
	}

	class HideAllWidgetsAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public HideAllWidgetsAction(String actionName) {
			super("Hide all widgets", actionName);
		}

		@Override
		protected void action() throws ActionException {
			hideAllWidgets();
		}

	}

	class JungLayoutActivity extends TrackedActivity {
		Layout layout;

		public JungLayoutActivity(Layout layout) {
			super("Performing layout: " + layout.getClass().getSimpleName());
			this.layout = layout;
		}

		@Override
		public void doActivity() {
			layout.initialize(getLayoutBounds());
			while (!layout.incrementsAreDone()) {
				layout.advancePositions();
			}

			/**
			 * Layout nodes needs to be done in a seperate UI-safe thread
			 */
			(new TrackedActivity("Layout nodes") {

				@Override
				public void doActivity() {
					/**
					 * Layout nodes
					 */
					Enumeration<PNeoNode> enumeration = getViewerNodes().elements();
					PTransformActivity nodeMoveActivity = null;
					while (enumeration.hasMoreElements()) {
						PNeoNode node = enumeration.nextElement();
						if (node.getVertex() != null) {
							Point2D coord = layout
									.getLocation(node.getVertex());

							if (coord != null) {
								nodeMoveActivity = node
										.animateToPositionScaleRotation(coord
												.getX(), coord.getY(), 1, 0,
												1000);
							}

						}

					}

					if (nodeMoveActivity != null) {
						(new ZoomActivity()).startAfter(nodeMoveActivity);

					}
				}
			}).invokeLater();

		}
	}

	class LayoutAction extends ReversableAction {

		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		Class layoutClass;

		/**
		 * TODO: Save node positions here, it's safer and won't get deleted by
		 * other layouts
		 */
		NodeLayout savedLayout;

		@SuppressWarnings("unchecked")
		public LayoutAction(Class layoutClass, String name) {
			super("Apply layout " + name, name);
			this.layoutClass = layoutClass;
		}

		public LayoutAction(String description, String actionName) {
			super(description, actionName);
		}

		@Override
		protected void action() throws ActionException {
			savedLayout = new NodeLayout("", NetworkViewer.this);
			applyJungLayout(layoutClass);

		}

		protected void restoreNodePositions() {
			Enumeration<PNeoNode> en = getViewerNodes().elements();

			while (en.hasMoreElements()) {
				PNeoNode node = en.nextElement();

				node.setOffset(savedLayout.getPosition(node.getName()));

			}
		}

		@Override
		protected void undo() throws ActionException {
			restoreNodePositions();
		}

	}

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

	class SaveLayout extends StandardAction {
		private static final long serialVersionUID = 1L;

		public SaveLayout(String description) {
			super("Save layout", description);
		}

		@Override
		protected void action() throws ActionException {
			String name = JOptionPane.showInputDialog(UIEnvironment
					.getInstance(), "Name");

			if (name != null) {
				saveNodeLayout(name);
			} else {
				throw new ActionException("Could not get layout name", false);
			}

		}

	}

	class ShowAllWidgetsAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public ShowAllWidgetsAction(String actionName) {
			super("Show all widgets", actionName);
		}

		@Override
		protected void action() throws ActionException {
			showAllWidgets();
		}

	}

	class SquareLayoutAction extends LayoutAction {

		private static final long serialVersionUID = 1L;

		public SquareLayoutAction() {
			super("Apply square layout", "Square");
		}

		@Override
		protected void action() throws ActionException {
			savedLayout = new NodeLayout("", NetworkViewer.this);
			applySquareLayout();
		}

	}

	class ZoomActivity extends PActivity {

		public ZoomActivity() {
			super(0);
			addActivity(this);
		}

		@Override
		protected void activityStarted() {
			zoomToFit();
		}

	}
}

class SetLayoutBoundsAction extends StandardAction implements IConfigurable {

	private static final long serialVersionUID = 1L;
	static final PropDescriptor pHeight = new PTInt("Height");

	static final PropDescriptor pWidth = new PTInt("Width");

	static final PropDescriptor[] zProperties = { pWidth, pHeight };

	NetworkViewer parent;

	public SetLayoutBoundsAction(String actionName, NetworkViewer parent) {
		super("Set layout bounds", actionName);
		this.parent = parent;
	}

	public void cancelConfiguration() {

	}

	public void completeConfiguration(PropertySet properties) {
		parent
				.setLayoutBounds(new Dimension((Integer) properties
						.getProperty(pWidth), (Integer) properties
						.getProperty(pHeight)));

	}

	public PropDescriptor[] getConfigSchema() {
		return zProperties;
	}

	public String getTypeName() {
		return "Layout bounds";
	}

	public boolean isConfigured() {
		return true;
	}

	@Override
	protected void action() throws ActionException {
		new UserConfig(this);

	}

}
