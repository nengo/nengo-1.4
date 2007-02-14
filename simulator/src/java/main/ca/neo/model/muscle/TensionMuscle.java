/*
 * Created on 30-Nov-2006
 */
package ca.neo.model.muscle;

/**
 * <p>A linear muscle that produces tension. Inputs to the muscle are length (m) and lengthening 
 * velocity (m/s) of the muscle-tendon unit. Output is tension (N).</p>
 * 
 * <p>To run a simulation, call setInputs(...), then run(...), then getTension() with each time 
 * step.</p> 
 * 
 * @author Bryan Tripp
 */
public interface TensionMuscle extends SkeletalMuscle {

	/**
	 * @param length Length of the muscle-tendon (m)
	 * @param velocity Lengthening velocity of the muscle-tenson (m/s)
	 */
	public void setInputs(float length, float velocity);
	
	/**
	 * @return Instantaneous tension in the muscle (N)
	 */
	public float getTension();
}
