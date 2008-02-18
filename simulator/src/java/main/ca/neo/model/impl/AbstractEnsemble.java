/*
 * Created on 31-May-2006
 */
package ca.neo.model.impl;

import java.io.Serializable;
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
import ca.neo.util.VisiblyMutable;
import ca.neo.util.VisiblyMutableUtils;
import ca.neo.util.impl.SpikePatternImpl;
import ca.neo.util.impl.TimeSeriesImpl;

/**
 * Abstract class that can be used as a basis for Ensemble implementations.
 * 
 * @author Bryan Tripp
 */
public abstract class AbstractEnsemble implements Ensemble, Probeable, VisiblyMutable {

	private static Logger ourLogger = Logger.getLogger(AbstractEnsemble.class);

	private String myName;
	private Node[] myNodes;
	private NodeRunner[] myNodeRunners;
	transient private Thread[] myThreads;
	private Map<String, Origin> myOrigins;
	private Map<String, Termination> myTerminations;
	private Map<String, List<Integer>> myStateNames; // for Probeable
	private SimulationMode myMode;
	private transient SpikePatternImpl mySpikePattern;
	protected boolean myCollectSpikesFlag;
	private String myDocumentation;
	private transient List<VisiblyMutable.Listener> myListeners;

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
		
		setupNodeRunners(10);
		
		myOrigins = new HashMap<String, Origin>(10);
		Origin[] origins = findOrigins(this, nodes);
		for (int i = 0; i < origins.length; i++) {
			myOrigins.put(origins[i].getName(), origins[i]);
		}
		
		myTerminations = new HashMap<String, Termination>(10);
		Termination[] terminations = findTerminations(this, nodes);
		for (int i = 0; i < terminations.length; i++) {
			myTerminations.put(terminations[i].getName(), terminations[i]);
		}

		myStateNames = findStateNames(nodes);

		myListeners = new ArrayList<Listener>(3);
		
