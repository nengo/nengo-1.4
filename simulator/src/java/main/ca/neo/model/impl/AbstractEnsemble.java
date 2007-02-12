/*
 * Created on 31-May-2006
 */
package ca.neo.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ca.neo.model.Ensemble;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Origin;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.neuron.Neuron;
import ca.neo.model.neuron.SpikeOutput;
import ca.neo.model.neuron.SpikePattern;
import ca.neo.model.neuron.impl.SpikePatternImpl;

/**
 * Abstract class that can be used as a basis for Ensemble implementations. 
 * 
 * @author Bryan Tripp
 */
public abstract class AbstractEnsemble implements Ensemble {

	private String myName;
	private Neuron[] myNeurons;
	private SimulationMode myMode;
	private SpikePatternImpl mySpikePattern;
	protected boolean myCollectSpikesFlag;
	
	/**
	 * Note that setMode(SimulationMode.DEFAULT) is called at construction time. 
	 * 
	 * @param name Unique name of Ensemble 
	 * @param neurons Neurons that Ensemble contains
	 */
	public AbstractEnsemble(String name, Neuron[] neurons) {
		myName = name;
		myNeurons = neurons; 
		mySpikePattern = new SpikePatternImpl(neurons.length);
		myCollectSpikesFlag = false;
		
		setMode(SimulationMode.DEFAULT);
	}

	/**
	 * @see ca.neo.model.Ensemble#getName()
	 */
	public String getName() {
		return myName;
	}

	public Neuron[] getNeurons() {
		return myNeurons;
	}

	/**
	 * When this method is called, setMode(...) is called on each Neuron in the Ensemble. 
	 * Each Neuron will then run in the mode that is closest to the requested mode (this 
	 * could be different for different Neurons). Note that at Ensemble construction time, 
	 * setMode(SimulationMode.DEFAULT) is called.
	 * 
	 * @see ca.neo.model.Ensemble#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
		myMode = mode;
		
		for (int i = 0; i < myNeurons.length; i++) {
			myNeurons[i].setMode(mode);
		}
	}

	/**
	 * Note that this reflects the latest mode requested of the Ensemble, and that individual 
	 * Neurons may run in different modes (see setMode).  
	 *   
	 * @see ca.neo.model.Ensemble#getMode()
	 */
	public SimulationMode getMode() {
		return myMode;
	}


	/**
	 * Runs each neuron in the Ensemble. 
	 * 
	 * @see ca.neo.model.Ensemble#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {
		for (int i = 0; i < myNeurons.length; i++) {
			myNeurons[i].run(startTime, endTime);

			if (myCollectSpikesFlag) {
				InstantaneousOutput output = myNeurons[i].getOrigin(Neuron.AXON).getValues();
				if (output instanceof SpikeOutput && ((SpikeOutput) output).getValues()[0]) {
					mySpikePattern.addSpike(i, endTime);
				}				
			}
		}		
	}

	/**
	 * Resets each Neuron in this Ensemble. 
	 * 
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		for (int i = 0; i < myNeurons.length; i++) {
			myNeurons[i].reset(randomize);
		}
		
		mySpikePattern = new SpikePatternImpl(myNeurons.length);
	}

	/**
	 * Finds existing Origins by same name on the given neurons, and groups them into 
	 * EnsembleOrigins.
	 * 
	 * @param neurons Neurons on which to look for Origins
	 * @return Ensemble Origins encompassing Neuron Origins
	 */
	public static Origin[] findOrigins(Neuron[] neurons) {
		Map groups = new HashMap(10);
		
		for (int i = 0; i < neurons.length; i++) {
			Origin[] origins = neurons[i].getOrigins();;
			for (int j = 0; j < origins.length; j++) {
				List group = (List) groups.get(origins[j].getName());
				if (group == null) {
					group = new ArrayList(neurons.length * 2);
					groups.put(origins[j].getName(), group);
				}
				group.add(origins[j]);
			}
		}
		
		Iterator it = groups.keySet().iterator();
		List result = new ArrayList(10);
		while (it.hasNext()) {
			String name = (String) it.next();
			List group = (List) groups.get(name);
			result.add(new EnsembleOrigin(name, (Origin[]) group.toArray(new Origin[0])));
		}
		
		return (Origin[]) result.toArray(new Origin[0]);
	}
	
	/**
	 * Finds existing Terminations by the same name on different neurons, and groups them
	 * into EnsembleTerminations. 
	 * 
	 * @param neurons Neurons on which to look for Terminations
	 * @return Ensemble Terminations encompassing Neuron Terminations 
	 */
	public static Termination[] findTerminations(Neuron[] neurons) throws StructuralException {
		Map groups = new HashMap(10);
		
		for (int i = 0; i < neurons.length; i++) {
			Termination[] terminations = neurons[i].getIntegrator().getTerminations();
			for (int j = 0; j < terminations.length; j++) {
				List group = (List) groups.get(terminations[j].getName());
				if (group == null) {
					group = new ArrayList(neurons.length * 2);
					groups.put(terminations[j].getName(), group);
				}
				group.add(terminations[j]);
			}
		}
		
		Iterator it = groups.keySet().iterator();
		List result = new ArrayList(10);
		while (it.hasNext()) {
			String name = (String) it.next();
			List group = (List) groups.get(name);
			result.add(new EnsembleTermination(name, (Termination[]) group.toArray(new Termination[0])));
		}
		
		return (Termination[]) result.toArray(new Termination[0]);
	}

	/**
	 * @see ca.neo.model.Ensemble#collectSpikes(boolean)
	 */
	public void collectSpikes(boolean collect) {
		myCollectSpikesFlag = collect;
	}

	/**
	 * @see ca.neo.model.Ensemble#getSpikePattern()
	 */
	public SpikePattern getSpikePattern() {
		if (!myCollectSpikesFlag) System.err.println("Warning: collect spikes flag is off"); //TODO: logging
		return mySpikePattern;
	}
	
	
}
