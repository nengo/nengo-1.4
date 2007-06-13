package ca.neo.ui.views.objects.properties;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ca.sw.graphics.basics.GDefaults;

public abstract class PropertyInputPanel extends JPanel {
	PropertySchema type;

	JLabel statusMessage;

	public PropertyInputPanel(PropertySchema property) {
		super();
		this.type = property;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentY(TOP_ALIGNMENT);

		setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));		
		
		add(property.getLabel());

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setAlignmentX(LEFT_ALIGNMENT);
		
		
		add(panel);
		init(panel);

		statusMessage = new JLabel("");
		statusMessage.setForeground(GDefaults.WARNING_COLOR);
		
		add(statusMessage);
	}

	protected void setStatusMsg(String msg) {
		statusMessage.setText(msg);
	}

	public abstract boolean isValueSet();

	public abstract void setValue(Object value);

	public abstract Object getValue();

	public abstract void init(JPanel panel);

	public PropertySchema getType() {
		return type;
	}

	public void setType(PropertySchema type) {
		this.type = type;
	}

}
