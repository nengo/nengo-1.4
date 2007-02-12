/*
 * Created on May 16, 2006
 */
package ca.neo.model;

import ca.neo.model.neuron.Neuron;
import ca.neo.model.neuron.SpikePattern;

/**
 * <p>A group of Neurons with largely overlapping inputs and outputs.</p>
 * 
 * <p>There are no strict rules for how to group Neurons into Ensembles, 
 * but some general guidelines follow: 
 * 
 * <ul><li>A group of neurons that together 'represent' something through 
 * a population code should be modelled as an Ensemble. Also consider using 
 * NEFEnsemble to make such representation explicit. </li>
 * 
 * <li>Making ensembles that correspond to physical structures (e.g. nuclei) 
 * and naming them appropriately will make the model clearer.</li> 
 * 
 * <li>Neuronal outputs from an ensemble are grouped together and passed to 
 * other ensembles during a simulation, and practical issues may arise 
 * from this. For example, putting all your neurons in a single large ensemble 
 * could result in a very large matrix of synaptic weights, which would impair 
 * performance. </li></ul>  
 * </p>
 * 
 * <p>The neuronal membership of an Ensemble is fixed once the Ensemble is created. 
 * This simplifies things (eg we don't have to handle Origins that change size over 
 * time, and may not fit in their Terminations) and makes things more efficient (we 
 * don't have to check for new Neurons every time step).</p>
 * 
 * <p>If you want to model Neuron death, you can deactivate dead Neurons or set all
 * their output weights to zero, instead of removing them from their Ensemble. If 
 * you want to model growth of new Neurons, you could have inactive Neurons as 
 * placeholders in an Ensemble, which you parameterize and activate as needed. The
 * need for more sophisticated growth and death models does not seem pressing at 
 * time of writing, but please contact us if you can make a case for it.</p> 
 * 
 * @author Bryan Tripp
 */
public interface Ensemble extends Node {

	/**
	 * @return Neurons that make up the Ensemble
	 */
	public Neuron[] getNeurons();
	
	/**
	 * @return Sets of ouput channels (eg spiking outputs, gap junctional outputs, etc.)
	 */
	public Origin[] getOrigins();
	
	/**
	 * @return A SpikePattern containing a record of spikes, provided collectSpikes(boolean) 
	 * 		has been set to true 
	 */
	public SpikePattern getSpikePattern();
	
	/**
 	 * @param collect If true, the spike pattern is recorded in subsequent runs and is available 
 	 * 		through getSpikePattern() (defaults to false)
	 */
	public void collectSpikes(boolean collect);
	
	/**
	 * @param name Name of an Origin on this Ensemble
	 * @return The named Origin if it exists
	 * @throws StructuralException if the named Origin does not exist
	 */
	public Origin getOrigin(String name) throws StructuralException;
	
	/**
	 * @return Sets of input channels (these have the same dimension as corresponding outputs 
	 * 		to which they are connected).  
	 */
	public Termination[] getTerminations();
	
	/**
	 * @param name Name of a Termination onto this Ensemble
	 * @return The named Termination if it exists 
	 * @throws StructuralException if the named Termination does not exist
	 */
	public Termination getTermination(String name) throws StructuralException;
	
}
