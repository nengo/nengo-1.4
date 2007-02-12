/*
 * Created on 30-Nov-2006
 */
package ca.neo.model.ms.impl;

import ca.neo.math.DifferentiableFunction;
import ca.neo.math.Function;
import ca.neo.model.SimulationException;
import ca.neo.model.ms.TensionMuscle;
import ca.neo.model.ms.TorqueMuscle;

/**
 * A TorqueMuscle that is based on an underlying TensionMuscle. 
 *  
 * @author Bryan Tripp
 */
public class TensionTorqueWrapper implements TorqueMuscle {

	private TensionMuscle myMuscle;
	private DifferentiableFunction myAngleLengthMapping;
	private Function myAngleMomentArmMapping; 
	private float myAngle;
	
	/**
	 * @param muscle Underlying TensionMuscle
	 * @param angleLengthMapping length=f(angle)
	 * @param angleMomentArmMapping arm=f(angle); <strong>negative moment arms for negative moments</strong>
	 */
	public TensionTorqueWrapper(TensionMuscle muscle, DifferentiableFunction angleLengthMapping, Function angleMomentArmMapping) {
		myMuscle = muscle;		
		myAngleLengthMapping = angleLengthMapping;
		myAngleMomentArmMapping = angleMomentArmMapping;
		myAngle = 0;
	}

	/**
	 * @see ca.neo.model.ms.SkeletalMuscle#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {
		myMuscle.run(startTime, endTime);
	}

	/**
	 * @see ca.neo.model.ms.SkeletalMuscle#setExcitation(float)
	 */
	public void setExcitation(float excitation) {
		myMuscle.setExcitation(excitation);
	}

	public void setInputs(float angle, float velocity) {
		float length = myAngleLengthMapping.map(new float[]{angle});
		float linearVelocity = myAngleLengthMapping.getDerivative().map(new float[]{angle}) * velocity; //dl(a)/dt = dl(a)/da * da/dt
		myMuscle.setInputs(length, linearVelocity);
		
		myAngle = angle;
	}

	/**
	 * @see ca.neo.model.ms.TorqueMuscle#getTorque()
	 */
	public float getTorque() {
		float tension = myMuscle.getTension();
		float momentArm = myAngleMomentArmMapping.map(new float[]{myAngle});
		return momentArm * tension;
	}

}
