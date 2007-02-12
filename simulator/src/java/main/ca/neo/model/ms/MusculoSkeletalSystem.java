/*
 * Created on 25-Nov-2006
 */
package ca.neo.model.ms;

import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;

/**
 * <p>A mechanical dynamical system consisting of physical parts of a body, 
 * actuated by muscles.</p>
 * 
 * <p>Terminations correspond to neuromuscular junctions. Origins correspond to 
 * interfaces between sensory organs and afferent neurons.</p>
 * 
 * <p>Origins and terminations can be modelled at various levels of detail. For example a 
 * high-level termination might consist of a scalar activation level for a torque motor 
 * that abstracts all the muscles crossing a joint. An intermediate termination might consist
 * of an activation level for a particular muscle. A low-level termination might consist 
 * of individual motor units that accept spiking input. </p>
 * 
 * <p>Similarly, a high-level Origin might provide a scalar joint angle, whereas a low-level origin 
 * might output the length of the mid-section of an individual intrafusil fibre.</p>  
 *
 * TODO: move getOrigins() and getTerminations() to Node? 
 * 
 * @author Bryan Tripp
 */
public interface MusculoSkeletalSystem extends Node {

	/**
	 * @return Sets of ouput channels 
	 */
	public Origin[] getOrigins();
	
	/**
	 * @param name Name of an Origin from this system 
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
	 * @param name Name of a Termination onto this system
	 * @return The named Termination if it exists 
	 * @throws StructuralException if the named Termination does not exist
	 */
	public Termination getTermination(String name) throws StructuralException;
	
}
