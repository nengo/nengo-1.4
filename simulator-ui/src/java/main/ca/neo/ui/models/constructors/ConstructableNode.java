package ca.neo.ui.models.constructors;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PString;
import ca.neo.ui.models.INodeContainer;

public abstract class ConstructableNode extends Constructable {
	private static final PropertyDescriptor pName = new PString("Name");

	private INodeContainer nodeContainer;

	public ConstructableNode(INodeContainer nodeContainer) {
		super();
		this.nodeContainer = nodeContainer;
	}

	@Override
	protected final Object configureModel(PropertySet configuredProperties) throws ConfigException {
		String name = (String) configuredProperties.getProperty(pName);

		if (nodeContainer.getNodeModel(name) != null) {
			throw new ConfigException("A node with the same name already exists");
		}

		return createNode(configuredProperties, name);
	}

	protected abstract Object createNode(PropertySet configuredProperties, String name)
			throws ConfigException;

	public final PropertyDescriptor[] getConfigSchema() {
		PropertyDescriptor[] nodeConfigSchema = getNodeConfigSchema();

		PropertyDescriptor[] config = new PropertyDescriptor[nodeConfigSchema.length + 1];
		config[0] = pName;

		for (int i = 0; i < nodeConfigSchema.length; i++) {
			config[i + 1] = nodeConfigSchema[i];
		}

		return config;
	}

	public abstract PropertyDescriptor[] getNodeConfigSchema();
}
