package ca.neo.ui.models;

import javax.swing.SwingUtilities;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.IConfigurable;

/**
 * This abstract class implements IConfigurable, which allows it to be
 * configured by any Configuration manager
 * 
 * @author Shu Wu
 * 
 */
public abstract class PModelConfigurable extends PModel implements
		IConfigurable {

	/**
	 * Default constructor
	 */
	public PModelConfigurable() {
		super();
		init();
	}

	public PModelConfigurable(Object model) {
		super(model);
		init();
	}

	/*
	 * (non-Javadoc) This function can be safely called from any thread
	 * 
	 * @see ca.neo.ui.views.objects.configurable.IConfigurable#completeConfiguration(ca.neo.ui.views.objects.configurable.managers.PropertySet)
	 */
	public void completeConfiguration(ConfigParam properties)
			throws ConfigException {

		Object model = null;

		model = configureModel(properties);

		setModel(model);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				afterModelCreated();
			}
		});

	}

	public abstract ConfigParamDescriptor[] getConfigSchema();

	private void init() {
		// nothing to initialize yet
	}

	/**
	 * This function is called after the model has been created Some parts of
	 * the UI may be constructed here
	 */
	protected void afterModelCreated() {

	}

	/**
	 * WARNING: This function may be called from any Thread, so it is not safe
	 * to put UI stuff here If there's UI Stuff to be done, put it in
	 * afterModelCreated
	 * 
	 * Creates a model for the configuration process, called if a ConfigManager
	 * is used
	 * 
	 * @param configuredProperties
	 *            the configured properties
	 */
	protected abstract Object configureModel(
			ConfigParam configuredProperties) throws ConfigException;

	class CompleteConfigurationRunner implements Runnable {
		Object model;

		public CompleteConfigurationRunner(Object model) {
			super();
			this.model = model;
		}

		public void run() {

			afterModelCreated();

		}

	}

}
