/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "SpikePlasticityRule.java". Description:
"A PlasticityRule that accepts spiking input.

  Spiking input must be dealt with in order to run learning rules in
  a spiking SimulationMode"

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
 * Created on 28-May-07
 */
package ca.nengo.model.plasticity.impl;

import ca.nengo.math.impl.IndicatorPDF;
import ca.nengo.model.Node;
import ca.nengo.model.PlasticNodeTermination;
import ca.nengo.model.StructuralException;
import ca.nengo.model.neuron.Neuron;

/**
 * BCM rule
 *
 * @author Trevor Bekolay
 */
public class BCMTermination extends PlasticEnsembleTermination {

    private static final long serialVersionUID = 1L;
    
    private static final float THETA_LENGTH = 1e5f;
    private float[] myInitialTheta;
    private float[] myTheta;

    public BCMTermination(Node node, String name, PlasticNodeTermination[] nodeTerminations, float[] initialTheta) throws StructuralException {
        super(node, name, nodeTerminations);
        setOriginName(Neuron.AXON);
        
        // If initial theta not passed in, randomly generate
        // between -0.001 and 0.001
        if (initialTheta == null) {
        	IndicatorPDF uniform = new IndicatorPDF(-0.001f, 0.001f);
        	
        	myInitialTheta = new float[nodeTerminations.length];
        	for (int i = 0; i < myInitialTheta.length; i ++) {
        		myInitialTheta[i] = uniform.sample()[0];
        	}
        } else {
        	myInitialTheta = initialTheta;
        }
        myTheta = myInitialTheta.clone();
    }

    /**
     * @see ca.nengo.model.Resettable#reset(boolean)
     */
    @Override
    public void reset(boolean randomize) {
    	super.reset(randomize);
    	for (int i = 0; i < myTheta.length; i++) {
    		myTheta[i] = myInitialTheta[i];
    	}
    }
    
    public void updateTransform(float time, int start, int end)
            throws StructuralException {
    	if (myOriginName == null) {
            throw new StructuralException("Origin name not set in BCMTermination");
        }
    	
    	if (myFilteredInput == null || myFilteredOutput == null) {
        	return;
        }
    	
    	// update omega
    	float[][] transform = this.getTransform();
        for (int i = start; i < end; i++) {
            for (int j = 0; j < transform[i].length; j++) {
                transform[i][j] += myFilteredInput[j] * myFilteredOutput[i]
                		* (myFilteredOutput[i] - myTheta[i])
                		* myLearningRate;
            }
        }
        this.setTransform(transform, false);
        
        // update theta
        for (int i = start; i < end; i++) {
        	myTheta[i] += (myFilteredOutput[i] - myTheta[i]) / THETA_LENGTH;
        }
    }

    @Override
    public PlasticEnsembleTermination clone() throws CloneNotSupportedException {
    	throw new CloneNotSupportedException("BCMTermination not cloneable yet.");
    }
}