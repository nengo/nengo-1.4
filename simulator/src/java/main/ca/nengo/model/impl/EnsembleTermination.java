/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "EnsembleTermination.java". Description:
"A Termination that is composed of Terminations onto multiple Nodes"

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
 * Created on 31-May-2006
 */
package ca.nengo.model.impl;

import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.SimulationException;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;

/**
 * <p>A Termination that is composed of Terminations onto multiple Nodes.
 * The dimensions of the Terminations onto each Node must be the same.</p>
 *
 * <p>Physiologically, this might correspond to a set of n axons passing into
 * a neuron pool. Each neuron in the pool receives synaptic connections
 * from as many as n of these axons (zero weight is equivalent to no
 * connection). Sometimes we deal with this set of axons only in terms
 * of the branches they send to one specific Neuron (a Node-level Termination)
 * but here we deal with all branches (an Ensemble-level Termination).
 * In either case the spikes transmitted by the axons are the same.</p>
 *
 * TODO: test
 *
 * @author Bryan Tripp
 */
public class EnsembleTermination implements Termination {

	private static final long serialVersionUID = 1L;

	private final Node myNode;
	private final String myName;
	private final Termination[] myNodeTerminations;

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
	 * @see ca.nengo.model.Termination#getName()
	 */
    public String getName() {
		return myName;
	}

	/**
	 * @see ca.nengo.model.Termination#getDimensions()
	 */
    public int getDimensions() {
		return myNodeTerminations[0].getDimensions();
	}

	/**
	 * @see ca.nengo.model.Termination#setValues(ca.nengo.model.InstantaneousOutput)
	 */
    public void setValues(InstantaneousOutput values) throws SimulationException {
		if (values.getDimension() != getDimensions()) {
			throw new SimulationException("Input to this Termination must have dimension " + getDimensions());
		}

		for (Termination myNodeTermination : myNodeTerminations) {
			myNodeTermination.setValues(values);
		}
	}

	/**
	 * @return Latest input to the underlying terminations.
	 */
	public InstantaneousOutput getInput(){
		return myNodeTerminations[0].getInput();
	}

	/**
	 * Returns true if more than half of node terminations are modulatory.
	 * @see ca.nengo.model.Termination#getModulatory()
	 */
    public boolean getModulatory() {
		int nModulatory = 0;
		for (Termination myNodeTermination : myNodeTerminations) {
			if (myNodeTermination.getModulatory()) {
                nModulatory++;
            }
		}
		return nModulatory > myNodeTerminations.length/2;
	}

	/**
	 * Returns the average.
	 *
	 * @see ca.nengo.model.Termination#getTau()
	 */
    public float getTau() {
		float sumTau = 0;
		for (Termination myNodeTermination : myNodeTerminations) {
			sumTau += myNodeTermination.getTau();
		}
		return sumTau / myNodeTerminations.length;
	}

	/**
	 * @see ca.nengo.model.Termination#setModulatory(boolean)
	 */
    public void setModulatory(boolean modulatory) {
		for (Termination myNodeTermination : myNodeTerminations) {
			myNodeTermination.setModulatory(modulatory);
		}
	}

	/**
	 * @see ca.nengo.model.Termination#setTau(float)
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
	 * @see ca.nengo.model.Termination#getNode()
	 */
    public Node getNode() {
		return myNode;
	}

	/**
	 * @return Array with all of the underlying node terminations
	 */
	public Termination[] getNodeTerminations(){
		return myNodeTerminations;
	}

	/**
	 * @see ca.nengo.model.Resettable#reset(boolean)
	 */
    public void reset(boolean randomize) {
		for (Termination myNodeTermination : myNodeTerminations) {
			myNodeTermination.reset(randomize);
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
