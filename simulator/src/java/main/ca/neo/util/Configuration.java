/*
 * Created on 13-Feb-2007
 */
package ca.neo.util;

import java.io.Serializable;
import ca.neo.model.StructuralException;

/**
 * Encapsulates certain properties of an object so that they can be accessed and 
 * modified in a standard way. Which properties? Ones that have reasonable defaults 
 * and are simple (like Strings, numbers, or things belonging to a short list). 
 * 
 * The reasons for having this interface are 1) to avoid cluttering other interfaces 
 * with trivial setters and getters, 2) to allow a single UI component to deal with 
 * a lot of configuration, 3) to provide a standard place to look for subtype-specific 
 * properties. 
 *  
 * @author Bryan Tripp
 */
public interface Configuration extends Serializable {

	/**
	 * @return List of property names for the associated Configurable.
	 * 		(For example a Termination may have properties like synaptic rise time and 
	 * 		probability of vescicle release.) 
	 */	
	public String[] listPropertyNames();

	/**
	 * @param name Name of a property from listPropertyNames()
	 * @return The type to which the property value must belong
	 */	
	public Class getType(String name);
	
	/**
	 * @param name Name of property from listPropertyNames()
	 * @return Value of corresponding property (the Termination is responsible for providing 
	 * 		appropriate defaults for properties that have not been set by the user). 
	 */
	public Object getProperty(String name);

	/**
	 * Sets a new property value and notifies the corresponding Configurable of the change. 
	 * 
	 * Note that if a property is set on a composite object, then then it is also   
	 * applied to the components. All components must accept the change, otherwise the 
	 * change is rolled back and an exception is thrown. The same property may be changed 
	 * subsequently for individual components. 
	 * 
	 * @param name Name of property from listPropertyNames()
	 * @param value New property value 
	 * @throws StructuralException if the named property is unknown or the given value can not be 
	 * 		meaningfully interpreted in relation to the property  
	 */
	public void setProperty(String name, Object value) throws StructuralException;	

}
