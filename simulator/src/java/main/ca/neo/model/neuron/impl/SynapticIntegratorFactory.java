/**
 * 
 */
package ca.neo.model.neuron.impl;

import java.io.Serializable;

import ca.neo.model.neuron.SynapticIntegrator;

/**
 * Creates SynapticIntegrators. Implementations should have a zero-arg
 * constructor that parameterizes the factory with defaults, and accessor
 * methods for changing these parameters as appropriate.  
 * 
 * @author Bryan Tripp
 */
public interface SynapticIntegratorFactory extends Serializable {

	public SynapticIntegrator make();
	
}
