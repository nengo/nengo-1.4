package ca.neo.ui.models.viewers;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.Projection;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.impl.FunctionInput;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.ui.actions.RunSimulatorAction;
import ca.neo.ui.models.PModel;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.actions.SaveNetworkAction;
import ca.neo.ui.models.icons.IconWrapper;
import ca.neo.ui.models.nodes.PFunctionInput;
import ca.neo.ui.models.nodes.PNEFEnsemble;
import ca.neo.ui.models.nodes.PNetwork;
import ca.neo.ui.models.nodes.connectors.POrigin;
import ca.neo.ui.models.nodes.connectors.PTermination;
import ca.neo.ui.views.objects.configurable.IConfigurable;
import ca.neo.ui.views.objects.configurable.managers.DialogConfig;
import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.neo.ui.views.objects.configurable.struct.PTInt;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.handlers.IContextMenu;
import ca.shu.ui.lib.handlers.StatusBarHandler;
import ca.shu.ui.lib.objects.widgets.TrackedActivity;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.NamedObject;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.impl.WorldImpl;
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
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * 
 * TODO: Save layout information to Network model TODO: Layout bounds should be
 * saved by Configuration manager
 * 
 * @author Shu Wu
 */
public class NetworkViewer extends WorldImpl implements NamedObject,
		IContextMenu {
	private static final long serialVersionUID = -3018937112672942653L;

	static final Dimension DEFAULT_BOUNDS = new Dimension(1000, 1000);

	Class currentLayoutType = null;

	IconWrapper icon;

	Dimension layoutBounds = DEFAULT_BOUNDS;

	Network network;

	Hashtable<String, PNeoNode> nodesUI = new Hashtable<String, PNeoNode>();

	PNetwork networkProxy;

	/**
	 * 
	 * @param pNetwork
	 * @param root
	 */
	public NetworkViewer(PNetwork pNetwork) {
		super("");
		this.networkProxy = pNetwork;
		this.network = pNetwork.getModelNetwork();

		// getSky().setViewScale(0.5);

		setStatusBarHandler(new ModelStatusBarHandler(this));

		setFrameVisible(false);

		(new TrackedActivity("Building network UI") {

			@Override
			public void doActivity() {
				constructChildrenNodes();

			}

		}).startThread(true);

	}

	@Override
	public void addedToWorld() {
		// TODO Auto-generated method stub
		super.addedToWorld();

		// this.animateToBounds(0, 0, 400, 300, 1000);
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

		if (updateModel) {
			try {

				getModel().addNode(nodeProxy.getNode());

			} catch (StructuralException e) {
				Util.Warning(e.toString());
				return;
			}
		}

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

	@SuppressWarnings( { "unchecked" })
	public void applyJungLayout(Class layoutType) {

		Layout layout = null;
		try {
			Class[] ctArgs = new Class[1];
			ctArgs[0] = Graph.class;

			Constructor ct = layoutType.getConstructor(ctArgs);
			Object[] args = new Object[1];
			args[0] = getGraph();

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
		(new JungLayoutActivity(layout)).startThread(false);

	}

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
	public PopupMenuBuilder constructPopupMenu() {
		PopupMenuBuilder menu = super.constructPopupMenu();

		// if (getParent() instanceof Window) {
		// menu.addSection("Viewer");
		// menu.addAction(new MinimizeAction());
		// }

		menu.addSection("Simulator");
		menu.addAction(new RunSimulatorAction("Run", network.getSimulator()));

		/*
		 * File menu
		 */
		menu.addSection("File");
		menu.addAction(new SaveNetworkAction("Save network", networkProxy));

		/*
		 * Create new models
		 */
		menu.addSection("Create new model");

		MenuBuilder nodesMenu = menu.createSubMenu("Nodes");
		MenuBuilder functionsMenu = menu.createSubMenu("Functions");

		/*
		 * Nodes
		 */
		nodesMenu.addAction(new AddModelAction(PNetwork.class));
		nodesMenu.addAction(new AddModelAction(PNEFEnsemble.class));

		/*
		 * Functions
		 */
		functionsMenu.addAction(new AddModelAction(PFunctionInput.class));

		/*
		 * Layouts
		 */
		menu.addSection("Layout");

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

	/**
	 * 
	 * @return The nodes as a graph
	 */
	public Graph getGraph() {
		DirectedSparseGraph graph = new DirectedSparseGraph();

		Enumeration<PNeoNode> enumeration = nodesUI.elements();

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
		Projection[] projections = getModel().getProjections();
		for (int i = 0; i < projections.length; i++) {
			Projection projection = projections[i];

			Origin origin = projection.getOrigin();
			Termination term = projection.getTermination();

			PNeoNode nodeOrigin = getNode(origin.getNode().getName());
			PNeoNode nodeTerm = getNode(term.getNode().getName());

			DirectedSparseEdge edge = new DirectedSparseEdge(nodeOrigin
					.getVertex(), nodeTerm.getVertex());

		}

		return graph;

	}

	/**
	 * TODO: Move these functions into a helper class
	 * 
	 * @return Layout bounds to be used by Layout algorithms
	 */
	protected Dimension getLayoutBounds() {
		return layoutBounds;
	}

	/**
	 * 
	 * @return NEO Network model
	 */
	public Network getModel() {
		return network;
	}

	public String getName() {
		return "Network Viewer: " + networkProxy.getName();
	}

	/**
	 * 
	 * @param name
	 *            of Node
	 * @return Node UI object
	 */
	public PNeoNode getNode(String name) {
		return nodesUI.get(name);
	}

	public void removeNode(PNeoNode nodeProxy) {
		removeNode(nodeProxy, true);
	}

	/**
	 * 
	 * @param nodeProxy
	 *            node to be removed
	 * @param updateModel
	 *            if true, the network model is updated. this may be false, if
	 *            it is known that the network model already contains this node
	 */
	public void removeNode(PNeoNode nodeProxy, boolean updateModel) {
		nodesUI.remove(nodeProxy);
		if (updateModel) {
			try {

				getModel().removeNode(nodeProxy.getName());

			} catch (StructuralException e) {
				Util.Warning(e.toString());
				return;
			}
		}
	}

	public void setLayoutBounds(Dimension layoutBounds) {
		this.layoutBounds = layoutBounds;
	}

	protected PNeoNode buildNodeUI(Node node) {
		if (node instanceof NEFEnsemble) {
			return new PNEFEnsemble((NEFEnsemble) node);
		} else if (node instanceof FunctionInput) {

			return new PFunctionInput((FunctionInput) node);
		} else if (node instanceof Network) {
			return new PNetwork((Network) node);

		} else {
			Util.Error("Unsupported node type");
			return null;
		}

	}

	/**
	 * Construct children UI nodes from the NEO Network model
	 */
	protected void constructChildrenNodes() {

		setName(network.getName());

		/*
		 * Construct UI objects for nodes
		 */

		Node[] nodes = network.getNodes();

		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];

			PNeoNode nodeUI = buildNodeUI(node);

			addNodeToUI(nodeUI, false, false);

		}

		/**
		 * TODO: get references to origins and terminations
		 * 
		 */
		Projection[] projections = network.getProjections();
		for (int i = 0; i < projections.length; i++) {
			Projection projection = projections[i];

			Origin origin = projection.getOrigin();
			Termination term = projection.getTermination();

			PNeoNode nodeOrigin = getNode(origin.getNode().getName());

			PNeoNode nodeTerm = getNode(term.getNode().getName());

			POrigin originUI = nodeOrigin.getOrigin(origin.getName());
			PTermination termUI = nodeTerm.getTermination(term.getName());

			// modifyModel is false because the connections already exist in the
			// NEO Network model
			originUI.connectTo(termUI, false);
		}
		restoreNodeLayout(DEFAULT_NODE_LAYOUT_NAME);
	}

	public static final String DEFAULT_NODE_LAYOUT_NAME = "AutoSaved";

	class AddModelAction extends ReversableAction {

		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		Class nc;

		PNeoNode nodeAdded;

		public AddModelAction(Class nc) {
			super("Add new " + nc.getSimpleName(), nc.getSimpleName());
			this.nc = nc;
		}

		@Override
		protected void action() throws ActionException {

			PNeoNode nodeProxy = null;
			try {

				nodeProxy = (PNeoNode) nc.newInstance();
				new DialogConfig(nodeProxy);

				if (nodeProxy.isModelCreated()) {

					addNodeToUI(nodeProxy, true, true);
				}
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (nodeProxy == null) {
				throw new ActionException("Could not create node", false);
			} else {
				nodeAdded = nodeProxy;
			}
		}

		@Override
		protected void undo() throws ActionException {
			nodeAdded.destroy();

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
					Enumeration<PNeoNode> enumeration = nodesUI.elements();
					PTransformActivity nodeMoveActivity = null;
					while (enumeration.hasMoreElements()) {
						PNeoNode node = enumeration.nextElement();

						Point2D coord = layout.getLocation(node.getVertex());

						// if (coord != null) {
						nodeMoveActivity = node.animateToPositionScaleRotation(
								coord.getX(), coord.getY(), node.getScale(),
								node.getRotation(), 1000);
						// }

					}

					if (nodeMoveActivity != null) {
						(new ZoomActivity()).startAfter(nodeMoveActivity);

					}
				}
			}).startThread(true);

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
			savedLayout = new NodeLayout("", nodesUI);
			applyJungLayout(layoutClass);

		}

		protected void restoreNodePositions() {
			Enumeration<PNeoNode> en = nodesUI.elements();

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

	class SquareLayoutAction extends LayoutAction {

		private static final long serialVersionUID = 1L;

		public SquareLayoutAction() {
			super("Apply square layout", "Square");
		}

		@Override
		protected void action() throws ActionException {
			savedLayout = new NodeLayout("", nodesUI);
			applySquareLayout();
		}

	}

	class ZoomActivity extends PActivity {

		public ZoomActivity() {
			super(0);
			getRoot().addActivity(this);
		}

		@Override
		protected void activityStarted() {
			zoomToFit();
		}

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

		Enumeration<PNeoNode> en = nodesUI.elements();

		while (en.hasMoreElements()) {
			PNeoNode node = en.nextElement();

			Point2D savedPosition = layout.getPosition(node.getName());
			if (savedPosition != null) {
				node.animateToPositionScaleRotation(savedPosition.getX(),
						savedPosition.getY(), node.getScale(), node
								.getRotation(), 1000);
			}
		}
		return true;
	}

	static final String LAYOUT_MANAGER_KEY = "layout/manager";

	public NodeLayoutManager getNodeLayoutManager() {
		NodeLayoutManager layoutManager = null;
		try {
			Object obj = getModel().getMetaData(LAYOUT_MANAGER_KEY);
			if (obj != null)
				layoutManager = (NodeLayoutManager) obj;
		} catch (Throwable e) {
			Util.Error("Could not access layout manager, creating a new one");
			// catch all exceptions
		}

		if (layoutManager == null) {
			layoutManager = new NodeLayoutManager();
			getModel().setMetaData(LAYOUT_MANAGER_KEY, layoutManager);
		}
		return (NodeLayoutManager) layoutManager;

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
		new DialogConfig(this);

	}

}