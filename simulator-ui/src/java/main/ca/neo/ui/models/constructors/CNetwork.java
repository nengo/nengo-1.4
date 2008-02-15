package ca.neo.ui.models.constructors;

import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PString;
import ca.neo.ui.models.nodes.UINetwork;

public class CNetwork extends Constructable {
	private static final PropertyDescriptor pName = new PString("Name");

	/**
	 * Config descriptors
	 */
	private static final PropertyDescriptor[] zConfig = { pName };

	@Override
	protected Node configureModel(PropertySet configuredProperties) throws ConfigException {

		NetworkImpl network = new NetworkImpl();
		try {
			network.setName((String) configuredProperties.getProperty(pName));
		} catch (StructuralException e) {
			throw new ConfigException(e.getMessage());
		}

		return network;
	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		return zConfig;
	}

	public String getTypeName() {
		return UINetwork.typeName;
	}
}
