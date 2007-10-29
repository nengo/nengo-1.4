package ca.neo.ui.configurable;

import java.awt.Component;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ca.shu.ui.lib.Style.Style;

/**
 * Swing Input panel to be used to enter in the value for a ConfigParam
 * 
 * @author Shu
 */
public abstract class PropertyInputPanel extends JPanel {
	private final JPanel innerPanel;

	private PropertyDescriptor propDescriptor;

	private JLabel statusMessage;

	/**
	 * @param property
	 *            A description of the Configuration parameter to be configured
	 */
	public PropertyInputPanel(PropertyDescriptor property) {
		super();
		this.propDescriptor = property;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentY(TOP_ALIGNMENT);

		setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		JLabel label = new JLabel(property.getName() + " ("
				+ property.getTypeName() + ")");
		add(label);

		innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
		innerPanel.setAlignmentX(LEFT_ALIGNMENT);

		add(innerPanel);

		statusMessage = new JLabel("");
		statusMessage.setForeground(Style.COLOR_HIGH_SALIENCE);

		add(statusMessage);

	}

	/**
	 * @return
	 */
	protected JDialog getDialogParent() {
		/*
		 * get the JDialog parent
		 */
		Container parent = getParent();
		while (parent != null) {
			if (parent instanceof JDialog) {
				return (JDialog) parent;
			}
			parent = parent.getParent();
		}

		throw new RuntimeException("Input panel does not have a dialog parent");

	}

	/**
	 * @param comp
	 *            Component to be added to the input panel
	 */
	protected void addToPanel(Component comp) {
		innerPanel.add(comp);

	}

	/**
	 * @param comp
	 *            Component to be removed from the input panel
	 */
	protected void removeFromPanel(Component comp) {
		innerPanel.remove(comp);
	}

	/**
	 * @param msg
	 */
	protected void setStatusMsg(String msg) {
		statusMessage.setText(msg);
	}

	/**
	 * @return Descriptor of the configuration parameter
	 */
	public PropertyDescriptor getDescriptor() {
		return propDescriptor;
	}

	@Override
	public String getName() {
		return propDescriptor.getName();
	}

	/**
	 * @return Value of the parameter
	 */
	public abstract Object getValue();

	/**
	 * @return True if configuration parameter is set
	 */
	public abstract boolean isValueSet();

	/**
	 * @param value
	 *            Sets the configuration parameter
	 */
	public abstract void setValue(Object value);

}
