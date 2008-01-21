/*
 * Created on 3-Dec-07
 */
package ca.neo.config.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.neo.config.Configuration;
import ca.neo.config.Property;
import ca.neo.model.StructuralException;

/**
 * <p>Base implementation of Configuration. A Configurable would normally have 
 * an associated implementation of <code>setValue(String, Object)</code> that 
 * maps to the Configurable's native setters.</p>
 * 
 * <p>This implementation reports property names in the order they are defined.</p>
 * 
 * @author Bryan Tripp
 */
public class ConfigurationImpl implements Configuration {

	private Object myConfigurable;
	private List<String> myPropertyNames;
	private Map<String, Property> myProperties;
	
	/**
	 * @param configurable The Object to which this Configuration belongs
	 */
	public ConfigurationImpl(Object configurable) {
		myConfigurable = configurable;
		myPropertyNames = new ArrayList<String>(20); 
		myProperties = new HashMap<String, Property>(20);
	}
	
	/**
	 * @see ca.neo.config.Configuration#getConfigurable()
	 */
	public Object getConfigurable() {
		return myConfigurable;
	}

	/**
	 * To be called by the associated Configurable, immediately after construction (once 
	 * per property). 
	 *   
	 * @param property The new Property 
	 */
	public void defineProperty(Property property) {
		String name = property.getName();
		myProperties.put(name, property);
		if (myPropertyNames.contains(name)) myPropertyNames.remove(name);
		myPropertyNames.add(name);
	}
	
	/**
	 * @param name Property to remove 
	 */
	public void removeProperty(String name) {
		myProperties.remove(name);
		myPropertyNames.remove(name);
	}
	
	public SingleValuedPropertyImpl defineSingleValuedProperty(String name, Class c, boolean mutable) {		
		SingleValuedPropertyImpl property = new SingleValuedPropertyImpl(this, name, c, mutable);
		defineProperty(property);
		return property;
	}
	
	public TemplateProperty defineTemplateProperty(String name, Class c, Object defaultValue) {
		TemplateProperty property = new TemplateProperty(this, name, c, defaultValue);
		defineProperty(property);
		return property;
	}
	
	/**
	 * @see ca.neo.config.Configuration#getPropertyNames()
	 */
	public List<String> getPropertyNames() {
		return new ArrayList<String>(myPropertyNames);
	}

	/**
	 * @see ca.neo.config.Configuration#getProperty(java.lang.String)
	 */
	public Property getProperty(String name) throws StructuralException {
		if (myProperties.containsKey(name)) {
			return myProperties.get(name);
		} else {
			throw new StructuralException("The property " + name + " is unknown");
		}
	}
	
}
