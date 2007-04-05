/*
 * Created on 3-Apr-07
 */
package ca.neo.model.muscle;

import ca.neo.model.Node;

/**
 * A model of a golgi tendon organ receptor. A GTO is embedded in the muscle-tendon 
 * junction. It has a neural Origin consisting of a Ib afferent neuron, the activity 
 * of which reflects muscle tension.   
 * 
 * @author Bryan Tripp
 */
public interface GolgiTendonOrgan extends Node {
	
	public static final String ORIGIN_NAME = "Ib";

	public SkeletalMuscle getMuscle();
	
}
