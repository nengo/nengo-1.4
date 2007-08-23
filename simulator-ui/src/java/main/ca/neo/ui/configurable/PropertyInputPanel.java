package ca.neo.ui.configurable;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ca.neo.ui.configurable.struct.PropDescriptor;
import ca.neo.ui.style.Style;

public abstract class PropertyInputPanel extends JPanel {
	private JPanel innerPanel;

	PropDescriptor propDescriptor;

	JLabel statusMessage;

	public PropertyInputPanel(PropDescriptor property) {
		super();
		this.propDescriptor = property;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentY(TOP_ALIGNMENT);

		setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		add(property.getLabel());

		innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
		innerPanel.setAlignmentX(LEFT_ALIGNMENT);

		add(innerPanel);
		init(innerPanel);

		statusMessage = new JLabel("");
		statusMessage.setForeground(Style.COLOR_HIGH_SALIENCE);

		add(statusMessage);
	}

	public void addToPanel(Component comp) {
		innerPanel.add(comp);
	}

	public PropDescriptor getDescriptor() {
		return propDescriptor;
	}

	public String getName() {
		return propDescriptor.getName();
	}

	public abstract Object getValue();

	public abstract void init(JPanel panel);

	public abstract boolean isValueSet();

	public void setDescriptor(PropDescriptor type) {
		this.propDescriptor = type;
	}

	public abstract void setValue(Object value);

	protected void setStatusMsg(String msg) {
		statusMessage.setText(msg);
	}

}
