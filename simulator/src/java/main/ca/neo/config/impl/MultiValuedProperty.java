/*
 * Created on 15-Jan-08
 */
package ca.neo.config.impl;

import ca.neo.config.Configuration;
import ca.neo.config.Property;
import ca.neo.model.StructuralException;

public abstract class MultiValuedProperty extends AbstractProperty implements Property {
	
	public MultiValuedProperty(Configuration configuration, String name, Class c, boolean mutable) {
		super(configuration, name, c, mutable);
	}

	/**
	 * @see ca.neo.config.Property#getNumValues()
	 */
	public abstract int getNumValues();

	/**
	 * @see ca.neo.config.Property#isMultiValued()
	 */
//	public boolean isMultiValued() {
//		return true;
//	}
	
	/**
	 * @see ca.neo.config.Property#isFixedCardinality()
	 */
	public boolean isFixedCardinality() {
		return false;
	}

	/**
	 * @see ca.neo.config.Property#getValue()
	 */
	public Object getValue() {
		try {
			return (getNumValues() > 0) ? getValue(0) : null;
		} catch (StructuralException e) {
			throw new RuntimeException("There are " + getNumValues() 
					+ " values but there was a problem returning the first (this may be a bug)", e);
		}
	}

	/**
	 * @see ca.neo.config.Property#getValue(int)
	 */
	public Object getValue(int index) throws StructuralException {
		try {
			return doGetValue(index);			 
		} catch (IndexOutOfBoundsException e) {
			throw new StructuralException("Value #" + index + " doesn't exist", e);
		}
	}
	
	public abstract Object doGetValue(int index) throws StructuralException;

	/**
	 * @see ca.neo.config.Property#addValue(java.lang.Object)
	 */
	public abstract void addValue(Object value) throws StructuralException;
	
	/**
	 * @see ca.neo.config.Property#insert(int, java.lang.Object)
	 */
	public void insert(int index, Object value) throws StructuralException {
		try {
			doInsert(index, value);				
		} catch (IndexOutOfBoundsException e) {
			throw new StructuralException("Value " + index + " doesn't exist", e);
		}
	}
	
	public abstract void doInsert(int index, Object value) throws IndexOutOfBoundsException, StructuralException;
	
	/**
	 * @see ca.neo.config.Property#remove(int)
	 */
	public void remove(int index) throws StructuralException {
		try {
			doRemove(index);				
		} catch (IndexOutOfBoundsException e) {
			throw new StructuralException("Value " + index + " doesn't exist", e);
		}
	}
	
	public abstract void doRemove(int index) throws IndexOutOfBoundsException, StructuralException;
	
	/**
	 * @see ca.neo.config.Property#setValue(java.lang.Object)
	 */
	public void setValue(Object value) throws StructuralException {
		setValue(0, value);
	}

	/**
	 * Calls doSetValue(...) and wraps any resulting {@link IndexOutOfBoundsException} in 
	 * a {@link StructuralException}. 
	 *  
	 * @see ca.neo.config.impl.MultiValuedProperty#setValue(int, java.lang.Object)
	 */
	public void setValue(int index, Object value) throws StructuralException {
		try {
			doSetValue(index, value);
		} catch (IndexOutOfBoundsException e) {
			throw new StructuralException("Value " + index + " doesn't exist", e);
		}
	}
	
	public abstract void doSetValue(int index, Object value) throws IndexOutOfBoundsException, StructuralException;
	
	/**
	 * Returns getValue() be default. 
	 */
	public Object getDefaultValue() {
		return getValue();
	}		
	
	
	
}