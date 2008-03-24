package ca.nengo.ui.models.constructors;

import ca.nengo.model.StructuralException;
import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.ConfigResult;
import ca.nengo.ui.configurable.ConfigSchemaImpl;
import ca.nengo.ui.models.nodes.UINetwork;

public class CNetwork extends ConstructableNode {
	public CNetwork() {
		super();
	}

	@Override
	public ConfigSchemaImpl getNodeConfigSchema() {
		return new ConfigSchemaImpl(); // nothing to configure
	}

	public String getTypeName() {
		return UINetwork.typeName;
	}

	@Override
	protected Object createNode(ConfigResult configuredProperties, String name)
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
