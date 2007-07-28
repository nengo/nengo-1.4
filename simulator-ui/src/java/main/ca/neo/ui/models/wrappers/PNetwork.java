package ca.neo.ui.models.wrappers;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.ui.models.PModelNode;
import ca.neo.ui.models.icons.NetworkIcon;
import ca.neo.ui.models.viewers.NetworkView;
import ca.neo.ui.views.objects.configurable.managers.IConfigurationManager;
import ca.neo.ui.views.objects.configurable.struct.PTString;
import ca.neo.ui.views.objects.configurable.struct.PropertyStructure;
import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;

/**
 * GUI Wrapper for a Network Model
 * 
 * @author Shu
 * 
 */
public class PNetwork extends PModelNode {

	private static final long serialVersionUID = 1L;

	static final PropertyStructure pName = new PTString("Name");

	static final String typeName = "Network";

	static final PropertyStructure[] zProperties = { pName };

	NetworkView networkViewer;

	Window networkWindow;

	public PNetwork(boolean useDefaultConfigManager) {
		super(useDefaultConfigManager);
		init();
	}

	public PNetwork(IConfigurationManager configManager) {
		super(configManager);
		init();
	}

	/**
	 * 
	 * @param name
	 *            Name of the network
	 */
	public PNetwork(String name) {
		super();
		setProperty(pName, name);
		initModel();
		init();
	}

	/**
	 * Delegate function for NetworkViewer
	 * 
	 * @param nodeProxy
	 */
	public void addNode(PModelNode nodeProxy) {

		getNetworkViewer().addNode(nodeProxy);
	}

	@Override
	public PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = super.constructMenu();
		menu.addSection("View Network");

		if (networkWindow == null
				|| (networkWindow.getWindowState() == Window.WindowState.MINIMIZED)) {

			menu.addAction(new AbstractAction("Expand Network") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					showNodes();
				}
			});

		} else {
			menu.addAction(new AbstractAction("Minimize Network") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
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
		networkViewer = new NetworkView(this, getRoot());
		networkWindow = new Window(this, networkViewer);
		networkWindow.translate(0, this.getHeight());
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
	public NetworkView getNetworkViewer() {
		if (networkViewer == null) {
			constructNetworkViewer();
		}
		return networkViewer;
	}

	@Override
	public PropertyStructure[] getPropertiesSchema() {
		return zProperties;
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
			networkWindow.minimize();

	}

	/**
	 * Note: this function can only be called after this World Object has been
	 * added to a scene graph which reaches up to a PRoot object ie. add this
	 * object to a parent object which is visible in the UI
	 * 
	 * @return opens the network viewer which contains the nodes of the Network
	 *         model
	 */
	public NetworkView showNodes() {
		if (networkViewer == null) {
			constructNetworkViewer();
		}
		networkWindow.restore();

		return getNetworkViewer();

	}

	/**
	 * Initializes the PNetwork
	 */
	private void init() {
		setIcon(new NetworkIcon(this));
	}

	@Override
	protected Node createModel() {
		NetworkImpl network = new NetworkImpl();
		network.setName((String) getProperty(pName));

		return network;
	}

}
