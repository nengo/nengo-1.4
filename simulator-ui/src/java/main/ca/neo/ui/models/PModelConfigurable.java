package ca.neo.ui.models;

import javax.swing.SwingUtilities;

import ca.neo.ui.configurable.IConfigurable;
import ca.neo.ui.configurable.managers.PropertySet;
import ca.neo.ui.configurable.struct.PropDescriptor;
import ca.neo.ui.exceptions.ModelConfigurationException;
import ca.shu.ui.lib.util.Util;

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

	public void cancelConfiguration() {
		removeFromParent();

	}

	/*
	 * (non-Javadoc) This function can be safely called from any thread
	 * 
	 * @see ca.neo.ui.views.objects.configurable.IConfigurable#completeConfiguration(ca.neo.ui.views.objects.configurable.managers.PropertySet)
	 */
	public void completeConfiguration(PropertySet properties) {

		Object model = null;
		try {
			model = configureModel(properties);
		} catch (ModelConfigurationException e1) {
			Util.UserError("Problem configuring model: " + e1.getMessage());
			destroy();
			return;
		}

		setModel(model);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				afterModelCreated();
			}
		});

	}

	public abstract PropDescriptor[] getConfigSchema();

	public boolean isConfigured() {
		if (getModel() != null) {
			return true;
		} else {
			return false;
		}
	}

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
	protected abstract Object configureModel(PropertySet configuredProperties)
			throws ModelConfigurationException;

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
