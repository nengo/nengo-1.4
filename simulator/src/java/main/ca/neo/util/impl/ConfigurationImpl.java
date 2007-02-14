/**
 * 
 */
package ca.neo.util.impl;

import java.util.HashMap;
import java.util.Map;

import ca.neo.model.StructuralException;
import ca.neo.util.Configurable;
import ca.neo.util.Configuration;

/**
 * Default implementation of Configuration. 
 * 
 * @author Bryan Tripp
 */
public class ConfigurationImpl implements Configuration {

	private static final long serialVersionUID = 1L;
	
	private Map<String, Object> myProperties;
	private Map<String, Class> myTypes;
	private Configurable myConfigurable;

	/**
	 * @param configurable The Configurable that this Configuration configures
	 */
	public ConfigurationImpl(Configurable configurable) {
		myProperties = new HashMap<String, Object>(10);
		myTypes = new HashMap<String, Class>(10);
		myConfigurable = configurable;
	}
	
	/**
	 * Note: this method is called by corresponding Configurable. To set properties as a user,  
	 * call setProperty(...). 
	 * 
	 * @param name Name of new property
	 * @param type Class to which values must belong
	 * @param defaultValue Default value of the property
	 */
	public void addProperty(String name, Class type, Object defaultValue) {
		if (!type.isAssignableFrom(defaultValue.getClass())) {
			throw new IllegalArgumentException("Property " + name + " has type " + type.getName() 
					+ " but default value is of type " + defaultValue.getClass().getName());
		}
		myTypes.put(name, type);
		myProperties.put(name, defaultValue);		
	}
	
	/** 
	 * @see ca.neo.util.Configuration#getProperty(java.lang.String)
	 */
	public Object getProperty(String name) {
		return myProperties.get(name);
	}

	/** 
	 * @see ca.neo.util.Configuration#getType(java.lang.String)
	 */
	public Class getType(String name) {
		return myTypes.get(name);
	}

	/** 
	 * @see ca.neo.util.Configuration#listPropertyNames()
	 */
	public String[] listPropertyNames() {
		return (String[]) myProperties.keySet().toArray(new String[0]);
	}

	/**
	 * @see ca.neo.util.Configuration#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String name, Object value) throws StructuralException {
		if (myTypes.get(name) == null) {
			throw new StructuralException("Property " + name + " is unknown");
		}
		
		if ( !myTypes.get(name).isAssignableFrom(value.getClass()) ) {
			throw new StructuralException(name + "must be of type " + myTypes.get(name).getName() 
					+ " but given value is of type " + value.getClass().getName());
		}
		
		myProperties.put(name, value);
		
		myConfigurable.propertyChange(name, value);
	}

}
