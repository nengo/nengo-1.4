/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "NetworkImpl.java". Description: 
"Default implementation of Network"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU 
Public License license (the GPL License), in which case the provisions of GPL 
License are applicable  instead of those above. If you wish to allow use of your 
version of this file only under the terms of the GPL License and not to allow 
others to use your version of this file under the MPL, indicate your decision 
by deleting the provisions above and replace  them with the notice and other 
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
*/

/*
 * Created on 23-May-2006
 */
package ca.nengo.model.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.LinkedList;
import java.util.Random;

import org.apache.log4j.Logger;

import ca.nengo.model.Ensemble;
import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.Probeable;
import ca.nengo.model.Projection;
import ca.nengo.model.SimulationException;
import ca.nengo.model.SimulationMode;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.nef.impl.NEFEnsembleImpl;
import ca.nengo.model.nef.impl.DecodableEnsembleImpl;
import ca.nengo.sim.Simulator;
import ca.nengo.sim.impl.LocalSimulator;
import ca.nengo.util.Probe;
import ca.nengo.util.TimeSeries;
import ca.nengo.util.VisiblyMutable;
import ca.nengo.util.VisiblyMutableUtils;

/**
 * Default implementation of Network. 
 *  
 * @author Bryan Tripp
 */
public class NetworkImpl implements Network, VisiblyMutable, VisiblyMutable.Listener {

	public static final String DEFAULT_NAME = "Network";
	
	private static final long serialVersionUID = 1L;
	private static Logger ourLogger = Logger.getLogger(NetworkImpl.class);
	
	private Map<String, Node> myNodeMap; //keyed on name
	private Map<Termination, Projection> myProjectionMap; //keyed on Termination
	private String myName;
	private SimulationMode myMode;
	private boolean myModeFixed;
	private Simulator mySimulator;
	private float myStepSize;
	private Map<String, Probeable> myProbeables;
	private Map<String, String> myProbeableStates;	
	private Map<String, Origin> myExposedOrigins;
	private Map<String, Termination> myExposedTerminations;
	
	private LinkedList <Origin> OrderedExposedOrigins;
	private LinkedList <Termination> OrderedExposedTerminations; 
	
	private String myDocumentation;
	private Map<String, Object> myMetaData;

	private Map<Origin, String> myExposedOriginNames;
	private Map<Termination, String> myExposedTerminationNames;
	
	private transient List<VisiblyMutable.Listener> myListeners;
	
	protected boolean myUseGPU = false;
	
	public NetworkImpl() {
		myNodeMap = new HashMap<String, Node>(20);
		myProjectionMap	= new HashMap<Termination, Projection>(50);
		myName = DEFAULT_NAME;
		myStepSize = .001f; 
		myProbeables = new HashMap<String, Probeable>(30);
		myProbeableStates = new HashMap<String, String>(30);
		myExposedOrigins = new HashMap<String, Origin>(10);
		myExposedOriginNames = new HashMap<Origin, String>(10);
		myExposedTerminations = new HashMap<String, Termination>(10);
		myExposedTerminationNames = new HashMap<Termination, String>(10);
		myMode = SimulationMode.DEFAULT;
		myModeFixed = false;
		myMetaData = new HashMap<String, Object>(20);
		myListeners = new ArrayList<Listener>(10);
		
		
		OrderedExposedOrigins = new LinkedList <Origin> ();
		OrderedExposedTerminations = new LinkedList <Termination> ();
	}
	
	/**
	 * @param simulator Simulator with which to run this Network
	 */
	public void setSimulator(Simulator simulator) {
		mySimulator = simulator;
		mySimulator.initialize(this);
	}
	
