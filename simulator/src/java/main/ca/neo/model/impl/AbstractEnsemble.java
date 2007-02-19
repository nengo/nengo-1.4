/*
 * Created on 31-May-2006
 */
package ca.neo.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.neo.model.Ensemble;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Node;
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

	private static Logger ourLogger = Logger.getLogger(AbstractEnsemble.class);
	
	private String myName;
	private Node[] myNodes;
	private SimulationMode myMode;
	private SpikePatternImpl mySpikePattern;
	protected boolean myCollectSpikesFlag;
	
	/**
	 * Note that setMode(SimulationMode.DEFAULT) is called at construction time. 
	 * 
	 * @param name Unique name of Ensemble 
	 * @param nodes Nodes that Ensemble contains
	 */
	public AbstractEnsemble(String name, Node[] nodes) {
		myName = name;
		myNodes = nodes; 
		mySpikePattern = new SpikePatternImpl(nodes.length);
		myCollectSpikesFlag = false;
		
		setMode(SimulationMode.DEFAULT);
	}

	/**
	 * @see ca.neo.model.Ensemble#getName()
	 */
	public String getName() {
		return myName;
	}

	/**
	 * @see ca.neo.model.Ensemble#getNodes()
	 */
	public Node[] getNodes() {
		return myNodes;
	}

	/**
	 * When this method is called, setMode(...) is called on each Node in the Ensemble. 
	 * Each Node will then run in the mode that is closest to the requested mode (this 
	 * could be different for different Node). Note that at Ensemble construction time, 
	 * setMode(SimulationMode.DEFAULT) is called.
	 * 
	 * @see ca.neo.model.Ensemble#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
		myMode = mode;
		
		for (int i = 0; i < myNodes.length; i++) {
			myNodes[i].setMode(mode);
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
		for (int i = 0; i < myNodes.length; i++) {
			myNodes[i].run(startTime, endTime);

			if (myCollectSpikesFlag) {
				try {
					InstantaneousOutput output = myNodes[i].getOrigin(Neuron.AXON).getValues();
					if (output instanceof SpikeOutput && ((SpikeOutput) output).getValues()[0]) {
						mySpikePattern.addSpike(i, endTime);
					}				
				} catch (StructuralException e) {
					//TODO: does this have to be an exception? ignore? log? 
					throw new SimulationException("Ensemble has been set to collect spikes, but not all components have Origin Neuron.AXON", e);
				}
			}
		}		
	}

	/**
	 * Resets each Node in this Ensemble. 
	 * 
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		for (int i = 0; i < myNodes.length; i++) {
			myNodes[i].reset(randomize);
		}
		
		mySpikePattern = new SpikePatternImpl(myNodes.length);
	}

	/**
	 * Finds existing one-dimensional Origins by same name on the given Nodes, and groups 
	 * them into EnsembleOrigins.
	 * 
	 * @param nodes Nodes on which to look for Origins
	 * @return Ensemble Origins encompassing Neuron Origins
	 */
	public static Origin[] findOrigins(Node[] nodes) {
		Map<String, List<Origin>> groups = new HashMap<String, List<Origin>>(10);
		
		for (int i = 0; i < nodes.length; i++) {
			Origin[] origins = nodes[i].getOrigins();;
			for (int j = 0; j < origins.length; j++) {
				if (origins[j].getDimensions() == 1) {
					List<Origin> group = groups.get(origins[j].getName());
					if (group == null) {
						group = new ArrayList<Origin>(nodes.length * 2);
						groups.put(origins[j].getName(), group);
					}
					group.add(origins[j]);					
				}
			}
		}
		
		Iterator<String> it = groups.keySet().iterator();
		List<Origin> result = new ArrayList<Origin>(10);
		while (it.hasNext()) {
			String name = it.next();
			List<Origin> group = groups.get(name);
			result.add(new EnsembleOrigin(name, (Origin[]) group.toArray(new Origin[0])));
		}
		
		return result.toArray(new Origin[0]);
	}
	
	/**
	 * Finds existing one-dimensional Terminations by the same name on different nodes, and 
	 * groups them into EnsembleTerminations. 
	 * 
	 * @param nodes Nodes on which to look for Terminations
	 * @return Ensemble Terminations encompassing Neuron Terminations 
	 */
	public static Termination[] findTerminations(Node[] nodes) throws StructuralException {
		Map<String, List<Termination>> groups = new HashMap<String, List<Termination>>(10);
		
		for (int i = 0; i < nodes.length; i++) {
			Termination[] terminations = nodes[i].getTerminations();
			for (int j = 0; j < terminations.length; j++) {
				if (terminations[j].getDimensions() == 1) {
					List<Termination> group = groups.get(terminations[j].getName());
					if (group == null) {
						group = new ArrayList<Termination>(nodes.length * 2);
						groups.put(terminations[j].getName(), group);
					}
					group.add(terminations[j]);					
				}
			}
		}
		
		Iterator<String> it = groups.keySet().iterator();
		List<Termination> result = new ArrayList<Termination>(10);
		while (it.hasNext()) {
			String name = it.next();
			List<Termination> group = groups.get(name);
			result.add(new EnsembleTermination(name, group.toArray(new Termination[0])));
		}
		
		return result.toArray(new Termination[0]);
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
		if (!myCollectSpikesFlag) ourLogger.warn("Warning: collect spikes flag is off"); 
		return mySpikePattern;
	}
	
	
}
