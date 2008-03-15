/*
 * Created on 24-May-07
 */
package ca.neo.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.SpikeOutput;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.util.Configuration;
import ca.neo.util.MU;
import ca.neo.util.VisiblyMutable;
import ca.neo.util.VisiblyMutableUtils;
import ca.neo.util.impl.ConfigurationImpl;

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

	public static final String TERMINATION = "termination";
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
	 * @see ca.neo.model.Node#getName()
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
	 * @see ca.neo.model.Node#getOrigin(java.lang.String)
	 */
	public Origin getOrigin(String name) throws StructuralException {
		if (ORIGIN.equals(name)) {
			return myOrigin;
		} else {
			throw new StructuralException("Unknown origin: " + name);
		}
	}

	/**
	 * @see ca.neo.model.Node#getOrigins()
	 */
	public Origin[] getOrigins() {
		return new Origin[]{myOrigin};
	}

	/**
	 * @see ca.neo.model.Node#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {		
		if (myTerminations.containsKey(name)) {
			return myTerminations.get(name);
		} else {
			throw new StructuralException("Unknown termination: " + name);
		}
	}

	/**
	 * @see ca.neo.model.Node#getTerminations()
	 */
	public Termination[] getTerminations() {
		return myTerminations.values().toArray(new PassthroughTermination[0]);
	}

	/**
	 * @see ca.neo.model.Node#run(float, float)
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
						if (spikes[i]) values[i] += 1f/(endTime - startTime); 
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
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		float time = 0; 
		try {
			if (myOrigin.getValues() != null) myOrigin.getValues().getTime();
		} catch (SimulationException e) {
			ourLogger.warn("Exception getting time from existing output during reset", e);
		}
		myOrigin.setValues(new RealOutputImpl(new float[myOrigin.getDimensions()], Units.UNK, time));
		myOrigin.reset(randomize);
	}

	/**
	 * @see ca.neo.model.SimulationMode.ModeConfigurable#getMode()
	 */
	public SimulationMode getMode() {
		return SimulationMode.DEFAULT;
	}

	/**
	 * Does nothing (only DEFAULT mode is supported). 
	 * 
	 * @see ca.neo.model.SimulationMode.ModeConfigurable#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
	}
	
	/**
	 * @see ca.neo.model.Node#getDocumentation()
	 */
	public String getDocumentation() {
		return myDocumentation;
	}

	/**
	 * @see ca.neo.model.Node#setDocumentation(java.lang.String)
	 */
	public void setDocumentation(String text) {
		myDocumentation = text;
	}

	/**
	 * @see ca.neo.util.VisiblyMutable#addChangeListener(ca.neo.util.VisiblyMutable.Listener)
	 */
	public void addChangeListener(Listener listener) {
		if (myListeners == null) {
			myListeners = new ArrayList<Listener>(2);
		}
		myListeners.add(listener);
	}

	/**
	 * @see ca.neo.util.VisiblyMutable#removeChangeListener(ca.neo.util.VisiblyMutable.Listener)
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

	private static class PassthroughTermination implements Termination {
		
		private static final long serialVersionUID = 1L;
		
		private Node myNode;
		private String myName;
		private int myDimension;
		private float[][] myTransform;
		private Configuration myConfiguration;
		private InstantaneousOutput myValues;
		
		public PassthroughTermination(Node node, String name, int dimension) {
			myNode = node;
			myName = name;
			myDimension = dimension;
			myConfiguration = new ConfigurationImpl(this);
		}
		
		public PassthroughTermination(Node node, String name, int dimension, float[][] transform) {
			assert MU.isMatrix(transform);
			assert dimension == transform.length;
			
			myNode = node;
			myName = name;
			myDimension = transform[0].length;
			myTransform = transform;
			myConfiguration = new ConfigurationImpl(this);
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
		
		public InstantaneousOutput getValues() {
			return myValues;
		}

		public Configuration getConfiguration() {
			return myConfiguration;
		}

		public void propertyChange(String propertyName, Object newValue) throws StructuralException {
		}

		public Node getNode() {
			return myNode;
		}
		
		public float[][] getTransform() {
			return myTransform;
		}

		/**
		 * @see ca.neo.model.Resettable#reset(boolean)
		 */
		public void reset(boolean randomize) {
			myValues = null;
		}

		@Override
		public Termination clone() throws CloneNotSupportedException {
			PassthroughTermination result = new PassthroughTermination(myNode, myName, myDimension, MU.clone(myTransform));
			result.myValues = myValues.clone();
			return result;
		}
		
	}

}
