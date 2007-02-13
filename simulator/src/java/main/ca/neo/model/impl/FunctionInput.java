/*
 * Created on 6-Jun-2006
 */
package ca.neo.model.impl;

/**
 * An Origin that produces real-valued output based on functions of time. 
 * 
 * @author Bryan Tripp
 */
import java.util.Properties;

import ca.neo.math.Function;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.Probeable;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.util.TimeSeries;
import ca.neo.util.impl.TimeSeriesImpl;

public class FunctionInput implements Node, Origin, Probeable {

	private static final long serialVersionUID = 1L;	
	private static final String ourStateName = "input";
	
	private String myName;
	private Function[] myFunctions;
	private Units[] myUnits;
	private float myTime;
	private float[] myValues;
	
	/**
	 * @param name The name of this Origin
	 * @param function Functions of time (simulation time) that produce the values
	 * 		that will be output by this Origin. Each given function corresponds to 
	 * 		a dimension in the output vectors. Each function must have input dimension 1.
	 * @param units The units in which the output values are to be interpreted 
	 * @throws StructuralException 
	 */
	public FunctionInput(String name, Function[] functions, Units units) throws StructuralException {
		checkFunctionDimension(functions);
		
		myName = name;
		myFunctions = functions;
		
		//TODO: resolve conflict between single Units in InstantaneousOutput and multiple Units in TimeSeries 
		myUnits = new Units[functions.length];
		for (int i = 0; i < myUnits.length; i++) {
			myUnits[i] = units;
		}
		
		run(0f, 0f); //set initial state to f(0)
	}
	
	private static void checkFunctionDimension(Function[] functions) throws StructuralException {
		for (int i = 0; i < functions.length; i++) {
			if (functions[i].getDimension() != 1) {
				throw new StructuralException("All functions in a FunctionOrigin must be 1-D functions of time");
			}
		}
	}

	/**
	 * @see ca.neo.model.Origin#getName()
	 */
	public String getName() {
		return myName;
	}

	/**
	 * 
	 * @see ca.neo.model.Origin#getDimensions()
	 */
	public int getDimensions() {
		return myFunctions.length;
	}
	
	/**
	 * @see ca.neo.model.Origin#getValues()
	 */
	public InstantaneousOutput getValues() throws SimulationException {
		return new RealOutputImpl(myValues, myUnits[0]);
	}

	/**
	 * @see ca.neo.model.ExternalInput#run(float, float)
	 */
	public void run(float startTime, float endTime) {
		myTime = endTime;
		
		myValues = new float[myFunctions.length];		
		for (int i = 0; i < myValues.length; i++) {
			myValues[i] = myFunctions[i].map(new float[]{myTime});
		}
	}

	/**
	 * This method does nothing, as the FunctionInput has no state. 
	 * 
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
	}

	/**
	 * This call has no effect. DEFAULT mode is always used. 
	 *   
	 * @see ca.neo.model.Node#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
	}

	/**
	 * @return SimulationMode.DEFAULT
	 * 
	 * @see ca.neo.model.Node#getMode()
	 */
	public SimulationMode getMode() {
		return SimulationMode.DEFAULT;
	}

	/**
	 * @see ca.neo.model.Probeable#getHistory(java.lang.String)
	 */
	public TimeSeries getHistory(String stateName) throws SimulationException {
		TimeSeries result = null;
		
		if (ourStateName.equals(stateName)) {
			if (myValues == null) {
				result = new TimeSeriesImpl(new float[0], new float[0][], myUnits);				
			} else {
				result = new TimeSeriesImpl(new float[]{myTime}, new float[][]{myValues}, myUnits);				
			}
		} else {
			throw new SimulationException("State " + stateName + " is unknown");
		}
		
		return result;
	}

	/**
	 * @see ca.neo.model.Probeable#listStates()
	 */
	public Properties listStates() {
		Properties result = new Properties();
		result.setProperty(ourStateName, "Function of time");
		return result;
	}
	
}
