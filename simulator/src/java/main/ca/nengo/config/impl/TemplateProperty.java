/*
 * Created on 15-Jan-08
 */
package ca.nengo.config.impl;

import ca.nengo.config.Configuration;
import ca.nengo.config.SingleValuedProperty;
import ca.nengo.model.StructuralException;

/**
 * <p>A SingleValuedProperty that is not attached to getter/setter methods on an underlying class, but instead stores 
 * its value internally. It can be used to manage values of constructor/method arguments (rather than object properties).</p> 
 *      
 * @author Bryan Tripp
 */
public class TemplateProperty extends AbstractProperty implements SingleValuedProperty {

	private Object myValue;

	/**
	 * @param configuration Configuration to which this Property belongs
	 * @param name Name of the property 
	 * @param c Type of the property value
	 * @param defaultValue Default property value
	 */
	public TemplateProperty(Configuration configuration, String name, Class c, Object defaultValue) {
		super(configuration, name, c, true);
		myValue = defaultValue;
	}

	/**
	 * @see ca.nengo.config.Property#getValue()
	 */
	public Object getValue() {
		return myValue;
	}

	/**
	 * @see ca.nengo.config.Property#isFixedCardinality()
	 */
	public boolean isFixedCardinality() {
		return true;
	}

	/**
	 * @see ca.nengo.config.Property#setValue(java.lang.Object)
	 */
	public void setValue(Object value) throws StructuralException {
		checkClass(value);
		myValue = value;
	}

	private void checkClass(Object value) throws StructuralException {
		if (!getType().isAssignableFrom(value.getClass())) {
			throw new StructuralException("Value must be of type " + getType() 
					+ " (was " + value.getClass().getName() + ")");
		}
	}
	
}