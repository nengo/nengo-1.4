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

	@Override
	public Object fromString(String s) {
		Object result = null;
		
		if (s.equals(SimulationMode.APPROXIMATE.toString())) {
			result = SimulationMode.APPROXIMATE;
		} else if (s.equals(SimulationMode.CONSTANT_RATE.toString())) {
			result = SimulationMode.CONSTANT_RATE;
		} else if (s.equals(SimulationMode.DEFAULT.toString())) {
			result = SimulationMode.DEFAULT;
		} else if (s.equals(SimulationMode.DIRECT.toString())) {
			result = SimulationMode.DIRECT;
		} else if (s.equals(SimulationMode.PRECISE.toString())) {
			result = SimulationMode.PRECISE;
		} else if (s.equals(SimulationMode.RATE.toString())) {
			result = SimulationMode.RATE;
		} else {
			throw new RuntimeException("SimulationMode " + s + " not recognized");
		}
		
		return result;
	}
	
	/**
	 * @see ca.neo.config.ConfigurationHandler#getDefaultValue(java.lang.Class)
	 */
	public Object getDefaultValue(Class c) {
		return SimulationMode.DEFAULT;
	}

}
