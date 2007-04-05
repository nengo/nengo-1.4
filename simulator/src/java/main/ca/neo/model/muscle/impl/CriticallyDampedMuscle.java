/*
 * Created on 4-Apr-07
 */
package ca.neo.model.muscle.impl;

import ca.neo.dynamics.DynamicalSystem;
import ca.neo.dynamics.impl.LTISystem;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;

/**
 * <p>A simple, phenomenological muscle model in which activation-force dynamics are 
 * modelled with a linear 2nd-order low-pass filter (see e.g. Winter, 1990, 
 * Biomechanics and Motor Control of Human Movement).</p>
 * 
 * <p>This type of model is most viable in isometric conditions.</p>
 *   
 * @author Bryan Tripp
 */
public class CriticallyDampedMuscle extends SkeletalMuscleImpl {

	private static final long serialVersionUID = 1L;
	
	/**
	 * @param name Name of muscle 
	 * @param cutoff Cutoff frequency of filter model (Hz)
	 * @throws StructuralException
	 */
	public CriticallyDampedMuscle(String name, float cutoff, float maxForce) throws StructuralException {
		super(name, makeDynamics(cutoff, maxForce));
	}

	//returns a Butterworth filter with requested cutoff (Hz)
	private static DynamicalSystem makeDynamics(float cutoff, float maxForce) {
		float wc = 2 * (float) Math.PI * cutoff;
		float[][] A = new float[][]{new float[]{0, 1}, new float[]{-wc*wc, - (float) Math.sqrt(2) * wc}};
		float[][] B = new float[][]{new float[]{0}, new float[]{wc*wc}};
		float[][] C = new float[][]{new float[]{maxForce, 0}};
		float[][] D = new float[][]{new float[]{0, 0}};
		return new LTISystem(A, B, C, D, new float[]{0, 0}, new Units[]{Units.N});
	}

}
