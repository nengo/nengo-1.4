/*
 * Created on 17-Dec-07
 */
package ca.neo.config.handlers;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ca.neo.config.IconRegistry;
import ca.neo.config.ConfigurationHandler;
import ca.neo.config.EditorProxy;
import ca.neo.config.ui.ConfigurationChangeListener;

public abstract class BaseHandler implements ConfigurationHandler {

	private Class<?> myClass;
	
	/**
	 * @param c Class of objects handled by this handler
	 */
	public BaseHandler(Class c) {
		myClass = c;
	}
	
	/**
	 * @return true if arg matches class given in constructor
	 * @see ca.neo.config.ConfigurationHandler#canHandle(java.lang.Class)
	 */
	public boolean canHandle(Class c) {
		return myClass.isAssignableFrom(c);
	}

	/**
	 * @return myClass.getConstructor(new Class[]{String.class}).newInstance(new Object[]{s})
	 * @see ca.neo.config.ConfigurationHandler#fromString(java.lang.String)
	 */
	public Object fromString(String s) {
		try {
			return myClass.getConstructor(new Class[]{String.class}).newInstance(new Object[]{s});
		} catch (Exception e) {
			Throwable t = e;
			if (t instanceof InvocationTargetException) t = e.getCause();
			throw new RuntimeException(t);
		}
	}

	/**
	 * Returns a JTextField. An object is built from the text using fromString().
	 *   
	 * @see ca.neo.config.ConfigurationHandler#getEditor(java.lang.Object, ConfigurationChangeListener)
	 */
	public Component getEditor(Object o, ConfigurationChangeListener listener) {
		final JTextField result = new JTextField(toString(o));
		if (result.getPreferredSize().width < 20) 
			result.setPreferredSize(new Dimension(20, result.getPreferredSize().height));

		listener.setProxy(new EditorProxy() {
			public Object getValue() {
				return fromString(result.getText());
			}
		});
		result.addActionListener(listener);
		
		return result;
	}

	/**
	 * @return null
	 * @see ca.neo.config.ConfigurationHandler#getRenderer(java.lang.Object)
	 */
	public Component getRenderer(Object o) {
		JLabel result = new JLabel(toString(o), IconRegistry.getInstance().getIcon(o), SwingConstants.LEFT);
		result.setFont(result.getFont().deriveFont(Font.PLAIN));
		return result;
	}

	/**
	 * @return o.toString()
	 * @see ca.neo.config.ConfigurationHandler#toString(java.lang.Object)
	 */
	public String toString(Object o) {
		return o.toString();
	}

}
