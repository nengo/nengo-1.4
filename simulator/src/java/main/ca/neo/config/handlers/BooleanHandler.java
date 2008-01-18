/*
 * Created on 17-Dec-07
 */
package ca.neo.config.handlers;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ca.neo.config.EditorProxy;
import ca.neo.config.ui.ConfigurationChangeListener;

public class BooleanHandler extends BaseHandler {

	public BooleanHandler() {
		super(Boolean.class);
	}

	@Override
	public Component getEditor(Object o, ConfigurationChangeListener listener) {
		JPanel result = new JPanel(new FlowLayout());
		result.setBackground(Color.WHITE);
		
		final JCheckBox cb = new JCheckBox("", ((Boolean) o).booleanValue());
		final JButton button = new JButton("OK");
		
		listener.setProxy(new EditorProxy() {
			public Object getValue() {
				return new Boolean(cb.isSelected());
			}
		});
		button.addActionListener(listener);

		result.add(cb);
		result.add(button);
		
		cb.setEnabled(false);
		button.setEnabled(false);
		Thread thread = new Thread() {
			public void run() {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {}
				cb.setEnabled(true);
				button.setEnabled(true);					
			}
		};
		thread.start();
		
		return result;
	}

	/**
	 * @see ca.neo.config.ConfigurationHandler#getDefaultValue(java.lang.Class)
	 */
	public Object getDefaultValue(Class c) {
		return new Boolean(false);
	}

	
}
