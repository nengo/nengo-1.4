/*
 * Created on May 23, 2006
 */
package ca.neo.model.neuron.impl;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Origin;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.Units;
import ca.neo.model.impl.RealOutputImpl;
import ca.neo.model.impl.SpikeOutputImpl;
import ca.neo.model.neuron.Neuron;
import ca.neo.model.neuron.SpikeGenerator;

/**
 * An Origin that obtains output from an underlying SpikeGenerator. This is a good Origin to use as 
 * the main (axonal) output of a spiking neuron. This Origin may produce SpikeOutput or RealOutput 
 * depending on whether it is running in DEFAULT or CONSTANT_RATE SimulationMode.  
 * 
 * @author Bryan Tripp
 */
public class SpikeGeneratorOrigin implements Origin {

	private static final long serialVersionUID = 1L;
	
	private static final Units ourSpikeUnit = Units.SPIKES;
	private static final Units ourRealUnit = Units.SPIKES_PER_S;
	
	private SimulationMode[] mySupportedModes; //TODO: fix test for generator-dependent modes	
	private SpikeGenerator myGenerator;
	private SimulationMode myMode;
	private InstantaneousOutput myOutput;
	
	/**
	 * @param generator The SpikeGenerator from which this Origin is to obtain output.  
	 */
	public SpikeGeneratorOrigin(SpikeGenerator generator) {
		myGenerator = generator;
		myMode = SimulationMode.DEFAULT;
		myOutput = new SpikeOutputImpl(new boolean[]{false}, ourSpikeUnit);
		
		if (generator.knownConstantRate()) {
			mySupportedModes = new SimulationMode[]{SimulationMode.DEFAULT, SimulationMode.CONSTANT_RATE};
		} else {
			mySupportedModes = new SimulationMode[]{SimulationMode.DEFAULT};
		}
	}
	
	/**
	 * @param mode SimulationMode in which it is desired that the Origin runs. CONSTANT_RATE is 
	 * 		supported. If any other mode is requested, then DEFAULT is used. 
	 */
	public void setMode(SimulationMode mode) {
		myMode = SimulationMode.getClosestMode(mode, mySupportedModes);
	}

	/**
	 * @return The SimulationMode in which this Origin is running (can be CONSTANT_RATE or 
	 * 		DEFAULT; defaults to DEFAULT).  
	 */
	public SimulationMode getMode() {
		return myMode;
	}

	/**
	 * @return Neuron.AXON
	 * @see ca.neo.model.Origin#getName()
	 */
	public String getName() {
		return Neuron.AXON;
	}

	/**
	 * @return 1
	 * @see ca.neo.model.Origin#getDimensions()
	 */
	public int getDimensions() {
		return 1;
	}
	
	/**
	 * @param times Passed on to the run() or runConstantRate() method of the wrapped SpikeGenerator
	 * 		depending on whether the SimulationMode is DEFAULT or CONSTANT_RATE (in the latter case 
	 * 		only the first value is used).  
	 * @param current Passed on like the times argument. 
	 * @throws SimulationException Arising From the underlying SpikeGenerator, or if the given times 
	 * 		or values arrays have length 0 when in CONSTANT_RATE mode (the latter because the first 
	 * 		entries must be extracted). 
	 */
	public void run(float[] times, float[] current) throws SimulationException {
		if (myMode == SimulationMode.DEFAULT) {
			boolean spike = myGenerator.run(times, current);
			myOutput = new SpikeOutputImpl(new boolean[]{spike}, ourSpikeUnit);
		} else {
			if (times.length == 0 || current.length == 0) {
				throw new SimulationException("Args time and current must have length >0 in CONSTANT_RATE mode");
			}
			float rate = myGenerator.runConstantRate(times[0], current[0]);
			myOutput = new RealOutputImpl(new float[]{rate}, ourRealUnit); 
		}
	}

	/**
	 * Returns spike values or real-valued spike rate values, depending on whether the mode
	 * is SimulationMode.DEFAULT or SimulationMode.CONSTANT_RATE.  
	 * 
	 * @see ca.neo.model.Origin#getValues()
	 */
	public InstantaneousOutput getValues() {
		return myOutput;
	}

}
