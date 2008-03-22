/*
 * Created on 17-Dec-07
 */
package ca.neo.config.handlers;

import java.awt.Component;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.swing.JComboBox;

import ca.neo.config.ui.ConfigurationChangeListener;

/**
 * ConfigurationHandler for SimulationMode values. 
 * 
 * @author Bryan Tripp
 */
public class EnumHandler extends BaseHandler {

	private Enum myDefaultValue;
	
	/**
	 * Defaults to type Enum with null default value. 
	 */
	public EnumHandler() {
		this(Enum.class, null);
	}
	
	/**
	 * @param type Type handled by this handler
	 * @param defaultValue Default value for this handler
	 */
	public EnumHandler(Class type, Enum defaultValue) {
		super(type);
		myDefaultValue = defaultValue;
	}

	@Override
	public Component getEditor(Object o, ConfigurationChangeListener listener) {
		Enum mode = (Enum) o;
		List<? extends Enum> all = new ArrayList<Enum>(EnumSet.allOf(mode.getClass()));
		
		final JComboBox result = new JComboBox(all.toArray());
		result.setSelectedItem(mode);
		
		listener.setProxy(new ConfigurationChangeListener.EditorProxy() {
			public Object getValue() {
				return result.getSelectedItem();
			}
		});
		result.addActionListener(listener);
		
		return result;
	}

	@Override
	public Object fromString(String s) {
		throw new RuntimeException("Can't get Enum instance from String (expected to get values from a combo box)");
	}
	
	/**
	 * @see ca.neo.config.ConfigurationHandler#getDefaultValue(java.lang.Class)
	 */
	public Object getDefaultValue(Class c) {
		return myDefaultValue;
	}

}
