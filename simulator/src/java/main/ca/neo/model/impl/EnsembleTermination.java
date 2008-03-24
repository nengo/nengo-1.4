/*
 * Created on 31-May-2006
 */
package ca.neo.model.impl;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Node;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;

/**
 * <p>A Termination that is composed of Terminations onto multiple Nodes. 
 * The dimensions of the Terminations onto each Node must be the same.</p>
 * 
 * <p>Physiologically, this might correspond to a set of n axons passing into 
 * a neuron pool. Each neuron in the pool receives synaptic connections 
 * from as many as n of these axons (zero weight is equivalent to no 
 * connection). Sometimes we deal with this set of axons only in terms 
 * of the branches they send to one specific Neuron (a Node-level Termination)
 * but here we deal with all branches (an Emsemble-level Termination). 
 * In either case the spikes transmitted by the axons are the same.</p>  
 * 
 * TODO: test
 *  
 * @author Bryan Tripp
 */
public class EnsembleTermination implements Termination {

	private static final long serialVersionUID = 1L;
	
	private Node myNode;
	private String myName;
	private Termination[] myNodeTerminations;
	
	/**
	 * @param node The parent Node
	 * @param name Name of this Termination
	 * @param nodeTerminations Node-level Terminations that make up this Termination
	 * @throws StructuralException If dimensions of different terminations are not all the same
	 */
	public EnsembleTermination(Node node, String name, Termination[] nodeTerminations) throws StructuralException {
		checkSameDimension(nodeTerminations, name);
		
		myNode = node;
		myName = name;
		myNodeTerminations = nodeTerminations;
	}
	
	private static void checkSameDimension(Termination[] terminations, String name) throws StructuralException {
		int dim = terminations[0].getDimensions();
		for (int i = 1; i < terminations.length; i++) {
			if (terminations[i].getDimensions() != dim) {
				throw new StructuralException("All Terminations " + name + " must have the same dimension");
			}
		}
	}
	
	/**
	 * @see ca.neo.model.Termination#getName()
	 */
	public String getName() {
		return myName;
	}

	/**
	 * @see ca.neo.model.Termination#getDimensions()
	 */
	public int getDimensions() {
		return myNodeTerminations[0].getDimensions();
	}

	/**
	 * @see ca.neo.model.Termination#setValues(ca.neo.model.InstantaneousOutput)
	 */
	public void setValues(InstantaneousOutput values) throws SimulationException {
		if (values.getDimension() != getDimensions()) {
			throw new SimulationException("Input to this Termination must have dimension " + getDimensions());
		}
		
		for (int i = 0; i < myNodeTerminations.length; i++) {
			myNodeTerminations[i].setValues(values);
		}
	}

	/**
	 * Returns true if more than half of node terminations are modulatory. 
	 * @see ca.neo.model.Termination#getModulatory()
	 */
	public boolean getModulatory() {
		int nModulatory = 0;
		for (int i = 0; i < myNodeTerminations.length; i++) {
			if (myNodeTerminations[i].getModulatory()) nModulatory++;
		}
		return nModulatory > myNodeTerminations.length/2;
	}

	/**
	 * Returns the average. 
	 * 
	 * @see ca.neo.model.Termination#getTau()
	 */
	public float getTau() {
		float sumTau = 0;
		for (int i = 0; i < myNodeTerminations.length; i++) {
			sumTau += myNodeTerminations[i].getTau();
		}
		return sumTau / (float) myNodeTerminations.length;
	}

	/**
	 * @see ca.neo.model.Termination#setModulatory(boolean)
	 */
	public void setModulatory(boolean modulatory) {
		for (int i = 0; i < myNodeTerminations.length; i++) {
			myNodeTerminations[i].setModulatory(modulatory);
		}
	}

	/**
	 * @see ca.neo.model.Termination#setTau(float)
	 */
	public void setTau(float tau) throws StructuralException {
		float[] oldValues = new float[myNodeTerminations.length];
		
		for (int i = 0; i < myNodeTerminations.length; i++) {
			oldValues[i] = myNodeTerminations[i].getTau();
			try {
				myNodeTerminations[i].setTau(tau);
			} catch (StructuralException e) {
				//roll back changes
				for (int j = 0; j < i; j++) {
					myNodeTerminations[j].setTau(oldValues[j]); 
				}
				throw new StructuralException(e);
			}
		}
	}

	/**
	 * @see ca.neo.model.Termination#getNode()
	 */
	public Node getNode() {
		return myNode;
	}

	/**
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		for (int i = 0; i < myNodeTerminations.length; i++) {
			myNodeTerminations[i].reset(randomize);
		}
	}

	@Override
	public Termination clone() throws CloneNotSupportedException {
		try {
			return new EnsembleTermination(myNode, myName, myNodeTerminations);
		} catch (StructuralException e) {
			throw new CloneNotSupportedException("Error trying to clone: " + e.getMessage());
		}
	}

}