	/**
	 * @return Simulator used to run this Network (a LocalSimulator by default) 
	 */
	public Simulator getSimulator() {
		if (mySimulator == null) {
			mySimulator = new LocalSimulator();
			mySimulator.initialize(this);
		}
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
	 * @see ca.nengo.model.Network#addNode(ca.nengo.model.Node)
	 */
	public void addNode(Node node) throws StructuralException {
		if (myNodeMap.containsKey(node.getName())) {
			throw new StructuralException("This Network already contains a Node named " + node.getName());
		}
		
		myNodeMap.put(node.getName(), node);
		node.addChangeListener(this);
		
		getSimulator().initialize(this);		
		fireVisibleChangeEvent();
	}
	
	/***
	 * Kills a certain percentage of neurons in the network (recursively including subnetworks).
	 * 
	 * @param killrate the percentage (0.0 to 1.0) of neurons to kill
	 */
	public void killNeurons(float killrate)
	{
		killNeurons(killrate, false);
	}
	
	/***
	 * Kills a certain percentage of neurons in the network (recursively including subnetworks).
	 * 
	 * @param killrate the percentage (0.0 to 1.0) of neurons to kill
	 * @param saveRelays if true, exempt populations with only one node from the slaughter
	 */
	public void killNeurons(float killrate, boolean saveRelays)
	{	
		Node[] nodes = getNodes();
		for(int i = 0; i < nodes.length; i++)
		{
			if(nodes[i] instanceof NetworkImpl)
				((NetworkImpl)nodes[i]).killNeurons(killrate, saveRelays);
			else if(nodes[i] instanceof NEFEnsembleImpl)
				((NEFEnsembleImpl)nodes[i]).killNeurons(killrate, saveRelays);
		}
		
	}
	
	/**
	 * Kills a certain percentage of the dendritic inputs in the network (recursively including subnetworks).
	 * 
	 * @param killrate the percentage (0.0 to 1.0) of dendritic inputs to kill
	 */
//	public void killDendrites(float killrate)
//	{
//		Node[] nodes = getNodes();
//		for(int i = 0; i < nodes.length; i++)
//		{
//			if(nodes[i] instanceof NetworkImpl)
//				((NetworkImpl)nodes[i]).killDendrites(killrate);
//			else if(nodes[i] instanceof NEFEnsembleImpl)
//				((NEFEnsembleImpl)nodes[i]).killDendrites(killrate);
//		}
//		
//	}
	
	/**
	 * Handles any changes/errors that may arise from objects within the network changing.
	 *  
	 * @see ca.nengo.util.VisiblyMutable.Listener#changed(ca.nengo.util.VisiblyMutable.Event)
	 */
	public void changed(Event e) throws StructuralException {
		if (e instanceof VisiblyMutable.NameChangeEvent) {
			VisiblyMutable.NameChangeEvent ne = (VisiblyMutable.NameChangeEvent) e;
			
			if (myNodeMap.containsKey(ne.getNewName()) && !ne.getNewName().equals(ne.getOldName())) {
				throw new StructuralException("This Network already contains a Node named " + ne.getNewName());
			}
			
			/*
			 * Only do the swap if the name has changed. 
			 * Otherwise, the node will be dereferenced from the map. 
			 */
			if (!ne.getOldName().equals(ne.getNewName())) {
				myNodeMap.put(ne.getNewName(), myNodeMap.get(ne.getOldName()));
				myNodeMap.remove(ne.getOldName());
			}
		}
	}
	
	/**
	 * Gathers all the terminations of nodes contained in this network.
	 * 
	 * @return arraylist of terminations
	 */
	public ArrayList<Termination> getNodeTerminations()
	{
		ArrayList<Termination> nodeTerminations = new ArrayList<Termination>();
		Node[] nodes = getNodes();
		for(int i = 0; i < nodes.length; i++)
		{
			Termination[] terms = nodes[i].getTerminations();
			for(int j = 0; j < terms.length; j++)
				nodeTerminations.add(terms[j]);
		}
		
		return nodeTerminations;
	}
	
	/**
	 * Gathers all the origins of nodes contained in this network.
	 * 
	 * @return arraylist of origins
	 */
	public ArrayList<Origin> getNodeOrigins()
	{
		ArrayList<Origin> nodeOrigins = new ArrayList<Origin>();
		Node[] nodes = getNodes();
		for(int i = 0; i < nodes.length; i++)
		{
			Origin[] origs = nodes[i].getOrigins();
			for(int j = 0; j < origs.length; j++)
				nodeOrigins.add(origs[j]);
		}
		
		return nodeOrigins;
	}

	/**
	 * @see ca.nengo.model.Network#getNodes()
	 */
	public Node[] getNodes() {
		return myNodeMap.values().toArray(new Node[0]);
	}

	/**
	 * @see ca.nengo.model.Network#getNode(java.lang.String)
	 */
	public Node getNode(String name) throws StructuralException {
		if (!myNodeMap.containsKey(name)) {
			throw new StructuralException("No Node named " + name + " in this Network");			
		}
		return myNodeMap.get(name);
	}

	public int getNodeCount(){
		return getNodes().length;
	}
	
	public float getNeuronCount(){
		float neuron_count = 0;
		Node[] nodes = getNodes();
		
		for(int i = 0; i < nodes.length; i++)
		{
			if(nodes[i] instanceof NetworkImpl)
				neuron_count += ((NetworkImpl)nodes[i]).getNeuronCount();
			else if(nodes[i] instanceof NEFEnsembleImpl)
				neuron_count += ((NEFEnsembleImpl)nodes[i]).getNeuronCount();
		}
		
		return neuron_count;
	}
		
	/**
	 * @see ca.nengo.model.Network#removeNode(java.lang.String)
	 */
	public void removeNode(String name) throws StructuralException {
		if (myNodeMap.containsKey(name)) {
			Node node = myNodeMap.get(name);
			
			if(node instanceof Network)
			{
				Network net = (Network)node;
				Probe[] probes = net.getSimulator().getProbes();
				for(int i = 0; i < probes.length; i++)
					try
					{
						net.getSimulator().removeProbe(probes[i]);
					}
					catch(SimulationException se)
					{
						System.err.println(se);
						return;
					}
				
				Node[] nodes = net.getNodes();
				for(int i = 0; i < nodes.length; i++)
					net.removeNode(nodes[i].getName());
			}
			else if(node instanceof DecodableEnsembleImpl)
			{
				NEFEnsembleImpl pop = (NEFEnsembleImpl)node;
				Origin[] origins = pop.getOrigins();
				for(int i = 0; i < origins.length; i++)
				{
					String exposedName = getExposedOriginName(origins[i]);
					if(exposedName != null)
						hideOrigin(exposedName);
				}
			}
			
			myNodeMap.remove(name);
			node.removeChangeListener(this);
//			VisiblyMutableUtils.nodeRemoved(this, node, myListeners);
		} else {
			throw new StructuralException("No Node named " + name + " in this Network");
		}

		getSimulator().initialize(this);		
		fireVisibleChangeEvent();
	}

	/**
	 * @see ca.nengo.model.Network#addProjection(ca.nengo.model.Origin, ca.nengo.model.Termination)
	 */
	public Projection addProjection(Origin origin, Termination termination) throws StructuralException {
		if (myProjectionMap.containsKey(termination)) {
			throw new StructuralException("There is already an Origin connected to the specified Termination");
		}
		
		if (origin.getDimensions() != termination.getDimensions()) {
			throw new StructuralException("Can't connect Origin of dimension " + origin.getDimensions() 
					+ " to Termination of dimension " + termination.getDimensions());
		}
		
		Projection result = new ProjectionImpl(origin, termination, this);
		myProjectionMap.put(termination, result);
		getSimulator().initialize(this);
		fireVisibleChangeEvent();
		
		return result;
	}

	/**
	 * @see ca.nengo.model.Network#getProjections()
	 */
	public Projection[] getProjections() {
		return myProjectionMap.values().toArray(new Projection[0]);
	}

	/**
	 * @see ca.nengo.model.Network#removeProjection(ca.nengo.model.Termination)
	 */
	public void removeProjection(Termination termination) throws StructuralException {
		if (myProjectionMap.containsKey(termination)) {
			myProjectionMap.remove(termination);
		} else {
			throw new StructuralException("The Network contains no Projection ending on the specified Termination");
		}
		
		getSimulator().initialize(this);
		fireVisibleChangeEvent();
	}

	/**
	 * @see ca.nengo.model.Node#getName()
	 */
	public String getName() {
		return myName;
	}
	
	/**
	 * @param name New name of Network (must be unique within any networks of which this one 
	 * 		will be a part) 
	 */
	public void setName(String name) throws StructuralException {
		VisiblyMutableUtils.nameChanged(this, getName(), name, myListeners);
		myName = name;
	}
	

	/**
	 * @see ca.nengo.model.Node#setMode(ca.nengo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
		if(!myModeFixed)
		{
			myMode = mode;
	
			Iterator<Node> it = myNodeMap.values().iterator();
			while (it.hasNext()) {
				Node node = it.next();
				if(node instanceof ca.nengo.model.nef.impl.NEFEnsembleImpl)
				{
					if(!((ca.nengo.model.nef.impl.NEFEnsembleImpl)node).getModeFixed())
						node.setMode(mode);
				}
				else
					node.setMode(mode);
			}
		}
//		else
//			System.err.println("Warning, trying to change mode on fixedmode network " + myName);
	}
	protected void setMyMode(SimulationMode mode) {
		if(!myModeFixed)
			myMode = mode;
//		else
//			System.err.println("Warning, trying to change mode on fixedmode network " + myName);
	}
	
	public void fixMode()
	{
		myModeFixed = true;
	}

	/**
	 * @see ca.nengo.model.Node#getMode()
	 */
	public SimulationMode getMode() {
		return myMode;
	}

	/**
	 * @see ca.nengo.model.Node#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {
		getSimulator().run(startTime, endTime, myStepSize);
	}
	
	/**
	 * Runs the model with the optional parameter topLevel.
	 * 
	 * @param topLevel true if the network being run is the top level network, false if it is a subnetwork
	 */
	public void run(float startTime, float endTime, boolean topLevel) throws SimulationException
	{
		getSimulator().run(startTime, endTime, myStepSize, topLevel);
	}

	/**
	 * @see ca.nengo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		Iterator<String> it = myNodeMap.keySet().iterator();
		while (it.hasNext()) {
			Node n = myNodeMap.get(it.next());
			n.reset(randomize);
		}
	}
	
	public void setUseGPU(boolean val)
	{
		myUseGPU = val;
		Node[] nodes = getNodes();
		
		for(int i = 0; i < nodes.length; i++){
			Node workingNode = nodes[i];
			
			if(workingNode instanceof NEFEnsembleImpl){
				((NEFEnsembleImpl) workingNode).setUseGPU(val);
			}else if(workingNode instanceof NetworkImpl){
				((NetworkImpl) workingNode).setUseGPU(val);
			}
		}
	}
	
	public boolean getUseGPU(){
		return myUseGPU && myMode == SimulationMode.DEFAULT;
	}

	/**
	 * @see ca.nengo.model.Probeable#getHistory(java.lang.String)
	 */
	public TimeSeries getHistory(String stateName) throws SimulationException {
		Probeable p = myProbeables.get(stateName);
		String n = myProbeableStates.get(stateName);
		
		return p.getHistory(n);
	}

	/**
	 * @see ca.nengo.model.Probeable#listStates()
	 */
	public Properties listStates() {
		Properties result = new Properties();

		Iterator<String> it = myProbeables.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Probeable p = myProbeables.get(key);
			String n = myProbeableStates.get(key);
			result.put(key, p.listStates().getProperty(n));
		}
		
		return result;
	}

