package ca.neo.config;

import ca.neo.model.StructuralException;

/**
 * A configuration property. Properties have restricted classes (see Configuration docs). 
 * Properties can have multiple values. 
 *  
 * @author Bryan Tripp
 */
public interface Property {

	/**
	 * @return Property name
	 */
	public String getName();
	
	/**
	 * @return Class to which values belong
	 */
	public Class getType();
	
	/**
	 * @return True if values can be changed after construction of the Configurable
	 */
	public boolean isMutable();
	
	/**
	 * @return True if the property can have multiple values   
	 */
//	public boolean isMultiValued();
	
	/**
	 * @return True if the property has a fixed number of values 
	 */
	public boolean isFixedCardinality();
	
//	/**
//	 * @return Value (for single-valued properties) or first value (for multi-valued properties)
//	 */
//	public Object getValue();
//	
//	/**
//	 * @param value New value (for single-valued properties) or first value (for multi-valued properties)
//	 * @throws StructuralException if the given value is not one of the allowed classes, or if the 
//	 * 		Configurable rejects it for any other reason (eg inconsistency with other properties)
//	 */
//	public void setValue(Object value) throws StructuralException;
//	
//	/**
//	 * @param index Index of a certain single value of a multi-valued property 
//	 * @return The value at the given index
//	 * @throws StructuralException if the given index is out of range
//	 */
//	public Object getValue(int index) throws StructuralException;
//	
//	/**
//	 * @param index Index of a certain single value of a multi-valued property 
//	 * @param value New value to replace that at the given index 
//	 * @throws StructuralException if the value is invalid (as in setValue) or the given index is 
//	 * 		out of range 
//	 */
//	public void setValue(int index, Object value) throws StructuralException;
//
//	/**
//	 * @param value New value to be added to the end of the list 
//	 * @throws StructuralException if the value is invalid (as in setValue) or the Property is 
//	 * 		not multi-valued 
//	 */
//	public void addValue(Object value) throws StructuralException;
//	
//	/**
//	 * @return Number of repeated values of this Property
//	 */
//	public int getNumValues();
//	
//	/**
//	 * @param index Index at which new value is to be inserted  
//	 * @param value New value
//	 * @throws StructuralException if the value is invalid (as in setValue) or the Property is 
//	 * 		not multi-valued or the index is out of range 
//	 */
//	public void insert(int index, Object value) throws StructuralException;
//	
//	/**
//	 * @param index Index of a single value of a multi-valued property that is to be removed
//	 * @throws StructuralException if the given index is out of range or the Property is immutable
//	 */
//	public void remove(int index) throws StructuralException;
//	
	
}