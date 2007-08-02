package ca.neo.ui.models.viewers;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

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
import ca.neo.ui.models.icons.IconWrapper;
import ca.neo.ui.models.nodes.PFunctionInput;
import ca.neo.ui.models.nodes.PNEFEnsemble;
import ca.neo.ui.models.nodes.PNetwork;
import ca.neo.ui.models.nodes.connectors.POrigin;
import ca.neo.ui.models.nodes.connectors.PTermination;
import ca.neo.ui.views.objects.configurable.managers.DialogConfig;
import ca.shu.ui.lib.handlers.IContextMenu;
import ca.shu.ui.lib.handlers.StatusBarHandler;
import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.objects.widgets.TrackedTask;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.NamedObject;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.impl.WorldImpl;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.event.PInputEvent;

public class NetworkViewer extends WorldImpl implements NamedObject,
		IContextMenu {
	private static final long serialVersionUID = -3018937112672942653L;

	IconWrapper icon;

	Network network;

	PNetwork pNetwork;

	public NetworkViewer(PNetwork pNetwork, PRoot root) {
		super("", root);
		this.pNetwork = pNetwork;
		this.network = pNetwork.getModelNetwork();

		// getSky().setViewScale(0.5);

		setStatusBarHandler(new ModelStatusBarHandler(this));

		setFrameVisible(false);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				TrackedTask task = new TrackedTask(NetworkViewer.this,
						"building Network UI");
				/*
				 * This function must be invoked after PNetwork is attached to
				 * the scene graph
				 */

				constructNetworkUI();
				task.finished();
			}
		});

	}

	Hashtable<String, PNeoNode> nodes = new Hashtable<String, PNeoNode>();

	/**
	 * 
	 * @param nodeProxy
	 *            node to be added
	 * @param updateModel
	 *            if true, the network model is updated. this may be false, if
	 *            it is known that the network model already contains this node
	 */
	public void addNodeToUI(PNeoNode nodeProxy, boolean updateModel) {
		/**
		 * Moves the camera to where the node is positioned
		 */
		setCameraPosition(nodeProxy.getOffset().getX(), nodeProxy.getOffset()
				.getY());

		if (updateModel) {
			try {

				getModel().addNode(nodeProxy.getNode());

			} catch (StructuralException e) {
				Util.Warning(e.toString());
				return;
			}
		}
		nodes.put(nodeProxy.getName(), nodeProxy);
		getGround().catchObject(nodeProxy);
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
		nodes.remove(nodeProxy);
		if (updateModel) {
			try {

				getModel().removeNode(nodeProxy.getName());

			} catch (StructuralException e) {
				Util.Warning(e.toString());
				return;
			}
		}
	}

	public PNeoNode getNode(String name) {
		return nodes.get(name);
	}

	public void addNodeToUI(PNeoNode nodeProxy) {
		addNodeToUI(nodeProxy, true);
	}

	public Window getFrame() {
		return (Window) getParent();
	}

	public Network getModel() {
		return network;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "Network Viewer: " + pNetwork.getName();
	}

	@Override
	public void addedToWorld() {
		// TODO Auto-generated method stub
		super.addedToWorld();

		// this.animateToBounds(0, 0, 400, 300, 1000);
	}

	public void minimize() {
		getFrame().minimize();
	}

	@Override
	public PopupMenuBuilder constructPopupMenu() {
		PopupMenuBuilder menu = super.constructPopupMenu();

		if (getParent() instanceof Window) {
			menu.addSection("Viewer");
			menu.addAction(new MinimizeAction());
		}

		menu.addSection("Simulator");
		menu.addAction(new RunSimulatorAction(network.getSimulator()));

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

		return menu;
	}

	class AddModelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		Class nc;

		public AddModelAction(Class nc) {
			super(nc.getSimpleName());
			this.nc = nc;
		}

		public void actionPerformed(ActionEvent e) {

			(new Thread() {
				public void run() {
					PNeoNode nodeProxy;
					try {

						nodeProxy = (PNeoNode) nc.newInstance();
						new DialogConfig(nodeProxy);

						if (nodeProxy.isModelCreated()) {
							addNodeToUI(nodeProxy);
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

				}
			}).start();
		}
	}

	protected void constructNetworkUI() {

		setName(network.getName());
		/*
		 * basic rectangle layout variables
		 */
		double x = 0;
		double y = 0;

		/*
		 * Construct UI objects for nodes
		 */

		Node[] nodes = network.getNodes();

		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];

			PNeoNode nodeUI = buildNodeUI(node);

			nodeUI.setOffset(x, y);
			x += 90;

			if (x > 200) {
				x = 0;
				y += 70;
			}

			addNodeToUI(nodeUI, false);

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

	class MinimizeAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public MinimizeAction() {
			super("Minimize");
		}

		public void actionPerformed(ActionEvent e) {
			minimize();
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
