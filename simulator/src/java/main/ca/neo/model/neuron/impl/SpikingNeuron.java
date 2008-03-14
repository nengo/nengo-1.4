/*
 * Created on May 3, 2006
 */
package ca.neo.model.neuron.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Origin;
import ca.neo.model.Probeable;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.SpikeOutput;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.impl.BasicOrigin;
import ca.neo.model.nef.NEFNode;
import ca.neo.model.neuron.Neuron;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.model.neuron.SynapticIntegrator;
import ca.neo.util.TimeSeries;
import ca.neo.util.TimeSeries1D;
import ca.neo.util.VisiblyMutable;
import ca.neo.util.VisiblyMutableUtils;
import ca.neo.util.impl.TimeSeries1DImpl;

/**
 * A neuron model composed of a SynapticIntegrator and a SpikeGenerator.  
 * 
 * @author Bryan Tripp
 */
public class SpikingNeuron implements Neuron, Probeable, NEFNode {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of Origin representing unscaled and unbiased current entering the soma.   
	 */
	public static final String CURRENT = "current";
	
	private SynapticIntegrator myIntegrator;
	private SpikeGenerator myGenerator;
	private SpikeGeneratorOrigin mySpikeOrigin;
	private BasicOrigin myCurrentOrigin;
	private float myUnscaledCurrent;
	private TimeSeries1D myCurrent;
	private String myName;
	private float myScale;
	private float myBias;
	private float myRadialInput;
	private String myDocumentation;
	private List<VisiblyMutable.Listener> myListeners;

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
		if (integrator == null) {
			integrator = new LinearSynapticIntegrator(.001f, Units.ACU);
		}		
		setIntegrator(integrator);
		
		if (generator == null) {
			generator = new LIFSpikeGenerator(.001f, .02f, .002f);
		}
		setGenerator(generator);
		
		myCurrentOrigin = new BasicOrigin(this, CURRENT, 1, Units.ACU);
		myCurrentOrigin.setValues(0, 0, new float[]{0});
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
			myUnscaledCurrent = (myRadialInput + integratorOutput[i]);
			generatorInput[i] = myBias + myScale * myUnscaledCurrent;
		}
		myCurrent = new TimeSeries1DImpl(current.getTimes(), generatorInput, Units.UNK);
		
		mySpikeOrigin.run(myCurrent.getTimes(), generatorInput);
		myCurrentOrigin.setValues(startTime, endTime, new float[]{myUnscaledCurrent});
	}

	/**
	 * @see ca.neo.model.neuron.Neuron#getOrigins()
	 */
	public Origin[] getOrigins() {
		return new Origin[]{mySpikeOrigin, myCurrentOrigin};
	}

	/**
	 * @see ca.neo.model.neuron.Neuron#getOrigin(java.lang.String)
	 */
	public Origin getOrigin(String name) throws StructuralException {
//		assert (name.equals(Neuron.AXON) || name.equals(CURRENT)); //this is going to be called a lot, so let's skip the exception
		//Shu: I added the exception back in because the UI needs it for reflection.
		if (name.equals(Neuron.AXON)) {
			return mySpikeOrigin;			
		} else if (name.equals(CURRENT)){
			return myCurrentOrigin;
		} else {
			throw new StructuralException("Origin does not exist");
		}
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
		} else if (stateName.equals("rate")) {
			InstantaneousOutput output = mySpikeOrigin.getValues();
			float[] times = myCurrent.getTimes();
			float rate = 0;
			if (output instanceof RealOutput) {
				rate = ((RealOutput) output).getValues()[0];
			} else if (output instanceof SpikeOutput) {
				rate = ((SpikeOutput) output).getValues()[0] ? 1/(times[times.length-1]-times[0]) : 0;
			}
			result = new TimeSeries1DImpl(new float[]{times[times.length-1]}, new float[]{rate}, Units.SPIKES_PER_S);				
		} else if (stateName.equals(CURRENT)) {
			float[] times = myCurrent.getTimes();
			result = new TimeSeries1DImpl(new float[]{times[times.length-1]}, new float[]{myUnscaledCurrent}, Units.ACU);
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
		p.setProperty("rate", "Firing rate");
		return p;
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
	 * @return The coefficient that scales summed input    
	 */
	public float getScale() {
		return myScale;
	}
	
	/**
	 * @param scale New scaling coefficient
	 */
	public void setScale(float scale) {
		myScale = scale;
	}

	/**
	 * @return The bias current that models unaccounted-for inputs and/or intrinsic currents 
	 */
	public float getBias() {
		return myBias;
	}

	/**
	 * @param bias New bias current
	 */
	public void setBias(float bias) {
		myBias = bias;
	}
	
	/**
	 * @return The SynapticIntegrator used to model dendritic/somatic integration of inputs
	 * 		to this Neuron
	 */
	public SynapticIntegrator getIntegrator() {
		return myIntegrator;
	}
	
	/**
	 * @param integrator New synaptic integrator
	 */
	public void setIntegrator(SynapticIntegrator integrator) {		
		myIntegrator = integrator;
		myIntegrator.setNode(this);		
	}

	/**
	 * @return The SpikeGenerator used to model spike generation at the axon hillock of this 
	 * 		Neuron
	 */
	public SpikeGenerator getGenerator() {
		return myGenerator;
	}

	/**
	 * @param generator New SpikeGenerator 
	 */
	public void setGenerator(SpikeGenerator generator) {
		myGenerator = generator;
		mySpikeOrigin = new SpikeGeneratorOrigin(this, generator);
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
	public SpikingNeuron clone() throws CloneNotSupportedException {
		SpikingNeuron result = (SpikingNeuron) super.clone();
		result.myCurrent = (TimeSeries1D) myCurrent.clone();
		
		result.myCurrentOrigin = (BasicOrigin) myCurrentOrigin.clone();
		result.myCurrentOrigin.setNode(result);
			
		result.myGenerator = myGenerator.clone();
		
		result.myIntegrator = myIntegrator.clone();
		result.myIntegrator.setNode(result);
		
		result.myListeners = new ArrayList<Listener>(5);
		result.mySpikeOrigin = new SpikeGeneratorOrigin(result, result.myGenerator);
		
		return result;
	}
	
}
