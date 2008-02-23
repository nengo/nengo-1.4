package ca.neo.ui.models.constructors;

import ca.neo.model.StructuralException;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.nodes.UINetwork;

public class CNetwork extends ConstructableNode {
	public CNetwork(INodeContainer nodeContainer) {
		super(nodeContainer);
	}

	private static final PropertyDescriptor[] zConfig = {};

	@Override
	public PropertyDescriptor[] getNodeConfigSchema() {
		return zConfig;
	}

	public String getTypeName() {
		return UINetwork.typeName;
	}

	@Override
	protected Object createNode(PropertySet configuredProperties, String name)
			throws ConfigException {
		NetworkImpl network = new NetworkImpl();

		try {
			network.setName(name);
		} catch (StructuralException e) {
			throw new ConfigException(e.getMessage());
		}

		return network;
	}
}
