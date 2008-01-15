package ca.neo.config;

import java.util.List;

import ca.neo.model.StructuralException;

/**
 * <p>Contains all the variable parameters of a Configurable object.</p> 
 * 
 * <p>Parameters can only be of the following classes:</p> 
 * 
 *  <ul>
 *  <li>Integer</li>
 *  <li>Float</li>
 *  <li>String</li>
 *  <li>Boolean</li>
 *  <li>float[]</li>
 *  <li>float[][]</li>
 *  <li>Configurable</li>
 *  <li>SimulationMode</li>
 *  <li>Units</li>
 *  </ul>
 *  
 *  TODO: should there be a mutableSize parameter to support vector length changes? (e.g. currents in DynamicalSystemSpikeGenerator)
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
	
	
	/**
	 * A listener of changes to a Configuration. 
	 *  
	 * @author Bryan Tripp
	 */
	public static interface Listener {

		/**
		 * @param event A description of the change. 
		 */
		public void configurationChange(Event event);		
	}
	
	
	
	/**
	 * A Configuration change event. 
	 * 
	 * @author Bryan Tripp
	 */
	public static interface Event {
		
		public enum Type { CHANGE, ADD, INSERT, REMOVE };

		/**
		 * @return The Configurable that has been changed
		 */
		public Configurable getConfigurable();
		
		/**
		 * @return The updated Property
		 */
		public Property getProperty();

		/**
		 * @return Type of operation performed
		 */
		public Type getEventType();
		
		/**
		 * @return Index of removed, added, inserted, or changed value (for multi-valued Properties)
		 */
		public int getLocationOfChange();
		
	}

}
