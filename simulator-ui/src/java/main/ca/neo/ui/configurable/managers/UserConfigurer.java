package ca.neo.ui.configurable.managers;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;
import ca.neo.ui.configurable.IConfigurable;
import ca.shu.ui.lib.objects.widgets.TrackedActivity;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;

/**
 * Configuration Manager which creates a dialog and let's the user enter
 * parameters to used for configuration
 * 
 * @author Shu Wu
 * 
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
	private Object configLock = new Object();

	/**
	 * Dialog parent, if there is one
	 */
	protected JDialog dialogParent;

	/**
	 * Frame parent, if there is one
	 */
	protected Frame frameParent;

	/**
	 * 
	 * @param configurable
	 *            Object to be configured
	 */
	public UserConfigurer(IConfigurable configurable) {
		super(configurable);
		this.frameParent = UIEnvironment.getInstance();
	}

	/**
	 * @param configurableObject
	 *            Object to be configured
	 * @param parent
	 *            Frame the user configuration dialog should be attached to
	 */
	public UserConfigurer(IConfigurable configurable, Frame parent) {
		super(configurable);
		this.frameParent = parent;
	}

	/**
	 * @param configurable
	 *            Object to be configured
	 * @param parent
	 *            Dialog the configuration dialog should be attached to
	 */
	public UserConfigurer(IConfigurable configurable, JDialog parent) {
		super(configurable);
		this.dialogParent = parent;
	}

	/**
	 * Creates the configuration dialog
	 * 
	 * @return Created Configuration dialog
	 */
	protected ConfigDialog createConfigDialog() {
		if (dialogParent != null) {
			return new ConfigDialog(this, dialogParent);
		} else {
			return new ConfigDialog(this, frameParent);
		}
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
	public void configureAndWait() throws ConfigException {
		createConfigDialog();
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

/**
 * Configuration dialog
 * 
 * @author Shu Wu
 * 
 */
class ConfigDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	protected JPanel panel;

	/**
	 * Parent ConfigurationManager
	 */
	protected UserConfigurer parent;

	protected Vector<ConfigParamInputPanel> propertyInputPanels;

	/**
	 * @param configManager
	 *            Parent Configuration Manager
	 * @param owner
	 *            Component this dialog shall be added to
	 */
	public ConfigDialog(UserConfigurer configManager, Frame owner) {
		super(owner, "New " + configManager.getConfigurable().getTypeName()
				+ " Properties");

		init(configManager, owner);

	}

	/**
	 * @param configManager
	 *            Parent Configuration Manager
	 * @param owner
	 *            Component this dialog shall be added to
	 */
	public ConfigDialog(UserConfigurer configManager, JDialog owner) {
		super(owner, "Configuring "
				+ configManager.getConfigurable().getTypeName());

		init(configManager, owner);

	}

	/**
	 * @param setPropertyFields
	 *            if True, the user's values will be applied to the properties
	 *            set
	 * 
	 * @return Whether the user has set all the values on the dialog correctly
	 */
	private boolean applyPropertyFieldsReal(boolean setPropertyFields) {
		Iterator<ConfigParamInputPanel> it = propertyInputPanels.iterator();

		while (it.hasNext()) {
			ConfigParamInputPanel inputPanel = it.next();
			ConfigParamDescriptor property = inputPanel.getDescriptor();

			if (inputPanel.isValueSet()) {
				if (setPropertyFields) {

					parent.setProperty(property.getName(), inputPanel
							.getValue());
				}
			} else {
				Util.UserWarning(property.getName()
						+ " is not set or is incomplete");
				return false;
			}

		}

		return true;
	}

	/**
	 * User wants to cancel the configuration
	 */
	private void cancelAction() {

		setVisible(false);

		parent.dialogConfigurationFinished(new ConfigDialogClosedException());
		super.dispose();
	}

	/**
	 * Creates ok, cancel buttons on the dialog
	 */
	private void createButtons() {
		JPanel buttonsPanel = new Panel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 5));

		JButton addToWorldButton = new JButton("Ok");
		addToWorldButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okAction();
			}
		});
		buttonsPanel.add(addToWorldButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelAction();
			}
		});
		buttonsPanel.add(cancelButton);
		panel.add(buttonsPanel);
	}

	/**
	 * Initialization to be called from the constructor
	 * 
	 * @param configManager
	 *            Configuration manager parent
	 * @param owner
	 *            Component the dialog is to be added to
	 */
	private void init(UserConfigurer configManager, Component owner) {
		this.parent = configManager;

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
				cancelAction();

			}
		});

		setResizable(false);
		setModal(true);

		panel = new Panel();
		panel.setVisible(true);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		initPanel();

		createDialog();

		createButtons();

		add(panel);
		pack();
		setLocationRelativeTo(owner);
		this.setVisible(true);
	}

	/**
	 * Loads the properties associated with the item selected in the file drop
	 * down list
	 */
	private void loadDefaultValues() {
		Iterator<ConfigParamInputPanel> it = propertyInputPanels.iterator();
		while (it.hasNext()) {
			ConfigParamInputPanel panel = it.next();

			Object currentValue = panel.getDescriptor().getDefaultValue();
			if (currentValue != null) {
				panel.setValue(currentValue);
			}

		}
	}

	/**
	 * What happens when the user presses the OK button
	 */
	private void okAction() {
		if (applyProperties()) {
			setVisible(false);
			dispose();

			(new TrackedActivity("Creating new "
					+ parent.getConfigurable().getTypeName()) {

				@Override
				public void doActivity() {
					ConfigException configException = null;
					try {
						parent.getConfigurable().completeConfiguration(
								new ConfigParam(parent.getProperties()));
						parent
								.savePropertiesFile(UserTemplateConfigurer.DEFAULT_TEMPLATE_NAME);
					} catch (ConfigException e) {
						configException = e;
					}

					parent.dialogConfigurationFinished(configException);

				}
			}).startThread();

		} else {

		}
	}

	/**
	 * Gets value entered in the dialog and applies them to the properties set
	 * 
	 * @return Whether operation was successfull
	 */
	protected boolean applyProperties() {
		/*
		 * first check if all the fields have been set correctly, then set them
		 * 
		 */
		if (applyPropertyFieldsReal(false)) {
			applyPropertyFieldsReal(true);
			return true;
		}
		return false;

	}

	/**
	 * Creates the dialog
	 */
	protected void createDialog() {

		ConfigParamDescriptor[] properties = parent.getConfigurable()
				.getConfigSchema();
		propertyInputPanels = new Vector<ConfigParamInputPanel>(
				properties.length);

		for (ConfigParamDescriptor property : properties) {

			ConfigParamInputPanel inputPanel = property.createInputPanel();
			panel.add(inputPanel);

			propertyInputPanels.add(inputPanel);

		}
		loadDefaultValues();
	}

	/**
	 * Initializes the dialog contents
	 */
	protected void initPanel() {
		/*
		 * Used by subclasses to add elements to the panel
		 */
	}

}

/**
 * Exception to be thrown if the Dialog is intentionally closed by the User
 * 
 * @author Shu
 * 
 */
class ConfigDialogClosedException extends ConfigException {

	private static final long serialVersionUID = 1L;

	public ConfigDialogClosedException() {
		super("Config dialog closed");

	}

	@Override
	public void defaultHandledBehavior() {
		/*
		 * Do nothing
		 */
	}

}