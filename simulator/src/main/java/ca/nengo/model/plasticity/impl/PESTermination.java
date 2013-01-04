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

import java.util.Arrays;

import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.PlasticNodeTermination;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SpikeOutput;
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
//    private static final Logger ourLogger = Logger.getLogger(PESTermination.class);

    private float myLastTime = 0.0f;
    private float[] myFilteredInput;
    private float[] myGain;
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
     * @see ca.nengo.model.Resettable#reset(boolean)
     */
    @Override
    public void reset(boolean randomize) {
        super.reset(randomize);
        myLastTime = 0.0f;

        if (myFilteredInput != null)
        	Arrays.fill(myFilteredInput, 0);
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

    private void updateInput() {
        InstantaneousOutput input = this.getInput();
        float integrationTime = 0.001f;
        float tauPSC = getNodeTerminations()[0].getTau(); //0.005

        if (input instanceof RealOutput) {
            float[] values = ((RealOutput) input).getValues();

            if (myFilteredInput == null) {
                myFilteredInput = new float[values.length];
            }

            for (int i=0; i < values.length; i++) {
                myFilteredInput[i] *= 1.0f - integrationTime / tauPSC;
                myFilteredInput[i] += values[i] * integrationTime / tauPSC;
            }
        } else if (input != null) {
            boolean[] values = ((SpikeOutput) input).getValues();

            if (myFilteredInput == null) {
            	myFilteredInput = new float[values.length];
            }

            for (int i=0; i < values.length; i++) {
                myFilteredInput[i] *= 1.0f - integrationTime / tauPSC;
                myFilteredInput[i] += values[i] ? integrationTime / tauPSC : 0;
            }
        } else {
        	// no input, so set filtered input to zero if it exists
        	if (myFilteredInput != null)
        		Arrays.fill(myFilteredInput, 0);
        	
        	// we should have a warning, but putting it here spams up the log (b/c each thread comes here)
//        	ourLogger.warn("Input values not set on termination " + this.getName() + ".  Assuming input of zero.");
        }
    }

    /**
     * @see ca.nengo.model.plasticity.impl.PlasticEnsembleTermination#updateTransform(float, int, int)
     */
    @Override
    public void updateTransform(float time, int start, int end) throws StructuralException {
        if (myModTermName == null || myOriginName == null) {
            throw new StructuralException("Origin name not set in PESTermination");
        }

        if (myLastTime < time) {
            this.updateInput();
            myLastTime = time;
        }
        
        if (myFilteredInput == null)
        	return;

        float[][] transform = this.getTransform();

        for (int i = start; i < end; i++) {
            for (int j = 0; j < transform[i].length; j++) {
                float e = 0.0f;
                for (int k = 0; k < myModInput.length; k++) {
                    e += myModInput[k] * myEncoders[i][k];
                }
                float delta = deltaOmega(myFilteredInput[j],time,transform[i][j],myGain[i],e);
                transform[i][j] += delta;
            }
        }

        this.setTransform(transform, false);
    }

    private float deltaOmega(float input, float time, float currentWeight, float gain, float e) {
        float oja = 0.0f;

        if (myOja) {
            for (float element : myOutput) {
                oja += myLearningRate*element*element*currentWeight;
            }
        }

        return myLearningRate * input * e * gain - oja;
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
