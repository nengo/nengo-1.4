/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "PassthroughNode.java". Description:
"A Node that passes values through unaltered.

  This can be useful if an input to a Network is actually routed to multiple destinations,
  but you want to handle this connectivity within the Network rather than expose multiple
  terminations"

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
 * Created on 24-May-07
 */
package ca.nengo.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SimulationException;
import ca.nengo.model.SimulationMode;
import ca.nengo.model.SpikeOutput;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.Units;
import ca.nengo.util.MU;
import ca.nengo.util.ScriptGenException;
import ca.nengo.util.VisiblyMutable;
import ca.nengo.util.VisiblyMutableUtils;

/**
 * <p>A Node that passes values through unaltered.</p>
 *
 * <p>This can be useful if an input to a Network is actually routed to multiple destinations,
 * but you want to handle this connectivity within the Network rather than expose multiple
 * terminations.</p>
 *
 * @author Bryan Tripp
 */
public class PassthroughNode implements Node {

	//implementation note: this class doesn't nicely extend AbstractNode

	private static Logger ourLogger = Logger.getLogger(PassthroughNode.class);

	/**
	 * Default name for a termination
	 */
	public static final String TERMINATION = "termination";


	/**
	 * Default name for an origin
	 */
	public static final String ORIGIN = "origin";

	private static final long serialVersionUID = 1L;

	private String myName;
	private int myDimension; //TODO: clean this up (can be obtained from transform)
	private Map<String, PassthroughTermination> myTerminations;
	private BasicOrigin myOrigin;
	private String myDocumentation;
	private transient List<VisiblyMutable.Listener> myListeners;

	/**
	 * Constructor for a simple passthrough with single input.
	 *
	 * @param name Node name
	 * @param dimension Dimension of data passing through
	 */
	public PassthroughNode(String name, int dimension) {
		myName = name;
		myDimension = dimension;
		myTerminations = new HashMap<String, PassthroughTermination>(10);
		myTerminations.put(TERMINATION, new PassthroughTermination(this, TERMINATION, dimension));
		myOrigin = new BasicOrigin(this, ORIGIN, dimension, Units.UNK);
		reset(false);
	}

	/**
	 * Constructor for a summing junction with multiple inputs.
	 *
	 * @param name Node name
	 * @param dimension Dimension of data passing through
	 * @param termDefinitions Name of each Termination (TERMINATION is used for the single-input case)
	 * 		and associated transform
	 */
	public PassthroughNode(String name, int dimension, Map<String, float[][]> termDefinitions) {
		myName = name;
		myDimension = dimension;
		myTerminations = new HashMap<String, PassthroughTermination>(10);

		Iterator<String> it = termDefinitions.keySet().iterator();
		while (it.hasNext()) {
			String termName = it.next();
			float[][] termTransform = termDefinitions.get(termName);
			myTerminations.put(termName, new PassthroughTermination(this, termName, dimension, termTransform));
		}
		myOrigin = new BasicOrigin(this, ORIGIN, dimension, Units.UNK);
		reset(false);
	}

	/**
	 * @see ca.nengo.model.Node#getName()
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
	 * @see ca.nengo.model.Node#getOrigin(java.lang.String)
	 */
	public Origin getOrigin(String name) throws StructuralException {
		if (ORIGIN.equals(name)) {
			return myOrigin;
		} else {
			throw new StructuralException("Unknown origin: " + name);
		}
	}

	/**
	 * @see ca.nengo.model.Node#getOrigins()
	 */
	public Origin[] getOrigins() {
		return new Origin[]{myOrigin};
	}

	/**
	 * @see ca.nengo.model.Node#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		if (myTerminations.containsKey(name)) {
			return myTerminations.get(name);
		} else {
			throw new StructuralException("Unknown termination: " + name);
		}
	}

	/**
	 * @see ca.nengo.model.Node#getTerminations()
	 */
	public Termination[] getTerminations() {
		return myTerminations.values().toArray(new PassthroughTermination[0]);
	}

	/**
	 * @see ca.nengo.model.Node#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {
		if (myTerminations.size() == 1) {
			myOrigin.setValues(myTerminations.values().iterator().next().getValues());
		} else {
			float[] values = new float[myDimension];
			Iterator<PassthroughTermination> it = myTerminations.values().iterator();
			while (it.hasNext()) {
				PassthroughTermination termination = it.next();
				InstantaneousOutput io = termination.getValues();
				if (io instanceof RealOutput) {
					values = MU.sum(values, ((RealOutput) io).getValues());
				} else if (io instanceof SpikeOutput) {
					boolean[] spikes = ((SpikeOutput) io).getValues();
					for (int i = 0; i < spikes.length; i++) {
						if (spikes[i]) {
                            values[i] += 1f/(endTime - startTime);
                        }
					}
				} else if (io == null) {
					throw new SimulationException("Null input to Termination " + termination.getName());
				} else {
					throw new SimulationException("Output type unknown: " + io.getClass().getName());
				}
			}
			myOrigin.setValues(new RealOutputImpl(values, Units.UNK, endTime));
		}
	}

	/**
	 * @see ca.nengo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		float time = 0;
		try {
			if (myOrigin.getValues() != null) {
                myOrigin.getValues().getTime();
            }
		} catch (SimulationException e) {
			ourLogger.warn("Exception getting time from existing output during reset", e);
		}
		myOrigin.setValues(new RealOutputImpl(new float[myOrigin.getDimensions()], Units.UNK, time));
		myOrigin.reset(randomize);
	}

	/**
	 * @see ca.nengo.model.SimulationMode.ModeConfigurable#getMode()
	 */
	public SimulationMode getMode() {
		return SimulationMode.DEFAULT;
	}

