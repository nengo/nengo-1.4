package ca.neo.ui.configurable.managers;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import ca.neo.ui.configurable.PropertyInputPanel;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.util.UserMessages;

/**
 * A Configuration dialog which allows the user to manage templates
 * 
 * @author Shu
 */
public class ConfigTemplateDialog extends ConfigDialog {

	private static final long serialVersionUID = 5650002324576913316L;

	private JComboBox fileList;

	public ConfigTemplateDialog(UserTemplateConfigurer configManager,
			Frame owner) {
		super(configManager, owner);
	}

	public ConfigTemplateDialog(UserTemplateConfigurer configManager,
			Dialog owner) {
		super(configManager, owner);
	}

	@Override
	protected void createPropertiesDialog(JPanel panel) {
		super.createPropertiesDialog(panel);
		updateDialog();
	}

	@Override
	protected void initPanelTop(JPanel panel) {
		/*
		 * Add existing templates
		 */
		String[] files = configurerParent.getPropertyFiles();

		fileList = new JComboBox(files);

		JPanel savedFilesPanel = new JCustomPanel();

		JPanel dropDownPanel = new JCustomPanel();

		/*
		 * Selects the default template
		 */
		boolean defaultFound = false;
		for (int i = 0; i < files.length; i++) {
			if (files[i]
					.compareTo(UserTemplateConfigurer.DEFAULT_TEMPLATE_NAME) == 0) {
				defaultFound = true;
				fileList.setSelectedIndex(i);

				configurerParent
						.loadPropertiesFromFile(UserTemplateConfigurer.DEFAULT_TEMPLATE_NAME);
			}
		}
		if (!defaultFound && fileList.getSelectedItem() != null) {
			configurerParent.loadPropertiesFromFile(fileList.getSelectedItem()
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

		JPanel buttonsPanel = new JCustomPanel();
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
						configurerParent.savePropertiesFile(name);
						fileList.addItem(name);
						fileList.setSelectedIndex(fileList.getItemCount() - 1);
					}
				} else {
					UserMessages.showWarning("Properties not complete");
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

				configurerParent.deletePropertiesFile(selectedFile);

				updateDialog();
			}
		});
		button.setFont(Style.FONT_SMALL);
		buttonsPanel.add(button);

		savedFilesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10,
				10));

		savedFilesPanel.add(buttonsPanel);

		JPanel wrapperPanel = new JCustomPanel();
		wrapperPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		wrapperPanel.add(savedFilesPanel);

		JPanel seperator = new JCustomPanel();
		seperator.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

		if (getConfigurerParent().isTemplateEditable()) {
			panel.add(wrapperPanel);
			panel.add(seperator);
		}
	}

	/**
	 * Loads the properties associated with the item selected in the file drop
	 * down list
	 */
	protected void updateDialog() {

		if (fileList.getSelectedItem() != null) {
			configurerParent.loadPropertiesFromFile((String) fileList
					.getSelectedItem());
			Iterator<PropertyInputPanel> it = propertyInputPanels.iterator();
			while (it.hasNext()) {
				PropertyInputPanel panel = it.next();

				Object currentValue = configurerParent.getProperty(panel
						.getName());
				if (currentValue != null && panel.isEnabled()) {
					panel.setValue(currentValue);
				}

			}
		}
	}

	@Override
	public UserTemplateConfigurer getConfigurerParent() {
		return (UserTemplateConfigurer) super.getConfigurerParent();
	}

}

/**
 * A JPanel which has some commonly used settings
 * 
 * @author Shu
 */
class JCustomPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public JCustomPanel() {
		super();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentY(TOP_ALIGNMENT);
		setAlignmentX(LEFT_ALIGNMENT);
	}

}