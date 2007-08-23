package ca.neo.ui.models.nodes;

import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.ui.configurable.managers.PropertySet;
import ca.neo.ui.configurable.struct.PTString;
import ca.neo.ui.configurable.struct.PropDescriptor;
import ca.neo.ui.models.icons.NetworkIcon;
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.ui.models.viewers.NetworkUIConfiguration;
import ca.neo.ui.models.viewers.NetworkViewer;
import ca.shu.ui.lib.util.Util;

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

	static final String LAYOUT_MANAGER_KEY = "layout/manager";

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
	@Override
	public NetworkViewer createNodeViewerInstance() {
		return new NetworkViewer(this);
	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		return zProperties;
	}

	@Override
	public String getFileName() {
		return getUIConfig().getFileName();
	}

	/**
	 * @return The Network Model
	 */
	@Override
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

	public NetworkUIConfiguration getUIConfig() {
		NetworkUIConfiguration layoutManager = null;
		try {
			Object obj = getModel().getMetaData(LAYOUT_MANAGER_KEY);

			if (obj != null)
				layoutManager = (NetworkUIConfiguration) obj;
		} catch (Throwable e) {
			Util
					.UserError("Could not access layout manager, creating a new one");
		}

		if (layoutManager == null) {
			layoutManager = new NetworkUIConfiguration(getName() + ".net");
			setUICOnfig(layoutManager);
		}

		return layoutManager;
	}

	@Override
	public NetworkViewer getViewer() {
		return (NetworkViewer) super.getViewer();
	}

	@Override
	public void saveConfig() {

		if (getViewer() != null) {
			getViewer().saveLayoutAsDefault();
		}

	}

	@Override
	public void setFileName(String fileName) {
		getUIConfig().setFileName(fileName);

	}

	public void setUICOnfig(NetworkUIConfiguration config) {
		getModel().setMetaData(LAYOUT_MANAGER_KEY, config);
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
	protected TooltipBuilder constructTooltips() {
		TooltipBuilder tooltips = super.constructTooltips();
		tooltips.addPart(new PropertyPart("# Projections", ""
				+ getModel().getProjections().length));

		tooltips.addPart(new PropertyPart("Simulator", ""
				+ getModel().getSimulator().getClass().getSimpleName()));

		return tooltips;
	}

	@Override
	protected boolean validateFullBounds() {
		// TODO Auto-generated method stub
		return super.validateFullBounds();
	}

}
