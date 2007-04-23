/*
 * Created on May 3, 2006
 */
package ca.neo.model.neuron.impl;

import java.util.Properties;

import ca.neo.model.Origin;
import ca.neo.model.Probeable;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.nef.NEFNode;
import ca.neo.model.neuron.Neuron;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.model.neuron.SynapticIntegrator;
import ca.neo.util.TimeSeries;
import ca.neo.util.TimeSeries1D;
import ca.neo.util.impl.TimeSeries1DImpl;

/**
 * A neuron model composed of a SynapticIntegrator and a SpikeGenerator.  
 * 
 * @author Bryan Tripp
 */
public class SpikingNeuron implements Neuron, Probeable, NEFNode {

	private static final long serialVersionUID = 1L;
	
	private SynapticIntegrator myIntegrator;
	private SpikeGenerator myGenerator;
	private SpikeGeneratorOrigin myOrigin;
	private TimeSeries1D myCurrent;
	private String myName;
	private float myScale;
	private float myBias;
	private float myRadialInput;

	/**
	 * Note: current = scale * (weighted sum of inputs at each termination) * (radial input) + bias.
	 * 
	 * @param integrator SynapticIntegrator used to model dendritic/somatic integration of inputs
	 * 		to this Neuron
	 * @param generator SpikeGenerator used to model spike generation at the axon hillock of this 
	 * 		Neuron
	 * @param scale A coefficient that scales summed input    
	 * @param bias A bias current that models unaccounted-for inputs and/or intrinsic currents 
	 * @param name A unique name for this neuron in the context of the Network or Ensemble to which 
	 * 		it belongs
	 */
	public SpikingNeuron(SynapticIntegrator integrator, SpikeGenerator generator, float scale, float bias, String name) {
		myIntegrator = integrator;
		myGenerator = generator;
		myOrigin = new SpikeGeneratorOrigin(generator);
		myName = name;
		myScale = scale;
		myBias = bias;
		myRadialInput = 0;
		myCurrent = new TimeSeries1DImpl(new float[]{0}, new float[]{0}, Units.UNK);
	}

	/**
	 * @see ca.neo.model.neuron.Neuron#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {
		//TODO: this method could use some cleanup and optimization
		TimeSeries1D current = myIntegrator.run(startTime, endTime);
		
		float[] integratorOutput = current.getValues1D();
		float[] generatorInput = new float[integratorOutput.length];
		for (int i = 0; i < integratorOutput.length; i++) {
			generatorInput[i] = myBias + myScale * (myRadialInput + integratorOutput[i]);
		}
		myCurrent = new TimeSeries1DImpl(current.getTimes(), generatorInput, Units.UNK);
		
		myOrigin.run(myCurrent.getTimes(), generatorInput);
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
	public Origin getOrigin(String name) throws StructuralException {
		assert name.equals(Neuron.AXON); //this is going to be called a lot, so let's skip the exception		
		return myOrigin;
	}

	/**
	 * @see ca.neo.model.neuron.Neuron#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
		myGenerator.setMode(mode);
	}

	/**
	 * @see ca.neo.model.neuron.Neuron#getMode()
	 */
	public SimulationMode getMode() {
		return myGenerator.getMode();
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
		Properties p = (myGenerator instanceof Probeable) ? ((Probeable) myGenerator).listStates() : new Properties();
		p.setProperty("I", "Net current (arbitrary units)");
		return p;
	}

	/**
	 * @see ca.neo.model.Node#getName()
	 */
	public String getName() {
		return myName;
	}

	/**
	 * @see ca.neo.model.Node#getTerminations()
	 */
	public Termination[] getTerminations() {
		return myIntegrator.getTerminations();
	}

	/**
	 * @see ca.neo.model.Node#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		return myIntegrator.getTermination(name);
	}

	/**
	 * @see ca.neo.model.nef.NEFNode#setRadialInput(float)
	 */
	public void setRadialInput(float value) {
		myRadialInput = value;
	}

}
