/*
 * Created on 17-Dec-07
 */
package ca.neo.config.handlers;

import java.awt.Component;

import javax.swing.JComboBox;

import ca.neo.config.EditorProxy;
import ca.neo.config.ui.ConfigurationChangeListener;
import ca.neo.model.SimulationMode;

public class SimulationModeHandler extends BaseHandler {

	public SimulationModeHandler() {
		super(SimulationMode.class);
	}

	@Override
	public Component getEditor(Object o, ConfigurationChangeListener listener) {
		SimulationMode mode = (SimulationMode) o;
		SimulationMode[] modes = new SimulationMode[]{
				SimulationMode.DIRECT, 
				SimulationMode.CONSTANT_RATE, 
				SimulationMode.RATE, 
				SimulationMode.APPROXIMATE, 
				SimulationMode.DEFAULT, 
				SimulationMode.PRECISE};
		
		final JComboBox result = new JComboBox(modes);
		result.setSelectedItem(mode);
		
		listener.setProxy(new EditorProxy() {
			public Object getValue() {
				return result.getSelectedItem();
			}
		});
		result.addActionListener(listener);
		
		return result;
	}

	
}
