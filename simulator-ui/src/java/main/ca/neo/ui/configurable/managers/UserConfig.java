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

public class UserConfig extends ConfigManager {
	Object configLock = new Object();
	ConfigDialog dialog;

	JDialog parent0;

	Frame parent1;

	public UserConfig(IConfigurable configurable) {
		super(configurable);
		this.parent1 = UIEnvironment.getInstance();
	}

	public UserConfig(IConfigurable configurable, Frame parent) {
		super(configurable);
		this.parent1 = parent;
	}

	public UserConfig(IConfigurable configurable, JDialog parent) {
		super(configurable);
		this.parent0 = parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.managers.ConfigManager#configure()
	 * 
	 * 
	 */
	@Override
	public void configureAndWait() throws ConfigException {
		dialog = createConfigDialog();

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

	public ConfigDialog createConfigDialog() {
		if (parent0 != null) {
			return new ConfigDialog(this, parent0);
		} else {
			return new ConfigDialog(this, parent1);
		}
	}

	public boolean isCancelled() {
		return dialog.isCancelled();
	}

	/**
	 * Exception thrown during configuration
	 */
	private ConfigException configException;

	/**
	 * @param configException
	 *            Configuration Exception thrown during configuration, none if
	 *            everything went smoothly
	 */
	protected void dialogConfigurationFinished(ConfigException configException) {
		this.configException = configException;
		synchronized (configLock) {
			configLock.notifyAll();
			// System.out.println("dialog closed");
			configLock = null;
		}
	}

}

class ConfigDialogClosedException extends ConfigException {

	@Override
	public void defaultHandledBehavior() {
		/*
		 * Do nothing
		 */
	}

	private static final long serialVersionUID = 1L;

	public ConfigDialogClosedException() {
		super("Config dialog closed");

	}

}

class ConfigDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected UserConfig configManager;

	protected Vector<ConfigParamInputPanel> propertyInputPanels;

	boolean isCancelled = false;

	JPanel panel;

	public ConfigDialog(UserConfig configManager, Frame owner) {
		super(owner, "New " + configManager.getConfigurable().getTypeName()
				+ " Properties");

		init(configManager, owner);

	}

	public ConfigDialog(UserConfig configManager, JDialog owner) {
		super(owner, "Configuring "
				+ configManager.getConfigurable().getTypeName());

		init(configManager, owner);

	}

	public boolean applyProperties() {
		/*
		 * first check if all the fields have been set correctly, then set them
		 * 
		 */
		if (applyPropertyFields0(false)) {
			applyPropertyFields0(true);
			return true;
		}
		return false;

	}

	public void cancelAction() {
		isCancelled = true;

		setVisible(false);

		configManager
				.dialogConfigurationFinished(new ConfigDialogClosedException());
		super.dispose();
	}

	public void createButtons() {
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

	public void createDialog() {

		ConfigParamDescriptor[] properties = configManager.getConfigurable()
				.getConfigSchema();
		propertyInputPanels = new Vector<ConfigParamInputPanel>(properties.length);

		for (ConfigParamDescriptor property : properties) {

			ConfigParamInputPanel inputPanel = property.createInputPanel();
			panel.add(inputPanel);

			propertyInputPanels.add(inputPanel);

		}
		loadDefaultValues();
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	private boolean applyPropertyFields0(boolean setPropertyFields) {
		Iterator<ConfigParamInputPanel> it = propertyInputPanels.iterator();

		while (it.hasNext()) {
			ConfigParamInputPanel inputPanel = it.next();
			ConfigParamDescriptor property = inputPanel.getDescriptor();

			if (inputPanel.isValueSet()) {
				if (setPropertyFields) {

					configManager.setProperty(property.getName(), inputPanel
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

	private void init(UserConfig configManager, Component c) {
		this.configManager = configManager;

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
		setLocationRelativeTo(c);
		this.setVisible(true);
	}

	private void okAction() {
		if (applyProperties()) {
			setVisible(false);
			dispose();

			(new TrackedActivity("Creating new "
					+ configManager.getConfigurable().getTypeName()) {

				@Override
				public void doActivity() {
					ConfigException configException = null;
					try {
						configManager.getConfigurable().completeConfiguration(
								new ConfigParam(configManager
										.getProperties()));
						configManager
								.savePropertiesFile(UserTemplateConfig.DEFAULT_PROPERTY_FILE_NAME);
					} catch (ConfigException e) {
						configException = e;
					}

					configManager.dialogConfigurationFinished(configException);

				}
			}).startThread();

		} else {

		}
	}

	protected void initPanel() {
		/*
		 * Used by subclasses to add elements to the panel
		 */
	}

	/**
	 * Loads the properties associated with the item selected in the file drop
	 * down list
	 */
	protected void loadDefaultValues() {
		Iterator<ConfigParamInputPanel> it = propertyInputPanels.iterator();
		while (it.hasNext()) {
			ConfigParamInputPanel panel = it.next();

			Object currentValue = panel.getDescriptor().getDefaultValue();
			if (currentValue != null) {
				panel.setValue(currentValue);
			}

		}
	}

}