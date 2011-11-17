/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "AbstractEnsemble.java". Description:
"Abstract class that can be used as a basis for Ensemble implementations.

  @author Bryan Tripp"

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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import ca.nengo.model.Ensemble;
import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.PreciseSpikeOutput;
import ca.nengo.model.Probeable;
import ca.nengo.model.SimulationException;
import ca.nengo.model.SimulationMode;
import ca.nengo.model.SpikeOutput;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.Units;
import ca.nengo.model.neuron.Neuron;
import ca.nengo.util.SpikePattern;
import ca.nengo.util.TimeSeries;
import ca.nengo.util.VisiblyMutable;
import ca.nengo.util.VisiblyMutableUtils;
import ca.nengo.util.impl.SpikePatternImpl;
import ca.nengo.util.impl.TimeSeriesImpl;

/**
 * Abstract class that can be used as a basis for Ensemble implementations.
 *
 * @author Bryan Tripp
 */
public abstract class AbstractEnsemble implements Ensemble, Probeable, VisiblyMutable {

	private static final long serialVersionUID = -5498397418584843304L;

	private static Logger ourLogger = Logger.getLogger(AbstractEnsemble.class);

	private String myName;
	private Map<String, List<Integer>> myStateNames; // for Probeable
	private SimulationMode myMode;
	private transient SpikePatternImpl mySpikePattern;
	private boolean myCollectSpikesFlag;
	private int myCollectSpikesRatio = 1;
	private String myDocumentation;
	private transient List<VisiblyMutable.Listener> myListeners;
	private Node[] myNodes;
	private Map<String, Origin> myOrigins;
	private Map<String, EnsembleTermination> myTerminations;

	/**
	 * Note that setMode(SimulationMode.DEFAULT) is called at construction time.
	 *
	 * @param name Unique name of Ensemble
	 * @param nodes Nodes that Ensemble contains
	 */
	public AbstractEnsemble(String name, Node[] nodes) {
		myName = name;
		myNodes = nodes;
		mySpikePattern = new SpikePatternImpl(nodes.length);
		myCollectSpikesFlag = false;

		init();

		setMode(SimulationMode.DEFAULT);
	}

	private void init() {
		// Using LinkedHashMap to keep ordering
		myOrigins = new LinkedHashMap<String, Origin>(10);
		Origin[] origins = findOrigins(this, myNodes);
		for (Origin origin : origins) {
		    myOrigins.put(origin.getName(), origin);
		}

		myTerminations = new LinkedHashMap<String, EnsembleTermination>(10);
		EnsembleTermination[] terminations = findTerminations(this, myNodes);
		for (EnsembleTermination termination : terminations) {
		    myTerminations.put(termination.getName(), termination);
		}

        myStateNames = findStateNames(myNodes);
        myListeners = new ArrayList<Listener>(3);
	}

	/**
	 * Replaces the set of nodes inside the Ensemble
	 */
    public void redefineNodes(Node[] nodes) {
		myNodes=nodes;
		mySpikePattern = new SpikePatternImpl(myNodes.length);
		//setupNodeRunners(numNodeRunners);

		init();
	}

	/**
	 * @see ca.nengo.model.Ensemble#getName()
	 */
    public String getName() {
		return myName;
	}

	/**
	 * @param name The new name
	 */
    public void setName(String name) throws StructuralException {
		VisiblyMutableUtils.nameChanged(this, getName(), name, myListeners);
		myName = name;
	}

	/**
	 * @see ca.nengo.model.Ensemble#getNodes()
	 */
    public Node[] getNodes() {
		return myNodes;
	}

	/**
	 * When this method is called, setMode(...) is called on each Node in the Ensemble.
	 * Each Node will then run in the mode that is closest to the requested mode (this
	 * could be different for different Node). Note that at Ensemble construction time,
	 * setMode(SimulationMode.DEFAULT) is called.
	 *
	 * @see ca.nengo.model.Ensemble#setMode(ca.nengo.model.SimulationMode)
	 */
    public void setMode(SimulationMode mode) {
		myMode = mode;

		for (Node myNode : myNodes) {
			myNode.setMode(mode);
		}
	}


	/**
	 * Note that this reflects the latest mode requested of the Ensemble, and that individual
	 * Neurons may run in different modes (see setMode).
	 *
	 * @see ca.nengo.model.Ensemble#getMode()
	 */
    public SimulationMode getMode() {
		return myMode;
	}


