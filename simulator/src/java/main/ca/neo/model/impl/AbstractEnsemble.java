/*
 * Created on 31-May-2006
 */
package ca.neo.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

import ca.neo.model.Ensemble;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.Probeable;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.SpikeOutput;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.neuron.Neuron;
import ca.neo.util.SpikePattern;
import ca.neo.util.TimeSeries;
import ca.neo.util.impl.SpikePatternImpl;
import ca.neo.util.impl.TimeSeriesImpl;

/**
 * Abstract class that can be used as a basis for Ensemble implementations. 
 * 
 * @author Bryan Tripp
 */
public abstract class AbstractEnsemble implements Ensemble, Probeable {

	private static Logger ourLogger = Logger.getLogger(AbstractEnsemble.class);
	
	private String myName;
	private Node[] myNodes;
	private Map<String, Origin> myOrigins;
	private Map<String, Termination> myTerminations;	
	private Map<String, List<Integer>> myStateNames; //for Probeable
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
		
		myOrigins = new HashMap<String, Origin>(10);
		Origin[] origins = findOrigins(nodes);
		for (int i = 0; i < origins.length; i++) {
			myOrigins.put(origins[i].getName(), origins[i]);
		}
		
		myTerminations = new HashMap<String, Termination>(10);
		Termination[] terminations = findTerminations(nodes);
		for (int i = 0; i < terminations.length; i++) {
			myTerminations.put(terminations[i].getName(), terminations[i]);
		}		
		
		myStateNames = findStateNames(nodes);
		
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
					ourLogger.warn("Ensemble has been set to collect spikes, but not all components have Origin Neuron.AXON", e);
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
	 * @see ca.neo.model.Ensemble#getOrigin(java.lang.String)
	 */
	public Origin getOrigin(String name) throws StructuralException {
		return myOrigins.get(name);
	}

	/**
	 * @see ca.neo.model.Ensemble#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		return myTerminations.get(name);
	}
	
	/**
	 * @see ca.neo.model.Node#getOrigins()
	 */
	public Origin[] getOrigins() {
		return myOrigins.values().toArray(new Origin[0]);
	}

	/**
	 * @see ca.neo.model.Ensemble#getTerminations()
	 */
	public Termination[] getTerminations() {
		return myTerminations.values().toArray(new Termination[0]);
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
		
	/**
	 * @return Composite of Node states by given name. States of different nodes may be defined at different 
	 * 		times, so only the states at the end of the most recent step are given. Only the first 
	 * 		dimension of each Node state is included in the composite. 
	 * @see ca.neo.model.Probeable#getHistory(java.lang.String)
	 */
	public TimeSeries getHistory(String stateName) throws SimulationException {
		if (!myStateNames.containsKey(stateName)) {
			throw new SimulationException("The state " + stateName + " is unknown");
		}
		
		List<Integer> nodeNumbers = myStateNames.get(stateName);
		float[] firstNodeTimes = ((Probeable) myNodes[nodeNumbers.get(0).intValue()]).getHistory(stateName).getTimes();		

		float[] times = new float[0];
		float[][] values = new float[0][];
		Units[] units = Units.uniform(Units.UNK, myNodes.length);
		
		if (firstNodeTimes.length >= 1) {
			times = new float[]{firstNodeTimes[firstNodeTimes.length - 1]};

			values = new float[][]{new float[myNodes.length]};
			for (int i = 0; i < myNodes.length; i++) {
				if (nodeNumbers.contains(new Integer(i))) {
					TimeSeries history = ((Probeable) myNodes[i]).getHistory(stateName);
					int index = history.getTimes().length - 1;
					values[0][i] = history.getValues()[index][0];
					if (i == 0) units[i] = history.getUnits()[0]; 
				}
			}
		}
		
		return new TimeSeriesImpl(times, values, units);
	}

	/**
	 * @see ca.neo.model.Probeable#listStates()
	 */
	public Properties listStates() {
		Properties result = new Properties();
		Iterator<String> keys = myStateNames.keySet().iterator();
		while (keys.hasNext()) {
			result.setProperty(keys.next(), "Composite of Node states by the same name");
		}
		return result;
	}

	/**
	 * Finds existing one-dimensional Origins by same name on the given Nodes, and groups 
	 * them into EnsembleOrigins.
	 * 
	 * @param nodes Nodes on which to look for Origins
	 * @return Ensemble Origins encompassing Node-level Origins
	 */
	private static Origin[] findOrigins(Node[] nodes) {
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
	 * @return Ensemble Terminations encompassing Node-level Terminations 
	 */
	private static Termination[] findTerminations(Node[] nodes) {
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
			try {
				result.add(new EnsembleTermination(name, group.toArray(new Termination[0])));
			} catch (StructuralException e) {
				throw new Error("Composite Termination should consist only of 1D Terminations, but apparently does not", e);
			}
		}
		
		return result.toArray(new Termination[0]);
	}
	
	private static Map<String, List<Integer>> findStateNames(Node[] nodes) {
		Map<String, List<Integer>> result = new HashMap<String, List<Integer>>(10);
		
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] instanceof Probeable) {
				Properties p = ((Probeable) nodes[i]).listStates();
				Iterator keys = p.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next().toString();
					if (!result.containsKey(key)) {
						result.put(key, new ArrayList<Integer>(10));
					}
					result.get(key).add(new Integer(i));
				}
			}
		}
		
		return result;
	}
	
}
