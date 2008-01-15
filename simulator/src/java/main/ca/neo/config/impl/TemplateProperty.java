/*
 * Created on 15-Jan-08
 */
package ca.neo.config.impl;

//import java.util.ArrayList;
//import java.util.List;

import ca.neo.config.Configuration;
import ca.neo.config.SingleValuedProperty;
import ca.neo.model.StructuralException;

public class TemplateProperty extends AbstractProperty implements SingleValuedProperty {

//	private boolean myMultiValued;
//	private boolean myFixedCardinality;
//	private List<Object> myValues;
	private Object myValue;
	
//	public TemplateProperty(Configuration configuration, String name, Class c, boolean multiValued, boolean fixedCardinality) {
//		super(configuration, name, c, true);
//		
//		myMultiValued = multiValued;
//		myFixedCardinality = fixedCardinality;
//		myValues = new ArrayList<Object>(10);
//	}
//	
	/**
	 * A shortcut for creating a single-valued template property with a default value. 
	 *     
	 * @param configuration
	 * @param name
	 * @param c
	 * @param defaultValue
	 */
	public TemplateProperty(Configuration configuration, String name, Class c, Object defaultValue) {
		super(configuration, name, c, true);
		myValue = defaultValue;
//		myMultiValued = false;
//		myFixedCardinality = true;
//		myValues = new ArrayList<Object>(10);
//		myValues.add(defaultValue);
	}

	/**
	 * @see ca.neo.config.Property#addValue(java.lang.Object)
	 */
//	public void addValue(Object value) throws StructuralException {
//		if (myMultiValued && !myFixedCardinality) {
//			checkClass(value);
//			myValues.add(value);
//		}
//	}

	/**
	 * @see ca.neo.config.Property#getNumValues()
	 */
//	public int getNumValues() {
//		return myValues.size();
//	}

	/**
	 * @see ca.neo.config.Property#getValue()
	 */
	public Object getValue() {
		return myValue;
	}

	/**
	 * @see ca.neo.config.Property#getValue(int)
	 */
//	public Object getValue(int index) throws StructuralException {
//		try {
//			return myValues.get(index);
//		} catch (IndexOutOfBoundsException e) {
//			throw new StructuralException("Value " + index + " doesn't exist", e);
//		}
//	}

	/**
	 * @see ca.neo.config.Property#insert(int, java.lang.Object)
	 */
//	public void insert(int index, Object value) throws StructuralException {
//		if (myMultiValued && !myFixedCardinality) {
//			checkClass(value);
//			try {
//				myValues.add(value);					
//			} catch (IndexOutOfBoundsException e) {
//				throw new StructuralException("Value " + index + " doesn't exist", e);
//			}
//		}			
//	}

	/**
	 * @see ca.neo.config.Property#isFixedCardinality()
	 */
	public boolean isFixedCardinality() {
		return true;
	}

	/**
	 * @see ca.neo.config.Property#isMultiValued()
	 */
//	public boolean isMultiValued() {
//		return myMultiValued;
//	}

	/**
	 * @see ca.neo.config.Property#remove(int)
	 */
//	public void remove(int index) throws StructuralException {
//		if (myMultiValued && !myFixedCardinality) {
//			try {
//				myValues.remove(index);					
//			} catch (IndexOutOfBoundsException e) {
//				throw new StructuralException("Value " + index + " doesn't exist", e);
//			}
//		}			
//	}

	/**
	 * @see ca.neo.config.Property#setValue(java.lang.Object)
	 */
	public void setValue(Object value) throws StructuralException {
		checkClass(value);
//		myValues.set(0, value);
		myValue = value;
	}

	/**
	 * @see ca.neo.config.Property#setValue(int, java.lang.Object)
	 */
//	public void setValue(int index, Object value) throws StructuralException {
//		checkClass(value);
//		try {
//			myValues.set(index, value);					
//		} catch (IndexOutOfBoundsException e) {
//			throw new StructuralException("Value " + index + " doesn't exist", e);
//		}
//	}
	
	private void checkClass(Object value) throws StructuralException {
		if (!getType().isAssignableFrom(value.getClass())) {
			throw new StructuralException("Value must be of type " + getType() 
					+ " (was " + value.getClass().getName() + ")");
		}
	}
	
	/**
	 * Returns getValue() be default. 
	 */
//	public Object getDefaultValue() {
//		return (myValues.size() > 0) ? myValues.get(0) : null;
//	}		
	
}