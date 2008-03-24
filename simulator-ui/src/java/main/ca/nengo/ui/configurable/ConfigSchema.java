package ca.nengo.ui.configurable;

import java.util.List;

/**
 * Encapsulates the descriptions of properties to be configured
 * 
 * @author Shu Wu
 */
public interface ConfigSchema {

	public List<Property> getProperties();

	public List<Property> getAdvancedProperties();
}