	/**
	 * Does nothing (only DEFAULT mode is supported).
	 *
	 * @see ca.nengo.model.SimulationMode.ModeConfigurable#setMode(ca.nengo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
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
			myListeners = new ArrayList<Listener>(2);
		}
		myListeners.add(listener);
	}

	/**
	 * @see ca.nengo.util.VisiblyMutable#removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)
	 */
	public void removeChangeListener(Listener listener) {
		myListeners.remove(listener);
	}

	@Override
	public Node clone() throws CloneNotSupportedException {
		PassthroughNode result = (PassthroughNode) super.clone();

		result.myOrigin = new BasicOrigin(result, FunctionInput.ORIGIN_NAME, myDimension, myOrigin.getUnits());
		result.myOrigin.setNoise(myOrigin.getNoise().clone());
		try {
			result.myOrigin.setValues(myOrigin.getValues());
		} catch (SimulationException e) {
			throw new CloneNotSupportedException("Problem copying origin values: " + e.getMessage());
		}

		result.myTerminations = new HashMap<String, PassthroughTermination>(10);
		for (PassthroughTermination oldTerm : myTerminations.values()) {
			PassthroughTermination newTerm = new PassthroughTermination(result, oldTerm.getName(),
					oldTerm.getDimensions(), MU.clone(oldTerm.getTransform()));
			result.myTerminations.put(newTerm.getName(), newTerm);
		}

		result.myListeners = new ArrayList<Listener>(5);

		return result;
	}

	/**
	 * Termination that receives input unaltered.
	 */
	public static class PassthroughTermination implements Termination {

		private static final long serialVersionUID = 1L;

		private Node myNode;
		private String myName;
		private int myDimension;
		private float[][] myTransform;
		private InstantaneousOutput myValues;

		/**
		 * @param node Parent node
		 * @param name Termination name
		 * @param dimension Dimensionality of input
		 */
		public PassthroughTermination(Node node, String name, int dimension) {
			myNode = node;
			myName = name;
			myDimension = dimension;
		}

		/**
		 * @param node Parent node
		 * @param name Termination name
		 * @param dimension Dimensionality of input
		 * @param transform Transformation matrix
		 */
		public PassthroughTermination(Node node, String name, int dimension, float[][] transform) {
			assert MU.isMatrix(transform);
			assert dimension == transform.length;

			myNode = node;
			myName = name;
			myDimension = transform[0].length;
			myTransform = transform;
		}

		public int getDimensions() {
			return myDimension;
		}

		public String getName() {
			return myName;
		}

		public void setValues(InstantaneousOutput values) throws SimulationException {
			if (values.getDimension() != myDimension) {
				throw new SimulationException("Input is wrong dimension (expected " + myDimension + " got " + values.getDimension() + ")");
			}

			if (myTransform != null) {
				if (values instanceof RealOutput) {
					float[] transformed = MU.prod(myTransform, ((RealOutput) values).getValues());
					values = new RealOutputImpl(transformed, values.getUnits(), values.getTime());
				} else {
					throw new SimulationException("Transforms can only be performed on RealOutput in a PassthroughNode");
				}
			}

			myValues = values;
		}

		/**
		 * @return Values currently stored in termination
		 */
		public InstantaneousOutput getValues() {
			return myValues;
		}

		public Node getNode() {
			return myNode;
		}

		/**
		 * @return Transformation matrix
		 */
		public float[][] getTransform() {
			return myTransform;
		}

		public boolean getModulatory() {
			return false;
		}

		public float getTau() {
			return 0;
		}

		public void setModulatory(boolean modulatory) {
			throw new RuntimeException("A termination on a passthrough node is never modulatory");
		}

		public void setTau(float tau) throws StructuralException {
			throw new StructuralException("A termination on a passthrough node has no dynamics");
		}
		
		public InstantaneousOutput getInput(){
			return myValues;
		}

		/**
		 * @see ca.nengo.model.Resettable#reset(boolean)
		 */
		public void reset(boolean randomize) {
			myValues = null;
		}

		@Override
		public PassthroughTermination clone() throws CloneNotSupportedException {
			return this.clone(myNode);
		}
		
		public PassthroughTermination clone(Node node) throws CloneNotSupportedException {
			PassthroughTermination result = (PassthroughTermination) super.clone();
			result.myNode = node;
			result.myValues = myValues.clone();
			result.myTransform = MU.clone(myTransform);
			return result;
		}

	}

	public Node[] getChildren() {
		return new Node[0];
	}

	public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException {
		return "";
	}

}
