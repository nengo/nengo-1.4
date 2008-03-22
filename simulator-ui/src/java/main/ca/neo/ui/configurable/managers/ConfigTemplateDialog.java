package ca.neo.ui.configurable.managers;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;

/**
 * A Configuration dialog which allows the user to manage templates
 * 
 * @author Shu
 */
public class ConfigTemplateDialog extends ConfigDialog {

	private static final long serialVersionUID = 5650002324576913316L;

	private JComboBox templateList;

	public ConfigTemplateDialog(UserTemplateConfigurer configManager, Dialog owner) {
		super(configManager, owner);
	}

	public ConfigTemplateDialog(UserTemplateConfigurer configManager, Frame owner) {
		super(configManager, owner);
	}

	@Override
	protected void completeConfiguration() throws ConfigException {
		super.completeConfiguration();

		getConfigurer().savePropertiesFile(UserTemplateConfigurer.DEFAULT_TEMPLATE_NAME);

	}

	@Override
	protected void createPropertiesDialog(JPanel panel) {
		super.createPropertiesDialog(panel);

		if (checkPropreties()) {
			/*
			 * Use existing properties
			 */
			templateList.setSelectedItem(null);
		} else {
			/*
			 * Selects the default template
			 */
			for (int i = 0; i < templateList.getItemCount(); i++) {
				if (templateList.getItemAt(i).toString().compareTo(
						UserTemplateConfigurer.DEFAULT_TEMPLATE_NAME) == 0) {
					templateList.setSelectedIndex(i);
					break;
				}
			}

			// updateDialogFromFile();
		}
	}

	@Override
	protected void initPanelTop(JPanel panel) {
		/*
		 * Add existing templates
		 */
		String[] files = getConfigurer().getPropertyFiles();

		templateList = new JComboBox(files);

		JPanel savedFilesPanel = new VerticalLayoutPanel();

		templateList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateDialogFromFile();
			}

		});

		savedFilesPanel.add(new JLabel("Templates"));

		savedFilesPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		buttonsPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		buttonsPanel.add(templateList);

		JButton button;
		button = new JButton("New");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (applyProperties()) {
					String name = JOptionPane.showInputDialog("Name:");

					if (name != null && name.compareTo("") != 0) {
						getConfigurer().savePropertiesFile(name);
						templateList.addItem(name);
						templateList.setSelectedIndex(templateList.getItemCount() - 1);
					}
				} else {
					UserMessages.showWarning("Cannot create template with incomplete properties");
				}
			}
		});
		button.setFont(Style.FONT_SMALL);
		buttonsPanel.add(button);

		button = new JButton("Remove");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedFile = (String) templateList.getSelectedItem();

				templateList.removeItem(selectedFile);

				getConfigurer().deletePropertiesFile(selectedFile);

				updateDialogFromFile();
			}
		});
		button.setFont(Style.FONT_SMALL);
		buttonsPanel.add(button);

		savedFilesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		savedFilesPanel.add(buttonsPanel);

		JPanel wrapperPanel = new VerticalLayoutPanel();
		wrapperPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		wrapperPanel.add(savedFilesPanel);

		JPanel seperator = new VerticalLayoutPanel();
		seperator.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

		if (getConfigurer().isTemplateEditable()) {
			panel.add(wrapperPanel);
			panel.add(seperator);
		}
	}

	/**
	 * Loads the properties associated with the item selected in the file drop
	 * down list
	 */
	protected void updateDialogFromFile() {
		try {
			if (templateList.getSelectedItem() != null) {
				getConfigurer().loadPropertiesFromFile((String) templateList.getSelectedItem());
				Iterator<PropertyInputPanel> it = propertyInputPanels.iterator();
				while (it.hasNext()) {
					PropertyInputPanel panel = it.next();

					Object currentValue = getConfigurer().getProperty(panel.getName());
					if (currentValue != null && panel.isEnabled()) {
						panel.setValue(currentValue);
					}

				}
			}
		} catch (ClassCastException e) {
			Util.debugMsg("Saved template has incompatible data, it will be ignored");
		}
	}

	@Override
	public UserTemplateConfigurer getConfigurer() {
		return (UserTemplateConfigurer) super.getConfigurer();
	}

}

/**
 * A JPanel which has some commonly used settings
 * 
 * @author Shu
 */
class VerticalLayoutPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public VerticalLayoutPanel() {
		super();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentY(TOP_ALIGNMENT);
		setAlignmentX(LEFT_ALIGNMENT);
	}

}