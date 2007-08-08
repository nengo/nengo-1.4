package ca.neo.ui.models.nodes;

import java.io.File;
import java.io.IOException;

import ca.neo.io.FileManager;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.ui.models.icons.NetworkIcon;
import ca.neo.ui.models.viewers.NetworkViewer;
import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.neo.ui.views.objects.configurable.struct.PTString;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;

/**
 * GUI Wrapper for a Network Model
 * 
 * @author Shu Wu
 * 
 */
public class PNetwork extends PNodeContainer {

	private static final long serialVersionUID = 1L;

	static final PropDescriptor pName = new PTString("Name");

	static final String typeName = "Network";

	static final PropDescriptor[] zProperties = { pName };

	public PNetwork() {
		super();
		init();

	}

	public PNetwork(Network model) {
		super(model);

		init();
	}

	/**
	 * Creates the Network Viewer
	 */
	public NetworkViewer createNodeViewerInstance() {
		return new NetworkViewer(this);
	}

	@Override
	public NetworkViewer getAndConstructViewer() {
		return (NetworkViewer) super.getAndConstructViewer();
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

	@Override
	public NetworkViewer getViewer() {
		return (NetworkViewer) super.getViewer();
	}

	@Override
	public void saveModel(File file) throws IOException {

		if (getViewer() != null) {
			getViewer().saveLayoutAsDefault();
		}

		FileManager fm = new FileManager();

		fm.save((Network) getModel(), file);
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

	@Override
	protected boolean validateFullBounds() {
		// TODO Auto-generated method stub
		return super.validateFullBounds();
	}

	@Override
	public int getNodesCount() {
		return getModel().getNodes().length;
	}

}
