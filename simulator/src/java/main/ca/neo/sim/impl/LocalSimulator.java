/*
 * Created on 7-Jun-2006
 */
package ca.neo.sim.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ca.neo.model.Ensemble;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Probeable;
import ca.neo.model.Projection;
import ca.neo.model.SimulationException;
import ca.neo.sim.Simulator;
import ca.neo.sim.SimulatorEvent;
import ca.neo.sim.SimulatorListener;
import ca.neo.util.Probe;
import ca.neo.util.VisiblyMutable;
import ca.neo.util.VisiblyMutableUtils;
import ca.neo.util.impl.ProbeImpl;

/**
 * A Simulator that runs locally (ie in the Java Virtual Machine in which it is
 * called). TODO: test
 * 
 * @author Bryan Tripp
 */
public class LocalSimulator implements Simulator {

	private Projection[] myProjections;
	private Node[] myNodes;
	private Map myNodeMap;
	private List<Probe> myProbes;
	private List<VisiblyMutable.Listener> myChangeListeners;
	

	/**
	 * Collection of Simulator
	 */
	private Collection<SimulatorListener> mySimulatorListeners;

	public LocalSimulator() {
		mySimulatorListeners = new ArrayList<SimulatorListener>(1);
		myChangeListeners = new ArrayList<Listener>(1);
	}

	/**
	 * @see ca.neo.sim.Simulator#initialize(ca.neo.model.Network)
	 */
	public synchronized void initialize(Network network) {
		myNodes = network.getNodes();

		myNodeMap = new HashMap(myNodes.length * 2);
		for (int i = 0; i < myNodes.length; i++) {
			myNodeMap.put(myNodes[i].getName(), myNodes[i]);
		}

		myProjections = network.getProjections();
		if (myProbes == null)
			myProbes = new ArrayList<Probe>(20);
	}

	/**
	 * @see ca.neo.sim.Simulator#run(float, float, float)
	 */
	public synchronized void run(float startTime, float endTime, float stepSize)
			throws SimulationException {
		fireSimulatorEvent(new SimulatorEvent(0, SimulatorEvent.Type.STARTED));
		// for (int i = 0; i < myNodes.length; i++) {
		// myNodes[i].setMode(mode);
		// }

		// //make each node produce its initial output
		// for (int i = 0; i < myNodes.length; i++) {
		// myNodes[i].run(startTime, startTime);
		// }
		//		
		float time = startTime;

		int c = 0;
		while (time < endTime - stepSize / 10000f) { // in case we're very
			// close with floating
			// point comparison
			if (c++ % 100 == 99)
				System.out.println("Step " + c); // TODO: change this to
			// listener/progress bar
			step(time, Math.min(endTime, time + stepSize));

			float currentProgress = (time - startTime) / (endTime - startTime);
			fireSimulatorEvent(new SimulatorEvent(currentProgress,
					SimulatorEvent.Type.STEP_TAKEN));

			time += stepSize;
		}
		fireSimulatorEvent(new SimulatorEvent(1f, SimulatorEvent.Type.FINISHED));
	}

	private void step(float startTime, float endTime)
			throws SimulationException {

		for (int i = 0; i < myProjections.length; i++) {
			InstantaneousOutput values = myProjections[i].getOrigin()
					.getValues();
			myProjections[i].getTermination().setValues(values);
		}

		for (int i = 0; i < myNodes.length; i++) {
			myNodes[i].run(startTime, endTime);
		}

		Iterator it = myProbes.iterator();
		while (it.hasNext()) {
			Probe p = (Probe) it.next();
			p.collect(endTime);
		}
	}

	/**
	 * @see ca.neo.sim.Simulator#resetNetwork(boolean)
	 */
	public synchronized void resetNetwork(boolean randomize) {
		for (int i = 0; i < myNodes.length; i++) {
			myNodes[i].reset(randomize);
		}
	}

	/**
	 * @see ca.neo.sim.Simulator#addProbe(java.lang.String, java.lang.String,
	 *      boolean)
	 */
	public Probe addProbe(String nodeName, String state, boolean record)
			throws SimulationException {
		Probeable p = getNode(nodeName);
		return addProbe(null, p, state, record);
	}

	/**
	 * @see ca.neo.sim.Simulator#addProbe(java.lang.String, int,
	 *      java.lang.String, boolean)
	 */
	public Probe addProbe(String ensembleName, int neuronIndex, String state,
			boolean record) throws SimulationException {
		Probeable p = getNeuron(ensembleName, neuronIndex);
		return addProbe(ensembleName, p, state, record);
	}

	/**
	 * @see ca.neo.sim.Simulator#addProbe(java.lang.String, int,
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

		myProbes.add(result);

		fireVisibleChangeEvent();
		return result;
	}

	/**
	 * @see ca.neo.sim.Simulator#removeProbe(ca.neo.util.Probe)
	 */
	public void removeProbe(Probe probe) throws SimulationException {
		if (!myProbes.remove(probe)) {
			throw new SimulationException("Probe could not be removed");
		}
		fireVisibleChangeEvent();
	}

	private Probeable getNode(String nodeName) throws SimulationException {
		Node result = (Node) myNodeMap.get(nodeName);

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
		Node ensemble = (Node) myNodeMap.get(nodeName);

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
	 * @see ca.neo.sim.Simulator#getProbes()
	 */
	public Probe[] getProbes() {
		return myProbes.toArray(new Probe[0]);
	}

	/**
	 * @see ca.neo.sim.Simulator#addSimulatorListener(ca.neo.sim.SimulatorListener)
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
	 * @see ca.neo.sim.Simulator#removeSimulatorListener(ca.neo.sim.SimulatorListener)
	 */
	public void removeSimulatorListener(SimulatorListener listener) {
		mySimulatorListeners.remove(listener);
	}

	/**
	 * @see ca.neo.util.VisiblyMutable#addChangeListener(ca.neo.util.VisiblyMutable.Listener)
	 */
	public void addChangeListener(Listener listener) {
		myChangeListeners.add(listener);
	}

	/**
	 * @see ca.neo.util.VisiblyMutable#removeChangeListener(ca.neo.util.VisiblyMutable.Listener)
	 */
	public void removeChangeListener(Listener listener) {
		myChangeListeners.remove(listener);
	}
	
	private void fireVisibleChangeEvent() {
		VisiblyMutableUtils.changed(this, myChangeListeners);
	}
}
