package ca.neo.ui.models.nodes;

import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.descriptors.CString;
import ca.neo.ui.models.icons.NetworkIcon;
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.ui.models.viewers.NetworkUISettings;
import ca.neo.ui.models.viewers.NetworkViewer;
import ca.shu.ui.lib.util.UserMessages;

/**
 * UI Wrapper for a Network
 * 
 * @author Shu Wu
 */
public class UINetwork extends NodeContainer {

	private static final String LAYOUT_MANAGER_KEY = "layout/manager";

	private static final ConfigParamDescriptor pName = new CString("Name");

	private static final long serialVersionUID = 1L;

	private static final String typeName = "Network";

	/**
	 * Config descriptors
	 */
	private static final ConfigParamDescriptor[] zConfig = { pName };

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
	protected Node configureModel(ConfigParam configuredProperties) {

		NetworkImpl network = new NetworkImpl();
		network.setName((String) configuredProperties.getProperty(pName));

		return network;
	}

	@Override
	protected TooltipBuilder constructTooltips() {
		TooltipBuilder tooltips = super.constructTooltips();
		tooltips.addPart(new PropertyPart("# Projections", ""
				+ getModel().getProjections().length));

		tooltips.addPart(new PropertyPart("Simulator", ""
				+ getModel().getSimulator().getClass().getSimpleName()));

		return tooltips;
	}

	@Override
	public NetworkViewer createViewerInstance() {
		return new NetworkViewer(this);
	}

	@Override
	public ConfigParamDescriptor[] getConfigSchema() {
		return zConfig;
	}

	@Override
	public String getFileName() {
		return getUIConfig().getFileName();
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

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return typeName;
	}

	/**
	 * @return UI Configuration manager associated with this network
	 */
	public NetworkUISettings getUIConfig() {
		NetworkUISettings layoutManager = null;
		try {
			Object obj = getModel().getMetaData(LAYOUT_MANAGER_KEY);

			if (obj != null)
				layoutManager = (NetworkUISettings) obj;
		} catch (Throwable e) {
			UserMessages
					.showError("Could not access layout manager, creating a new one");
		}

		if (layoutManager == null) {
			layoutManager = new NetworkUISettings(getName() + ".net");
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
	public void setFileName(String fileName) {
		getUIConfig().setFileName(fileName);

	}

	/**
	 * @param config
	 *            UI Configuration manager
	 */
	public void setUICOnfig(NetworkUISettings config) {
		getModel().setMetaData(LAYOUT_MANAGER_KEY, config);
	}

}
