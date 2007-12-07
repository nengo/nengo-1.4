package ca.neo.model;

/**
 * <p>An object with parameters that can be written and read in a standard way, through 
 * the Configuration interface. All of the variable parameters of a Configurable object 
 * must be accessible through this interface.</p> 
 * 
 * <p>A Configurable must have a zero-argument constructor.</p>
 * 
 * <p>The reasons for this interface are 1) to facilitate saving and loading of model elements, 
 * and 2) to simplify graphical user interfaces by allowing the configuration of all model elements 
 * through a common panel.</p>   
 * 
 * TODO: discuss importance of restrictions for robust serialization (not as much for UI) vs bean patterns
 * TODO: can this be used for UI undoable actions? (mixed with purely visual)
 * TODO: contrast with JAXB (generates Java classes from schema)
 *  
 * @author Bryan Tripp
 */
public interface Configurable {
	
	/**
	 * @return This Configurable's Configuration data 
	 */
	public Configuration getConfiguration();

}
