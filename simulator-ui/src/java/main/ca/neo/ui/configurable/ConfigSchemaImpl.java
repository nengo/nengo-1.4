package ca.neo.ui.configurable;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of a IConfigSchema
 * 
 * @author Shu Wu
 */
public class ConfigSchemaImpl implements ConfigSchema {
	private List<Property> advancedProperties;
	private List<Property> properties;

	/**
	 * Default constructor, no property descriptors
	 */
	public ConfigSchemaImpl() {
		this(new Property[] {}, new Property[] {});
	}

	public ConfigSchemaImpl(Property property) {
		this(new Property[] { property }, new Property[] {});
	}

	public ConfigSchemaImpl(Property[] properties) {
		this(properties, new Property[] {});
	}

	public ConfigSchemaImpl(Property[] properties,
			Property[] advancedProperties) {
		super();
		this.properties = new ArrayList<Property>(properties.length);
		for (Property property : properties) {
			this.properties.add(property);
		}

		this.advancedProperties = new ArrayList<Property>(properties.length);
		for (Property property : advancedProperties) {
			this.advancedProperties.add(property);
		}
	}

	/**
	 * @param propDesc
	 *            Property Descriptor
	 * @param position
	 *            Location to insert into the property list
	 */
	public void addProperty(Property propDesc, int position) {
		properties.add(position, propDesc);
	}

	public List<Property> getAdvancedProperties() {
		return advancedProperties;
	}

	public List<Property> getProperties() {
		return properties;
	}

}
