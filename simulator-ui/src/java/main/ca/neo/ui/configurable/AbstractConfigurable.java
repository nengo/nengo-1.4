package ca.neo.ui.configurable;

import ca.neo.ui.configurable.managers.PropertySet;
import ca.neo.ui.configurable.struct.PropDescriptor;

public abstract class AbstractConfigurable implements IConfigurable {

	boolean isConfigured = false;

	public AbstractConfigurable() {
		super();

	}

	public void cancelConfiguration() {

	}

	public void completeConfiguration(PropertySet properties) {

		setConfigured(true);
	}

	public abstract PropDescriptor[] getConfigSchema();

	public abstract String getTypeName();

	public boolean isConfigured() {
		return isConfigured;
	}

	public void setConfigured(boolean isConfigured) {
		this.isConfigured = isConfigured;
	}
}
