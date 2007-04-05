/*
 * Created on 3-Apr-07
 */
package ca.neo.model.muscle.impl;

import java.util.Properties;

import ca.neo.dynamics.DynamicalSystem;
import ca.neo.dynamics.Integrator;
import ca.neo.dynamics.impl.EulerIntegrator;
import ca.neo.dynamics.impl.RK45Integrator;
import ca.neo.dynamics.impl.SimpleLTISystem;
import ca.neo.model.Origin;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.impl.BasicTermination;
import ca.neo.model.muscle.SkeletalMuscle;
import ca.neo.util.MU;
import ca.neo.util.TimeSeries;
import ca.neo.util.impl.TimeSeries1DImpl;
import ca.neo.util.impl.TimeSeriesImpl;

/**
 * Basic SkeletalMuscle implementation with unspecified activation-force dynamics.
 * 
 * TODO: origins (need spindle and GTO implementations)
 * 
 * @author Bryan Tripp
 */
public class SkeletalMuscleImpl implements SkeletalMuscle {

	private static final long serialVersionUID = 1L;
	
	private String myName;
	private BasicTermination myTermination;
	private DynamicalSystem myEADynamics; //excitation-activation dynamics
	private DynamicalSystem myAFDynamics; //activation-force dynamics
	private Integrator myIntegrator;
	private float myLength;
	
	private TimeSeries myActivationHistory; //saved for single timestep to support Probeable
	private TimeSeries myForceHistory;
	private TimeSeries myLengthHistory;
	
	public SkeletalMuscleImpl(String name, DynamicalSystem dynamics) throws StructuralException {
		myName = name;
		myTermination = makeTermination();
		
		if (dynamics.getInputDimension() != 2) throw new StructuralException("Input dimension of dynamics must be 2 (activation; length)");
		if (dynamics.getOutputDimension() != 1) throw new StructuralException("Output dimension of dynamics must be 1 (force)");
		myAFDynamics = dynamics;
		
		myIntegrator = new RK45Integrator();		
	}
	
	private BasicTermination makeTermination() {
		Units[] units = Units.uniform(Units.UNK, 1);
		DynamicalSystem myEADynamics = new SimpleLTISystem(new float[]{-1f/.005f}, MU.I(1), MU.I(1), new float[1], units) {
			private static final long serialVersionUID = 1L;

			//override to rectify excitation (can't have negative excitation to muscles)
			public float[] f(float t, float[] u) {
				u[0] = Math.abs(u[0]);
				return super.f(t, u);
			}
		};
		return new BasicTermination(myEADynamics, new EulerIntegrator(.001f), SkeletalMuscle.EXCITATION_TERMINATION);
	}
	
	/**
	 * @see ca.neo.model.Node#getMode()
	 */
	public SimulationMode getMode() {
		return SimulationMode.DEFAULT;
	}

	/**
	 * @see ca.neo.model.Node#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
		//only default is supported
	}

	/**
	 * @see ca.neo.model.Node#getName()
	 */
	public String getName() {
		return myName;
	}

	/**
	 * @see ca.neo.model.Node#getOrigin(java.lang.String)
	 */
	public Origin getOrigin(String name) throws StructuralException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see ca.neo.model.Node#getOrigins()
	 */
	public Origin[] getOrigins() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see ca.neo.model.Node#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		if (name.equals(SkeletalMuscle.EXCITATION_TERMINATION)) {
			return myTermination;
		} else {
			throw new StructuralException("Termination " + name + " does not exist");
		}
	}

	/**
	 * @see ca.neo.model.Node#getTerminations()
	 */
	public Termination[] getTerminations() {
		return new Termination[]{myTermination};
	}

	/**
	 * @see ca.neo.model.Node#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {
		myTermination.run(startTime, endTime);
		myActivationHistory = myTermination.getOutput();
		myLengthHistory = new TimeSeries1DImpl(new float[]{startTime}, new float[]{myLength}, Units.M);
		
		float[][] activation = myActivationHistory.getValues();
		float[][] input = new float[activation.length][];
		for (int i = 0; i < input.length; i++) {
			
		}
		myForceHistory = myIntegrator.integrate(myAFDynamics, new TimeSeriesImpl(myActivationHistory.getTimes(), input, new Units[]{Units.UNK, Units.M}));
	}

	/**
	 * @see ca.neo.model.muscle.SkeletalMuscle#getForce()
	 */
	public float getForce() {
		float[][] force = myForceHistory.getValues();
		return force[force.length][0];
	}

	/**
	 * @see ca.neo.model.muscle.SkeletalMuscle#setLength(float)
	 */
	public void setLength(float length) {
		myLength = length;
	}

	/**
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		myEADynamics.setState(new float[]{myEADynamics.getState().length});
		myAFDynamics.setState(new float[]{myAFDynamics.getState().length});
	}

	/**
	 * @see ca.neo.model.Probeable#getHistory(java.lang.String)
	 */
	public TimeSeries getHistory(String stateName) throws SimulationException {
		TimeSeries result = null;
		
		if (stateName.equals(SkeletalMuscle.ACTIVATION)) {
			result = myActivationHistory;
		} else if (stateName.equals(SkeletalMuscle.FORCE)) {
			result = myForceHistory;
		} else if (stateName.equals(SkeletalMuscle.LENGTH)) {
			result = myLengthHistory;
		}
		
		return result;
	}

	/**
	 * @see ca.neo.model.Probeable#listStates()
	 */
	public Properties listStates() {
		Properties result = new Properties();
		
		result.setProperty(SkeletalMuscle.ACTIVATION, "Muscle activation level (0 to 1)");
		result.setProperty(SkeletalMuscle.FORCE, "Tension in muscle (N)");
		result.setProperty(SkeletalMuscle.LENGTH, "Muscle length (m)");
		
		return result;
	}

}
