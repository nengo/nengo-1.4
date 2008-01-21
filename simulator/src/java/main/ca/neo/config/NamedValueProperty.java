/*
 * Created on 17-Jan-08
 */
package ca.neo.config;

import java.util.List;

import ca.neo.model.StructuralException;

/**
 * TODO: note getX(String) : Object and getX() : Object[] are an OK combination (as in Node) if getName() is there
 *  
 * @author Bryan Tripp
 */
public interface NamedValueProperty extends Property {
	
	public Object getValue(String name);
	
	public boolean isNamedAutomatically();
	
	public void setValue(String name, Object value) throws StructuralException;
	
	public void setValue(Object value) throws StructuralException;
	
	public void removeValue(String name) throws StructuralException;
	
	public List<String> getValueNames();
	
}