	/**
	 * @see ca.nengo.model.Network#exposeOrigin(ca.nengo.model.Origin,
	 *      java.lang.String)
	 */
	public void exposeOrigin(Origin origin, String name) {
		OriginWrapper temp = new OriginWrapper(this, origin, name); 
		
		myExposedOrigins.put(name, temp );
		myExposedOriginNames.put(origin, name);		
		OrderedExposedOrigins.add(temp);
				
		// automatically add exposed origin to exposed states
		if (origin.getNode() instanceof Probeable) {
			Probeable p=(Probeable)(origin.getNode());			
			try {
				exposeState(p,origin.getName(),name);
			} catch (StructuralException e) {					
			}
		}
		fireVisibleChangeEvent();
	}

	/**
	 * @see ca.nengo.model.Network#hideOrigin(java.lang.String)
	 */
	public void hideOrigin(String name) throws StructuralException {
		if(myExposedOrigins.get(name) == null)
			throw new StructuralException("No origin named " + name + " exists");
		
		OrderedExposedOrigins.remove(myExposedOrigins.get(name));
		OriginWrapper originWr = (OriginWrapper)myExposedOrigins.remove(name);

				
		if (originWr != null) {
			myExposedOriginNames.remove(originWr.myWrapped);
					
			
			// remove the automatically exposed state
			if (originWr.myWrapped.getNode() instanceof Probeable) {
				this.hideState(name);				
			}
		}
		
		fireVisibleChangeEvent();
	}

