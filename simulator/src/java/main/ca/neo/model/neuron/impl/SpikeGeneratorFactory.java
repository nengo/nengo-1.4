/**
 * 
 */
package ca.neo.model.neuron.impl;

import java.io.Serializable;

import ca.neo.model.neuron.SpikeGenerator;

/**
 * Creates SpikeGenerators. Implementations should have a zero-arg
 * constructor that parameterizes the factory with defaults, and accessor
 * methods for changing these parameters as appropriate.  
 * 
 * @author Bryan Tripp
 */
public interface SpikeGeneratorFactory extends Serializable {

	public SpikeGenerator make();
}
