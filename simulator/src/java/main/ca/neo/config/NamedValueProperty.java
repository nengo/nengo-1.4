/*
 * Created on 17-Jan-08
 */
package ca.neo.config;

import java.util.List;

import ca.neo.model.StructuralException;

/**
 * A property that can have multiple values, each of which is indexed by a String name. 
 *  
 * @author Bryan Tripp
 */
public interface NamedValueProperty extends Property {

	/**
	 * @param name Name of a value of this property 
	 * @return The value
	 * @throws StructuralException if there is no value of the given name
	 */
	public Object getValue(String name) throws StructuralException;
	
	/**
	 * @return True if values are named automatically, in which case the setter 
	 * 		setValue(Object) can be used; otherwise value names must be provided 
	 * 		by the caller via setValue(String, Object)    
	 */
	public boolean isNamedAutomatically();

	/**
	 * Sets a value by name. 
	 * 
	 * @param name Name of the value
	 * @param value New value of the value
	 * @throws StructuralException if !isMutable
	 */
	public void setValue(String name, Object value) throws StructuralException;
	
	/**
	 * Sets an automatically-named value  
	 *  
	 * @param value New value of the value, from which the Property can automaticall
	 * 		determine the name 
	 * @throws StructuralException if !isNamedAutomatically() or !isMutable 
	 */
	public void setValue(Object value) throws StructuralException;
	
	/**
	 * Removes a value by name
	 * 
	 * @param name Name of value to remove
	 * @throws StructuralException if isFixedCardinality()
	 */
	public void removeValue(String name) throws StructuralException;
	
	/**
	 * @return Names of all values
	 */
	public List<String> getValueNames();
	
}