	/**
	 * @see ca.nengo.model.Network#getExposedOriginName(ca.nengo.model.Origin)
	 */
	public String getExposedOriginName(Origin insideOrigin) {
		return myExposedOriginNames.get(insideOrigin);
	}

	/**
	 * @see ca.nengo.model.Network#getOrigin(java.lang.String)
	 */
	public Origin getOrigin(String name) throws StructuralException {
		if ( !myExposedOrigins.containsKey(name) ) {
			throw new StructuralException("There is no exposed Origin named " + name);
		}
		return myExposedOrigins.get(name);
	}

	/**
	 * @see ca.nengo.model.Network#getOrigins()
	 */
	public Origin[] getOrigins() {
		if (myExposedOrigins.values().size() == 0)
			return myExposedOrigins.values().toArray(new Origin[0]);	
		return OrderedExposedOrigins.toArray(new Origin [0]);			
	}

	/**
	 * @see ca.nengo.model.Network#exposeTermination(ca.nengo.model.Termination, java.lang.String)
	 */
	public void exposeTermination(Termination termination, String name) {
		TerminationWrapper term = new TerminationWrapper(this, termination, name);
		
		myExposedTerminations.put(name, term);
		myExposedTerminationNames.put(termination, name);		
		OrderedExposedTerminations.add(term);
		fireVisibleChangeEvent();
	}

