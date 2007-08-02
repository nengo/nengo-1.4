package ca.neo.ui.views.objects.configurable;

import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;

/**
 * Describes a object which can be configured programatically by a
 * IConfigurationManager
 * 
 * @author Shu Wu
 * 
 */
public interface IConfigurable {
	public PropDescriptor[] getConfigSchema();

	public void completeConfiguration(PropertySet properties);

	public void cancelConfiguration();

	public boolean isConfigured();

	public String getTypeName();

	// public MutableAttributeSet getPropertiesReference();

	// public void setProperties(MutableAttributeSet properties);
}
