package ca.neo.ui.configurable;

import ca.neo.ui.configurable.managers.PropertySet;
import ca.neo.ui.configurable.struct.PropDescriptor;

/**
 * Describes a object which can be configured programatically by a
 * IConfigurationManager
 * 
 * @author Shu Wu
 * 
 */
public interface IConfigurable {
	public void cancelConfiguration();

	public void completeConfiguration(PropertySet properties);

	public PropDescriptor[] getConfigSchema();

	public String getTypeName();

	public boolean isConfigured();

	// public MutableAttributeSet getPropertiesReference();

	// public void setProperties(MutableAttributeSet properties);
}
