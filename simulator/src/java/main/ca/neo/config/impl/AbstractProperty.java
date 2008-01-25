/*
 * Created on 15-Jan-08
 */
package ca.neo.config.impl;

import ca.neo.config.Configuration;
import ca.neo.config.Property;

/**
 * Base implementation of Property. 
 *  
 * @author Bryan Tripp
 */
public abstract class AbstractProperty implements Property {
		
	private Configuration myConfiguration;
	private String myName;
	private Class myClass;
	private boolean myMutable;
	private String myDocumentation;
	
	/**
	 * @param configuration Configuration to which the Property belongs
	 * @param name Name of the Property
	 * @param c Type of the Property
	 * @param mutable Whether the Property value(s) can be modified
	 */
	public AbstractProperty(Configuration configuration, String name, Class c, boolean mutable) {
		myConfiguration = configuration;
		myName = name;
		myClass = c;	
		myMutable = mutable;
	}
	
	/**
	 * @see ca.neo.config.Property#getName()
	 */
	public String getName() {
		return myName;
	}
	
	/**
	 * @see ca.neo.config.Property#getType()
	 */
	public Class getType() {
		return myClass;
	}

	/**
	 * @see ca.neo.config.Property#isMutable()
	 */
	public boolean isMutable() {
		return myMutable;
	}
	
	protected Configuration getConfiguration() {
		return myConfiguration;
	}

	/**
	 * @see ca.neo.config.Property#getDocumentation()
	 */
	public String getDocumentation() {
		return myDocumentation;
	}

	/**
	 * @param text New documentation text (can be plain text or HTML)
	 */
	public void setDocumentation(String text) {
		myDocumentation = text;
	}

}