/*
 * Created on 13-Feb-2007
 */
package ca.neo.model;

/**
 * Encapsulates certain properties of an object so that they can be accessed and 
 * modified in a standard way. These properties are typically of secondary importance, 
 * so that they don't warrant their own type-safe setters and getters. They should 
 * have reasonable defaults.   
 *  
 * @author Bryan Tripp
 */
public interface Configuration {

	/**
	 * @return List of property names for optional custom details of this Termination.
	 * 		The optional properties available depend on the Termination, but may 
	 * 		include things synaptic rise time, probability of vescicle release, etc.      
	 */	
	public String[] listPropertyNames();

	public Class getType(String name);
	
	/**
	 * @param name Name of property from listPropertyNames()
	 * @return Value of corresponding property (the Termination is responsible for providing 
	 * 		appropriate defaults for properties that have not been set by the user). 
	 */
	public Object getProperty(String name);

	/**
	 * Note that if a property is set on a Termination onto an Ensemble, then it is also 
	 * applied to the correponding Terminations onto each Neuron in the Ensemble. The same 
	 * property may be changed subsequently for individual Neurons. When a property is set 
	 * for an Ensemble Termination, all the Ensemble's Neurons must accept the property, 
	 * otherwise the change is rolled back, and an exception is thrown.
	 * 
	 * Note also that the above doesn't apply to <strong>decoded</strong> Terminations onto 
	 * NEFEnsembles. In this case the NEFEnsemble handles all details of the Termination 
	 * (including filtering inputs, etc.) so there is nothing to coordinate with member Neurons.  
	 * 
	 * @param name Name of property from listPropertyNames()
	 * @param value New property value (an exception may be thrown if the given 
	 * 		value does not make sense in the context of the specified property).
	 * @throws StructuralException if the named property is unknown or the given value can not be 
	 * 		interpreted in relation to the property  
	 */
	public void setProperty(String name, Object value) throws StructuralException;		
	
}
