/*
 * Created on 3-Apr-07
 */
package ca.neo.model.impl;

import org.apache.log4j.Logger;

import ca.neo.dynamics.DynamicalSystem;
import ca.neo.dynamics.Integrator;
import ca.neo.dynamics.impl.CanonicalModel;
import ca.neo.dynamics.impl.LTISystem;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Node;
import ca.neo.model.RealOutput;
import ca.neo.model.Resettable;
import ca.neo.model.SimulationException;
import ca.neo.model.SpikeOutput;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.util.TimeSeries;
import ca.neo.util.impl.TimeSeriesImpl;

/**
 * A basic implementation of Termination with configurable dynamics and no special 
 * integrative features.
 *    
 * @author Bryan Tripp
 */
public class BasicTermination implements Termination, Resettable {

	private static final long serialVersionUID = 1L;
	
	private static Logger ourLogger = Logger.getLogger(BasicTermination.class);
	
	private Node myNode;
	private DynamicalSystem myDynamics;
	private Integrator myIntegrator;
	private String myName;
	private InstantaneousOutput myInput;
	public TimeSeries myOutput;
	public boolean myModulatory;
	
	public BasicTermination(Node node, DynamicalSystem dynamics, Integrator integrator, String name) {
		myNode = node;
		myDynamics = dynamics;
		myIntegrator = integrator;
		myName = name;
		myModulatory = false;
	}
	
	/**
	 * @see ca.neo.model.Termination#getDimensions()
	 */
	public int getDimensions() {
		return myDynamics.getInputDimension();
	}

	/**
	 * @see ca.neo.model.Termination#getName()
	 */
	public String getName() {
		return myName;
	}

	/**
	 * @see ca.neo.model.Termination#setValues(ca.neo.model.InstantaneousOutput)
	 */
	public void setValues(InstantaneousOutput values) throws SimulationException {
		myInput = values;
	}
	
	/**
	 * Runs the Termination, making a TimeSeries of output from this Termination 
	 * available from getOutput().     
	 * 
	 * @param startTime simulation time at which running starts (s)
	 * @param endTime simulation time at which running ends (s)
	 * @throws SimulationException if a problem is encountered while trying to run 
	 */
	public void run(float startTime, float endTime) throws SimulationException {
		float[] input = null;
		if (myInput instanceof RealOutput) {
			input = ((RealOutput) myInput).getValues();
		} else if (myInput instanceof SpikeOutput) {
			boolean[] spikes = ((SpikeOutput) myInput).getValues();
			input = new float[spikes.length];
			float amplitude = 1f / (endTime - startTime);
			for (int i = 0; i < spikes.length; i++) {
				if (spikes[i]) input[i] = amplitude;
			}
		}
		
		TimeSeries inSeries = new TimeSeriesImpl(new float[]{startTime, endTime}, new float[][]{input, input}, Units.uniform(Units.UNK, input.length)); 
		myOutput = myIntegrator.integrate(myDynamics, inSeries);
	}

	/**
	 * Note: typically called by the Node to which the Termination belongs. 
	 *  
	 * @return The most recent input multiplied  
	 */
	public TimeSeries getOutput() {
		return myOutput;
	}

	/**
	 * @see ca.neo.model.Termination#getNode()
	 */
	public Node getNode() {
		return myNode;
	}

	/**
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		myInput = null;
	}

	/**
	 * @see ca.neo.model.Termination#getModulatory()
	 */
	public boolean getModulatory() {
		return myModulatory;
	}

	/**
	 * @see ca.neo.model.Termination#getTau()
	 */
	public float getTau() {
		if (myDynamics instanceof LTISystem) {
			return CanonicalModel.getDominantTimeConstant((LTISystem) myDynamics);
		} else {
			ourLogger.warn("Can't get time constant for non-LTI dynamics. Returning 0.");
			return 0;
		}
	}

	/**
	 * @see ca.neo.model.Termination#setModulatory(boolean)
	 */
	public void setModulatory(boolean modulatory) {
		myModulatory = modulatory;
	}

	/**
	 * @see ca.neo.model.Termination#setTau(float)
	 */
	public void setTau(float tau) throws StructuralException {
		if (myDynamics instanceof LTISystem) {
			CanonicalModel.changeTimeConstant((LTISystem) myDynamics, tau);	
		} else {
			throw new StructuralException("Can't set time constant of non-LTI dynamics");
		}
	}

	@Override
	public Termination clone() throws CloneNotSupportedException {
		BasicTermination result = (BasicTermination) super.clone();
		result.myDynamics = myDynamics.clone();
		result.myIntegrator = myIntegrator.clone();
		result.myInput = myInput.clone();
		result.myOutput = myOutput.clone();
		return result;
	}

}
