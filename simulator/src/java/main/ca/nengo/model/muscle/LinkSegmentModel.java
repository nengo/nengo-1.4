/**
 * 
 */
package ca.nengo.model.muscle;

import ca.nengo.model.Node;
import ca.nengo.model.Probeable;

/**
 * TODO: javadocs 
 * 
 * @author Bryan Tripp
 */
public interface LinkSegmentModel extends Node, Probeable {

	public SkeletalMuscle[] getMuscles();
	
	public String[] getJointNames();
	
}