	/**
	 * Runs each neuron in the Ensemble.
	 *
	 * @see ca.nengo.model.Ensemble#run(float, float)
	 */
    public void run(float startTime, float endTime) throws SimulationException {
		if (mySpikePattern == null) {
			mySpikePattern = new SpikePatternImpl(myNodes.length);
		}

		for (int i = 0; i < myNodes.length; i++) {
			myNodes[i].run(startTime, endTime);

			if (myCollectSpikesFlag && (myCollectSpikesRatio == 1 || i % myCollectSpikesRatio == 0)) {
				try {
					InstantaneousOutput output = myNodes[i].getOrigin(Neuron.AXON).getValues();
					if (output instanceof PreciseSpikeOutput) {
						PreciseSpikeOutput precise=((PreciseSpikeOutput) output);
						if (precise.getValues()[0]) {
							mySpikePattern.addSpike(i, endTime+precise.getSpikeTimes()[0]);
						}
					} else if (output instanceof SpikeOutput && ((SpikeOutput) output).getValues()[0]) {
						mySpikePattern.addSpike(i, endTime);
					}
				} catch (StructuralException e) {
					ourLogger.warn("Ensemble has been set to collect spikes, but not all components have Origin Neuron.AXON", e);
				}
			}
		}
	}

	/**
	 * Resets each Node in this Ensemble.
	 *
	 * @see ca.nengo.model.Resettable#reset(boolean)
	 */
    public void reset(boolean randomize) {
		for (Node myNode : myNodes) {
			myNode.reset(randomize);
		}

		mySpikePattern = new SpikePatternImpl(myNodes.length);
	}

	/**
	 * @see ca.nengo.model.Ensemble#getOrigin(java.lang.String)
	 */
    public Origin getOrigin(String name) throws StructuralException {
		return myOrigins.get(name);
	}

	/**
	 * @see ca.nengo.model.Ensemble#getTermination(java.lang.String)
	 */
    public Termination getTermination(String name) throws StructuralException {
		return myTerminations.get(name);
	}

    /**
     * @throws StructuralException
     * @see ca.nengo.model.ExpandableNode#removeTermination(java.lang.String)
     */
    public synchronized Origin removeOrigin(String name) throws StructuralException {
        if (myOrigins.containsKey(name)) {
            Origin result = myOrigins.remove(name);

            fireVisibleChangeEvent();
            return result;
        }
        throw new StructuralException("Origin " + name + " does not exist");
    }

    /**
     * @throws StructuralException
     * @see ca.nengo.model.ExpandableNode#removeTermination(java.lang.String)
     */
    public synchronized Termination removeTermination(String name) throws StructuralException {
        if (myTerminations.containsKey(name)) {
            Termination result = myTerminations.remove(name);

            fireVisibleChangeEvent();
            return result;
        }

        throw new StructuralException("Termination " + name + " does not exist");
    }

	/**
	 * @see ca.nengo.model.Node#getOrigins()
	 */
    public Origin[] getOrigins() {
        ArrayList<Origin> result = new ArrayList<Origin>(10);
        for (Origin o : myOrigins.values()) {
            result.add(o);
        }
        return result.toArray(new Origin[0]);
	}

	/**
	 * @see ca.nengo.model.Ensemble#getTerminations()
	 */
    public Termination[] getTerminations() {
	    ArrayList<Termination> result = new ArrayList<Termination>(10);
	    for (Termination t : myTerminations.values()) {
            result.add(t);
        }
	    return result.toArray(new Termination[0]);
	}

	/**
	 * @see ca.nengo.model.Ensemble#collectSpikes(boolean)
	 */
    public void collectSpikes(boolean collect) {
		myCollectSpikesFlag = collect;
	}

	/**
	 * @see ca.nengo.model.Ensemble#isCollectingSpikes()
	 */
    public boolean isCollectingSpikes() {
		return myCollectSpikesFlag;
	}

	/**
	 * @return Inverse of the proportion of nodes from which to collect spikes
	 */
	public int getCollectSpikesRatio() {
		return myCollectSpikesRatio;
	}

	/**
	 * @param n Inverse of the proportion of nodes from which to collect spikes
	 */
	public void setCollectSpikesRatio(int n) {
		myCollectSpikesRatio = n;
	}

