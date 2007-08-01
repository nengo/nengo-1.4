/*
 * Created on 23-May-2006
 */
package ca.neo.model.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.Probeable;
import ca.neo.model.Projection;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.sim.Simulator;
import ca.neo.sim.impl.LocalSimulator;
import ca.neo.util.Configuration;
import ca.neo.util.TimeSeries;

/**
 * Default implementation of Network. 
 *  
 * @author Bryan Tripp
 */
public class NetworkImpl implements Network {

	public static final String DEFAULT_NAME = "Network";
	
	private static final long serialVersionUID = 1L;
	
	private Map<String, Node> myNodeMap; //keyed on name
	private Map myProjectionMap; //keyed on Termination
	private String myName;
	private SimulationMode myMode;
	private Simulator mySimulator;
	private float myStepSize;
	private Map myProbeables;
	private Map myProbeableStates;
	private Map myExposedOrigins;
	private Map myExposedTerminations;
	private String myDocumentation;
	private Map<String, Object> myMetaData;

	public NetworkImpl() {
		myNodeMap = new HashMap<String, Node>(20);
		myProjectionMap	= new HashMap(50);
		myName = DEFAULT_NAME;
		mySimulator = new LocalSimulator();
		myStepSize = .001f; 
		myProbeables = new HashMap(30);
		myProbeableStates = new HashMap(30);
		myExposedOrigins = new HashMap(10);
		myExposedTerminations = new HashMap(10);
		myMode = SimulationMode.DEFAULT;
		myMetaData = new HashMap<String, Object>(20);
	}
	
	/**
	 * @param simulator Simulator with which to run this Network
	 */
	public void setSimulator(Simulator simulator) {
		mySimulator = simulator;
		mySimulator.initialize(this);
	}
	
	/**
	 * @return Simulator used to run this Network
	 */
	public Simulator getSimulator() {
		return mySimulator;
	}	
	
	/**
	 * @param stepSize New timestep size at which to simulate Network (some components of the network 
	 * 		may run with different step sizes, but information is exchanged between components with 
	 * 		this step size). Defaults to 0.001s. 
	 */
	public void setStepSize(float stepSize) {
		myStepSize = stepSize;
	}
	
	/**
	 * @return Timestep size at which Network is simulated. 
	 */
	public float getStepSize() {
		return myStepSize;
	}
	
	/**
	 * @see ca.neo.model.Network#addNode(ca.neo.model.Node)
	 */
	public void addNode(Node node) throws StructuralException {
		if (myNodeMap.containsKey(node.getName())) {
			throw new StructuralException("This Network already contains a Node named " + node.getName());
		}
		
		myNodeMap.put(node.getName(), node);
		
		mySimulator.initialize(this);
	}

	/**
	 * @see ca.neo.model.Network#getNodes()
	 */
	public Node[] getNodes() {
		return myNodeMap.values().toArray(new Node[0]);
	}

	/**
	 * @see ca.neo.model.Network#getNode(java.lang.String)
	 */
	public Node getNode(String name) throws StructuralException {
		if (!myNodeMap.containsKey(name)) {
			throw new StructuralException("No Node named " + name + " in this Network");			
		}
		return myNodeMap.get(name);
	}

	/**
	 * @see ca.neo.model.Network#removeNode(java.lang.String)
	 */
	public void removeNode(String name) throws StructuralException {
		if (myNodeMap.containsKey(name)) {
			myNodeMap.remove(name);
		} else {
			throw new StructuralException("No Node named " + name + " in this Network");
		}

		mySimulator.initialize(this);		
	}

	/**
	 * @see ca.neo.model.Network#addProjection(ca.neo.model.Origin, ca.neo.model.Termination)
	 */
	public void addProjection(Origin origin, Termination termination) throws StructuralException {
		if (myProjectionMap.containsKey(termination)) {
			throw new StructuralException("There is already an Origin connected to the specified Termination");
		}
		
		if (origin.getDimensions() != termination.getDimensions()) {
			throw new StructuralException("Can't connect Origin of dimension " + origin.getDimensions() 
					+ " to Termination of dimension " + termination.getDimensions());
		}
		
		myProjectionMap.put(termination, new ProjectionImpl(origin, termination));

		mySimulator.initialize(this);
	}

	/**
	 * @see ca.neo.model.Network#getProjections()
	 */
	public Projection[] getProjections() {
		return (Projection[]) myProjectionMap.values().toArray(new Projection[0]);
	}

	/**
	 * @see ca.neo.model.Network#removeProjection(ca.neo.model.Termination)
	 */
	public void removeProjection(Termination termination) throws StructuralException {
		if (myProjectionMap.containsKey(termination)) {
			myProjectionMap.remove(termination);
		} else {
			throw new StructuralException("The Network contains no Projection ending on the specified Termination");
		}
		
		mySimulator.initialize(this);
	}

	/**
	 * @see ca.neo.model.Node#getName()
	 */
	public String getName() {
		return myName;
	}
	
	/**
	 * @param name New name of Network (must be unique within any networks of which this one 
	 * 		will be a part) 
	 */
	public void setName(String name) {
		myName = name;
	}

	/**
	 * @see ca.neo.model.Node#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
		myMode = mode;

		Iterator<Node> it = myNodeMap.values().iterator();
		while (it.hasNext()) {
			it.next().setMode(mode);
		}
	}

	/**
	 * @see ca.neo.model.Node#getMode()
	 */
	public SimulationMode getMode() {
		return myMode;
	}

