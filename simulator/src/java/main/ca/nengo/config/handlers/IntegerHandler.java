/*
 * Created on 17-Dec-07
 */
package ca.nengo.config.handlers;

/**
 * ConfigurationHandler for Integer values. 
 * 
 * @author Bryan Tripp
 */
public class IntegerHandler extends BaseHandler {

	public IntegerHandler() {
		super(Integer.class);
	}

	/**
	 * @see ca.nengo.config.ConfigurationHandler#getDefaultValue(java.lang.Class)
	 */
	public Object getDefaultValue(Class c) {
		return new Integer(0);
	}

}
