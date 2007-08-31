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
import javax.swing.border.EtchedBorder;

import ca.neo.ui.configurable.ConfigParamInputPanel;
import ca.neo.ui.configurable.IConfigurable;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.util.UserMessages;

/**
 * A lot like UserConfigurer, except it allows the user to use templates to save
 * and re-use values
 * 
 * @author Shu Wu
 * 
 */
public class UserTemplateConfigurer extends UserConfigurer {

	/**
	 * Name of the default template
	 */
	public static final String DEFAULT_TEMPLATE_NAME = "last_used";

	public UserTemplateConfigurer(IConfigurable configurable) {
		super(configurable);
	}

	public UserTemplateConfigurer(IConfigurable configurable, Frame parent) {
		super(configurable, parent);
	}

	public UserTemplateConfigurer(IConfigurable configurable, JDialog parent) {
		super(configurable, parent);
	}

	@Override
	protected ConfigTemplateDialog createConfigDialog() {
		if (frameParent != null) {
			return new ConfigTemplateDialog(this, frameParent);
		} else {
			return new ConfigTemplateDialog(this, dialogParent);
		}
	}
}

/**
 * A Configuration dialog which allows the user to manage templates
 * 
 * @author Shu
 * 
 */
class ConfigTemplateDialog extends ConfigDialog {

	private static final long serialVersionUID = 5650002324576913316L;

	private JComboBox fileList;

	public ConfigTemplateDialog(UserTemplateConfigurer configManager,
			Frame owner) {
		super(configManager, owner);
	}

	public ConfigTemplateDialog(UserTemplateConfigurer configManager,
			JDialog owner) {
		super(configManager, owner);
	}

	@Override
	protected void createDialog() {
		super.createDialog();
		updateDialog();
	}

	@Override
	protected void initPanel() {
		/*
		 * Add existing templates
		 */
		String[] files = parent.getPropertyFiles();

		fileList = new JComboBox(files);

		JPanel savedFilesPanel = new Panel();

		JPanel dropDownPanel = new Panel();

		/*
		 * Selects the default template
		 */
		boolean defaultFound = false;
		for (int i = 0; i < files.length; i++) {
			if (files[i]
					.compareTo(UserTemplateConfigurer.DEFAULT_TEMPLATE_NAME) == 0) {
				defaultFound = true;
				fileList.setSelectedIndex(i);

				parent
						.loadPropertiesFromFile(UserTemplateConfigurer.DEFAULT_TEMPLATE_NAME);
			}
		}
		if (!defaultFound && fileList.getSelectedItem() != null) {
			parent
					.loadPropertiesFromFile(fileList.getSelectedItem()
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
						parent.savePropertiesFile(name);
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

				parent.deletePropertiesFile(selectedFile);

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
			parent.loadPropertiesFromFile((String) fileList.getSelectedItem());
			Iterator<ConfigParamInputPanel> it = propertyInputPanels.iterator();
			while (it.hasNext()) {
				ConfigParamInputPanel panel = it.next();

				Object currentValue = parent.getProperty(panel.getName());
				if (currentValue != null) {
					panel.setValue(currentValue);
				}

			}
		}
	}

}

/**
 * A JPanel which has some commonly used settings
 * 
 * @author Shu
 * 
 */
class Panel extends JPanel {

	private static final long serialVersionUID = 1L;

	public Panel() {
		super();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentY(TOP_ALIGNMENT);
		setAlignmentX(LEFT_ALIGNMENT);
	}

}
