package ca.neo.config;

import java.util.List;

import ca.neo.model.StructuralException;

/**
 * <p>Contains all the variable parameters of a Configurable object.</p> 
 * 
 * @author Bryan Tripp
 */
public interface Configuration {
	
	/**
	 * @return The Object to which this Configuration belongs
	 */
	public Object getConfigurable();

	/**
	 * @return Names of configuration properties 
	 */
	public List<String> getPropertyNames();
	
	/**
	 * @param name Name of a configuration property
	 * @return Parameter of the given name
	 * @throws StructuralException if the named property does not exist
	 */
	public Property getProperty(String name) throws StructuralException;
	
}
