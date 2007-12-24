package ca.neo.ui.models.viewers;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.Probeable;
import ca.neo.model.Projection;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.ui.actions.CreateModelAction;
import ca.neo.ui.actions.LayoutAction;
import ca.neo.ui.actions.OpenNeoFileAction;
import ca.neo.ui.actions.RunSimulatorAction;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PInt;
import ca.neo.ui.configurable.managers.ConfigManager;
import ca.neo.ui.configurable.managers.ConfigManager.ConfigMode;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.nodes.UINetwork;
import ca.neo.ui.models.nodes.widgets.UIOrigin;
import ca.neo.ui.models.nodes.widgets.UITermination;
import ca.neo.util.Probe;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.exceptions.UIException;
import ca.shu.ui.lib.objects.activities.TrackedAction;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.ISOMLayout;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.contrib.CircleLayout;
import edu.uci.ics.jung.visualization.contrib.KKLayout;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Viewer for peeking into a Network
 * 
 * @author Shu Wu
 */
public class NetworkViewer extends NodeViewer {
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
	protected void constructLayoutMenu(MenuBuilder layoutMenu) {
		layoutMenu.addSection("Elastic layout");
		if (!getGround().isElasticMode()) {
			layoutMenu.addAction(new SetElasticLayoutAction("Enable", true));
		} else {
			layoutMenu.addAction(new SetElasticLayoutAction("Disable", false));
		}

		layoutMenu.addSection("Apply layout");
		super.constructLayoutMenu(layoutMenu);
		MenuBuilder algorithmLayoutMenu = layoutMenu.addSubMenu("Algorithm");

		layoutMenu.addSection("File");
		layoutMenu.addAction(new SaveLayout("Save"));

		MenuBuilder restoreLayout = layoutMenu.addSubMenu("Restore");

		algorithmLayoutMenu.addAction(new JungLayoutAction(FRLayout.class,
				"Fruchterman-Reingold"));
		algorithmLayoutMenu.addAction(new JungLayoutAction(KKLayout.class,
				"Kamada-Kawai"));
		algorithmLayoutMenu.addAction(new JungLayoutAction(CircleLayout.class,
				"Circle"));
		algorithmLayoutMenu.addAction(new JungLayoutAction(ISOMLayout.class,
				"ISOM"));

		MenuBuilder layoutSettings = algorithmLayoutMenu.addSubMenu("Settings");
		layoutSettings.addAction(new SetLayoutBoundsAction(
				"Set preferred bounds", this));

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
	protected void prepareForDestroy() {

		saveLayoutAsDefault();
		super.prepareForDestroy();
	}

	@Override
	protected void removeNeoNode(UINeoNode nodeUI) {

		try {
			nodeUI.showPopupMessage("Node " + nodeUI.getName()
					+ " removed from Network");
			getNetwork().removeNode(nodeUI.getName());

		} catch (StructuralException e) {
			UserMessages.showWarning(e.toString());
			return;
		}
		super.removeNeoNode(nodeUI);
	}

	@Override
	public void addNeoNode(UINeoNode node, boolean updateModel,
			boolean dropInCenterOfCamera, boolean moveCamera) {

		if (updateModel) {
			try {

				getNetwork().addNode(node.getModel());

			} catch (StructuralException e) {
				UserMessages.showWarning(e.toString());
				return;
			}
		}
		super.addNeoNode(node, updateModel, dropInCenterOfCamera, moveCamera);

		if (updateModel) {
			node.showPopupMessage("Node " + getName() + " added to Network");

		}
	}

	@Override
	public void applyDefaultLayout() {
		if (getNeoNodes().size() != 0) {
			if (restoreNodeLayout(DEFAULT_NODE_LAYOUT_NAME)) {
				return;
			} else {
				(new DoJungLayout(KKLayout.class)).doAction();
			}
		}
		// enable elastic layout for Jung && when no nodes are loaded.
		getGround().setElasticEnabled(true);
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
		menu.addSection("Add model to network");
		MenuBuilder createNewMenu = menu.addSubMenu("Create new");

		// Nodes
		for (Class element : UINeoNode.NODE_TYPES) {
			try {
				createNewMenu.addAction(new CreateModelAction(this, element));
			} catch (UIException e) {
				// swallow this, not all model types can be instantiated
			}
		}
		menu.addAction(new OpenNeoFileAction(this));

		/*
		 * Origins & Terminations
		 */
		menu.addSection("Origins and Terminations");
		menu.addAction(new ShowAllOriginTerminations("Show all"));
		menu.addAction(new HideAllOriginTerminations("Hide all"));

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
	 * @return NEO Network model represented by the viewer
	 */
	public Network getNetwork() {
		return (Network) getModel();
	}

	/**
	 * @return Static settings including saved layouts
	 */
	public NetworkViewerConfig getConfig() {
		return getViewerParent().getSavedConfig();
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

		Enumeration<UINeoNode> en = getNeoNodes().elements();

		double startX = Double.MAX_VALUE;
		double startY = Double.MAX_VALUE;
		double endX = Double.MIN_VALUE;
		double endY = Double.MIN_VALUE;
		boolean foundSavedPosition = false;

		while (en.hasMoreElements()) {
			UINeoNode node = en.nextElement();

			Point2D savedPosition = layout.getPosition(node.getName());
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
			PBounds fullBounds = new PBounds(startX, startY, endX - startX,
					endY - startY);
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
			NodeLayout nodeLayout = new NodeLayout(name, this, getGround()
					.isElasticMode());

			layouts.addLayout(nodeLayout);
		} else {
			UserMessages.showError("Could not save node layout");
		}
	}

	/**
	 * Construct UI Nodes from the NEO Network model
	 */
	@Override
	public void updateViewFromModel() {
		getGround().destroyAndClearChildren();

		/*
		 * Construct Nodes from the Network model
		 */

		Node[] nodes = getNetwork().getNodes();

		for (Node node : nodes) {

			/*
			 * only add nodes if they don't already exist
			 */
			if (getNode(node.getName()) == null) {
				UINeoNode nodeUI = UINeoNode.createNodeUI(node);
				addNeoNode(nodeUI, false, false, false);
			}
		}

		/*
		 * Construct projections
		 */
		Projection[] projections = getNetwork().getProjections();
		for (Projection projection : projections) {
			Origin origin = projection.getOrigin();
			Termination term = projection.getTermination();

			UINeoNode nodeOrigin = getNode(origin.getNode().getName());

			UINeoNode nodeTerm = getNode(term.getNode().getName());

			UIOrigin originUI = nodeOrigin.showOrigin(origin.getName());
			UITermination termUI = nodeTerm.showTermination(term.getName());

			// modifyModel is false because the connections already exist in the
			// NEO Network model
			originUI.connectTo(termUI, false);
		}

		/*
		 * Construct probes
		 */
		Probe[] probes = getNetwork().getSimulator().getProbes();

		for (Probe probe : probes) {
			Probeable target = probe.getTarget();

			if (!(target instanceof Node)) {
				UserMessages.showError("Unsupported target type for probe");
			} else {

				if (!probe.isInEnsemble()) {

					Node node = (Node) target;

					UINeoNode nodeUI = getNode(node.getName());
					nodeUI.showProbe(probe);

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
	 * Activity for performing a Jung Layout.
	 * 
	 * @author Shu Wu
	 */
	class DoJungLayout extends TrackedAction {

		private static final long serialVersionUID = 1L;

		private Class<? extends Layout> layoutType;

		public DoJungLayout(Class<? extends Layout> layoutType) {
			super("Performing layout: " + layoutType.getSimpleName());
			this.layoutType = layoutType;
		}

		private Layout layout;

		@Override
		protected void action() throws ActionException {

			try {
				Class<?>[] ctArgs = new Class[1];
				ctArgs[0] = Graph.class;

				Constructor<?> ct = layoutType.getConstructor(ctArgs);
				Object[] args = new Object[1];

				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						getGround().updateGraph();
					}
				});

				args[0] = getGround().getGraph();

				layout = (Layout) ct.newInstance(args);

			} catch (Exception e) {
				e.printStackTrace();
				throw new ActionException("Could not apply layout: "
						+ e.getMessage());
			}

			layout.initialize(getLayoutBounds());

			if (layout.isIncremental()) {
				long timeNow = System.currentTimeMillis();
				while (!layout.incrementsAreDone()
						&& (System.currentTimeMillis() - timeNow < 1000 && !layout
								.incrementsAreDone())) {
					layout.advancePositions();
				}
			}
			/**
			 * Layout nodes needs to be done in the Swing dispatcher thread
			 */
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						getGround()
								.updateChildrenFromLayout(layout, true, true);
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Action to hide all widgets
	 * 
	 * @author Shu Wu
	 */
	class HideAllOriginTerminations extends StandardAction {

		private static final long serialVersionUID = 1L;

		public HideAllOriginTerminations(String actionName) {
			super("Hide all origins & terminations", actionName);
		}

		@Override
		protected void action() throws ActionException {
			hideAllOriginTerminations();
		}

	}

	/**
	 * Action for applying a Jung Layout. It implements LayoutAction, which
	 * allows it to be reversable.
	 * 
	 * @author Shu
	 */
	class JungLayoutAction extends LayoutAction {

		private static final long serialVersionUID = 1L;

		Class<? extends Layout> layoutClass;

		public JungLayoutAction(Class<? extends Layout> layoutClass, String name) {
			super(NetworkViewer.this, "Apply layout " + name, name);
			this.layoutClass = layoutClass;
		}

		@Override
		protected void applyLayout() {
			getGround().setElasticEnabled(false);
			(new DoJungLayout(layoutClass)).doAction();
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
			String name = JOptionPane.showInputDialog(UIEnvironment
					.getInstance(), "Name");

			if (name != null) {
				saveNodeLayout(name);
			} else {
				throw new ActionException("Could not get layout name", false);
			}

		}

	}

	/**
	 * Action for starting and running a Iterable Jung Layout
	 * 
	 * @author Shu
	 */
	class SetElasticLayoutAction extends LayoutAction {

		private static final long serialVersionUID = 1L;
		private boolean enabled;

		public SetElasticLayoutAction(String name, boolean enabled) {
			super(NetworkViewer.this, "Set Spring Layout: " + enabled, name);
			this.enabled = enabled;
		}

		@Override
		protected void applyLayout() {
			getGround().setElasticEnabled(enabled);
		}

	}

	/**
	 * Action to show all widgets
	 * 
	 * @author Shu Wu
	 */
	class ShowAllOriginTerminations extends StandardAction {

		private static final long serialVersionUID = 1L;

		public ShowAllOriginTerminations(String actionName) {
			super("Show all origins / terminations", actionName);
		}

		@Override
		protected void action() throws ActionException {
			showAllOriginTerminations();
		}

	}

}

/**
 * Action to set layout bounds.
 * 
 * @author Shu Wu
 */
class SetLayoutBoundsAction extends StandardAction {

	private static final PropertyDescriptor pHeight = new PInt("Height");
	private static final PropertyDescriptor pWidth = new PInt("Width");
	private static final long serialVersionUID = 1L;
	private static final PropertyDescriptor[] zProperties = { pWidth, pHeight };

	private NetworkViewer parent;

	public SetLayoutBoundsAction(String actionName, NetworkViewer parent) {
		super("Set layout bounds", actionName);
		this.parent = parent;
	}

	private void completeConfiguration(PropertySet properties) {
		parent
				.setLayoutBounds(new Dimension((Integer) properties
						.getProperty(pWidth), (Integer) properties
						.getProperty(pHeight)));

	}

	@Override
	protected void action() throws ActionException {

		try {
			PropertySet properties = ConfigManager.configure(zProperties,
					"Layout bounds", UIEnvironment.getInstance(),
					ConfigMode.TEMPLATE_NOT_CHOOSABLE);
			completeConfiguration(properties);

		} catch (ConfigException e) {
			e.defaultHandleBehavior();
		}

	}

}