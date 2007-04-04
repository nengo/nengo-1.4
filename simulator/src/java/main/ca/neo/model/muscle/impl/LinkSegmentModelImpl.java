/**
 * 
 */
package ca.neo.model.muscle.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import ca.neo.dynamics.DynamicalSystem;
import ca.neo.math.Function;
import ca.neo.model.Origin;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.muscle.LinkSegmentModel;
import ca.neo.model.muscle.SkeletalMuscle;
import ca.neo.util.TimeSeries;
import ca.neo.util.impl.TimeSeriesImpl;

/**
 * Default implementation of LinkSegmentModel. 
 * 
 * TODO: origins and terminations
 * TODO: step implementation
 * 
 * @author Bryan Tripp
 */
public class LinkSegmentModelImpl implements LinkSegmentModel {

	private static final long serialVersionUID = 1L;

	private String myName;
	private DynamicalSystem myDynamics;
	private Map<String, Function[]> myJointDefinitions;
	private SkeletalMuscle[] myMuscles;
	private Properties myStates;
	private float myTimeStep;
	private float myTime;
	
	public LinkSegmentModelImpl(String name, DynamicalSystem dynamics, SkeletalMuscle[] muscles, float timeStep) {
		myDynamics = dynamics;
		myMuscles = muscles;
		myJointDefinitions = new HashMap<String, Function[]>(10);
		
		myStates = new Properties();
		for (int i = 0; i < dynamics.getState().length; i++) {
			myStates.setProperty("q" + i, "Generalized coordinate " + i);
		}
	}
	
	/**
	 * @param name Name of joint
	 * @param definition 2 or 3 Functions of generalized coordinates, corresponding to (x,y) position
	 * 		of the joint or (x,y,z) position of the joint
	 */
	public void defineJoint(String name, Function[] definition) {
		if (definition.length != 2 && definition.length != 3) { 
			throw new IllegalArgumentException("Either 2 or 3 functions of generalized coordinates " +
					"are needed to define a joint: (x,y) or (x.y,z)");
		}
		
		myJointDefinitions.put(name, definition);
		myStates.setProperty(name, "Joint coordinates for " + name);
	}
	
	/** 
	 * @see ca.neo.model.muscle.LinkSegmentModel#getJointNames()
	 */
	public String[] getJointNames() {
		return myJointDefinitions.keySet().toArray(new String[0]);
	}

	/**
	 * @see ca.neo.model.muscle.LinkSegmentModel#getMuscles()
	 */
	public SkeletalMuscle[] getMuscles() {
		return myMuscles;
	}

	/** 
	 * @see ca.neo.model.Node#getMode()
	 */
	public SimulationMode getMode() {
		return SimulationMode.DEFAULT;
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
		return null;
	}

	/** 
	 * @see ca.neo.model.Node#getOrigins()
	 */
	public Origin[] getOrigins() {
		return null;
	}

	/** 
	 * @see ca.neo.model.Node#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		return null;
	}

	/** 
	 * @see ca.neo.model.Node#getTerminations()
	 */
	public Termination[] getTerminations() {
		return null;
	}

	/** 
	 * @see ca.neo.model.Node#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {		
		myTime = startTime;
		
		while (myTime < endTime) {
			float stepLength = (myTime + myTimeStep * 1.1 >= endTime) ? endTime - myTime : myTimeStep;
			step(stepLength);
		}
		
		myTime = endTime;
	}
	
	private void step(float stepLength) {
		//TODO: run muscles, run dynamics
	}

	/** 
	 * @see ca.neo.model.Node#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
	}

	/** 
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		myDynamics.setState(new float[myDynamics.getState().length]);
	}

	/** 
	 * @see ca.neo.model.Probeable#getHistory(java.lang.String)
	 */
	public TimeSeries getHistory(String stateName) throws SimulationException {
		TimeSeries result = null;
		
		if (myJointDefinitions.containsKey(stateName)) {
			Function[] definition = myJointDefinitions.get(stateName);
			float[] jointCoordinates = new float[definition.length];
			float[] genCoordinates = myDynamics.getState();
			for (int i = 0; i < definition.length; i++) {
				jointCoordinates[i] = definition[i].map(genCoordinates);
			}
			result = new TimeSeriesImpl(new float[]{myTime}, 
					new float[][]{jointCoordinates}, Units.uniform(Units.M, definition.length));
		} else if (stateName.matches("p\\d+")) {
			//TODO
		} else {
			throw new SimulationException("The state " + stateName + " is unknown");
		}
		
		return result;
	}

	/** 
	 * @see ca.neo.model.Probeable#listStates()
	 */
	public Properties listStates() {
		return myStates;
	}

}