	/**
	 * @see ca.nengo.model.Ensemble#getSpikePattern()
	 */
    public SpikePattern getSpikePattern() {
		if (!myCollectSpikesFlag) {
            ourLogger.warn("Warning: collect spikes flag is off");
        }
		return mySpikePattern;
	}

	public void setSpikePattern(float[] spikes, float endTime) {
		if(myCollectSpikesFlag) {
			if (mySpikePattern == null) {
				mySpikePattern = new SpikePatternImpl(myNodes.length);
			}

			for(int i = 0; i < myNodes.length; i++) {

				if(myCollectSpikesRatio == 1 || i % myCollectSpikesRatio == 0) {
					if(spikes[i] != 0.0f) {
						mySpikePattern.addSpike(i, endTime);
					}
				}
			}
		}
	}

	/**
	 * @return Composite of Node states by given name. States of different nodes may be defined at different
	 * 		times, so only the states at the end of the most recent step are given. Only the first
	 * 		dimension of each Node state is included in the composite.
	 * @see ca.nengo.model.Probeable#getHistory(java.lang.String)
	 */
    public TimeSeries getHistory(String stateName) throws SimulationException {
		if (!myStateNames.containsKey(stateName)) {
			throw new SimulationException("The state " + stateName + " is unknown");
		}

		List<Integer> nodeNumbers = myStateNames.get(stateName);
		float[] firstNodeTimes = ((Probeable) myNodes[nodeNumbers.get(0).intValue()]).getHistory(stateName).getTimes();

		float[] times = new float[0];
		float[][] values = new float[0][];
		Units[] units = Units.uniform(Units.UNK, myNodes.length);

		if (firstNodeTimes.length >= 1) {
			times = new float[]{firstNodeTimes[firstNodeTimes.length - 1]};

			values = new float[][]{new float[myNodes.length]};
			for (int i = 0; i < myNodes.length; i++) {
				if (nodeNumbers.contains(Integer.valueOf(i))) {
					TimeSeries history = ((Probeable) myNodes[i]).getHistory(stateName);
					int index = history.getTimes().length - 1;
					values[0][i] = history.getValues()[index][0];
					if (i == 0) {
                        units[i] = history.getUnits()[0];
                    }
				}
			}
		}

		return new TimeSeriesImpl(times, values, units);
	}

	/**
	 * @see ca.nengo.model.Probeable#listStates()
	 */
    public Properties listStates() {
		Properties result = new Properties();
		Iterator<String> keys = myStateNames.keySet().iterator();
		while (keys.hasNext()) {
			result.setProperty(keys.next(), "Composite of Node states by the same name");
		}
		return result;
	}

	/**
	 * Finds existing one-dimensional Origins by same name on the given Nodes, and groups
	 * them into EnsembleOrigins.
	 *
	 * @param nodes Nodes on which to look for Origins
	 * @return Ensemble Origins encompassing Node-level Origins
	 */
	private static Origin[] findOrigins(Node parent, Node[] nodes) {
		Map<String, List<Origin>> groups = group1DOrigins(nodes);
		Iterator<String> it = groups.keySet().iterator();
		List<Origin> result = new ArrayList<Origin>(10);
		while (it.hasNext()) {
			String name = it.next();
			List<Origin> group = groups.get(name);
			result.add(new EnsembleOrigin(parent, name, group.toArray(new Origin[0])));
		}

		return result.toArray(new Origin[0]);
	}

	/**
	 * @param nodes A list of Nodes in an Ensemble
	 * @return A grouping of one-dimensional origins on these nodes, by name
	 */
	private static Map<String, List<Origin>> group1DOrigins(Node[] nodes) {
		Map<String, List<Origin>> groups = new LinkedHashMap<String, List<Origin>>(10);

		for (Node node : nodes) {
			Origin[] origins = node.getOrigins();
			for (Origin origin : origins) {
				if (origin.getDimensions() == 1) {
					List<Origin> group = groups.get(origin.getName());
					if (group == null) {
						group = new ArrayList<Origin>(nodes.length * 2);
						groups.put(origin.getName(), group);
					}
					group.add(origin);
				}
			}
		}

		return groups;
	}

	/**
	 * @param nodes A list of Nodes
	 * @return Names of one-dimensional origins that are shared by all the nodes
	 */
	public static List<String> findCommon1DOrigins(Node[] nodes) {
		List<String> result = get1DOriginNames(nodes[0]);

		for (int i = 1; i < nodes.length; i++) {
			result.retainAll(get1DOriginNames(nodes[i]));
		}

		return result;
	}

