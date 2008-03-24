package ca.neo.ui.models.constructors;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigSchema;
import ca.neo.ui.configurable.IConfigurable;
import ca.neo.ui.configurable.ConfigResult;

/**
 * A UIModel which can be configured through the IConfigurable interface
 * 
 * @author Shu Wu
 */
public abstract class AbstractConstructable implements IConfigurable {

	private Object model;

	public AbstractConstructable() {
		super();
	}

	/**
	 * This function is called from a common thread, so it is not safe to put UI
	 * stuff here If there's UI Stuff to be done, put it in afterModelCreated
	 * Creates a model for the configuration process, called if a ConfigManager
	 * is used
	 * 
	 * @param configuredProperties
	 *            the configured properties
	 */
	protected abstract Object configureModel(ConfigResult configuredProperties)
			throws ConfigException;

	public void completeConfiguration(final ConfigResult properties) throws ConfigException {
		model = null;
		model = configureModel(properties);
	}

	public String getDescription() {
		return getTypeName() + " Constructor";
	}

	public Object getModel() {
		return model;
	}

	public abstract ConfigSchema getSchema();

	public void preConfiguration(ConfigResult props) throws ConfigException {
		// do nothing
	}
}
