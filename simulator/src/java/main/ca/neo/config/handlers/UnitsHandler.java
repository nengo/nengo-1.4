/*
 * Created on 17-Dec-07
 */
package ca.neo.config.handlers;

import java.awt.Component;

import javax.swing.JComboBox;

import ca.neo.config.EditorProxy;
import ca.neo.config.ui.ConfigurationChangeListener;
import ca.neo.model.Units;

public class UnitsHandler extends BaseHandler {

	public UnitsHandler() {
		super(Units.class);
	}

	@Override
	public Component getEditor(Object o, ConfigurationChangeListener listener) {
		Units unit = (Units) o;
		Units[] units = new Units[]{
				Units.ACU, 
				Units.AVU, 
				Units.M, 
				Units.M_PER_S, 
				Units.mV, 
				Units.N, 
				Units.Nm, 
				Units.RAD, 
				Units.RAD_PER_S, 
				Units.S, 
				Units.SPIKES, 
				Units.SPIKES_PER_S, 
				Units.uA, 
				Units.uAcm2, 
				Units.UNK 
		};
		
		final JComboBox result = new JComboBox(units);
		result.setSelectedItem(unit);
		
		listener.setProxy(new EditorProxy() {
			public Object getValue() {
				return result.getSelectedItem();
			}
		});
		result.addActionListener(listener);
		
		return result;
	}
	
}
