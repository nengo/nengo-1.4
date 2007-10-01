package ca.neo.ui.models;

import javax.swing.SwingUtilities;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.IConfigurable;

/**
 * A UIModel which can be configured through the IConfigurable interface
 * 
 * @author Shu Wu
 * 
 */
public abstract class UIModelConfigurable extends UIModel implements
		IConfigurable {

	public UIModelConfigurable() {
		super();
	}

	public UIModelConfigurable(Object model) {
		super(model);
	}

	/**
	 * This function is called after the model has been created. This function
	 * is executed in the Swing Thread, so UI modifications can be made here.
	 */
	protected void afterModelCreated() {

	}

	/**
	 * This function is called from a common thread, so it is not safe to put UI
	 * stuff here If there's UI Stuff to be done, put it in afterModelCreated
	 * 
	 * Creates a model for the configuration process, called if a ConfigManager
	 * is used
	 * 
	 * @param configuredProperties
	 *            the configured properties
	 */
	protected abstract Object configureModel(PropertySet configuredProperties)
			throws ConfigException;

	/*
	 * (non-Javadoc) This function can be safely called from any thread
	 * 
	 * @see ca.neo.ui.views.objects.configurable.IConfigurable#completeConfiguration(ca.neo.ui.views.objects.configurable.managers.PropertySet)
	 */
	public void completeConfiguration(PropertySet properties)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.IConfigurable#getConfigSchema()
	 */
	public abstract PropertyDescriptor[] getConfigSchema();

}
