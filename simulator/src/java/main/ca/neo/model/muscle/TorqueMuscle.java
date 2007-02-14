/*
 * Created on 30-Nov-2006
 */
package ca.neo.model.muscle;

/**
 * <p>An abstraction of a muscle/group that produces torque at a hinge joint instead of tension 
 * across it. Inputs to the muscle are length (m) and lengthening velocity (m/s) of the 
 * muscle-tendon unit. Output is tension (N).</p>
 *   
 * @author Bryan Tripp
 */
public interface TorqueMuscle extends SkeletalMuscle {
	
	/**
	 * @param angle Relative angle between two segments, in the plane of action of the muscle (rad)
	 * @param velocity Rate at which relative angle is increasing (rad/s)
	 */
	public void setInputs(float angle, float velocity);
	
	/**
	 * @return Instantaneous torque produced by the muscle (N)
	 */
	public float getTorque();
	
}
