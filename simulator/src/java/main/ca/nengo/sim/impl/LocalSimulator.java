/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "LocalSimulator.java". Description:
"A Simulator that runs locally (ie in the Java Virtual Machine in which it is
  called)"

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
 * Created on 7-Jun-2006
 */
package ca.nengo.sim.impl;

import java.lang.System;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ca.nengo.model.Ensemble;
import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.Probeable;
import ca.nengo.model.Projection;
import ca.nengo.model.SimulationException;
import ca.nengo.model.Termination;
import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.model.plasticity.impl.PlasticEnsembleTermination;
import ca.nengo.sim.Simulator;
import ca.nengo.sim.SimulatorEvent;
import ca.nengo.sim.SimulatorListener;
import ca.nengo.util.Probe;
import ca.nengo.util.ThreadTask;
import ca.nengo.util.VisiblyMutable;
import ca.nengo.util.VisiblyMutableUtils;
import ca.nengo.util.impl.NodeThreadPool;
import ca.nengo.util.impl.ProbeImpl;

/**
 * A Simulator that runs locally (ie in the Java Virtual Machine in which it is
 * called). TODO: test
 *
 * @author Bryan Tripp
 */
public class LocalSimulator implements Simulator, java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private Projection[] myProjections;
    private Node[] myNodes;
    private ThreadTask[] myTasks;
    private List<ThreadTask> myProbeTasks;
    private Map<String, Node> myNodeMap;
    private List<Probe> myProbes;
    private Network myNetwork;
    private boolean myDisplayProgress;
    private transient List<VisiblyMutable.Listener> myChangeListeners;
    private transient NodeThreadPool myNodeThreadPool;

    /**
     * Collection of Simulator
     */
    private final Collection<SimulatorListener> mySimulatorListeners;

    public LocalSimulator() {
        mySimulatorListeners = new ArrayList<SimulatorListener>(1);
        myChangeListeners = new ArrayList<Listener>(1);
        myDisplayProgress = true;
    }

    /**
     * @see ca.nengo.sim.Simulator#initialize(ca.nengo.model.Network)
     */
    public synchronized void initialize(Network network) {
    	
    	myNetwork = network;
        
        myNodes = network.getNodes();
        myProjections = network.getProjections();

        myNodeMap = new HashMap<String, Node>(myNodes.length * 2);
        for (Node myNode : myNodes) {
            myNodeMap.put(myNode.getName(), myNode);
        }

        if (myProbes == null) {
            myProbes = new ArrayList<Probe>(20);
        }
        
        if(myProbeTasks == null){
        	myProbeTasks = new ArrayList<ThreadTask>(20);
        }

        myTasks = NodeThreadPool.collectTasks(myNodes).toArray(new ThreadTask[0]);
    }

    /**
     * @see ca.nengo.sim.Simulator#resetProbes()
     */
    public void resetProbes()
    {
        Iterator<Probe> it = myProbes.iterator();
        while (it.hasNext()) {
            it.next().reset();
        }

        for(Node node : myNodes)
        {
            if(node instanceof Network) {
                ((Network)node).getSimulator().resetProbes();
            }
        }
    }

    /**
     * @see ca.nengo.sim.Simulator#run(float, float, float)
     */
    public synchronized void run(float startTime, float endTime, float stepSize)
            throws SimulationException {
        this.run(startTime, endTime, stepSize, true);
    }
    
    /**
     * Run function with option to display (or not) the progress in the console
     */
    public synchronized void run(float startTime, float endTime, float stepSize, boolean topLevel)
            throws SimulationException {
    	
    	 myNodeThreadPool = null;
         myNodeThreadPool = new NodeThreadPool(myNetwork, myProbeTasks);

        //		float pre_time = System.nanoTime();

        double time = startTime;
        double thisStepSize = stepSize;

        if(topLevel)
        {
            resetProbes();
        }

        fireSimulatorEvent(new SimulatorEvent(0, SimulatorEvent.Type.STARTED));

        // for (int i = 0; i < myNodes.length; i++) {
        // myNodes[i].setMode(mode);
        // }

        // //make each node produce its initial output
        // for (int i = 0; i < myNodes.length; i++) {
        // myNodes[i].run(startTime, startTime);
        // }
        //



        // Casting the float to a double above causes some unexpected rounding.  To avoid this
        //  we force the stepSize to be divisible by 0.000001 (1 microsecond)

        thisStepSize=Math.round(thisStepSize*1000000)/1000000.0;
        if (thisStepSize<0.000001) {
            thisStepSize=0.000001;
        }

        int c = 0;
        boolean interrupt=false;
        
        while (time < endTime && !interrupt) {

            if (c++ % 100 == 99 && myDisplayProgress) {
                System.out.println("Step " + c + " " + Math.min(endTime, time + thisStepSize));
            }

            if (time + 1.5*thisStepSize > endTime) { //fudge step size to hit end exactly
                thisStepSize = endTime - time;
            }

            step((float) time, (float) (time+thisStepSize));

            float currentProgress = ((float) time - startTime) / (endTime - startTime);
            
            SimulatorEvent event=new SimulatorEvent(currentProgress,
                    SimulatorEvent.Type.STEP_TAKEN);
            fireSimulatorEvent(event);
            if (event.getInterrupt()) interrupt=true;

            time += thisStepSize;
        }

        fireSimulatorEvent(new SimulatorEvent(1f, SimulatorEvent.Type.FINISHED));


        if(myNodeThreadPool != null){
            myNodeThreadPool.kill();
            myNodeThreadPool = null;
        }

    }

    public void step(float startTime, float endTime)
            throws SimulationException {
    	
        if(NodeThreadPool.isMultithreading() && myNodeThreadPool != null){
            myNodeThreadPool.step(startTime, endTime);
        }else{
            for (Projection myProjection : myProjections) {
                InstantaneousOutput values = myProjection.getOrigin().getValues();
                myProjection.getTermination().setValues(values);
            }

            for (Node myNode : myNodes) {
                if(myNode instanceof NetworkImpl) {
                    ((NetworkImpl)myNode).run(startTime, endTime, false);
                } else {
                    myNode.run(startTime, endTime);
                }
            }

            
            for (ThreadTask myTask : myTasks) {
                myTask.run(startTime, endTime);
            }
            
            Iterator<Probe> it = myProbes.iterator();
            while (it.hasNext()) {
                it.next().collect(endTime);
            }
        }
    }

    /**
     * @see ca.nengo.sim.Simulator#resetNetwork(boolean, boolean)
     */
    public synchronized void resetNetwork(boolean randomize, boolean saveWeights) {
        if (saveWeights) {
            Termination[] terms;
            for (Node myNode : myNodes) {
                terms = myNode.getTerminations();
                for (Termination term : terms) {
                    if (term instanceof PlasticEnsembleTermination) {
                        ((PlasticEnsembleTermination) term).saveTransform();
                    }
                }
            }
        }

        for (Node myNode : myNodes) {
            myNode.reset(randomize);
        }
        
        // Force garbage collection
        System.gc();
    }

    /**
     * @see ca.nengo.sim.Simulator#addProbe(java.lang.String, java.lang.String,
     *      boolean)
     */
    public Probe addProbe(String nodeName, String state, boolean record)
            throws SimulationException {
        Probeable p = getNode(nodeName);
        return addProbe(null, p, state, record);
    }

    /**
     * @see ca.nengo.sim.Simulator#addProbe(java.lang.String, int,
     *      java.lang.String, boolean)
     */
    public Probe addProbe(String ensembleName, int neuronIndex, String state,
            boolean record) throws SimulationException {
        Probeable p = getNeuron(ensembleName, neuronIndex);
        return addProbe(ensembleName, p, state, record);
    }

    /**
     * @see ca.nengo.sim.Simulator#addProbe(java.lang.String, int,
     *      java.lang.String, boolean)
     */
    public Probe addProbe(String ensembleName, Probeable target, String state,
            boolean record) throws SimulationException {

        /*
         * Check that no duplicate probes are created
         */
        for (Probe probe : myProbes) {
            if (probe.getTarget() == target) {
                if (probe.getStateName().compareTo(state) == 0) {
                    throw new SimulationException("A probe already exists on this target & state");
                }
            }
        }

        Probe result = new ProbeImpl();
        result.connect(ensembleName, target, state, record);
        
        myProbeTasks.add(result.getProbeTask());
        myProbes.add(result);

        fireVisibleChangeEvent();
        return result;
    }

    /**
     * @see ca.nengo.sim.Simulator#removeProbe(ca.nengo.util.Probe)
     */
    public void removeProbe(Probe probe) throws SimulationException {
        if (!myProbes.remove(probe)) {
            throw new SimulationException("Probe could not be removed");
        }
        
        if (!myProbeTasks.remove(probe.getProbeTask())) {
            throw new SimulationException("Probe could not be removed");
        }
        
        fireVisibleChangeEvent();
    }

    private Probeable getNode(String nodeName) throws SimulationException {
        Node result = myNodeMap.get(nodeName);

        if (result == null) {
            throw new SimulationException("The named Node does not exist");
        }

        if (!(result instanceof Probeable)) {
            throw new SimulationException("The named Node is not Probeable");
        }

        return (Probeable) result;
    }

    private Probeable getNeuron(String nodeName, int index)
            throws SimulationException {
        Node ensemble = myNodeMap.get(nodeName);

        if (ensemble == null) {
            throw new SimulationException("The named Ensemble does not exist");
        }

        if (!(ensemble instanceof Ensemble)) {
            throw new SimulationException("The named Node is not an Ensemble");
        }

        Node[] nodes = ((Ensemble) ensemble).getNodes();
        if (index < 0 || index >= nodes.length) {
            throw new SimulationException("The Node index " + index
                    + " is out of range for Ensemble size " + nodes.length);
        }

        if (!(nodes[index] instanceof Probeable)) {
            throw new SimulationException("The specified Node is not Probeable");
        }

        return (Probeable) nodes[index];
    }

    /**
     * @see ca.nengo.sim.Simulator#getProbes()
     */
    public Probe[] getProbes() {
        return myProbes.toArray(new Probe[0]);
    }

    public void setDisplayProgress(boolean display)
    {
        myDisplayProgress = display;
    }
	
    /**
     * @see ca.nengo.sim.Simulator#addSimulatorListener(ca.nengo.sim.SimulatorListener)
     */
    public void addSimulatorListener(SimulatorListener listener) {
        if (mySimulatorListeners.contains(listener)) {
            System.out
            .println("Trying to add simulator listener that already exists");
        } else {
            mySimulatorListeners.add(listener);
        }
    }

    /**
     * @param event
     */
    protected void fireSimulatorEvent(SimulatorEvent event) {
        for (SimulatorListener listener : mySimulatorListeners) {
            listener.processEvent(event);
        }
    }

    /**
     * @see ca.nengo.sim.Simulator#removeSimulatorListener(ca.nengo.sim.SimulatorListener)
     */
    public void removeSimulatorListener(SimulatorListener listener) {
        mySimulatorListeners.remove(listener);
    }

    /**
     * @see ca.nengo.util.VisiblyMutable#addChangeListener(ca.nengo.util.VisiblyMutable.Listener)
     */
    public void addChangeListener(Listener listener) {
        if (myChangeListeners == null) {
            myChangeListeners = new ArrayList<Listener>(1);
        }
        myChangeListeners.add(listener);
    }

    /**
     * @see ca.nengo.util.VisiblyMutable#removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)
     */
    public void removeChangeListener(Listener listener) {
        if (myChangeListeners != null) {
            myChangeListeners.remove(listener);
        }
    }

    private void fireVisibleChangeEvent() {
        VisiblyMutableUtils.changed(this, myChangeListeners);
    }

    @Override
    public Simulator clone() throws CloneNotSupportedException {
        return new LocalSimulator();
    }
}
