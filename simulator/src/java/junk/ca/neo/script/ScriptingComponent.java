/*
 * Created on 12-Jun-2006
 */
package ca.neo.script;

/**
 * <p>A configurable part of a ScriptingNetwork (for example an ensemble, a 
 * projection, etc.) A component has a name, a type, and various properties, 
 * some of which can be changed.</p>    
 * 
 * TO DO: Ensemble properties: neuronType, dimension, etc. Connection properties: tauPSC, order, etc. 
 *   
 * @author Bryan Tripp
 */
public interface ScriptingComponent {

	/**
	 * Type code for external input components. 
	 */
	public static final String EXTERNAL_INPUT = "EXTERNAL_INPUT";

	/**
	 * Type code for ensemble components. 
	 */
	public static final String ENSEMBLE = "ENSEMBLE";

	/**
	 * Type code for output channel components. 
	 */
	public static final String OUTPUT_CHANNEL = "OUTPUT_CHANNEL";

	/**
	 * Type code for connection components. 
	 */
	public static final String CONNECTION = "CONNECTION";
	
	
	
	/**
	 * @return The user-defined name of this component
	 */
	public String getName();

	/**
	 * @return A code indicating the type of thing this component is. Possible 
	 * 		values are drawn from the static members of this class.  
	 */
	public String getType();
	
	/**
	 * @return Names of the properties of this component   
	 */
	public String[] listPropertyNames();
	
	/**
	 * @param name A property name
	 * @return The value of the named property 
	 * 
	 * @throws ScriptingException if the named property does not exist
	 */
	public String getProperty(String name) throws ScriptingException;

	/**
	 * Sets the named property to a new value, if possible. 
	 * 
	 * @param name Name of a property of this component
	 * @param value New value for the given property 
	 * 
	 * @throws ScriptingException if the named property does not exist or the given value 
	 * 		is invalid in the context of the property 
	 */
	public void setProperty(String name, String value) throws ScriptingException;	
	
}
