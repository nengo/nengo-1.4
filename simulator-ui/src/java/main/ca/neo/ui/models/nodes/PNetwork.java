package ca.neo.ui.models.nodes;

import java.io.File;
import java.io.IOException;

import ca.neo.io.FileManager;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.actions.SaveNetworkAction;
import ca.neo.ui.models.icons.NetworkIcon;
import ca.neo.ui.models.viewers.NetworkViewer;
import ca.neo.ui.models.viewers.NodeViewer;
import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.neo.ui.views.objects.configurable.struct.PTString;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.objects.Window.WindowState;
import ca.shu.ui.lib.util.PopupMenuBuilder;

/**
 * GUI Wrapper for a Network Model
 * 
 * @author Shu
 * 
 */
public class PNetwork extends PNodeContainer {

	private static final long serialVersionUID = 1L;

	static final PropDescriptor pName = new PTString("Name");

	static final String typeName = "Network";

	static final PropDescriptor[] zProperties = { pName };



	public void saveNetwork(File file) throws IOException {
		FileManager fm = new FileManager();

		if (getViewer() != null) {
			getViewer().saveNodeLayout(NetworkViewer.DEFAULT_NODE_LAYOUT_NAME);
		}

		fm.save((Network) getModel(), file);

	}

	

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
		getViewer().addNodeToUI(nodeProxy, true, false);
	}

	@Override
	public NetworkViewer getViewer() {
		return (NetworkViewer) super.getViewer();
	}

	@Override
	public PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = super.constructMenu();
		menu.addSection("Network");

		menu.addAction(new SaveNetworkAction("Save", this));

		
		return menu;

	}

	/**
	 * Creates the Network Viewer
	 */
	public NetworkViewer createNodeViewerInstance() {
		return new NetworkViewer(this);
	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		return zProperties;
	}

	/**
	 * @return The Network Model
	 */
	public NetworkImpl getModel() {
		return (NetworkImpl) super.getModel();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		if (getModel() == null) {
			return super.getName();
		} else {
			return getModel().getName();
		}
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return typeName;
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