	/**
	 * @see ca.nengo.model.Network#hideTermination(java.lang.String)
	 */
	public void hideTermination(String name) {
		OrderedExposedTerminations.remove(myExposedTerminations.get(name));
		TerminationWrapper termination = (TerminationWrapper)myExposedTerminations.remove(name);
		if (termination != null) {			
			myExposedTerminationNames.remove(termination.myWrapped);
		}
		fireVisibleChangeEvent();
	}

	/**
	 * @see ca.nengo.model.Network#getExposedTerminationName(ca.nengo.model.Termination)
	 */
	public String getExposedTerminationName(Termination insideTermination) {
		return myExposedTerminationNames.get(insideTermination);
	}	

	/**
	 * @see ca.nengo.model.Network#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		if ( !myExposedTerminations.containsKey(name) ) {
			throw new StructuralException("There is no exposed Termination named " + name);
		}
		return myExposedTerminations.get(name);
	}

	/**
	 * @see ca.nengo.model.Network#getTerminations()
	 */
	public Termination[] getTerminations() {
		if (myExposedTerminations.values().size() == 0)
			return myExposedTerminations.values().toArray(new Termination[0]);
		return OrderedExposedTerminations.toArray(new Termination[0]);
	}

	/**
	 * @see ca.nengo.model.Network#exposeState(ca.nengo.model.Probeable, java.lang.String, java.lang.String)
	 */
	public void exposeState(Probeable probeable, String stateName, String name) throws StructuralException {
		if (probeable.listStates().get(stateName) == null) {
			throw new StructuralException("The state " + stateName + " does not exist");
		}
		
		myProbeables.put(name, probeable);
		myProbeableStates.put(name, stateName);
	}

