/*
 * Created on May 3, 2006
 */
package ca.neo.model.neuron.impl;

import java.util.Properties;

import ca.neo.model.Origin;
import ca.neo.model.Probeable;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.neuron.Neuron;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.model.neuron.SynapticIntegrator;
import ca.neo.util.TimeSeries;
import ca.neo.util.TimeSeries1D;

/**
 * A neuron model composed of a SynapticIntegrator and a SpikeGenerator.  
 * 
 * @author Bryan Tripp
 */
public class SpikingNeuron implements Neuron, Probeable {

	private static final long serialVersionUID = 1L;
	
	private SynapticIntegrator myIntegrator;
	private SpikeGenerator myGenerator;
	private SpikeGeneratorOrigin myOrigin;
	private TimeSeries1D myCurrent;

	/**
	 * @param integrator SynapticIntegrator used to model dendritic/somatic integration of inputs
	 * 		to this Neuron
	 * @param generator SpikeGenerator used to model spike generation at the axon hillock of this 
	 * 		Neuron
	 */
	public SpikingNeuron(SynapticIntegrator integrator, SpikeGenerator generator) {
		myIntegrator = integrator;
		myGenerator = generator;
		myOrigin = new SpikeGeneratorOrigin(generator);
	}

	/**
	 * @see ca.neo.model.neuron.Neuron#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {
		TimeSeries1D current = myIntegrator.run(startTime, endTime); 
		myCurrent = current; 
		myOrigin.run(current.getTimes(), current.getValues1D());
	}

	/**
	 * @see ca.neo.model.neuron.Neuron#getIntegrator()
	 */
	public SynapticIntegrator getIntegrator() {
		return myIntegrator;
	}

	/**
	 * @see ca.neo.model.neuron.Neuron#getOrigins()
	 */
	public Origin[] getOrigins() {
		return new Origin[]{myOrigin};
	}

	/**
	 * @see ca.neo.model.neuron.Neuron#getOrigin(java.lang.String)
	 */
	public Origin getOrigin(String name) throws SimulationException {
		assert name.equals(Neuron.AXON);
		return myOrigin;
	}

	/**
	 * @see ca.neo.model.neuron.Neuron#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
		myOrigin.setMode(mode);
	}

	/**
	 * @see ca.neo.model.neuron.Neuron#getMode()
	 */
	public SimulationMode getMode() {
		return myOrigin.getMode();
	}

	/**
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		myIntegrator.reset(randomize);
		myGenerator.reset(randomize);
	}

	/**
	 * Available states include "I" (net current into SpikeGenerator) and the states of the 
	 * SpikeGenerator. 
	 * 
	 * @see ca.neo.model.Probeable#getHistory(java.lang.String)
	 */
	public TimeSeries getHistory(String stateName) throws SimulationException {
		TimeSeries result = null;
		if (stateName.equals("I")) {
			result = myCurrent; 
		} else if (myGenerator instanceof Probeable) {
			result = ((Probeable) myGenerator).getHistory(stateName);
		} else {
			throw new SimulationException("The state " + stateName + " is unknown");
		}
		return result;
	}

	/**
	 * @see ca.neo.model.Probeable#listStates()
	 */
	public Properties listStates() {
		Properties p = null;
		if (myGenerator instanceof Probeable) {
			p = ((Probeable) myGenerator).listStates();
		} else {
			p = new Properties();
		}

		p.setProperty("I", "Net current (arbitrary units)");
		
		return p;
	}

}
