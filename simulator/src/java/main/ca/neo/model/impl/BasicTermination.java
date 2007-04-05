/*
 * Created on 3-Apr-07
 */
package ca.neo.model.impl;

import ca.neo.dynamics.DynamicalSystem;
import ca.neo.dynamics.Integrator;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.SpikeOutput;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.util.Configuration;
import ca.neo.util.TimeSeries;
import ca.neo.util.impl.ConfigurationImpl;
import ca.neo.util.impl.TimeSeriesImpl;

/**
 * A basic implementation of Termination with configurable dynamics and no special 
 * integrative features.
 *    
 * @author Bryan Tripp
 */
public class BasicTermination implements Termination {

	private static final long serialVersionUID = 1L;
	
	private DynamicalSystem myDynamics;
	private Integrator myIntegrator;
	private String myName;
	private Configuration myConfiguration;
	private InstantaneousOutput myInput;
	public TimeSeries myOutput;
	
	public BasicTermination(DynamicalSystem dynamics, Integrator integrator, String name) {
		myDynamics = dynamics;
		myIntegrator = integrator;
		myName = name;
		myConfiguration = new ConfigurationImpl(this);
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
	 * @see ca.neo.util.Configurable#getConfiguration()
	 */
	public Configuration getConfiguration() {
		return myConfiguration;
	}

	/**
	 * @see ca.neo.util.Configurable#propertyChange(java.lang.String, java.lang.Object)
	 */
	public void propertyChange(String propertyName, Object newValue) throws StructuralException {
		//TODO: new dynamics on TAU_PSC property change? 
	}

}
