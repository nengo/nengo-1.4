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
package ca.nengo.model.plasticity.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import ca.nengo.model.Ensemble;
import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.PlasticNodeTermination;
import ca.nengo.model.SimulationException;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.Units;
import ca.nengo.model.impl.EnsembleImpl;
import ca.nengo.model.impl.NodeFactory;
import ca.nengo.model.impl.RealOutputImpl;
import ca.nengo.model.nef.impl.DecodedTermination;
import ca.nengo.util.TaskSpawner;
import ca.nengo.util.ThreadTask;
import ca.nengo.util.impl.LearningTask;

/**
 * <p>An extension of the default ensemble; connection weights can be modified
 * by a plasticity rule.</p>
 *
 * TODO: test
 *
 * @author Trevor Bekolay
 */
public class PlasticEnsembleImpl extends EnsembleImpl implements TaskSpawner {

    private static final long serialVersionUID = 1L;

    private float myPlasticityInterval;
    private float myLastPlasticityTime;
    private boolean myLearning = true;

    protected final Map<String, PlasticEnsembleTermination> myPlasticEnsembleTerminations;

    private ArrayList<LearningTask> myTasks;

    /**
     * @param name Name of Ensemble
     * @param nodes Nodes that make up the Ensemble
     * @throws StructuralException if the given Nodes contain Terminations with the same
     *      name but different dimensions
     */
    public PlasticEnsembleImpl(String name, Node[] nodes) throws StructuralException {
        super(name, nodes);
        myTasks = new ArrayList<LearningTask>();
        myPlasticEnsembleTerminations = new LinkedHashMap<String, PlasticEnsembleTermination>(6);
        myLastPlasticityTime = 0.0f;
    }

    public PlasticEnsembleImpl(String name, NodeFactory factory, int n) throws StructuralException {
        super(name, factory, n);
        myTasks = new ArrayList<LearningTask>();
        myPlasticEnsembleTerminations = new LinkedHashMap<String, PlasticEnsembleTermination>(6);
        myLastPlasticityTime = 0.0f;
    }

    public boolean getLearning() {
        return myLearning;
    }

    public void setLearning(boolean learning) {
        for (PlasticEnsembleTermination pet : myPlasticEnsembleTerminations.values()) {
            pet.setLearning(learning);
        }
        myLearning = learning;
    }

    protected static boolean isPopulationPlastic(Termination[] terminations) {
        boolean result = true;

        for (int i=0; i < terminations.length; i++) {
            if (!(terminations[i] instanceof PlasticNodeTermination)) {
                result = false;
            }
        }

        return result;
    }

    /**
     * @see ca.nengo.model.plasticity.PlasticEnsemble#setPlasticityInterval(float)
     */
    public void setPlasticityInterval(float time) {
        myPlasticityInterval = time;
    }

    /**
     * @see ca.nengo.model.plasticity.PlasticEnsemble#getPlasticityInterval()
     */
    public float getPlasticityInterval() {
        return myPlasticityInterval;
    }

    /**
     * @see ca.nengo.model.Ensemble#run(float, float)
     */
    @Override
    public void run(float startTime, float endTime) throws SimulationException {
        super.run(startTime, endTime);

        setStates(endTime); // updates myLastPlasticityTime

        if ((myPlasticityInterval <= 0 && myLearning) ||
                (myLearning && endTime >= myLastPlasticityTime + myPlasticityInterval)) {
            for (LearningTask task : myTasks) {
                task.reset(false);
            }
        }
    }

    public void setStates(float endTime) throws SimulationException {
        if (myLastPlasticityTime < endTime) {
            for (PlasticEnsembleTermination pet : myPlasticEnsembleTerminations.values()) {
                try {
                    Origin origin = this.getOrigin(pet.getOriginName());
                    pet.setOriginState(origin.getName(), origin.getValues(), endTime);

                    if (pet instanceof ModulatedPlasticEnsembleTermination) {
                        DecodedTermination modTerm = (DecodedTermination)
                        this.getTermination(((ModulatedPlasticEnsembleTermination) pet).getModTermName());

                        InstantaneousOutput input = new RealOutputImpl(modTerm.getOutput(), Units.UNK, endTime);
                        ((ModulatedPlasticEnsembleTermination) pet).setModTerminationState
                        (modTerm.getName(), input, endTime);
                    }
                }
                catch (StructuralException e) {
                    throw new SimulationException(e.getMessage());
                }
            }

            myLastPlasticityTime = endTime;
        }
    }

    /**
     * @see ca.nengo.model.Resettable#reset(boolean)
     */
    public void reset(boolean randomize) {
        super.reset(randomize);
        myLastPlasticityTime = 0.0f;
    }

    /**
     * @see ca.nengo.util.TaskSpawner#getTasks
     */
    public ThreadTask[] getTasks() {
        return myTasks.toArray(new LearningTask[0]);
    }

    /**
     * @see ca.nengo.model.Node#getTermination(java.lang.String)
     */
    @Override
    public Termination getTermination(String name) throws StructuralException {
        return myPlasticEnsembleTerminations.containsKey(name) ?
                myPlasticEnsembleTerminations.get(name) : super.getTermination(name);
    }

    /**
     * @see ca.nengo.model.Ensemble#getTerminations()
     */
    @Override
    public Termination[] getTerminations() {
        ArrayList<Termination> result = new ArrayList<Termination>(10);
        Termination[] composites = super.getTerminations();
        for (Termination composite : composites) {
            result.add(composite);
        }

        for (Termination t : myPlasticEnsembleTerminations.values()) {
            result.add(t);
        }
        return result.toArray(new Termination[0]);
    }

    /**
     * @see ca.nengo.util.TaskSpawner#addTasks
     */
    public void addTasks(ThreadTask[] tasks) {
        myTasks.addAll(Arrays.asList((LearningTask[]) tasks));
    }

    /**
     * @see ca.nengo.util.TaskSpawner#setTasks
     */
    public void setTasks(ThreadTask[] tasks) {
        myTasks.clear();
        this.addTasks(tasks);
    }

    @Override
    public Ensemble clone() throws CloneNotSupportedException {
        PlasticEnsembleImpl result = (PlasticEnsembleImpl) super.clone();
        return result;
    }
}
