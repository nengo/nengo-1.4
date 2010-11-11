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
import ca.nengo.model.StructuralException;
import ca.nengo.model.PlasticTermination;

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
 * @author Trevor Bekolay
 */
public class PlasticEnsembleTermination extends EnsembleTermination implements PlasticTermination {

	private static final long serialVersionUID = 1L;
	
	/**
	 * @param node The parent Node
	 * @param name Name of this Termination
	 * @param nodeTerminations Node-level Terminations that make up this Termination. Must be
	 *        all LinearExponentialTerminations
	 * @throws StructuralException If dimensions of different terminations are not all the same
	 */
	public PlasticEnsembleTermination(Node node, String name, LinearExponentialTermination[] nodeTerminations) throws StructuralException {
		super(node, name, nodeTerminations);
	}
	
	/**
	 * @see ca.nengo.model.PlasticTermination#getTransform()
	 */
	public float[][] getTransform() {
		float[][] transform = new float[myNodeTerminations.length][];
		for (int i=0; i < myNodeTerminations.length; i++) {
			LinearExponentialTermination let = (LinearExponentialTermination) myNodeTerminations[i];
			transform[i] = let.getWeights();
		}
		
		return transform;
	}
	
	/**
	 * @see ca.nengo.model.PlasticTermination#setTransform(float[][] transform)
	 */
	public void setTransform(float[][] transform) 
	{
		for(int i = 0; i < myNodeTerminations.length; i++)
		{
			LinearExponentialTermination let = (LinearExponentialTermination) myNodeTerminations[i];
			let.setWeights(transform[i]);
		}
	}
		
	
	/**
	 * @see ca.nengo.model.PlasticTermination#getInput()
	 */
	public InstantaneousOutput getInput() {
		LinearExponentialTermination let = (LinearExponentialTermination)myNodeTerminations[0];
		
		return let.getInput();
	}
	
	/**
	 * @see ca.nengo.model.PlasticTermination#saveTransform()
	 */
	public void saveTransform() {
		for (int i=0; i < myNodeTerminations.length; i++) {
			((LinearExponentialTermination) myNodeTerminations[i]).saveWeights();
		}
	}

	@Override
	public PlasticTermination clone() throws CloneNotSupportedException {
		try {
			return new PlasticEnsembleTermination(getNode(), getName(), (LinearExponentialTermination[]) myNodeTerminations);
		} catch (StructuralException e) {
			throw new CloneNotSupportedException("Error trying to clone: " + e.getMessage());
		}
	}
}
