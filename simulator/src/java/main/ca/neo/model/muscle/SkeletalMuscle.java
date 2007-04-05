/*
 * Created on 30-Nov-2006
 */
package ca.neo.model.muscle;

import ca.neo.model.Node;
import ca.neo.model.Probeable;

/**
 * <p>A model of a skeletal muscle or muscle group.</p>
 * 
 * @author Bryan Tripp
 */
public interface SkeletalMuscle extends Node, Probeable {

	public static final String GTO_ORIGIN = GolgiTendonOrgan.ORIGIN_NAME;
	public static final String DYNAMIC_SPINDLE_ORIGIN = MuscleSpindle.DYNAMIC_ORIGIN_NAME;
	public static final String STATIC_SPINDLE_ORIGIN = MuscleSpindle.STATIC_ORIGIN_NAME;
	
	public static final String EXCITATION_TERMINATION = "excitation"; 	
	
	public static final String ACTIVATION = "activation";
	public static final String FORCE = "force";
	public static final String LENGTH = "length";
	
	public void setLength(float length);
	
	public float getForce();
}
