package ca.neo.ui.configurable.managers;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
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
import javax.swing.text.MutableAttributeSet;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.PropertySet;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.objects.activities.TrackedAction;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Configuration dialog
 * 
 * @author Shu Wu
 */
public class ConfigDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel panel;

	/**
	 * Parent ConfigurationManager
	 */
	private UserConfigurer myConfigurer;

	protected Vector<PropertyInputPanel> propertyInputPanels;

	/**
	 * @param configManager
	 *            Parent Configuration Manager
	 * @param owner
	 *            Component this dialog shall be added to
	 */
	public ConfigDialog(UserConfigurer configManager, Frame owner) {
		super(owner, configManager.getConfigurable().getTypeName());

		init(configManager, owner);

	}

	/**
	 * @param configManager
	 *            Parent Configuration Manager
	 * @param owner
	 *            Component this dialog shall be added to
	 */
	public ConfigDialog(UserConfigurer configManager, Dialog owner) {
		super(owner, "Configuring " + configManager.getConfigurable().getTypeName());

		init(configManager, owner);

	}

	/**
	 * @param setPropertyFields
	 *            if True, the user's values will be applied to the properties
	 *            set
	 * @return Whether the user has set all the values on the dialog correctly
	 */
	private boolean processPropertiesInternal(boolean setPropertyFields, boolean showMessage) {
		Iterator<PropertyInputPanel> it = propertyInputPanels.iterator();

		while (it.hasNext()) {
			PropertyInputPanel inputPanel = it.next();
			PropertyDescriptor property = inputPanel.getDescriptor();

			if (inputPanel.isValueSet()) {
				if (setPropertyFields) {

					myConfigurer.setProperty(property.getName(), inputPanel.getValue());
				}
			} else {
				if (showMessage) {
					UserMessages.showWarning(property.getName() + " is not set or is incomplete");
				}
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

		myConfigurer.dialogConfigurationFinished(new ConfigDialogClosedException());
		super.dispose();
	}

	/**
	 * Creates ok, cancel buttons on the dialog
	 */
	private void createButtons(JPanel panel) {
		JPanel buttonsPanel = new VerticalLayoutPanel();
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
		this.myConfigurer = configManager;

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
				cancelAction();

			}
		});

		setResizable(false);
		setModal(true);

		panel = new VerticalLayoutPanel();

		panel.setVisible(true);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		initPanelTop(panel);

		createPropertiesDialog(panel);

		initPanelBottom(panel);

		createButtons(panel);

		add(panel);
		pack();
		setMinimumSize(new Dimension(300, this.getHeight()));
		setLocationRelativeTo(owner);
		this.setVisible(true);
	}

	protected void completeConfiguration() throws ConfigException {
		myConfigurer.getConfigurable().completeConfiguration(
				new PropertySet(myConfigurer.getProperties()));
	}

	/**
	 * What happens when the user presses the OK button
	 */
	private void okAction() {
		if (applyProperties()) {
			boolean preConfigurationSuccess = true;
			try {
				PropertySet properties = new PropertySet(myConfigurer.getProperties());
				myConfigurer.getConfigurable().preConfiguration(properties);
			} catch (ConfigException e1) {
				e1.defaultHandleBehavior();
				preConfigurationSuccess = false;
			}

			if (preConfigurationSuccess) {
				setVisible(false);
				dispose();

				(new TrackedAction("Configuring " + myConfigurer.getConfigurable().getTypeName()) {

					private static final long serialVersionUID = 1L;

					@Override
					protected void action() throws ActionException {
						ConfigException configException = null;

						try {
							completeConfiguration();
						} catch (ConfigException e) {
							configException = e;

						}

						myConfigurer.dialogConfigurationFinished(configException);

					}
				}).doAction();
			}
		}
	}

	protected boolean checkPropreties() {
		return processPropertiesInternal(false, false);
	}

	/**
	 * Gets value entered in the dialog and applies them to the properties set
	 * 
	 * @return Whether operation was successful
	 */
	protected boolean applyProperties() {
		/*
		 * first check if all the fields have been set correctly, then set them
		 */
		if (processPropertiesInternal(false, true)) {
			processPropertiesInternal(true, false);
			return true;
		}
		return false;

	}

	/**
	 * Creates the dialog
	 */

	protected void createPropertiesDialog(JPanel panel) {
		PropertyDescriptor[] propDescriptors = myConfigurer.getConfigurable().getConfigSchema();
		propertyInputPanels = new Vector<PropertyInputPanel>(propDescriptors.length);

		MutableAttributeSet properties = myConfigurer.getProperties();

		for (PropertyDescriptor property : propDescriptors) {

			PropertyInputPanel inputPanel = property.getInputPanel();
			panel.add(inputPanel.getJPanel());

			/*
			 * Try to get the configurer's current value and apply it to the
			 * input panels
			 */
			Object currentValue = properties.getAttribute(inputPanel.getName());
			if (currentValue != null) {
				inputPanel.setValue(currentValue);
			}

			propertyInputPanels.add(inputPanel);
		}
		checkPropreties();
	}

	/**
	 * Initializes the dialog contents top
	 */
	protected void initPanelTop(JPanel panel) {
		/*
		 * Used by subclasses to add elements to the panel
		 */
	}

	/**
	 * Initializes the dialog contents bottom
	 */
	protected void initPanelBottom(JPanel panel) {
		/*
		 * Used by subclasses to add elements to the panel
		 */
	}

	public UserConfigurer getConfigurer() {
		return myConfigurer;
	}

}

/**
 * Exception to be thrown if the Dialog is intentionally closed by the User
 * 
 * @author Shu
 */
class ConfigDialogClosedException extends ConfigException {

	private static final long serialVersionUID = 1L;

	public ConfigDialogClosedException() {
		super("Config dialog closed");

	}

	@Override
	public void defaultHandleBehavior() {
		/*
		 * Do nothing
		 */
	}

}