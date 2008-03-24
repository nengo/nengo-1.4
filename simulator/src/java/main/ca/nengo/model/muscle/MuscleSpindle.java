/*
 * Created on 3-Apr-07
 */
package ca.nengo.model.muscle;

import ca.nengo.model.Node;

/**
 * A model of a muscle spindle receptor. A muscle spindle is embedded in 
 * a skeletal muscle, and has both efferent and afferent innervation. It 
 * receives excitatory drive from gamma motor neurons, which parallels the 
 * excitation of the surrounding muscle. It has two neural Origins which 
 * provide different information about stretch dynamics. 
 *     
 * @author Bryan Tripp
 */
public interface MuscleSpindle extends Node {
	
	public static final String DYNAMIC_ORIGIN_NAME = "Ia";
	public static final String STATIC_ORIGIN_NAME = "II";

	public SkeletalMuscle getMuscle();
		
}
