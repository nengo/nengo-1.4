/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "ConfigTemplateDialog.java". Description: 
"A Configuration dialog which allows the user to manage templates
  
  @author Shu"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU 
Public License license (the GPL License), in which case the provisions of GPL 
License are applicable  instead of those above. If you wish to allow use of your 
version of this file only under the terms of the GPL License and not to allow 
others to use your version of this file under the MPL, indicate your decision 
by deleting the provisions above and replace  them with the notice and other 
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
*/

package ca.nengo.ui.configurable.managers;

import java.awt.Dialog;
import java.awt.Dimension;
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

import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.PropertyInputPanel;
import ca.nengo.ui.lib.Style.NengoStyle;
import ca.nengo.ui.lib.util.UserMessages;
import ca.nengo.ui.lib.util.Util;

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
		init();
	}

	public ConfigTemplateDialog(UserTemplateConfigurer configManager, Frame owner) {
		super(configManager, owner);
		init();
	}

	private void init() {

		if (checkPropreties()) {
			/*
			 * Use existing properties
			 */
			templateList.setSelectedItem(null);
		} else {
			/*
			 * Selects the last used template
			 */
			boolean foundTemplate = false;
			for (int i = 0; i < templateList.getItemCount(); i++) {
				if (templateList.getItemAt(i).toString().compareTo(
						UserTemplateConfigurer.PREFERRED_TEMPLATE_NAME) == 0) {
					templateList.setSelectedIndex(i);
					foundTemplate = true;
					break;
				}
			}
			
			/*
			 * Failing that, selects the default template
			 */
			if (!foundTemplate){
				for (int i = 0; i < templateList.getItemCount(); i++) {
					if (templateList.getItemAt(i).toString().compareTo(
							UserTemplateConfigurer.DEFAULT_TEMPLATE_NAME) == 0) {
						templateList.setSelectedIndex(i);
						break;
					}
				}
			}

//			updateFromTemplate();
		}
	}

	@Override
	protected void completeConfiguration() throws ConfigException {
		super.completeConfiguration();

		getConfigurer().savePropertiesFile(UserTemplateConfigurer.PREFERRED_TEMPLATE_NAME);

	}

	@Override
	protected void initPanelTop(JPanel panel) {
		/*
		 * Add existing templates
		 */
		String[] files = getConfigurer().getPropertyFiles();
		JPanel templatesPanel = new VerticalLayoutPanel();
		templatesPanel.add(new JLabel("Templates"));
		templatesPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		templateList = new JComboBox(files);
		templateList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateFromTemplate();
			}
		});
		templateList.setMaximumSize(new Dimension(300, 100));
		templateList.setPreferredSize(new Dimension(100, templateList.getHeight()));

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

		button.setFont(NengoStyle.FONT_SMALL);
		buttonsPanel.add(button);

		button = new JButton("Remove");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedFile = (String) templateList.getSelectedItem();

				templateList.removeItem(selectedFile);

				getConfigurer().deletePropertiesFile(selectedFile);

				updateFromTemplate();
			}
		});
		button.setFont(NengoStyle.FONT_SMALL);
		buttonsPanel.add(button);

		templatesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		templatesPanel.add(buttonsPanel);

		JPanel wrapperPanel = new VerticalLayoutPanel();
		wrapperPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		wrapperPanel.add(templatesPanel);

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
	protected void updateFromTemplate() {
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