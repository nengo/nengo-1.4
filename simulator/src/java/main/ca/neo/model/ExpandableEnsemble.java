/*
 * Created on May 18, 2006
 */
package ca.neo.model;

import ca.neo.model.neuron.Neuron;

/**
 * <p>An Ensemble to which Terminations can be added after construction, in a standard 
 * way. This makes it more convenient to construct circuits, particularly through a 
 * script or user interface. </p>
 * 
 * <p>Normally all of an ExpandableEnsemble's Neurons would have ExpandableSynapticIntegrators.
 * ExpandableEnsembles can also contain neurons with non-expandable SynapticIntegrators, but 
 * added Terminations will not apply to those Neurons.</p> 
 * 
 * @author Bryan Tripp
 */
public interface ExpandableEnsemble extends Ensemble {

	/**
	 * @return Neurons in the Ensemble that have ExpandableSynapticIntegrators
	 */
	public Neuron[] getExpandableNeurons();
	
	/**
	 * Adds a new Termination into this Ensemble. 
	 *  
	 * @param name Unique name for this Termination (in the scope of this Ensemble)
	 * @param weights Synaptic weights. Length must equal length of getExpandableNeurons(). 
	 * 		Each component must have length equal to the dimension of the Origin that will 
	 * 		connect to this Termination.   
	 * @param tauPSC Time constant of post-synaptic current decay (all Terminations have  
	 * 		this property but it may have slightly different interpretations depending on 
	 * 		the SynapticIntegrators in this Ensemble, or other properties of the 
	 * 		Termination)
	 * @return resulting Termination  
	 * @throws StructuralException if length of weights doesn't equal length of getExpandableNeurons(), 
	 * 		or if there are different numbers of weights given for different neurons.  
	 */
	public Termination addTermination(String name, float[][] weights, float tauPSC) throws StructuralException;
	
	/**
	 * @param name Name of Termination to remove. 
	 */
	public void removeTermination(String name);
	
}
