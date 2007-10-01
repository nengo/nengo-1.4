package ca.neo.ui.configurable;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ca.shu.ui.lib.Style.Style;

/**
 * Swing Input panel to be used to enter in the value for a ConfigParam
 * 
 * @author Shu
 */
public abstract class ConfigParamInputPanel extends JPanel {
	private final JPanel innerPanel;

	private ConfigParamDescriptor propDescriptor;

	private JLabel statusMessage;

	/**
	 * @param property
	 *            A description of the Configuration parameter to be configured
	 */
	public ConfigParamInputPanel(ConfigParamDescriptor property) {
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
	 * @param comp
	 *            Component to be added to the input panel
	 */
	protected void addToPanel(Component comp) {
		innerPanel.add(comp);
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
	public ConfigParamDescriptor getDescriptor() {
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
