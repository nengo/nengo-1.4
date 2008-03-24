/*
 * Created on 17-Dec-07
 */
package ca.nengo.config;

import java.awt.Component;

import ca.nengo.config.ui.ConfigurationChangeListener;

/**
 * <p>Manages configuration of <code>Property</code>s of of a certain Class.
 * A ConfigurationHandler provides default property values as well as user interface 
 * components that can be used to display and edit a Property.</p>
 * 
 * <p>Not all classes need a ConfigurationHandler. If a Property value does not have 
 * an associated handler, then a property editor must create a Configuration for 
 * it based on its accessor methods. This Configuration may in turn include property values
 * for which there is no ConfigurationHandler, so that a tree of Configurations results. 
 * The leaves of this tree are the values with ConfigurationHandlers.</p>   
 *
 * @author Bryan Tripp
 */
public interface ConfigurationHandler {
	
	/**
	 * @param c A Class 
	 * @return True if this handler can handle values of the given class
	 */
	public boolean canHandle(Class c);
	
	/**
	 * @param o An object for which canHandle(o.getClass()) == true 
	 * @return A UI component (eg JLabel) that shows the object's value.
	 * 		If null, the calling property editor will attempt to create 
	 * 		a default display, possibly using toString(o). 
	 */
	public Component getRenderer(Object o);
	
	/**
	 * @param o An object for which canHandle(o.getClass()) == true 
	 * @param listener An ActionListener. The returned editor component must 1) add this listener 
	 * 		to the part of itself that produces an event when editing is complete, and 2) call 
	 * 		setProxy() with an EditorProxy through which the listener can retrieve a  
	 * 		new object value when editing is complete
	 * @return A UI component (eg JTextField) that allows the user to change the 
	 * 		object's value. If null, the calling property editor will attempt 
	 * 		to create a default editor, possibly using fromString(...).   
	 */
	public Component getEditor(Object o, ConfigurationChangeListener listener);
	
	/**
	 * @param o An object for which canHandle(o.getClass()) == true 
	 * @return A String representation of the object, suitable for user display
	 */
	public String toString(Object o);

	/**
	 * @param s A String representation of an object, eg from toString(o) or user 
	 * 		input
	 * @return An object built from the given string 
	 */
	public Object fromString(String s);

	/**
	 * @param c A class for which canHandle(c) == true
	 * @return A default value of the given class
	 */
	public Object getDefaultValue(Class c);

}
