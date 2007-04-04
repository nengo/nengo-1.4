/**
 * 
 */
package ca.neo.model.muscle;

import ca.neo.model.Node;
import ca.neo.model.Probeable;

/**
 * TODO: javadocs 
 * 
 * @author Bryan Tripp
 */
public interface LinkSegmentModel extends Node, Probeable {

	public SkeletalMuscle[] getMuscles();
	
	public String[] getJointNames();
	
}
