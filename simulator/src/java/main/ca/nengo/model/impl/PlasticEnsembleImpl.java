/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "PlasticEnsembleImpl.java". Description: 
"An extension of the default ensemble; connection weights can be modified by a plasticity rule"

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

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import ca.nengo.model.Ensemble;
import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SimulationException;
import ca.nengo.model.SpikeOutput;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.PlasticTermination;
import ca.nengo.model.Units;
import ca.nengo.model.nef.impl.DecodedTermination;
import ca.nengo.model.plasticity.PlasticityRule;

/**
 * <p>An extension of the default ensemble; connection weights can be modified
 * by a plasticity rule.</p>
 * 
 * TODO: test
 * 
 * @author Trevor Bekolay
 */
public class PlasticEnsembleImpl extends EnsembleImpl {

    private static final long serialVersionUID = 1L;
    
    private Map<String, PlasticityRule> myPlasticityRules;
    
    private float myPlasticityInterval;
    private float myLastPlasticityTime;
    private boolean myLearning = true;
    
    /**
     * @param name Name of Ensemble
     * @param nodes Nodes that make up the Ensemble
     * @throws StructuralException if the given Nodes contain Terminations with the same 
     *      name but different dimensions
     */
    public PlasticEnsembleImpl(String name, Node[] nodes) throws StructuralException {
        super(name, nodes);
        
        myPlasticityRules = new HashMap<String, PlasticityRule>(10);
    }
    
    public PlasticEnsembleImpl(String name, NodeFactory factory, int n) throws StructuralException {
        super(name, factory, n);
        
        myPlasticityRules = new HashMap<String, PlasticityRule>(10);
    }
    
	/**
	 * @see ca.nengo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		Iterator<PlasticityRule> ruleIter = myPlasticityRules.values().iterator();
		
		while (ruleIter.hasNext()) {
			PlasticityRule rule = ruleIter.next();
			rule.reset(randomize);
		}
		super.reset(randomize);
	}
	
	/**
	 * @see ca.nengo.model.Resettable#reset(boolean)
	 */
	public void setLearning(boolean learning) {
		myLearning = learning;
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
		
		EnsembleTermination result;
		
		// Make sure that the components are plastic, otherwise make a non-plastic termination
		if (isPopulationPlastic(components)) {
			LinearExponentialTermination[] lets = new LinearExponentialTermination[components.length];
			for (int i=0; i<components.length; i++) {
				lets[i] = (LinearExponentialTermination) components[i];
			}
			
			result = new PlasticEnsembleTermination(this, name, lets);
		} else {
			result = new EnsembleTermination(this, name, components);
		}
		
		myExpandedTerminations.put(name, result);
		OrderedTerminations.add(result);
		
		fireVisibleChangeEvent();
		
		return result;
	}
	
	// At the moment, LinearExponentialTerminations are the only ones
	// that can be used for plasticity. This can be amended if other types
	// of terminations are created with changeable weights.
	private static boolean isPopulationPlastic(Termination[] terminations) {
		boolean result = true;
		
		for (int i=0; i < terminations.length; i++) {
			if (!(terminations[i] instanceof LinearExponentialTermination)) {
				result = false;
			}
		}
		
		return result;
	}
	
    /**
     * Sets the given plasticity rule for this Ensemble. Termination must be plastic.
     *  
     * @see ca.nengo.model.PlasticEnsemble#setPlasticityRule(java.lang.String, ca.nengo.model.plasticity.PlasticityRule)
     */
    public void setPlasticityRule(String terminationName, PlasticityRule rule) throws StructuralException {
    	if (!(getTermination(terminationName) instanceof PlasticTermination)) {
    		throw new StructuralException(terminationName + " is not plastic.");
    	}
        myPlasticityRules.put(terminationName, rule);
    }
    
    /**
     * @see ca.nengo.model.PlasticEnsemble#setPlasticityInterval(float)
     */
    public void setPlasticityInterval(float time) {
        myPlasticityInterval = time;
    }
    
    /**
     * @see ca.nengo.model.PlasticEnsemble#getPlasticityInterval()
     */
    public float getPlasticityInterval() {
        return myPlasticityInterval;
    }
    
    /**
     * @see ca.nengo.model.PlasticEnsemble#getPlasticityRule(java.lang.String)
     */
    public PlasticityRule getPlasticityRule(String terminationName) throws StructuralException {
        return myPlasticityRules.get(terminationName);
    }

    /**
     * @see ca.nengo.model.Ensemble#run(float, float)
     */
    public void run(float startTime, float endTime) throws SimulationException {
        super.run(startTime, endTime);
        if (myPlasticityInterval <= 0 && myLearning) {
            learn(startTime, endTime);
        } else if (myLearning && endTime >= myLastPlasticityTime + myPlasticityInterval) {
            learn(myLastPlasticityTime, endTime);
            myLastPlasticityTime = endTime;
        }
    }
    
    //run ensemble plasticity rules (assume constant input/state over given elapsed time)
    // TODO Have plasticity work in DIRECT mode
    private void learn(float startTime, float endTime) throws SimulationException {
        Iterator<String> ruleIter = myPlasticityRules.keySet().iterator();
        while (ruleIter.hasNext()) {
        	try {
	            String name = (String) ruleIter.next();
	            PlasticTermination termination = (PlasticTermination) getTermination(name);
	            PlasticityRule rule = myPlasticityRules.get(name);
	            
	            if (rule != null) {
	                Termination[] terms = getTerminations();
	                for (int i = 0; i < terms.length; i++) {
	                	// Right now, we only accept decoded terminations for modulatory input.
	                	// This can be changed.
	                    if (terms[i] instanceof DecodedTermination) {
	                    	DecodedTermination t = (DecodedTermination) terms[i];
	                    	InstantaneousOutput input = new RealOutputImpl(t.getOutput(), Units.UNK, endTime);
	                    	rule.setModTerminationState(t.getName(), input, endTime);
	                    }
	                }
	                
	                Origin[] origins = getOrigins();
	                for (int i = 0; i < origins.length; i++) {
	                    rule.setOriginState(origins[i].getName(), origins[i].getValues(), endTime);
	                }
	                
	                float[][] transform = termination.getTransform();
	                float[][] derivative = rule.getDerivative(transform, termination.getInput(), endTime);
	                float scale = (termination.getInput() instanceof SpikeOutput) ? 1 : (endTime - startTime); 
	                for (int i = 0; i < transform.length; i++) {
	                    for (int j = 0; j < transform[i].length; j++) {
	                        transform[i][j] += derivative[i][j] * scale;
	                    }
	                }
	            }
        	} catch (StructuralException e) {
        		throw new SimulationException(e.getMessage());
        	}
        }
    }

    @Override
    public Ensemble clone() throws CloneNotSupportedException {
        try {
            PlasticEnsembleImpl result = (PlasticEnsembleImpl) super.clone();
            
            for (String key : myPlasticityRules.keySet()) {
                result.setPlasticityRule(key, myPlasticityRules.get(key).clone());
            }

            return result;  
        } catch (StructuralException e) {
            throw new CloneNotSupportedException("Problem making clone: " + e.getMessage());
        }
    }
    
}
