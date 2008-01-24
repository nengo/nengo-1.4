/*
 * Created on 15-Jan-08
 */
package ca.neo.config;

import ca.neo.model.StructuralException;

/**
 * A Property that has a single value. 
 * 
 * @author Bryan Tripp
 */
public interface SingleValuedProperty extends Property {

	/**
	 * @return Value (for single-valued properties) or first value (for multi-valued properties)
	 */
	public Object getValue();
	
	/**
	 * @param value New value (for single-valued properties) or first value (for multi-valued properties)
	 * @throws StructuralException if the given value is not one of the allowed classes, or if the 
	 * 		Configurable rejects it for any other reason (eg inconsistency with other properties)
	 */
	public void setValue(Object value) throws StructuralException;

}