	private static List<String> get1DOriginNames(Node node) {
		List<String> result = new ArrayList<String>(10);
		Origin[] origins = node.getOrigins();
		for (Origin origin : origins) {
			if (origin.getDimensions() == 1) {
                result.add(origin.getName());
            }
		}
		return result;
	}

	/**
	 * Finds existing one-dimensional Terminations by the same name on different nodes, and
	 * groups them into EnsembleTerminations.
	 *
	 * @param parent The ensemble to which new terminations will belong
	 * @param nodes Nodes on which to look for Terminations
	 * @return Ensemble Terminations encompassing Node-level Terminations
	 */
	private static EnsembleTermination[] findTerminations(Node parent, Node[] nodes) {
		Map<String, List<Termination>> groups = new LinkedHashMap<String, List<Termination>>(10);

		for (Node node : nodes) {
			Termination[] terminations = node.getTerminations();
			for (Termination termination : terminations) {
				if (termination.getDimensions() == 1) {
					List<Termination> group = groups.get(termination.getName());
					if (group == null) {
						group = new ArrayList<Termination>(nodes.length * 2);
						groups.put(termination.getName(), group);
					}
					group.add(termination);
				}
			}
		}

		Iterator<String> it = groups.keySet().iterator();
		List<EnsembleTermination> result = new ArrayList<EnsembleTermination>(10);
		while (it.hasNext()) {
			String name = it.next();
			List<Termination> group = groups.get(name);
			try {
				result.add(new EnsembleTermination(parent, name, group.toArray(new Termination[0])));
			} catch (StructuralException e) {
				throw new Error("Composite Termination should consist only of 1D Terminations, but apparently does not", e);
			}
		}

		return result.toArray(new EnsembleTermination[0]);
	}

	private static Map<String, List<Integer>> findStateNames(Node[] nodes) {
		Map<String, List<Integer>> result = new LinkedHashMap<String, List<Integer>>(10);

		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] instanceof Probeable) {
				Properties p = ((Probeable) nodes[i]).listStates();
				Iterator<Object> keys = p.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next().toString();
					if (!result.containsKey(key)) {
						result.put(key, new ArrayList<Integer>(10));
					}
					result.get(key).add(Integer.valueOf(i));
				}
			}
		}

		return result;
	}

	/**
	 * @see ca.nengo.model.Node#getDocumentation()
	 */
    public String getDocumentation() {
		return myDocumentation;
	}

	/**
	 * @see ca.nengo.model.Node#setDocumentation(java.lang.String)
	 */
    public void setDocumentation(String text) {
		myDocumentation = text;
	}

	/**
	 * @see ca.nengo.util.VisiblyMutable#addChangeListener(ca.nengo.util.VisiblyMutable.Listener)
	 */
    public void addChangeListener(Listener listener) {
		if (myListeners == null) {
			myListeners = new ArrayList<Listener>(1);
		}
		myListeners.add(listener);
	}

	/**
	 * @see ca.nengo.util.VisiblyMutable#removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)
	 */
    public void removeChangeListener(Listener listener) {
		if (myListeners != null) {
            myListeners.remove(listener);
        }
	}

	/**
	 * Called by subclasses when properties have changed in such a way that the
	 * display of the ensemble may need updating.
	 */
	protected void fireVisibleChangeEvent() {
		VisiblyMutableUtils.changed(this, myListeners);
	}

	@Override
    public Ensemble clone() throws CloneNotSupportedException {
		AbstractEnsemble result = (AbstractEnsemble) super.clone();

		Node[] oldNodes = getNodes();
		Node[] nodes = oldNodes.clone(); //use clone rather than new Node[] to retain array type, e.g. NEFNode[]
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = oldNodes[i].clone();
		}
		result.myNodes = nodes;

		result.myOrigins = new LinkedHashMap<String, Origin>(10);
		for (Origin origin : getOrigins()) {
			result.myOrigins.put(origin.getName(), origin.clone());
		}

		result.myTerminations = new LinkedHashMap<String, EnsembleTermination>(10);
		for (Termination termination : getTerminations()) {
			result.myTerminations.put(termination.getName(), (EnsembleTermination) termination.clone());
		}

		if (mySpikePattern != null) {
            result.mySpikePattern = (SpikePatternImpl) mySpikePattern.clone();
        }

		return result;
	}
}