	/**
	 * @see ca.nengo.model.Network#hideState(java.lang.String)
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
	public class OriginWrapper implements Origin {
		
		private static final long serialVersionUID = 1L;
		
		private Node myNode;
		private Origin myWrapped;
		private String myName;
		
		public OriginWrapper(Node node, Origin wrapped, String name) {
			myNode = node;
			myWrapped = wrapped;
			myName = name;
		}
		
		public OriginWrapper() {
			this(null, null, "exposed");
		}
		
		public Origin getWrappedOrigin() {
			return myWrapped;
		}
		
		// unwraps origin until it finds one that isn't wrapped
		public Origin getBaseOrigin(){
			if(myWrapped instanceof OriginWrapper)
				return ((OriginWrapper) myWrapped).getBaseOrigin();
			else
				return myWrapped;
		}
		
		public void setWrappedOrigin(Origin wrapped) {
			myWrapped = wrapped;
		}

		public String getName() {
			return myName;
		}
		
		public void setName(String name) {
			myName = name;
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
		
		public void setNode(Node node) {
			myNode = node;
		}

		@Override
		public Origin clone() throws CloneNotSupportedException {
			return (Origin) super.clone();
		}
		
	}
	
	/**
	 * Wraps a Termination with a new name (for exposing outside Network).
	 *  
	 * @author Bryan Tripp
	 */
	public class TerminationWrapper implements Termination {

		private static final long serialVersionUID = 1L;
		
		private Node myNode;
		private Termination myWrapped;
		private String myName;
		
		public TerminationWrapper(Node node, Termination wrapped, String name) {
			myNode = node;
			myWrapped = wrapped;
			myName = name;
		}
		
		public Termination getWrappedTermination() {
			return myWrapped;
		}
		
		// unwraps terminations until it finds one that isn't wrapped
		public Termination getBaseTermination(){
			if(myWrapped instanceof TerminationWrapper)
				return ((TerminationWrapper) myWrapped).getBaseTermination();
			else
				return myWrapped;
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

		public Node getNode() {
			return myNode;
		}

		public boolean getModulatory() {
			return myWrapped.getModulatory();
		}

		public float getTau() {
			return myWrapped.getTau();
		}

		public void setModulatory(boolean modulatory) {
			myWrapped.setModulatory(modulatory);
		}

		public void setTau(float tau) throws StructuralException {
			myWrapped.setTau(tau);
		}

		/**
		 * @see ca.nengo.model.Resettable#reset(boolean)
		 */
		public void reset(boolean randomize) {
			myWrapped.reset(randomize);
		}

		@Override
		public Termination clone() throws CloneNotSupportedException {
			return (Termination) super.clone();
		}
		
	}

	/**
	 * @see ca.nengo.model.Node#getDocumentation()
	 */
	public String getDocumentation() {
		return myDocumentation;
	}

	/**
	 * @see ca.nengo.model.Node#setDocumentation(java.lang.String)
	 */
	public void setDocumentation(String text) {
		myDocumentation = text;
	}

	/**
	 * @see ca.nengo.model.Network#getMetaData(java.lang.String)
	 */
	public Object getMetaData(String key) {
		return myMetaData.get(key);
	}

	/**
	 * @see ca.nengo.model.Network#setMetaData(java.lang.String, java.lang.Object)
	 */
	public void setMetaData(String key, Object value) {
		if ( !(value instanceof Serializable) ) {
			throw new RuntimeException("Metadata must be serializable");
		}
		myMetaData.put(key, value);
	}

	/**
	 * @see ca.nengo.util.VisiblyMutable#addChangeListener(ca.nengo.util.VisiblyMutable.Listener)
	 */
	public void addChangeListener(Listener listener) {
		if (myListeners == null) {
			myListeners = new ArrayList<Listener>(1);
		}
		myListeners.add(listener);
	}

	/**
	 * @see ca.nengo.util.VisiblyMutable#removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)
	 */
	public void removeChangeListener(Listener listener) {
		if (myListeners != null) myListeners.remove(listener);
	}
	
	private void fireVisibleChangeEvent() {
		VisiblyMutableUtils.changed(this, myListeners);
	}

