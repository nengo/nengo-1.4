package ca.neo.ui.views.objects;

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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ca.sw.util.Util;

public class PropertiesDialog extends JDialog {

	private static final long serialVersionUID = 5650002324576913316L;

	private Vector<PropertyField> propertyFields;

	ProxyObject proxyObj;

	public PropertiesDialog(Frame owner, ProxyObject proxyObj) {
		super(owner, "New " + proxyObj.getIcon().getName());
		this.proxyObj = proxyObj;

		setLocationRelativeTo(owner);

		MetaProperty[] properties = proxyObj.getMetaProperties();
		// Iterator<MetaProperty> propertyIt = properties.iterator();

		JPanel panel = new JPanel();
		panel.setVisible(true);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		setResizable(false);
		setModal(true);

		propertyFields = new Vector<PropertyField>(properties.length);

		for (int i = 0; i < properties.length; i++) {

			MetaProperty property = properties[i];
			JLabel label = new JLabel(property.getName() + " ("
					+ property.getType().getSimpleName() + ")");

			PropertyField tf = new PropertyField(property);
			Object currentValue = proxyObj.getProperty(property.getName());
			if (currentValue != null) {
				tf.setText(currentValue.toString());
			}

			
			
			propertyFields.add(tf);
			panel.add(label);
			panel.add(tf);

		}

		JPanel closePanel = new JPanel();
		closePanel.setLayout(new BoxLayout(closePanel, BoxLayout.LINE_AXIS));
		closePanel.add(Box.createHorizontalGlue());

		closePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));

		panel.add(closePanel);

		JButton addToWorldButton = new JButton("Add to World");
		addToWorldButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkPropertyFields()) {
					PropertiesDialog.this.proxyObj.initProxy0();
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
				PropertiesDialog.this.proxyObj.removeFromParent();
			}
		});
		closePanel.add(cancelButton);

		add(panel);
		pack();

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
		Iterator<PropertyField> it = propertyFields.iterator();

		while (it.hasNext()) {
			PropertyField propertyField = it.next();
			MetaProperty property = propertyField.getProperty();

			String strValue = propertyField.getText();

			Class propertyType = property.getType();

			Object objValue = null;

			if (strValue.compareTo("") == 0 || strValue == null) {
				Util.Warning(property.getName() + " is not set");
				return false;

			}

			try {
				if (propertyType == Integer.class) {
					objValue = new Integer(strValue);

				} else if (propertyType == String.class) {
					objValue = strValue;
				} else {
					Util.Error("unrecognized property type");
					return false;
				}

			} catch (NumberFormatException e) {

				Util.Warning(property.getName() + " must be a "
						+ propertyType.getName());
				return false;
			}

			if (setPropertyFields) {
				proxyObj.setProperty(property.getName(), objValue);
			}

		}

		return true;
	}
}

class PropertyField extends JTextField {
	private static final long serialVersionUID = 5108856120484394597L;

	MetaProperty property;

	public PropertyField(MetaProperty property) {
		super(10);
		this.property = property;

		property.getClass();
	}

	public MetaProperty getProperty() {
		return property;
	}

}