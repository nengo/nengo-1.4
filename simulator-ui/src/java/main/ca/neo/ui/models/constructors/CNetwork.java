package ca.neo.ui.models.constructors;

import ca.neo.model.StructuralException;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigResult;
import ca.neo.ui.configurable.ConfigSchemaImpl;
import ca.neo.ui.models.nodes.UINetwork;

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
