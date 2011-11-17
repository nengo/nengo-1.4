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
package ca.nengo.model.plasticity.impl;

import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.PlasticNodeTermination;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SpikeOutput;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.impl.EnsembleTermination;
import ca.nengo.model.nef.NEFEnsemble;

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
 * @author Jonathan Lai
 */
public abstract class PlasticEnsembleTermination extends EnsembleTermination {

	private static final long serialVersionUID = 1L;
	protected float myLearningRate = 5e-7f;
	protected boolean myLearning = true;
	protected String myOriginName;
	protected float[] myOutput;

	/**
	 * @param node The parent Node
	 * @param name Name of this Termination
	 * @param nodeTerminations Node-level Terminations that make up this Termination. Must be
	 *        all LinearExponentialTerminations
	 * @throws StructuralException If dimensions of different terminations are not all the same
	 */
	public PlasticEnsembleTermination(Node node, String name, PlasticNodeTermination[] nodeTerminations) throws StructuralException {
		super(node, name, nodeTerminations);
		setOriginName(NEFEnsemble.X); // Start with the X origin by default
	}

    /**
     * @return Name of Origin from which post-synaptic activity is drawn
     */
    public String getOriginName() {
        return myOriginName;
    }

    /**
     * @param originName Name of Origin from which post-synaptic activity is drawn
     */
    public void setOriginName(String originName) {
        myOriginName = originName;
    }

    /**
     * @see ca.nengo.model.plasticity.PlasticityRule#setOriginState(java.lang.String, ca.nengo.model.InstantaneousOutput, float)
     */
    public void setOriginState(String name, InstantaneousOutput state, float time) throws StructuralException {
        if (myOriginName == null) {
            throw new StructuralException("Origin name not set in PESTermination");
        }
        if (name.equals(myOriginName)) {
            if (state instanceof RealOutput) {
                myOutput = ((RealOutput) state).getValues();
            } else if (state instanceof SpikeOutput) {
                boolean[] vals = ((SpikeOutput) state).getValues();
                if (myOutput==null) {myOutput = new float[vals.length];}
                for (int i=0; i<vals.length; i++) {
                    myOutput[i] = vals[i] ? 0.001f : 0.0f;
                }
            }
        }
    }

	/**
	 * @see ca.nengo.model.PlasticTermination#getTransform()
	 */
	public float[][] getTransform() {
	    Termination[] terms = this.getNodeTerminations();
		float[][] transform = new float[terms.length][];
		for (int i=0; i < terms.length; i++) {
			PlasticNodeTermination pnt = (PlasticNodeTermination) terms[i];
			transform[i] = pnt.getWeights();
		}

		return transform;
	}

	/**
	 * @see ca.nengo.model.PlasticTermination#setTransform(float[][] transform)
	 */
	public void setTransform(float[][] transform) {
	    Termination[] terms = this.getNodeTerminations();
		for(int i = 0; i < terms.length; i++) {
			PlasticNodeTermination pnt = (PlasticNodeTermination) terms[i];
			pnt.setWeights(transform[i]);
		}
	}

	/**
     * @see ca.nengo.model.PlasticTermination#saveTransform()
     */
    public void saveTransform() {
        Termination[] terms = this.getNodeTerminations();
        for (int i=0; i < terms.length; i++) {
            ((PlasticNodeTermination) terms[i]).saveWeights();
        }
    }

	/**
	 * @see ca.nengo.model.PlasticTermination#setTransform(float[][] transform)
	 */
	public abstract void updateTransform(float time, int start, int end) throws StructuralException;

	/**
	 * @see ca.nengo.model.PlasticTermination#getInput()
	 */
	@Override
    public InstantaneousOutput getInput() {
	    Termination[] terms = this.getNodeTerminations();
		PlasticNodeTermination pnt = (PlasticNodeTermination) terms[0];

		return pnt.getInput();
	}

	/**
	 * @see ca.nengo.model.PlasticTermination#getOutputs()
	 */
	public float[] getOutputs() {
	    Termination[] terms = this.getNodeTerminations();
		float[] currents = new float[terms.length];
		for (int i=0; i < terms.length; i++) {
			PlasticNodeTermination pnt = (PlasticNodeTermination) terms[i];
			currents[i] = pnt.getOutput();
		}

		return currents;
	}

    /**
     * @see ca.nengo.model.PlasticTermination#getTransform()
     */
    public float getLearningRate() {
        return myLearningRate;
    }

    /**
     * @see ca.nengo.model.PlasticTermination#setTransform(float[][] transform)
     */
    public void setLearningRate(float learningRate)
    {
        myLearningRate = learningRate;
    }

    /**
     * @see ca.nengo.model.PlasticTermination#getTransform()
     */
    public boolean getLearning() {
        return myLearning;
    }

    /**
     * @see ca.nengo.model.PlasticTermination#setTransform(float[][] transform)
     */
    public void setLearning(boolean learning)
    {
        myLearning = learning;
    }

	@Override
	public PlasticEnsembleTermination clone() throws CloneNotSupportedException {
		return (PlasticEnsembleTermination) super.clone();
	}
}
