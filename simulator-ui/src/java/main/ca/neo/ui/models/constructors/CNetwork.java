package ca.neo.ui.models.constructors;

import ca.neo.model.Node;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PString;

public class CNetwork extends Constructable {
	private static final PropertyDescriptor pName = new PString("Name");

	/**
	 * Config descriptors
	 */
	private static final PropertyDescriptor[] zConfig = { pName };
	@Override
	protected Node configureModel(PropertySet configuredProperties) {

		NetworkImpl network = new NetworkImpl();
		network.setName((String) configuredProperties.getProperty(pName));

		return network;
	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		return zConfig;
	}

	public String getTypeName() {
		return "Network";
	}
}
