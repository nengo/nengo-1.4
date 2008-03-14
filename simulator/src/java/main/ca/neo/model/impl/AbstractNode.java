/*
 * Created on 9-Mar-07
 */
package ca.neo.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.util.VisiblyMutable;
import ca.neo.util.VisiblyMutableUtils;

/**
 * A base implementation of Node. 
 * 
 * @author Bryan Tripp
 */
public abstract class AbstractNode implements Node {

	private String myName;
	private SimulationMode myMode;
	private Map<String, Origin> myOrigins;
	private Map<String, Termination> myTerminations;
	private String myDocumentation;
	private transient List<VisiblyMutable.Listener> myListeners;
	
	/**
	 * @param name Name of Node
	 * @param origins List of Origins from the Node
	 * @param terminations List of Terminations onto the Node
	 */
	public AbstractNode(String name, List<Origin> origins, List<Termination> terminations) {
		myName = name;
		myMode = SimulationMode.DEFAULT;
		
		myOrigins = new HashMap<String, Origin>(10);
		for (Iterator<Origin> it = origins.iterator(); it.hasNext(); ) {
			Origin o = it.next();
			myOrigins.put(o.getName(), o);
		}
		
		myTerminations = new HashMap<String, Termination>(10);
		for (Iterator<Termination> it = terminations.iterator(); it.hasNext(); ) {
			Termination t = it.next();
			myTerminations.put(t.getName(), t);
		}
	}

	/**
	 * @see ca.neo.model.Node#getMode()
	 */
	public SimulationMode getMode() {
		return myMode;
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
	 * @see ca.neo.model.Node#getOrigin(java.lang.String)
	 */
	public Origin getOrigin(String name) throws StructuralException {
		return myOrigins.get(name);
	}

	/**
	 * @see ca.neo.model.Node#getOrigins()
	 */
	public Origin[] getOrigins() {
		return myOrigins.values().toArray(new Origin[0]);
	}

	/**
	 * @see ca.neo.model.Node#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		return myTerminations.get(name);
	}

	/**
	 * @see ca.neo.model.Node#getTerminations()
	 */
	public Termination[] getTerminations() {
		return myTerminations.values().toArray(new Termination[0]);
	}

	/**
	 * Does nothing. 
	 * 
	 * @see ca.neo.model.Node#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {
	}

	/**
	 * @see ca.neo.model.Node#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
		myMode = mode;
	}

	/**
	 * Does nothing. 
	 * 
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
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

	/**
	 * Performs a shallow copy. Origins and Terminations are not cloned, because generally they 
	 * will have to be reparameterized, at least to point to the new Node. 
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Node clone() throws CloneNotSupportedException {
		Node result = (Node) super.clone();
		return result;
	}

}
