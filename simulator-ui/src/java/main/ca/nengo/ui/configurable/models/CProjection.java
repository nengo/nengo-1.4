package ca.nengo.ui.configurable.models;

import java.util.Map;

import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.properties.PString;

public abstract class CProjection extends AbstractModel {
	protected final Property pName = new PString("Name", null, null);
	protected abstract boolean isNameAvailable(String name);
	protected abstract Object createModel(Map<Property, Object> configuredProperties, String uniqueName) throws ConfigException;

	@Override protected final Object configureModel(Map<Property, Object> configuredProperties) throws ConfigException {
		String originalName = (String) configuredProperties.get(pName);
		String name = originalName;

		// Ensure unique name
		int i = 1;
		while (isNameAvailable(name)) {
			name = String.format("%s (%d)", originalName, i++);
		}

		return createModel(configuredProperties, name);
	}
}
