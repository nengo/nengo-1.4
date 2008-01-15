package ca.neo.config.impl;

import java.util.List;

import ca.neo.config.Configuration;
import ca.neo.config.Property;
import ca.neo.model.StructuralException;

/**
 * A multi-valued property based on an underlying List. 
 * 
 * @author Bryan Tripp
 */
public class ListBasedProperty extends MultiValuedProperty implements Property {

	private List myList;
	
	public ListBasedProperty(Configuration configuration, String name, Class c, List list) {
		super(configuration, name, c, true);
	}

	@Override
	public void addValue(Object value) throws StructuralException {
		myList.add(value);
	}

	@Override
	public Object doGetValue(int index) throws StructuralException {
		return myList.get(index);
	}

	@Override
	public void doInsert(int index, Object value) throws IndexOutOfBoundsException, StructuralException {
		myList.add(index, value);
	}

	@Override
	public void doRemove(int index) throws IndexOutOfBoundsException, StructuralException {
		myList.remove(index);
	}

	@Override
	public void doSetValue(int index, Object value) throws IndexOutOfBoundsException, StructuralException {
		myList.set(index, value);
	}

	@Override
	public int getNumValues() {
		return myList.size();
	}

	/**
	 * Returns getValue() be default. 
	 */
	public Object getDefaultValue() {
		return (myList.size() > 0) ? myList.get(0) : null;
	}		
	
	
}