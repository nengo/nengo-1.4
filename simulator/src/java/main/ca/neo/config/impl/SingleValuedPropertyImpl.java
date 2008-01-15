package ca.neo.config.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ca.neo.config.Configuration;
import ca.neo.config.SingleValuedProperty;
import ca.neo.model.StructuralException;

/**
 * Default implementation of single-valued Properties. 
 * 
 * @author Bryan Tripp
 */
public class SingleValuedPropertyImpl extends AbstractProperty implements SingleValuedProperty {
	
	public SingleValuedPropertyImpl(Configuration configuration, String name, Class c, boolean mutable) {
		super(configuration, name, c, mutable);
	}

	/**
	 * @see ca.neo.config.Property#addValue(java.lang.Object)
	 */
//	public void addValue(Object value) throws StructuralException {
//		throw new StructuralException("Can't add a value to the single-valued property " + getName());
//	}
	
	/**
	 * @see ca.neo.config.Property#getNumValues()
	 */
//	public int getNumValues() {
//		return 1;
//	}

	/**
	 * @see ca.neo.config.Property#getValue()
	 */
	public Object getValue() {
		Object result = null;
		
		Object o = getConfiguration().getConfigurable();
		String methodName = "get" + Character.toUpperCase(getName().charAt(0)) + getName().substring(1);
		
		try {
			Method method = o.getClass().getMethod(methodName, new Class[0]);
			result = method.invoke(o, new Object[0]);
		} catch (Exception e) {
			throw new RuntimeException("Can't get property", e);
		}
		
		return result; 
	}

	/**
	 * @see ca.neo.config.Property#getValue(int)
	 */
//	public Object getValue(int index) throws StructuralException {
//		if (index == 0) {
//			return getValue();
//		} else {
//			throw new StructuralException("Value " + index + " of the single-valued property " + getName() + " does not exist");
//		}
//	}

	/**
	 * @see ca.neo.config.Property#insert(int, java.lang.Object)
	 */
//	public void insert(int index, Object value) throws StructuralException {
//		throw new StructuralException("Can't add a value to the single-valued property " + getName());
//	}

	/**
	 * @see ca.neo.config.Property#isMultiValued()
	 */
//	public boolean isMultiValued() {
//		return false;
//	}
	
	/**
	 * @see ca.neo.config.Property#isFixedCardinality()
	 */
	public boolean isFixedCardinality() {
		return true;
	}

	/**
	 * @see ca.neo.config.Property#remove(int)
	 */
//	public void remove(int index) throws StructuralException {
//		throw new StructuralException("Can't remove a value from the single-valued property " + getName());
//	}

	/**
	 * By default, attempts to call method setX(y) on Configurable, where X is the name of the property (with 
	 * first letter capitalized) and y is the value (changed to a primitive if it's a primitive wrapper).
	 * A Configurable that needs different behaviour should override this method.   
	 *  
	 * @see ca.neo.config.Property#setValue(java.lang.Object)
	 */
	public void setValue(Object value) throws StructuralException {
		if (getType().isAssignableFrom(value.getClass())) {
			Object o = getConfiguration().getConfigurable();
			String methodName = "set" + Character.toUpperCase(getName().charAt(0)) + getName().substring(1);
			Class argClass = getType();
			//TODO: use handlers? support other primitives?
			if (argClass.equals(Integer.class)) {
				argClass = Integer.TYPE;
			} else if (argClass.equals(Float.class)) {
				argClass = Float.TYPE;					
			} else if (argClass.equals(Boolean.class)) {
				argClass = Boolean.TYPE;					
			}
			
			try {
				Method method = o.getClass().getMethod(methodName, new Class[]{argClass});
				method.invoke(o, value);
			} catch (Exception e) {
				Throwable t = e;
				if (t instanceof InvocationTargetException) {
					t = ((InvocationTargetException) t).getCause();
				}
				throw new StructuralException("Can't change property: " + t.getMessage(), t);
			}
		} else {
			throw new StructuralException("Value must be of class " + getType().getName());
		}
	}
	
	/**
	 * @see ca.neo.config.Property#setValue(int, java.lang.Object)
	 */
//	public void setValue(int index, Object value) throws StructuralException {
//		if (index == 0) {
//			setValue(value);
//		} else {
//			throw new StructuralException("Can't set value " + index + " of the single-valued property " + getName());
//		}
//	}
	
	/**
	 * Returns getValue() be default. 
	 */
//	public Object getDefaultValue() {
//		return getValue();
//	}		
	
}