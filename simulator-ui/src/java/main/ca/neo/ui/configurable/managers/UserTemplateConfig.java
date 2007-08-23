package ca.neo.ui.configurable.managers;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

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

import ca.neo.ui.configurable.IConfigurable;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.struct.PropDescriptor;
import ca.neo.ui.style.Style;
import ca.shu.ui.lib.util.Util;

public class UserTemplateConfig extends UserConfig {

	public static final String DEFAULT_PROPERTY_FILE_NAME = "last_used";

	public UserTemplateConfig(IConfigurable configurable) {
		super(configurable);
	}

	public UserTemplateConfig(IConfigurable configurable, Frame parent) {
		super(configurable, parent);
	}

	public UserTemplateConfig(IConfigurable configurable, JDialog parent) {
		super(configurable, parent);
	}

	@Override
	public ConfigTemplateDialog createConfigDialog() {
		if (parent0 != null) {
			return new ConfigTemplateDialog(this, parent0);
		} else {
			return new ConfigTemplateDialog(this, parent1);
		}
	}
}

class ConfigTemplateDialog extends ConfigDialog {

	private static final long serialVersionUID = 5650002324576913316L;

	JComboBox fileList;

	public ConfigTemplateDialog(UserTemplateConfig configManager, Frame owner) {
		super(configManager, owner);
	}

	public ConfigTemplateDialog(UserTemplateConfig configManager, JDialog owner) {
		super(configManager, owner);
	}

	@Override
	public void createDialog() {
		super.createDialog();
		updateDialog();
	}

	@Override
	public void initPanel() {
		/*
		 * construct existing properties
		 */
		String[] files = configManager.getPropertyFiles();

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
			if (files[i]
					.compareTo(UserTemplateConfig.DEFAULT_PROPERTY_FILE_NAME) == 0) {
				defaultFound = true;
				fileList.setSelectedIndex(i);

				configManager
						.loadPropertiesFromFile(UserTemplateConfig.DEFAULT_PROPERTY_FILE_NAME);
			}
		}
		if (!defaultFound && fileList.getSelectedItem() != null) {
			configManager.loadPropertiesFromFile(fileList.getSelectedItem()
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
						configManager.savePropertiesFile(name);
						fileList.addItem(name);
						fileList.setSelectedIndex(fileList.getItemCount() - 1);
					}
				} else {
					Util.UserWarning("Properties not complete");
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

				configManager.deletePropertiesFile(selectedFile);

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

	/**
	 * Loads the properties associated with the item selected in the file drop
	 * down list
	 */
	protected void updateDialog() {

		if (fileList.getSelectedItem() != null) {
			configManager.loadPropertiesFromFile((String) fileList
					.getSelectedItem());
			Iterator<PropertyInputPanel> it = propertyInputPanels.iterator();
			while (it.hasNext()) {
				PropertyInputPanel panel = it.next();

				Object currentValue = configManager
						.getProperty(panel.getName());
				if (currentValue != null) {
					panel.setValue(currentValue);
				}

			}
		}
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

	PropDescriptor property;

	public PropertyField(PropDescriptor property) {
		super(10);
		this.property = property;

		property.getClass();
	}

	public PropDescriptor getProperty() {
		return property;
	}

}