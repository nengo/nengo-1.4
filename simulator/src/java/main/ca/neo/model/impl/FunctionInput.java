/*
 * Created on 6-Jun-2006
 */
package ca.neo.model.impl;

/**
 * An Node that produces real-valued output based on functions of time. 
 * 
 * @author Bryan Tripp
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ca.neo.math.Function;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.Probeable;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.util.TimeSeries;
import ca.neo.util.VisiblyMutable;
import ca.neo.util.VisiblyMutableUtils;
import ca.neo.util.impl.TimeSeriesImpl;

public class FunctionInput implements Node, Probeable {

	public static final String ORIGIN_NAME = "origin";
	public static final String STATE_NAME = "input";
	
	private static final long serialVersionUID = 1L;
	
	private String myName;
	private Function[] myFunctions;
	private Units myUnits;
	private float myTime;
//	private float[] myValues;
	private BasicOrigin myOrigin;
	private String myDocumentation;
	private transient List<VisiblyMutable.Listener> myListeners;
	
	/**
	 * @param name The name of this Node
	 * @param functions Functions of time (simulation time) that produce the values
	 * 		that will be output by this Node. Each given function corresponds to 
	 * 		a dimension in the output vectors. Each function must have input dimension 1.
	 * @param units The units in which the output values are to be interpreted 
	 * @throws StructuralException 
	 */
	public FunctionInput(String name, Function[] functions, Units units) throws StructuralException {
		myOrigin = new BasicOrigin(this, FunctionInput.ORIGIN_NAME, functions.length, units);		
		setFunctions(functions);
		
		myName = name;
		myUnits = units;
		
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
	 * @param functions New list of functions (of simulation time) that define the output of this Node. 
	 * 		(Must have the same length as existing Function list.)  
	 * @throws StructuralException 
	 */
	public void setFunctions(Function[] functions) throws StructuralException {
		checkFunctionDimension(functions);
		myOrigin.setDimensions(functions.length);
		myFunctions = functions;
	}

	/**
	 * @return array of functions
	 */
	public Function[] getFunctions() {
		return myFunctions;
	}
	
	/**
	 * @see ca.neo.model.Node#getName()
	 */
	public String getName() {
		return myName;
	}
	
	/**
	 * @param name The new name
	 */
	public void setName(String name) throws StructuralException {
		VisiblyMutableUtils.nameChanged(this, getName(), name, myListeners);
		myName = name;
	}

	/**
	 * @see ca.neo.model.Node#run(float, float)
	 */
	public void run(float startTime, float endTime) {
		myTime = endTime;
		
		float[] values = new float[myFunctions.length];		
		for (int i = 0; i < values.length; i++) {
			values[i] = myFunctions[i].map(new float[]{myTime});
		}
		
		myOrigin.setValues(startTime, endTime, values);
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
		
		if (!STATE_NAME.equals(stateName)) {
			throw new SimulationException("State " + stateName + " is unknown");
		}

		float[] values = ((RealOutput) myOrigin.getValues()).getValues(); 
		result = new TimeSeriesImpl(new float[]{myTime}, new float[][]{values}, Units.uniform(myUnits, values.length));
		
		return result;
	}

	/**
	 * @see ca.neo.model.Probeable#listStates()
	 */
	public Properties listStates() {
		Properties result = new Properties();
		result.setProperty(STATE_NAME, "Function of time");
		return result;
	}
	
	/** 
	 * @see ca.neo.model.Node#getOrigin(java.lang.String)
	 */
	public Origin getOrigin(String name) throws StructuralException {
		if (!ORIGIN_NAME.equals(name)) {
			throw new StructuralException("This Node only has origin FunctionInput.ORIGIN_NAME");
		}
		
		return myOrigin;
	}

	/** 
	 * @see ca.neo.model.Node#getOrigins()
	 */
	public Origin[] getOrigins() {
		return new Origin[]{myOrigin};
	}

	/**
	 * @see ca.neo.model.Node#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		throw new StructuralException("This node has no Terminations");		
	}

	/**
	 * @see ca.neo.model.Node#getTerminations()
	 */
	public Termination[] getTerminations() {
		return new Termination[0];
	}

	/**
	 * @see ca.neo.model.Node#getDocumentation()
	 */
	public String getDocumentation() {
		return myDocumentation;
	}

	/**
	 * @see ca.neo.model.Node#setDocumentation(java.lang.String)
	 */
	public void setDocumentation(String text) {
		myDocumentation = text;
	}

	/**
	 * @see ca.neo.util.VisiblyMutable#addChangeListener(ca.neo.util.VisiblyMutable.Listener)
	 */
	public void addChangeListener(Listener listener) {
		if (myListeners == null) {
			myListeners = new ArrayList<Listener>(2);
		}
		myListeners.add(listener);
	}

	/**
	 * @see ca.neo.util.VisiblyMutable#removeChangeListener(ca.neo.util.VisiblyMutable.Listener)
	 */
	public void removeChangeListener(Listener listener) {
		myListeners.remove(listener);
	}

	@Override
	public Node clone() throws CloneNotSupportedException {
		FunctionInput result = (FunctionInput) super.clone();
		
		Function[] functions = new Function[myFunctions.length];
		for (int i = 0; i < functions.length; i++) {
			functions[i] = myFunctions[i].clone();
		}
		result.myFunctions = functions;
		
		result.myOrigin = new BasicOrigin(result, FunctionInput.ORIGIN_NAME, functions.length, myUnits);
		if (myOrigin.getNoise() != null) result.myOrigin.setNoise(myOrigin.getNoise().clone());
		try {
			result.myOrigin.setValues(myOrigin.getValues());
		} catch (SimulationException e) {
			throw new CloneNotSupportedException("Problem copying origin values: " + e.getMessage());
		}
		
		result.myListeners = new ArrayList<Listener>(5);
		
		return result;
	}
	
}
