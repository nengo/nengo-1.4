package ca.neo.ui.models.nodes;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.Projection;
import ca.neo.model.Termination;
import ca.neo.model.impl.FunctionInput;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.icons.NetworkIcon;
import ca.neo.ui.models.nodes.connectors.POrigin;
import ca.neo.ui.models.nodes.connectors.PTermination;
import ca.neo.ui.models.viewers.NetworkViewer;
import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.neo.ui.views.objects.configurable.struct.PTString;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.objects.Window.WindowState;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;

/**
 * GUI Wrapper for a Network Model
 * 
 * @author Shu
 * 
 */
public class PNetwork extends PNeoNode {

	private static final long serialVersionUID = 1L;

	static final PropDescriptor pName = new PTString("Name");

	static final String typeName = "Network";

	static final PropDescriptor[] zProperties = { pName };

	NetworkViewer networkViewer;

	Window networkWindow;

	public PNetwork() {
		super();
		init();
	}

	public PNetwork(Network model) {
		super(model);

		setName(model.getName());
		init();

	}

	/**
	 * Delegate function for NetworkViewer
	 * 
	 * @param nodeProxy
	 */
	public void addNode(PNeoNode nodeProxy) {

		getViewer().addNodeToUI(nodeProxy);
	}

	@Override
	public PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = super.constructMenu();
		menu.addSection("View Network");

		if (networkWindow == null
				|| (networkWindow.getWindowState() == Window.WindowState.MINIMIZED)) {

			menu.addAction(new StandardAction("Expand Network") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void action() throws ActionException {
					openViewer();
				}
			});

		} else {
			menu.addAction(new StandardAction("Minimize Network") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void action() throws ActionException {
					minimizeNetwork();
				}
			});

		}
		return menu;

	}

	/**
	 * Creates the Network Viewer
	 */
	public void constructNetworkViewer() {
		networkViewer = new NetworkViewer(this, getRoot());
		networkWindow = new Window(this, networkViewer);
		networkWindow.translate(0, this.getHeight());
	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		return zProperties;
	}

	/**
	 * @return The Network Model
	 */
	public NetworkImpl getModelNetwork() {
		return (NetworkImpl) getModel();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		if (getModelNetwork() == null) {
			return super.getName();
		} else {
			return getModelNetwork().getName();
		}
	}

	/**
	 * 
	 * @return The Network Viewer
	 */
	public NetworkViewer getViewer() {

		if (networkViewer == null) {
			constructNetworkViewer();
		}

		return networkViewer;
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return typeName;
	}

	/**
	 * Minimizes the Network Viewer GUI
	 */
	public void minimizeNetwork() {
		if (networkWindow != null)
			networkWindow.setWindowState(WindowState.MINIMIZED);

	}

	/**
	 * Note: this function can only be called after this World Object has been
	 * added to a scene graph which reaches up to a PRoot object ie. add this
	 * object to a parent object which is visible in the UI
	 * 
	 * @return opens the network viewer which contains the nodes of the Network
	 *         model
	 */
	public NetworkViewer openViewer() {
		if (networkViewer == null) {
			constructNetworkViewer();
		}
		networkWindow.setWindowState(WindowState.WINDOW);

		return getViewer();

	}

	/**
	 * Initializes the PNetwork
	 */
	private void init() {
		setIcon(new NetworkIcon(this));
	}

	@Override
	protected Node configureModel(PropertySet configuredProperties) {

		NetworkImpl network = new NetworkImpl();
		network.setName((String) configuredProperties.getProperty(pName));

		return network;
	}

}
