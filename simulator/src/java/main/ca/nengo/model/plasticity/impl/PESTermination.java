/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "RealPlasticityRule.java". Description:
"A basic implementation of PlasticityRule for real valued input"

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
 * Created on 30-Jan-2007
 */
package ca.nengo.model.plasticity.impl;

import ca.nengo.model.Node;
import ca.nengo.model.PlasticNodeTermination;
import ca.nengo.model.StructuralException;
import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.model.neuron.impl.SpikingNeuron;
import ca.nengo.util.MU;

/**
 * A termination whose transformation evolves according to the PES rule.
 *
 * The learning rate is defined by an AbstractRealLearningFunction (see its declaration for
 * the inputs it receives). This learning rate function is applied to each In each case, the presynaptic-variable
 * input to the function is the corresponding dimension of input to the Termination. The postsynaptic variable is taken
 * as the corresponding dimension of the Origin NEFEnsemble.X. This implementation supports only a single separate
 * modulatory variable, though it can be multi-dimensional. This is also user-defined, as some other Termination
 * onto the same NEFEnsemble.
 *
 * TODO: test
 *
 * @author Bryan Tripp
 * @author Jonathan Lai
 * @author Trevor Bekolay
 */
public class PESTermination extends ModulatedPlasticEnsembleTermination  {

    private static final long serialVersionUID = 1L;

    protected float[] myGain;
    private float[][] myEncoders;

    private boolean myOja = false; // Apply Oja smoothing?

    /**
     * @param ensemble The ensemble this termination belongs to
     * @param name Name of this Termination
     * @param nodeTerminations Node-level Terminations that make up this Termination. Must be
     *        all LinearExponentialTerminations
     * @throws StructuralException If dimensions of different terminations are not all the same
     */
    public PESTermination(NEFEnsemble ensemble, String name, PlasticNodeTermination[] nodeTerminations) throws StructuralException {
        super(ensemble, name, nodeTerminations);
        myEncoders = ensemble.getEncoders();
        myGain = new float[nodeTerminations.length];
        for (int i = 0; i < nodeTerminations.length; i++) {
            SpikingNeuron neuron = (SpikingNeuron) nodeTerminations[i].getNode();
            myGain[i] = neuron.getScale();
        }
    }

    /**
     * @return Name of Origin from which post-synaptic activity is drawn
     */
    public boolean getOja() {
        return myOja;
    }

    /**
     * @param oja Should this termination use Oja smoothing?
     */
    public void setOja(boolean oja) {
        myOja = oja;
    }

    /**
     * @see ca.nengo.model.plasticity.impl.PlasticEnsembleTermination#updateTransform(float, int, int)
     */
    @Override
    public void updateTransform(float time, int start, int end) throws StructuralException {
        if (myModTermName == null || myOriginName == null) {
            throw new StructuralException("Origin name not set in PESTermination");
        }

        if (myFilteredInput == null) {
        	return;
        }

        float[][] transform = this.getTransform();
        for (int i = start; i < end; i++) {
            for (int j = 0; j < transform[i].length; j++) {
                transform[i][j] += deltaOmega(i, j, transform[i][j]);
            }
        }
        this.setTransform(transform, false);
    }

    protected float deltaOmega(int i, int j, float currentWeight) {
        float oja = 0.0f;

        if (myOja) {
            for (float element : myOutput) {
                oja += myLearningRate * element * element * currentWeight;
            }
        }
        
        float e = 0.0f;
        for (int k = 0; k < myFilteredModInput.length; k++) {
            e += myFilteredModInput[k] * myEncoders[i][k];
        }

        return myLearningRate * myFilteredInput[j] * e * myGain[i] - oja;
    }
    
    @Override
    public PESTermination clone(Node node) throws CloneNotSupportedException {
        PESTermination result = (PESTermination)super.clone(node);
        result.myFilteredInput = (myFilteredInput != null) ? myFilteredInput.clone() : null;
//        result.myFilteredInput = null;
        result.myGain = myGain.clone();
        result.myEncoders = MU.clone(myEncoders);
        return result;
    }

}
