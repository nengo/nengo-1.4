package ca.nengo.config;

/**
 * An element of a Configuration; wraps some property of an object (eg a bean-pattern property). 
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
	 * @return True if the property has a fixed number of values 
	 */
	public boolean isFixedCardinality();

	/**
	 * @return Text describing the property semantics (plain text or HTML)
	 */
	public String getDocumentation();
	
}