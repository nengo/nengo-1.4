package ca.neo.ui.views.objects.configurable;

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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import ca.neo.ui.style.Style;
import ca.neo.ui.views.objects.configurable.managers.IConfigurationManager;
import ca.neo.ui.views.objects.configurable.struct.PropertyStructure;
import ca.shu.ui.lib.util.Util;

public class UIConfigManager implements IConfigurationManager {
	JDialog parent0;
	Frame parent1;

	public static final String DEFAULT_PROPERTY_FILE_NAME = "last_used";
	
	public UIConfigManager(Frame parent) {
		super();
		this.parent1 = parent;
	}

	public UIConfigManager(JDialog parent) {
		super();
		this.parent0 = parent;
	}

	public void configure(IConfigurable configurable) {
		if (parent0 != null) {
			new ConfigDialog(parent0, configurable);
		} else {
			new ConfigDialog(parent1, configurable);
		}
	}

}

class ConfigDialog extends JDialog {

	private static final long serialVersionUID = 5650002324576913316L;

	private Vector<PropertyInputPanel> propertyInputPanels;

	IConfigurable proxyObj;

	public ConfigDialog(JDialog owner, IConfigurable proxyObj) {
		super(owner, "New " + proxyObj.getTypeName() + " properties");

		init(owner, proxyObj);

	}

	public ConfigDialog(Frame owner, IConfigurable proxyObj) {
		super(owner, "New " + proxyObj.getTypeName() + " Properties");

		init(owner, proxyObj);

	}



	JComboBox fileList;

	JPanel panel;

	public void init(Component c, IConfigurable proxy) {
		proxyObj = proxy;
		setResizable(false);
		setModal(true);
		setLocationRelativeTo(c);

		panel = new Panel();
		panel.setVisible(true);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		createFilePanel();

		createDialog();

		createButtons();

		add(panel);
		pack();

		this.setVisible(true);
	}

	public void createDialog() {

		PropertyStructure[] properties = proxyObj.getPropertiesSchema();
		propertyInputPanels = new Vector<PropertyInputPanel>(properties.length);

		for (int i = 0; i < properties.length; i++) {

			PropertyStructure property = properties[i];

			PropertyInputPanel inputPanel = property.createInputPanel();
			panel.add(inputPanel);

			propertyInputPanels.add(inputPanel);

		}
		updateDialog();
	}

	/**
	 * Loads the properties associated with the item selected in the file drop
	 * down list
	 */
	public void updateDialog() {
		if (fileList.getSelectedItem() != null) {
			proxyObj
					.loadPropertiesFromFile((String) fileList.getSelectedItem());
			Iterator<PropertyInputPanel> it = propertyInputPanels.iterator();
			while (it.hasNext()) {
				PropertyInputPanel panel = it.next();

				Object currentValue = proxyObj.getProperty(panel.getName());
				if (currentValue != null) {
					panel.setValue(currentValue);
				}

			}
		}
	}

	public void createFilePanel() {
		/*
		 * construct existing properties
		 */
		String[] files = proxyObj.getPropertyFiles();

		fileList = new JComboBox(files);

		JPanel savedFilesPanel = new Panel();

		// list.setPreferredSize(new Dimension(100, list.getHeight()));
		// list.revalidate();

		JPanel dropDownPanel = new Panel();

		/*
		 * Selects the default property configuration
		 */
		boolean defaultFound = false;
		for (int i = 0; i < files.length; i++) {
			if (files[i].compareTo(UIConfigManager.DEFAULT_PROPERTY_FILE_NAME) == 0) {
				defaultFound = true;
				fileList.setSelectedIndex(i);
				proxyObj.loadPropertiesFromFile(UIConfigManager.DEFAULT_PROPERTY_FILE_NAME);
			}
		}
		if (!defaultFound && fileList.getSelectedItem() != null) {
			proxyObj.loadPropertiesFromFile(fileList.getSelectedItem()
					.toString());
		}

		fileList.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				updateDialog();

			}

		});
		savedFilesPanel.add(new JLabel("Templates"));
		dropDownPanel.add(fileList);
		dropDownPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		savedFilesPanel.add(dropDownPanel);

		JPanel buttonsPanel = new Panel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 5));

		JButton button;
		button = new JButton("New");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (applyProperties()) {
					String name = JOptionPane.showInputDialog("Name:");

					if (name != null && name.compareTo("") != 0) {
						proxyObj.savePropertiesToFile(name);
						fileList.addItem(name);
						fileList.setSelectedIndex(fileList.getItemCount() - 1);
					}
				} else {
					Util.Warning("Properties not complete");
				}
			}
		});
		button.setFont(Style.FONT_SMALL);
		buttonsPanel.add(button);

		button = new JButton("Remove");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedFile = (String) fileList.getSelectedItem();

				fileList.removeItem(selectedFile);
				proxyObj.deletePropretiesFile(selectedFile);

				updateDialog();
			}
		});
		button.setFont(Style.FONT_SMALL);
		buttonsPanel.add(button);

		savedFilesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10,
				10));

		savedFilesPanel.add(buttonsPanel);

		JPanel wrapperPanel = new Panel();
		wrapperPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		wrapperPanel.add(savedFilesPanel);

		JPanel seperator = new Panel();
		seperator.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

		panel.add(wrapperPanel);
		panel.add(seperator);
	}

	public void createButtons() {
		JPanel buttonsPanel = new Panel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 5));

		JButton addToWorldButton = new JButton("Ok");
		addToWorldButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (applyProperties()) {
					setVisible(false);
					dispose();

					(new Thread() {
						public void run() {
							ConfigDialog.this.proxyObj
									.completeConfiguration();
							ConfigDialog.this.proxyObj
									.savePropertiesToFile(UIConfigManager.DEFAULT_PROPERTY_FILE_NAME);
						}
					}).start();

				} else {

				}
			}
		});
		buttonsPanel.add(addToWorldButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
				ConfigDialog.this.proxyObj.cancelConfiguration();
			}
		});
		buttonsPanel.add(cancelButton);
		panel.add(buttonsPanel);
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

	private boolean applyPropertyFields0(boolean setPropertyFields) {
		Iterator<PropertyInputPanel> it = propertyInputPanels.iterator();

		while (it.hasNext()) {
			PropertyInputPanel inputPanel = it.next();
			PropertyStructure property = inputPanel.getType();

			if (inputPanel.isValueSet()) {
				if (setPropertyFields) {
					proxyObj.setProperty(property.getName(), inputPanel
							.getValue());
				}
			} else {
				Util.Warning(property.getName()
						+ " is not set or is incomplete");
				return false;
			}

		}

		return true;
	}

}

class Panel extends JPanel {

	private static final long serialVersionUID = 1L;

	public Panel() {
		super();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentY(TOP_ALIGNMENT);
		setAlignmentX(LEFT_ALIGNMENT);
	}

}

class PropertyField extends JTextField {
	private static final long serialVersionUID = 5108856120484394597L;

	PropertyStructure property;

	public PropertyField(PropertyStructure property) {
		super(10);
		this.property = property;

		property.getClass();
	}

	public PropertyStructure getProperty() {
		return property;
	}

}