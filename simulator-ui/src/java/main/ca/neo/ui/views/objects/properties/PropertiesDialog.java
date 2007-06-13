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

import ca.sw.util.Util;

public class PropertiesDialog extends JDialog {

	private static final long serialVersionUID = 5650002324576913316L;

	private Vector<PropertyInputPanel> propertyInputPanels;

	IPropertiesConfigurable proxyObj;

	public PropertiesDialog(JDialog dialog, IPropertiesConfigurable proxyObj) {
		super(dialog, "New " + proxyObj.getName());
		init(dialog, proxyObj);

	}

	public PropertiesDialog(Frame owner, IPropertiesConfigurable proxyObj) {
		super(owner, "New " + proxyObj.getName());

		init(owner, proxyObj);
		// this.setVisible(true);
	}

	public void init(Component c, IPropertiesConfigurable proxyObj) {
		this.proxyObj = proxyObj;
		setLocationRelativeTo(c);
		

		PropertySchema[] properties = proxyObj.getMetaProperties();
		// Iterator<MetaProperty> propertyIt = properties.iterator();

		JPanel panel = new JPanel();
		panel.setVisible(true);
		panel.setAlignmentX(LEFT_ALIGNMENT);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// panel.setPreferredSize(new Dimension(800,400));

		// setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// panel.setPreferredSize(new Dimension(500, 0));
		// this.pack();

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

			// JLabel label = new JLabel(property.getName() + " ("
			// + property.getType().getSimpleName() + ")");
			//
			// PropertyField tf = new PropertyField(property);
			// Object currentValue = proxyObj.getProperty(property.getName());
			// if (currentValue != null) {
			// tf.setText(currentValue.toString());
			// }
			//
			//			
			//			

			// panel.add(label);
			// panel.add(tf);

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
					PropertiesDialog.this.proxyObj.configurationComplete();
					setVisible(false);
					dispose();
				} else {

					// JOptionPane.showMessageDialog(null, "Error");
				}
			}
		});
		closePanel.add(addToWorldButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
				PropertiesDialog.this.proxyObj.configurationCancelled();
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
			//			
			//			
			// Class propertyType = property.getType();
			//
			// Object objValue = null;
			//
			// if (strValue.compareTo("") == 0 || strValue == null) {
			// Util.Warning(property.getName() + " is not set");
			// return false;
			//
			// }
			//
			// try {
			// if (propertyType == Integer.class) {
			// objValue = new Integer(strValue);
			//
			// } else if (propertyType == String.class) {
			// objValue = strValue;
			// } else {
			// Util.Error("unrecognized property type");
			// return false;
			// }
			//
			// } catch (NumberFormatException e) {
			//
			// Util.Warning(property.getName() + " must be a "
			// + propertyType.getName());
			// return false;
			// }
			//
			// if (setPropertyFields) {
			// proxyObj.setProperty(property.getName(), objValue);
			// }

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