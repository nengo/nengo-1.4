package ca.neo.ui.models.constructors;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigResult;
import ca.neo.ui.configurable.ConfigSchema;
import ca.neo.ui.configurable.ConfigSchemaImpl;
import ca.neo.ui.configurable.Property;
import ca.neo.ui.configurable.descriptors.PString;

public abstract class ConstructableNode extends AbstractConstructable {
	private static final Property pName = new PString("Name", "Name of the model", "");

	public ConstructableNode() {
		super();
	}

	@Override
	protected final Object configureModel(ConfigResult configuredProperties) throws ConfigException {
		String name = (String) configuredProperties.getValue(pName);

		// if (nodeContainer.getNodeModel(name) != null) {
		// throw new ConfigException("A node with the same name already
		// exists");
		// }

		return createNode(configuredProperties, name);
	}

	protected abstract Object createNode(ConfigResult configuredProperties, String name)
			throws ConfigException;

	public final ConfigSchema getSchema() {
		ConfigSchema nodeConfigSchema = getNodeConfigSchema();

		ConfigSchemaImpl newConfigSchema = new ConfigSchemaImpl(nodeConfigSchema.getProperties()
				.toArray(new Property[] {}), nodeConfigSchema.getAdvancedProperties().toArray(
				new Property[] {}));

		newConfigSchema.addProperty(pName, 0);

		return newConfigSchema;
	}

	public abstract ConfigSchema getNodeConfigSchema();
}
