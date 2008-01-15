/*
 * Created on 15-Jan-08
 */
package ca.neo.config.impl;

import ca.neo.config.Configuration;
import ca.neo.config.Property;
import ca.neo.model.StructuralException;

public abstract class FixedCardinalityProperty extends MultiValuedProperty implements Property {

	public FixedCardinalityProperty(Configuration configuration, String name, Class c, boolean mutable) {
		super(configuration, name, c, mutable);
	}
	
	/**
	 * @see ca.neo.config.Property#isFixedCardinality()
	 */
	public boolean isFixedCardinality() {
		return true;
	}


	@Override
	public void addValue(Object value) throws StructuralException {
		throw new StructuralException("Can't add a value after construction; "
				+ getName() + " has exactly " + getNumValues() + " values");
	}

	@Override
	public void doInsert(int index, Object value) throws StructuralException {
		throw new StructuralException("Can't insert a value after construction; "
				+ getName() + " has exactly " + getNumValues() + " values");
	}

	@Override
	public void doRemove(int index) throws StructuralException {
		throw new StructuralException("Can't remove a value after construction; "
				+ getName() + " has exactly " + getNumValues() + " values");
	}

	
}