package ca.neo.ui.views.objects.configurable;

import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;

public abstract class AbstractConfigurable implements IConfigurable {

	boolean isConfigured = false;

	public boolean isConfigured() {
		return isConfigured;
	}

	public void setConfigured(boolean isConfigured) {
		this.isConfigured = isConfigured;
	}

	public AbstractConfigurable() {
		super();

	}

	public void completeConfiguration(PropertySet properties) {

		setConfigured(true);
	}

	public void cancelConfiguration() {

	}

	public abstract PropDescriptor[] getConfigSchema();

	public abstract String getTypeName();
}
