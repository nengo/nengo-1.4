/**
 * 
 */
package ca.neo.model;

/**
 * A Node to which Terminations can be added after construction, in a standard manner. 
 * Note that a given Node might provide additional methods for adding Terminations if more  
 * customization is needed. 
 *  
 * @author Bryan Tripp
 */
public interface ExpandableNode extends Node {

	/**
	 * @return Output dimension of Terminations onto this Node
	 */
	public int getDimension();
	
	/**
	 * Adds a new Termination onto this Node. 
	 *  
	 * @param name Unique name for the Termination (in the scope of this Node)
	 * @param weights Connection weights. Length must equal getDimension(). Each component 
	 * 		must have length equal to the dimension of the Origin that will connect to this Termination.   
	 * @param tauPSC Time constant with which incoming signals are filtered. (All Terminations have  
	 * 		this property, but it may have slightly different interpretations per implementation.)
	 * @param modulatory If true, inputs to the Termination are not summed with other inputs (they 
	 * 		only have modulatory effects, eg on plasticity, which must be defined elsewhere).   
	 * @return resulting Termination  
	 * @throws StructuralException if length of weights doesn't equal getDimension(), 
	 * 		or if there are different numbers of weights given in different rows.  
	 */
	public Termination addTermination(String name, float[][] weights, float tauPSC, boolean modulatory) throws StructuralException;
	
	/**
	 * @param name Name of Termination to remove.
	 * @throws StructuralException 
	 */
	public void removeTermination(String name) throws StructuralException;
	
}
