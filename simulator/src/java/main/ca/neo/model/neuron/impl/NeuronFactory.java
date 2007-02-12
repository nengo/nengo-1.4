/*
 * Created on 21-Jun-2006
 */
package ca.neo.model.neuron.impl;

import ca.neo.model.StructuralException;
import ca.neo.model.neuron.Neuron;

/**
 * Produces Neurons. This interface does not define rules as to how the Neurons are parameterized, 
 * but a given implementation might use parameters that are constant across neurons, or drawn 
 * from a PDF, or selected from a database, etc.    
 * 
 * @author Bryan Tripp
 */
public interface NeuronFactory {

	/**
	 * @return A new Neuron 
	 * @throws StructuralException for any problem that prevents creation of a valid neuron model 
	 * 		(eg invalid probabilistic parameters generated)   
	 */
	public Neuron make() throws StructuralException;
	
}
