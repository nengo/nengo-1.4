/**
 * 
 */
package ca.neo.util;

import ca.neo.model.StructuralException;

/**
 * An object that has a Configuration. 
 * 
 * @author Bryan Tripp
 */
public interface Configurable {

	/**
	 * @return Configuration of this object
	 */
	public Configuration getConfiguration();
	
	/**
	 * Called by Configuration to notify the Configurable that its configuration has changed. 
	 * 
	 * @param propertyName Name of changed property
	 * @param newValue New value of changed property
	 * @throws StructuralException if the name or new value is invalid 
	 */
	public void propertyChange(String propertyName, Object newValue) throws StructuralException;
	
}
