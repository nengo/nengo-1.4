/*
 * Created on 17-Dec-07
 */
package ca.neo.config.handlers;

/**
 * ConfigurationHandler for String values. 
 * 
 * @author Bryan Tripp
 */
public class StringHandler extends BaseHandler {

	public StringHandler() {
		super(String.class);
	}

	/**
	 * @see ca.neo.config.ConfigurationHandler#getDefaultValue(java.lang.Class)
	 */
	public Object getDefaultValue(Class c) {
		return new String();
	}

}
