/*
 * Created on 17-Dec-07
 */
package ca.neo.config;

import java.awt.Component;

import ca.neo.config.ui.ConfigurationChangeListener;

public interface ConfigurationHandler {
	
	public boolean canHandle(Class c);
	
	public Component getRenderer(Object o);
	
	public Component getEditor(Object o, ConfigurationChangeListener listener);
	
	public String toString(Object o);
	
	public Object fromString(String s);

}
