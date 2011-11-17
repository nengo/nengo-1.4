/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "EnsembleImpl.java". Description:
"Default implementation of Ensemble.

  Origins or Terminations can be set up on Nodes before they are grouped into an
  Ensemble"

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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import ca.nengo.model.Ensemble;
import ca.nengo.model.ExpandableNode;
import ca.nengo.model.Node;
import ca.nengo.model.SimulationMode;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;

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
public class EnsembleImpl extends AbstractEnsemble implements ExpandableNode {

	private static final long serialVersionUID = 1L;

	protected final ExpandableNode[] myExpandableNodes;
	protected Map<String, Termination> myExpandedTerminations;

	/**
	 * @param name Name of Ensemble
	 * @param nodes Nodes that make up the Ensemble
	 * @throws StructuralException if the given Nodes contain Terminations with the same
	 * 		name but different dimensions
	 */
	public EnsembleImpl(String name, Node[] nodes) throws StructuralException {
		super(name, nodes);

		myExpandableNodes = findExpandable(nodes);
		myExpandedTerminations = new LinkedHashMap<String, Termination>(10);
	}

	public EnsembleImpl(String name, NodeFactory factory, int n) throws StructuralException {
		super(name, make(factory, n));

		myExpandableNodes = findExpandable(this.getNodes());
		myExpandedTerminations = new LinkedHashMap<String, Termination>(10);
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

		for (Node node : nodes) {
			if (node instanceof ExpandableNode) {
				result.add((ExpandableNode) node);
			}
		}

		return result.toArray(new ExpandableNode[0]);
	}

    /**
     * @see ca.nengo.model.Node#getTermination(java.lang.String)
     */
    @Override
    public Termination getTermination(String name) throws StructuralException {
        return myExpandedTerminations.containsKey(name) ?
                myExpandedTerminations.get(name) : super.getTermination(name);
    }

	/**
	 * @see ca.nengo.model.Ensemble#getTerminations()
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
	 * @see ca.nengo.model.Ensemble#setMode(ca.nengo.model.SimulationMode)
	 */
	@Override
    public void setMode(SimulationMode mode) {
		super.setMode(mode);
	}

	/**
	 * @param weights Each row is used as a 1 by m matrix of weights in a new termination on the nth expandable node
	 *
	 * @see ca.nengo.model.ExpandableNode#addTermination(java.lang.String, float[][], float, boolean)
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

		EnsembleTermination result = new EnsembleTermination(this, name, components);
		myExpandedTerminations.put(name, result);

		fireVisibleChangeEvent();

		return result;
	}

	/**
	 * @throws StructuralException
	 * @see ca.nengo.model.ExpandableNode#removeTermination(java.lang.String)
	 */
	@Override
    public synchronized Termination removeTermination(String name) throws StructuralException {
		if (myExpandedTerminations.containsKey(name)) {
		    Termination result = myExpandedTerminations.remove(name);
			for (ExpandableNode myExpandableNode : myExpandableNodes) {
				myExpandableNode.removeTermination(name);
			}

			fireVisibleChangeEvent();
			return result;
		} else if (getTermination(name) != null) {
			return super.removeTermination(name);
		}
		throw new StructuralException("Termination " + name + " does not exist");
	}

	/**
	 * @see ca.nengo.model.ExpandableNode#getDimension()
	 */
    public int getDimension() {
		return myExpandableNodes.length;
	}

	@Override
	public Ensemble clone() throws CloneNotSupportedException {
		EnsembleImpl result = (EnsembleImpl) super.clone();

		//Note: at this point, AbstractEnsemble.clone() has called its init() method,
		//which sets up EnsembleTerminations based on existing node terminations. Since
		//the nodes have been cloned, this includes any "expanded terminations" created
		//on this EnsembleImpl. We now move these terminations from AbstractEnsemble
		//EnsembleImpl, where they belong ...
        
		result.myExpandedTerminations = new LinkedHashMap<String, Termination>(10);

		for (String key : myExpandedTerminations.keySet()) {
			try {
				Termination et = result.removeTermination(key).clone();
				result.myExpandedTerminations.put(key, et);
			} catch (StructuralException e) {
				throw new CloneNotSupportedException(e.getMessage());
			}
		}

		return result;
	}
}