	@Override
	public Network clone() throws CloneNotSupportedException {
		NetworkImpl result = (NetworkImpl) super.clone();
		
		result.myNodeMap = new HashMap<String, Node>(10);
		for (Node oldNode : myNodeMap.values()) {
			Node newNode = oldNode.clone();
			result.myNodeMap.put(newNode.getName(), newNode);
			newNode.addChangeListener(result);
		}
		
		//TODO: Exposed states aren't handled currently, pending redesign of Probes (it should be possible 
		//		to probe things that are nested deeply, in which case exposing state woulnd't be necessary) 
//		result.myProbeables
//		result.myProbeableStates
		
		//TODO: this works with a single Projection impl & no params; should add Projection.copy(Origin, Termination, Network)?  
		result.myProjectionMap = new HashMap<Termination, Projection>(10);
		for (Projection oldProjection : getProjections()) {
			try {
				Origin newOrigin = result.getNode(oldProjection.getOrigin().getNode().getName())
					.getOrigin(oldProjection.getOrigin().getName());
				Termination newTermination = result.getNode(oldProjection.getTermination().getNode().getName())
					.getTermination(oldProjection.getTermination().getName());
				Projection newProjection = new ProjectionImpl(newOrigin, newTermination, result);
				result.myProjectionMap.put(newTermination, newProjection);
			} catch (StructuralException e) {
				throw new CloneNotSupportedException("Problem copying Projectio: " + e.getMessage());
			}
		}
		
		for (Origin exposed : getOrigins()) {
			String name = exposed.getName();
			Origin wrapped = ((OriginWrapper) exposed).getWrappedOrigin();
			try {
				Origin toExpose = result.getNode(wrapped.getNode().getName()).getOrigin(wrapped.getName());
				result.exposeOrigin(toExpose, name);				
			} catch (StructuralException e) {
				throw new CloneNotSupportedException("Problem exposing Origin: " + e.getMessage());
			}
		}

		for (Termination exposed : getTerminations()) {
			String name = exposed.getName();
			Termination wrapped = ((TerminationWrapper) exposed).getWrappedTermination();
			try {
				Termination toExpose = result.getNode(wrapped.getNode().getName()).getTermination(wrapped.getName());
				result.exposeTermination(toExpose, name);
			} catch (StructuralException e) {
				throw new CloneNotSupportedException("Problem exposing Termination: " + e.getMessage());
			}
		}

		result.myListeners = new ArrayList<Listener>(5);
		
		result.myMetaData = new HashMap<String, Object>(10);
		for (String key : myMetaData.keySet()) {
			Object o = myMetaData.get(key);
			if (o instanceof Cloneable) {
				Object copy = tryToClone((Cloneable) o);
				result.myMetaData.put(key, copy);
			} else {
				result.myMetaData.put(key, o);
			}
		}
		
		//TODO: take another look at Probe design (maybe Probeables reference Probes?)  
		result.mySimulator = mySimulator.clone();
		result.mySimulator.initialize(result);
		Probe[] oldProbes = mySimulator.getProbes();
		for (int i = 0; i < oldProbes.length; i++) {
			Probeable target = oldProbes[i].getTarget();
			if (target instanceof Node) {
				Node oldNode = (Node) target;
				if (oldProbes[i].isInEnsemble()) {
					try {
						Ensemble oldEnsemble = (Ensemble) getNode(oldProbes[i].getEnsembleName());
						int neuronIndex = -1;
						for (int j = 0; j < oldEnsemble.getNodes().length && neuronIndex < 0; j++) {
							if (oldNode == oldEnsemble.getNodes()[j]) neuronIndex = j;
						}
						result.mySimulator.addProbe(oldProbes[i].getEnsembleName(), neuronIndex, oldProbes[i].getStateName(), true);						
					} catch (SimulationException e) {
						ourLogger.warn("Problem copying Probe", e);
					} catch (StructuralException e) {
						ourLogger.warn("Problem copying Probe", e);
					}
				} else {
					try {
						result.mySimulator.addProbe(oldNode.getName(), oldProbes[i].getStateName(), true);
					} catch (SimulationException e) {
						ourLogger.warn("Problem copying Probe", e);
					}
				}
			} else {
				ourLogger.warn("Can't copy Probe on type " + target.getClass().getName() 
						+ " (to be addressed in a future release)");
			}
		}

		return result;
	}
	
	private static Object tryToClone(Cloneable o) {
		Object result = null;
		
		try {
			Method cloneMethod = o.getClass().getMethod("clone", new Class[0]);
			result = cloneMethod.invoke(o, new Object[0]);
		} catch (Exception e) {
			ourLogger.warn("Couldn't clone data of type " + o.getClass().getName(), e);
		} 
		
		return result;
	}

}
