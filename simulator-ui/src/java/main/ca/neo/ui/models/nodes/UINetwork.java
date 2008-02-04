package ca.neo.ui.models.nodes;

import java.io.File;
import java.io.IOException;

import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.sim.Simulator;
import ca.neo.ui.NeoGraphics;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PString;
import ca.neo.ui.models.icons.NetworkIcon;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.ui.models.viewers.NetworkViewer;
import ca.neo.ui.models.viewers.NetworkViewerConfig;
import ca.shu.ui.lib.util.UserMessages;

/**
 * UI Wrapper for a Network
 * 
 * @author Shu Wu
 */
public class UINetwork extends NodeContainer {

	private static final String LAYOUT_MANAGER_KEY = "layout/manager";

	private static final PropertyDescriptor pName = new PString("Name");

	private static final long serialVersionUID = 1L;

	private static final String typeName = "Network";

	/**
	 * Config descriptors
	 */
	private static final PropertyDescriptor[] zConfig = { pName };

	public UINetwork() {
		super();
		init();
	}

	public UINetwork(Network model) {
		super(model);
		init();
	}

	/**
	 * Initializes this instance
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
	protected void constructTooltips(TooltipBuilder tooltips) {
		super.constructTooltips(tooltips);
		tooltips.addProperty("# Projections", "" + getModel().getProjections().length);

		tooltips.addProperty("Simulator", "" + getSimulator().getClass().getSimpleName());
	}

	@Override
	public NetworkViewer createViewerInstance() {
		return new NetworkViewer(this);
	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		return zConfig;
	}

	@Override
	public String getFileName() {
		return getSavedConfig().getFileName();
	}

	@Override
	public NetworkImpl getModel() {
		return (NetworkImpl) super.getModel();
	}

	@Override
	public String getName() {
		if (getModel() == null) {
			return super.getName();
		} else {
			return getModel().getName();
		}
	}

	@Override
	public int getNodesCount() {
		if (getModel() != null)
			return getModel().getNodes().length;
		else
			return 0;
	}

	/**
	 * @return Simulator
	 */
	public Simulator getSimulator() {
		return getModel().getSimulator();
	}

	@Override
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @return UI Configuration manager associated with this network
	 */
	public NetworkViewerConfig getSavedConfig() {
		NetworkViewerConfig layoutManager = null;
		try {
			Object obj = getModel().getMetaData(LAYOUT_MANAGER_KEY);

			if (obj != null)
				layoutManager = (NetworkViewerConfig) obj;
		} catch (Throwable e) {
			UserMessages.showError("Could not access layout manager, creating a new one");
		}

		if (layoutManager == null) {
			layoutManager = new NetworkViewerConfig(getName() +"." + NeoGraphics.NEONODE_FILE_EXTENSION);
			setUICOnfig(layoutManager);
		}

		return layoutManager;
	}

	@Override
	public NetworkViewer getViewer() {
		return (NetworkViewer) super.getViewer();
	}

	@Override
	public void saveContainerConfig() {

		if (getViewer() != null) {
			getViewer().saveLayoutAsDefault();
		}

	}

	@Override
	public void saveModel(File file) throws IOException {
		getSavedConfig().setFileName(file.toString());
		super.saveModel(file);
	}

	/**
	 * @param config
	 *            UI Configuration manager
	 */
	public void setUICOnfig(NetworkViewerConfig config) {
		getModel().setMetaData(LAYOUT_MANAGER_KEY, config);
	}

}
