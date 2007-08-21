/*
 * Created on 31-May-2006
 */
package ca.neo.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.neo.model.ExpandableNode;
import ca.neo.model.Node;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.plasticity.Plastic;
import ca.neo.model.plasticity.PlasticityRule;

/**
 * <p>Default implementation of Ensemble.</p>
 * 
 * <p>Origins or Terminations can be set up on Nodes before they are grouped into an 
 * Ensemble. After Nodes are added to an Ensemble, no Origins or Terminations should 
 * be added to them directly. Terminations can be added with EnsembleImpl.addTermination(...) 
 * If a Termination is added directly to a Node after the Node is added to the 
 * Ensemble, the Termination will not appear in Ensemble.getTerminations()</p>  
 * 
 * TODO: test
 * 
 * @author Bryan Tripp
 */
public class EnsembleImpl extends AbstractEnsemble implements ExpandableNode, Plastic {

	private static final long serialVersionUID = 1L;
	
	private ExpandableNode[] myExpandableNodes;
	private Map<String, Termination> myExpandedTerminations;
	
	/**
	 * @param name Name of Ensemble
	 * @param nodes Nodes that make up the Ensemble
	 * @throws StructuralException if the given Nodes contain Terminations with the same 
	 * 		name but different dimensions
	 */
	public EnsembleImpl(String name, Node[] nodes) throws StructuralException {
		super(name, nodes);
		
		myExpandableNodes = findExpandable(nodes);
		myExpandedTerminations = new HashMap<String, Termination>(10);		
	}
	
	public EnsembleImpl(String name, NodeFactory factory, int n) throws StructuralException {
		super(name, make(factory, n));
		
		myExpandableNodes = findExpandable(getNodes());
		myExpandedTerminations = new HashMap<String, Termination>(10);				
	}
	
	private static Node[] make(NodeFactory factory, int n) throws StructuralException {
		Node[] result = new Node[n];
		
		for (int i = 0; i < n; i++) {
			result[i] = factory.make("node " + i);
		}
		
		return result;
	}

	//finds neurons with expandable synaptic integrators 
	private static ExpandableNode[] findExpandable(Node[] nodes) {
		ArrayList<ExpandableNode> result = new ArrayList<ExpandableNode>(nodes.length * 2);
		
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] instanceof ExpandableNode) {
				result.add((ExpandableNode) nodes[i]);
			}
		}
		
		return result.toArray(new ExpandableNode[0]);
	}

	/**
	 * @see ca.neo.model.Ensemble#getTerminations()
	 */
	public Termination[] getTerminations() {
		ArrayList<Termination> result = new ArrayList<Termination>(10);
		result.addAll(myExpandedTerminations.values());

		Termination[] composites = super.getTerminations();
		for (int i = 0; i < composites.length; i++) {
			result.add(composites[i]);
		}
		
		return result.toArray(new Termination[0]);
	}

	/**
	 * This Ensemble does not support SimulationMode.DIRECT. 
	 * 
	 * @see ca.neo.model.Ensemble#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
//		if (mode.equals(SimulationMode.DIRECT)) {
//			mode = mode.getFallbackMode();
//		}
		
		super.setMode(mode);
	}

	/**
	 * @param weights Each row is used as a 1 by m matrix of weights in a new termination on the nth expandable node
	 * 
	 * @see ca.neo.model.ExpandableNode#addTermination(java.lang.String, float[][], float, boolean)
	 */
	public synchronized Termination addTermination(String name, float[][] weights, float tauPSC, boolean modulatory) throws StructuralException {
		//TODO: check name for duplicate
		if (myExpandableNodes.length != weights.length) {
			throw new StructuralException(weights.length + " sets of weights given for " 
					+ myExpandableNodes.length + " expandable nodes");
		}
		
		int dimension = weights[0].length;
		
		Termination[] components = new Termination[myExpandableNodes.length];
		for (int i = 0; i < myExpandableNodes.length; i++) {
			if (weights[i].length != dimension) {
				throw new StructuralException("Equal numbers of weights are needed for termination onto each node");
			}
			
			components[i] = myExpandableNodes[i].addTermination(name, new float[][]{weights[i]}, tauPSC, modulatory);
		}
		
		Termination result = new EnsembleTermination(this, name, components);
		if (modulatory) {
			result.getConfiguration().setProperty(Termination.MODULATORY, new Boolean(true));
		}
		
		myExpandedTerminations.put(name, result);
		
		return result;
	}

	/**
	 * @see ca.neo.model.ExpandableNode#removeTermination(java.lang.String)
	 */
	public synchronized void removeTermination(String name) {
		myExpandedTerminations.remove(name);
	}

	/** 
	 * @see ca.neo.model.ExpandableNode#getDimension()
	 */
	public int getDimension() {
		return myExpandableNodes.length;
	}

	/**
	 * @see ca.neo.model.Ensemble#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		return myExpandedTerminations.containsKey(name) ? myExpandedTerminations.get(name) : super.getTermination(name);
	}

	/**
	 * Sets the given plasticity rule for all Plastic Nodes in the Ensemble.
	 *  
	 * @see ca.neo.model.plasticity.Plastic#setPlasticityRule(java.lang.String, ca.neo.model.plasticity.PlasticityRule)
	 */
	public void setPlasticityRule(String terminationName, PlasticityRule rule) throws StructuralException {
		for (int i = 0; i < myExpandableNodes.length; i++) {
			if (myExpandableNodes[i] instanceof Plastic) {
				((Plastic) myExpandableNodes[i]).setPlasticityRule(terminationName, rule);				
			}
		}
	}

	/**
	 * @see ca.neo.model.plasticity.Plastic#setPlasticityInterval(float)
	 */
	public void setPlasticityInterval(float time) {
		for (int i = 0; i < myExpandableNodes.length; i++) {
			if (myExpandableNodes[i] instanceof Plastic) {
				((Plastic) myExpandableNodes[i]).setPlasticityInterval(time);				
			}
		}
	}

}
