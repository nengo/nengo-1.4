package ca.neo.ui.views.objects.properties;

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
import javax.swing.JTextField;

import ca.shu.ui.lib.util.Util;

public class PropertiesDialog extends JDialog {

	private static final long serialVersionUID = 5650002324576913316L;

	private Vector<PropertyInputPanel> propertyInputPanels;

	IConfigurable proxyObj;

	public PropertiesDialog(JDialog dialog, IConfigurable proxyObj) {
		super(dialog, "New " + proxyObj.getName());
		proxyObj.loadPropertiesFromFile("defaultProperties");
		init(dialog, proxyObj);

	}

	public PropertiesDialog(Frame owner, IConfigurable proxyObj) {
		super(owner, "New " + proxyObj.getName());
		proxyObj.loadPropertiesFromFile("defaultProperties");
		init(owner, proxyObj);

		

	}

	public void init(Component c, IConfigurable proxyObj) {
		this.proxyObj = proxyObj;
		setLocationRelativeTo(c);

		PropertySchema[] properties = proxyObj.getPropertiesSchema();

		JPanel panel = new JPanel();
		panel.setVisible(true);
		panel.setAlignmentX(LEFT_ALIGNMENT);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		setResizable(false);
		setModal(true);

		propertyInputPanels = new Vector<PropertyInputPanel>(properties.length);

		for (int i = 0; i < properties.length; i++) {

			PropertySchema property = properties[i];

			PropertyInputPanel inputPanel = property.createInputPanel();
			panel.add(inputPanel);

			Object currentValue = proxyObj.getProperty(property.getName());
			if (currentValue != null) {
				inputPanel.setValue(currentValue);
			}
			propertyInputPanels.add(inputPanel);

		}

		JPanel closePanel = new JPanel();
		closePanel.setLayout(new BoxLayout(closePanel, BoxLayout.X_AXIS));
		closePanel.setAlignmentX(LEFT_ALIGNMENT);
		closePanel.add(Box.createHorizontalGlue());

		closePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 5));

		JButton addToWorldButton = new JButton("Ok");
		addToWorldButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkPropertyFields()) {
					setVisible(false);
					dispose();

					(new Thread() {
						public void run() {
							PropertiesDialog.this.proxyObj
									.completeConfiguration();
							PropertiesDialog.this.proxyObj
									.savePropertiesToFile("defaultProperties");
						}
					}).start();

				} else {

				}
			}
		});
		closePanel.add(addToWorldButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
				PropertiesDialog.this.proxyObj.cancelConfiguration();
			}
		});
		closePanel.add(cancelButton);

		panel.add(closePanel);
		add(panel);
		pack();

		this.setVisible(true);
	}

	public boolean checkPropertyFields() {
		/*
		 * first check if all the fields have been set correctly, then set them
		 * 
		 */
		if (processPropertyFields(false)) {
			processPropertyFields(true);
			return true;
		}
		return false;

	}

	private boolean processPropertyFields(boolean setPropertyFields) {
		Iterator<PropertyInputPanel> it = propertyInputPanels.iterator();

		while (it.hasNext()) {
			PropertyInputPanel inputPanel = it.next();
			PropertySchema property = inputPanel.getType();

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

class PropertyField extends JTextField {
	private static final long serialVersionUID = 5108856120484394597L;

	PropertySchema property;

	public PropertyField(PropertySchema property) {
		super(10);
		this.property = property;

		property.getClass();
	}

	public PropertySchema getProperty() {
		return property;
	}

}