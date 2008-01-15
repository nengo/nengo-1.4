/*
 * Created on 15-Jan-08
 */
package ca.neo.config.impl;

import ca.neo.config.Configurable;
import ca.neo.config.Configuration;
import ca.neo.config.MainHandler;
import ca.neo.config.Property;

abstract class AbstractProperty implements Property {
		
	private Configuration myConfiguration;
	private String myName;
	private Class myClass;
	private boolean myMutable;
	
	public AbstractProperty(Configuration configuration, String name, Class c, boolean mutable) {
		if (!Configurable.class.isAssignableFrom(c) && !MainHandler.getInstance().canHandle(c)) {
			throw new IllegalArgumentException("No handler for property type " + c.getName());
		}
			
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

}