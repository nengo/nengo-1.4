/*
 * Created on 15-Jan-08
 */
package ca.nengo.config;

import ca.nengo.model.StructuralException;

/**
 * A Property that can have multiple values, each of which is identified by an integer index. 
 * 
 * @author Bryan Tripp
 */
public interface ListProperty extends Property {

	/**
	 * @param index Index of a certain single value of a multi-valued property 
	 * @return The value at the given index
	 * @throws StructuralException if the given index is out of range
	 */
	public Object getValue(int index) throws StructuralException;
	
	/**
	 * @param index Index of a certain single value of a multi-valued property 
	 * @param value New value to replace that at the given index 
	 * @throws StructuralException if the value is invalid (as in setValue) or the given index is 
	 * 		out of range or the Property is immutable
	 */
	public void setValue(int index, Object value) throws StructuralException;

	/**
	 * @param value New value to be added to the end of the list 
	 * @throws StructuralException if the value is invalid (as in setValue) or the Property is 
	 * 		immutable or fixed-cardinality 
	 */
	public void addValue(Object value) throws StructuralException;
	
	/**
	 * @return Number of repeated values of this Property
	 */
	public int getNumValues();
	
	/**
	 * @param index Index at which new value is to be inserted  
	 * @param value New value
	 * @throws StructuralException if the value is invalid (as in setValue) or the Property is 
	 * 		immutable or fixed-cardinality or the index is out of range 
	 */
	public void insert(int index, Object value) throws StructuralException;
	
	/**
	 * @param index Index of a single value of a multi-valued property that is to be removed
	 * @throws StructuralException if the given index is out of range or the Property is immutable or fixed cardinality
	 */
	public void remove(int index) throws StructuralException;
	
	/**
	 * @return Default value for insertions
	 * TODO: remove; use default from NewConfigurableDialog (move to ConfigUtil)
	 */
	public Object getDefaultValue(); 

}