	/**
	 * @see ca.neo.model.Node#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {
		mySimulator.run(startTime, endTime, myStepSize);
	}

	/**
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		Iterator it = myNodeMap.keySet().iterator();
		while (it.hasNext()) {
			Node n = (Node) myNodeMap.get(it.next());
			n.reset(randomize);
		}
	}

	/**
	 * @see ca.neo.model.Probeable#getHistory(java.lang.String)
	 */
	public TimeSeries getHistory(String stateName) throws SimulationException {
		Probeable p = (Probeable) myProbeables.get(stateName);
		String n = (String) myProbeableStates.get(stateName);
		
		return p.getHistory(n);
	}

	/**
	 * @see ca.neo.model.Probeable#listStates()
	 */
	public Properties listStates() {
		Properties result = new Properties();

		Iterator it = myProbeables.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Probeable p = (Probeable) myProbeables.get(key);
			String n = (String) myProbeableStates.get(key);
			result.put(key, p.listStates().getProperty(n));
		}
		
		return result;
	}

	/**
	 * @see ca.neo.model.Network#exposeOrigin(ca.neo.model.Origin, java.lang.String)
	 */
	public void exposeOrigin(Origin origin, String name) {
		myExposedOrigins.put(name, new OriginWrapper(this, origin, name));
	}

	/**
	 * @see ca.neo.model.Network#hideOrigin(java.lang.String)
	 */
	public void hideOrigin(String name) {
		myExposedOrigins.remove(name);
	}

	/**
	 * @see ca.neo.model.Network#getOrigin(java.lang.String)
	 */
	public Origin getOrigin(String name) throws StructuralException {
		if ( !myExposedOrigins.containsKey(name) ) {
			throw new StructuralException("There is no exposed Origin named " + name);
		}
		return (Origin) myExposedOrigins.get(name);
	}

	/**
	 * @see ca.neo.model.Network#getOrigins()
	 */
	public Origin[] getOrigins() {
		return (Origin[]) myExposedOrigins.entrySet().toArray(new Origin[0]);
	}

	/**
	 * @see ca.neo.model.Network#exposeTermination(ca.neo.model.Termination, java.lang.String)
	 */
	public void exposeTermination(Termination termination, String name) {
		myExposedTerminations.put(name, new TerminationWrapper(termination, name));
	}

	/**
	 * @see ca.neo.model.Network#hideTermination(java.lang.String)
	 */
	public void hideTermination(String name) {
		myExposedTerminations.remove(name);
	}

	/**
	 * @see ca.neo.model.Network#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		if ( !myExposedTerminations.containsKey(name) ) {
			throw new StructuralException("There is no exposed Termination named " + name);
		}
		return (Termination) myExposedTerminations.get(name);
	}

	/**
	 * @see ca.neo.model.Network#getTerminations()
	 */
	public Termination[] getTerminations() {
		return (Termination[]) myExposedTerminations.entrySet().toArray(new Termination[0]);
	}

	/**
	 * @see ca.neo.model.Network#exposeState(ca.neo.model.Probeable, java.lang.String, java.lang.String)
	 */
	public void exposeState(Probeable probeable, String stateName, String name) {
		myProbeables.put(name, probeable);
		myProbeableStates.put(name, stateName);
	}

	/**
	 * @see ca.neo.model.Network#hideState(java.lang.String)
	 */
	public void hideState(String name) {
		myProbeables.remove(name);
		myProbeableStates.remove(name);
	}
	
	/**
	 * Wraps an Origin with a new name (for exposing outside Network).
	 *  
	 * @author Bryan Tripp
	 */
	private class OriginWrapper implements Origin {
		
		private static final long serialVersionUID = 1L;
		
		private Node myNode;
		private Origin myWrapped;
		private String myName;
		
		public OriginWrapper(Node node, Origin wrapped, String name) {
			myNode = node;
			myWrapped = wrapped;
			myName = name;
		}

		public String getName() {
			return myName;
		}

		public int getDimensions() {
			return myWrapped.getDimensions();
		}

		public InstantaneousOutput getValues() throws SimulationException {
			return myWrapped.getValues();
		}

		public Node getNode() {
			return myNode;
		}
		
	}
	
	/**
	 * Wraps a Termination with a new name (for exposing outside Network).
	 *  
	 * @author Bryan Tripp
	 */
	private class TerminationWrapper implements Termination {

		private static final long serialVersionUID = 1L;
		
		private Termination myWrapped;
		private String myName;
		
		public TerminationWrapper(Termination wrapped, String name) {
			myWrapped = wrapped;
			myName = name;
		}
		
		public String getName() {
			return myName;
		}

		public int getDimensions() {
			return myWrapped.getDimensions();
		}

		public void setValues(InstantaneousOutput values) throws SimulationException {
			myWrapped.setValues(values);
		}

		public Configuration getConfiguration() {
			return myWrapped.getConfiguration();
		}

		public void propertyChange(String propertyName, Object newValue) throws StructuralException {
			myWrapped.propertyChange(propertyName, newValue);
		}
		
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
	 * @see ca.neo.model.Network#getMetaData(java.lang.String)
	 */
	public Object getMetaData(String key) {
		return myMetaData.get(key);
	}

	/**
	 * @see ca.neo.model.Network#setMetaData(java.lang.String, java.lang.Object)
	 */
	public void setMetaData(String key, Object value) {
		if ( !(value instanceof Serializable) ) {
			throw new RuntimeException("Metadata must be serializable");
		}
		myMetaData.put(key, value);
	}
	
}
