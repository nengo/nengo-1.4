package ca.neo.ui.configurable.managers;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.IConfigurable;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;

/**
 * Configuration Manager which creates a dialog and let's the user enter
 * parameters to used for configuration
 * 
 * @author Shu Wu
 */
public class UserConfigurer extends ConfigManager {

	/**
	 * Exception thrown during configuration
	 */
	private ConfigException configException;

	/**
	 * Lock to be used to communicate cross-thread between this instance and the
	 * Configuration Dialog
	 */
	private Object configLock;

	/**
	 * Parent, if there is one
	 */
	protected Container parent;

	/**
	 * @param configurable
	 *            Object to be configured
	 */
	public UserConfigurer(IConfigurable configurable) {
		super(configurable);
		this.parent = UIEnvironment.getInstance();
	}

	/**
	 * @param configurableObject
	 *            Object to be configured
	 * @param parent
	 *            Frame the user configuration dialog should be attached to
	 */
	public UserConfigurer(IConfigurable configurable, Container parent) {
		super(configurable);
		this.parent = parent;
	}

	/**
	 * Creates the configuration dialog
	 * 
	 * @return Created Configuration dialog
	 */
	protected ConfigDialog createConfigDialog() {
		if (parent instanceof Frame) {

			return new ConfigDialog(this, (Frame) parent);
		} else if (parent instanceof Dialog) {
			return new ConfigDialog(this, (Dialog) parent);
		} else {
			Util.Assert(false,
					"Could not create config dialog because parent type if not supported");

		}
		return null;
	}

	/**
	 * @param configException
	 *            Configuration Exception thrown during configuration, none if
	 *            everything went smoothly
	 */
	protected void dialogConfigurationFinished(ConfigException configException) {
		this.configException = configException;
		synchronized (configLock) {
			configLock.notifyAll();
			configLock = null;
		}
	}

	@Override
	public synchronized void configureAndWait() throws ConfigException {
		if (configLock == null) {
			configLock = new Object();
		}

		ConfigDialog dialog = createConfigDialog();
		dialog.setVisible(true);

		/*
		 * Block until configuration has completed
		 */
		if (configLock != null) {
			synchronized (configLock) {
				try {
					if (configLock != null)
						configLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		if (configException != null)
			throw configException;

	}

}