		setMode(SimulationMode.DEFAULT);
	}
	
	private void setupNodeRunners(int n) {
		myNodeRunners = new NodeRunner[n];
			
		List[] nodeLists = new ArrayList[n];
		for (int i = 0; i < n; i++) {
			nodeLists[i] = new ArrayList<Node>(50);
		}
		
		for (int i = 0; i < myNodes.length; i++) {
			int runner = i % myNodeRunners.length;	
			nodeLists[runner].add(myNodes[i]);
		}
		
		for (int i = 0; i < n; i++) {
			myNodeRunners[i] = new NodeRunner((Node[]) nodeLists[i].toArray(new Node[0]));
		}
	}
	
	private void setupThreads() {
		myThreads = new Thread[myNodeRunners.length];
		for (int i = 0; i < myNodeRunners.length; i++) {
			myThreads[i] = new Thread(myNodeRunners[i]);			
		}
	}

	/**
	 * @see ca.neo.model.Ensemble#getName()
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
		if (mySpikePattern == null) {
			mySpikePattern = new SpikePatternImpl(myNodes.length);
		}
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

//		if (myThreads == null) setupThreads();
//		for (int i = 0; i < myNodeRunners.length; i++) {
//			myNodeRunners[i].setTime(startTime, endTime);
//			myThreads[i].run();
//		}
//		
//		for (int i = 0; i < myThreads.length; i++) {
//			try {
//				myThreads[i].join();
//			} catch (InterruptedException e) {
//				throw new SimulationException(e);
//			}
//			
//			if (myNodeRunners[i].getException() != null) throw myNodeRunners[i].getException();			
//		}
//		
//		for (int i = 0; i < myNodes.length; i++) {
//			if (myCollectSpikesFlag) {
//				try {
//					InstantaneousOutput output = myNodes[i].getOrigin(Neuron.AXON).getValues();
//					if (output instanceof SpikeOutput && ((SpikeOutput) output).getValues()[0]) {
//						mySpikePattern.addSpike(i, endTime);
//					}				
//				} catch (StructuralException e) {
//					ourLogger.warn("Ensemble has been set to collect spikes, but not all components have Origin Neuron.AXON", e);
//				}
//			}
//		}				
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
	 * @see ca.neo.model.Ensemble#isCollectingSpikes()
	 */
	public boolean isCollectingSpikes() {
		return myCollectSpikesFlag;
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
	private static Origin[] findOrigins(Node parent, Node[] nodes) {
		Map<String, List<Origin>> groups = group1DOrigins(nodes);
		Iterator<String> it = groups.keySet().iterator();
		List<Origin> result = new ArrayList<Origin>(10);
		while (it.hasNext()) {
			String name = it.next();
			List<Origin> group = groups.get(name);
			result.add(new EnsembleOrigin(parent, name, (Origin[]) group.toArray(new Origin[0])));
		}
		
		return result.toArray(new Origin[0]);
	}
	
	/**
	 * @param nodes A list of Nodes in an Ensemble
	 * @return A grouping of one-dimensional origins on these nodes, by name
	 */
	private static Map<String, List<Origin>> group1DOrigins(Node[] nodes) {
		Map<String, List<Origin>> groups = new HashMap<String, List<Origin>>(10);
		
		for (int i = 0; i < nodes.length; i++) {
			Origin[] origins = nodes[i].getOrigins();
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
		
		return groups;
	}

	/**
	 * @param nodes A list of Nodes
	 * @return Names of one-dimensional origins that are shared by all the nodes 
	 */
	public static List<String> findCommon1DOrigins(Node[] nodes) {
		List<String> result = get1DOriginNames(nodes[0]);
		
		for (int i = 1; i < nodes.length; i++) {
			result.retainAll(get1DOriginNames(nodes[i]));
		}
		
		return result;
	}
	
	private static List<String> get1DOriginNames(Node node) {
		List<String> result = new ArrayList<String>(10);
		Origin[] origins = node.getOrigins();
		for (int i = 0; i < origins.length; i++) {
			if (origins[i].getDimensions() == 1) result.add(origins[i].getName());
		}
		return result;
	}
	
	/**
	 * Finds existing one-dimensional Terminations by the same name on different nodes, and 
	 * groups them into EnsembleTerminations. 
	 * 
	 * @param parent The ensemble to which new terminations will belong
	 * @param nodes Nodes on which to look for Terminations
	 * @return Ensemble Terminations encompassing Node-level Terminations 
	 */
	private static Termination[] findTerminations(Node parent, Node[] nodes) {
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
				result.add(new EnsembleTermination(parent, name, group.toArray(new Termination[0])));
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
			myListeners = new ArrayList<Listener>(1);
		}
		myListeners.add(listener);
	}

	/**
	 * @see ca.neo.util.VisiblyMutable#removeChangeListener(ca.neo.util.VisiblyMutable.Listener)
	 */
	public void removeChangeListener(Listener listener) {
		if (myListeners != null) myListeners.remove(listener);
	}
	
	/**
	 * Called by subclasses when properties have changed in such a way that the 
	 * display of the ensemble may need updating. 
	 */
	protected void fireVisibleChangeEvent() {
		VisiblyMutableUtils.changed(this, myListeners);
	}

	/**
	 * Allows us to run a node in a separate thread. 
	 *  
	 * @author Bryan Tripp
	 */
	private static class NodeRunner implements Runnable, Serializable {

		private static final long serialVersionUID = 1L;
		
		private Node[] myNodes;
		private float myStartTime;
		private float myEndTime;
		private SimulationException myException;

		/**
		 * @param nodes The Nodes that will be run by this runner. 
		 */
		public NodeRunner(Node[] nodes) {
			myNodes = nodes;
		}

		/**
		 * @param start Simulation time at next run is to start
		 * @param end Simulation time at which next run is to end
		 */
		public void setTime(float start, float end) {
			myStartTime = start;
			myEndTime = end;
		}

		/**
		 * @return The exception thrown in the last run, if any, otherwise null.   
		 */
		public SimulationException getException() {
			return myException;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			myException = null;
			try {
				for (int i = 0; i < myNodes.length; i++) {
					myNodes[i].run(myStartTime, myEndTime);					
				}
			} catch (SimulationException e) {
				myException = e;
			}
		}
		
	}
	
}
