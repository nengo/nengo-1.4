/*
 * Created on 15-Jan-08
 */
package ca.neo.config.impl;

import java.util.ArrayList;
import java.util.List;

import ca.neo.config.Configuration;
import ca.neo.config.ListProperty;
import ca.neo.model.StructuralException;

public class TemplateArrayProperty extends AbstractProperty implements ListProperty {

	private List<Object> myValues;
	
	public TemplateArrayProperty(Configuration configuration, String name, Class c) {
		super(configuration, name, c, true);
		myValues = new ArrayList<Object>(10);
	}

	/**
	 * @see ca.neo.config.ListProperty#addValue(java.lang.Object)
	 */
	public void addValue(Object value) throws StructuralException {
		checkClass(value);
		myValues.add(value);
	}

	/**
	 * @see ca.neo.config.ListProperty#getNumValues()
	 */
	public int getNumValues() {
		return myValues.size();
	}

	/**
	 * @see ca.neo.config.ListProperty#getValue(int)
	 */
	public Object getValue(int index) throws StructuralException {
		try {
			return myValues.get(index);
		} catch (IndexOutOfBoundsException e) {
			throw new StructuralException("Value " + index + " doesn't exist", e);
		}
	}

	/**
	 * @see ca.neo.config.ListProperty#insert(int, java.lang.Object)
	 */
	public void insert(int index, Object value) throws StructuralException {
		checkClass(value);
		try {
			myValues.add(value);					
		} catch (IndexOutOfBoundsException e) {
			throw new StructuralException("Value " + index + " doesn't exist", e);
		}
	}

	/**
	 * @see ca.neo.config.Property#isFixedCardinality()
	 */
	public boolean isFixedCardinality() {
		return false;
	}

	/**
	 * @see ca.neo.config.ListProperty#remove(int)
	 */
	public void remove(int index) throws StructuralException {
		try {
			myValues.remove(index);					
		} catch (IndexOutOfBoundsException e) {
			throw new StructuralException("Value " + index + " doesn't exist", e);
		}
	}

	/**
	 * @see ca.neo.config.ListProperty#setValue(int, java.lang.Object)
	 */
	public void setValue(int index, Object value) throws StructuralException {
		checkClass(value);
		try {
			myValues.set(index, value);					
		} catch (IndexOutOfBoundsException e) {
			throw new StructuralException("Value " + index + " doesn't exist", e);
		}
	}
	
	private void checkClass(Object value) throws StructuralException {
		if (!getType().isAssignableFrom(value.getClass())) {
			throw new StructuralException("Value must be of type " + getType() 
					+ " (was " + value.getClass().getName() + ")");
		}
	}

	/**
	 * @see ca.neo.config.ListProperty#getDefaultValue()
	 */
	public Object getDefaultValue() {
		return (myValues.size() > 0) ? myValues.get(0) : null;
	}		
	
}