/*
 * Created on 31-May-2006
 */
package ca.neo.model.impl;

import java.util.HashSet;
import java.util.Set;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Node;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.util.Configuration;
import ca.neo.util.MU;
import ca.neo.util.impl.ConfigurationImpl;

/**
 * <p>A Termination that is composed of Terminations onto multiple Nodes. 
 * The dimensions of the Terminations onto each Node must be the same.</p>
 * 
 * <p>Physiologically, this might correspond to a set of n axons passing into 
 * a neuron pool. Each neuron in the pool receives synaptic connections 
 * from as many as n of these axons (zero weight is equivalent to no 
 * connection). Sometimes we deal with this set of axons only in terms 
 * of the branches they send to one specific Neuron (a Node-level Termination)
 * but here we deal with all branches (an Emsemble-level Termination). 
 * In either case the spikes transmitted by the axons are the same.</p>  
 * 
 * TODO: test
 *  
 * @author Bryan Tripp
 */
public class EnsembleTermination implements Termination {

	private static final long serialVersionUID = 1L;
	
	private Node myNode;
	private String myName;
	private Termination[] myNodeTerminations;
	private ConfigurationImpl myConfiguration;
	
	/**
	 * @param node The parent Node
	 * @param name Name of this Termination
	 * @param nodeTerminations Node-level Terminations that make up this Termination
	 * @throws StructuralException If dimensions of different terminations are not all the same
	 */
	public EnsembleTermination(Node node, String name, Termination[] nodeTerminations) throws StructuralException {
		checkSameDimension(nodeTerminations, name);
		
		myNode = node;
		myName = name;
		myNodeTerminations = nodeTerminations;
		
		myConfiguration = new ConfigurationImpl(this);
		refreshProperties();
	}
	
	public void refreshProperties() {
		String[] propertyNames = findSharedProperties(myNodeTerminations);
		for (int i = 0; i < propertyNames.length; i++) {
			//not strict on type at this level (may be different types in components)
			myConfiguration.addProperty(propertyNames[i], Object.class, getProperty(propertyNames[i]));
		}	
		
		float[][] weights = new float[myNodeTerminations.length][];
		for (int i = 0; i < weights.length; i++) {
			Object o = myNodeTerminations[i].getConfiguration().getProperty(Termination.WEIGHTS);
			weights[i] = (o != null && o instanceof float[][]) ? ((float[][]) o)[0] : new float[myNodeTerminations[i].getDimensions()]; 
		}
//		myConfiguration.addProperty(Termination.WEIGHTS, float[][].class, MU.transpose(weights));
		myConfiguration.addProperty(Termination.WEIGHTS, float[][].class, weights);
	}
	
	private static void checkSameDimension(Termination[] terminations, String name) throws StructuralException {
		int dim = terminations[0].getDimensions();
		for (int i = 1; i < terminations.length; i++) {
			if (terminations[i].getDimensions() != dim) {
				throw new StructuralException("All Terminations " + name + " must have the same dimension");
			}
		}
	}
	
	//return property names shared by all given terminations
	private static String[] findSharedProperties(Termination[] terminations) {
		Set<String> result = copyIntoSet(terminations[0].getConfiguration().listPropertyNames());		
		
		for (int i = 1; i < terminations.length; i++) {
			Set comparison = copyIntoSet(terminations[i].getConfiguration().listPropertyNames());
			result.retainAll(comparison);
		}
		
		return result.toArray(new String[0]);
	}
	
	//used above
	private static Set<String> copyIntoSet(String[] things) {
		Set<String> result = new HashSet<String>(things.length * 2);
		for (int i = 0; i < things.length; i++) {
			result.add(things[i]);
		}
		return result;
	}

	private Object getProperty(String name) {
		Object result = myNodeTerminations[0].getConfiguration().getProperty(name);
		
		boolean mixed = false;
		for (int i = 1; i < myNodeTerminations.length && !mixed; i++) {
			if ( !myNodeTerminations[i].getConfiguration().getProperty(name).equals(result) ) {
				mixed = true;
			}
		}
		
		if (mixed) {
			result = Termination.MIXED_VALUE;
		}
		
		return result;
	}

	/**
	 * @see ca.neo.model.Termination#getName()
	 */
	public String getName() {
		return myName;
	}

	/**
	 * @see ca.neo.model.Termination#getDimensions()
	 */
	public int getDimensions() {
		return myNodeTerminations[0].getDimensions();
	}

	/**
	 * @see ca.neo.model.Termination#setValues(ca.neo.model.InstantaneousOutput)
	 */
	public void setValues(InstantaneousOutput values) throws SimulationException {
		if (values.getDimension() != getDimensions()) {
			throw new SimulationException("Input to this Termination must have dimension " + getDimensions());
		}
		
		for (int i = 0; i < myNodeTerminations.length; i++) {
			myNodeTerminations[i].setValues(values);
		}
	}

	/** 
	 * @see ca.neo.util.Configurable#getConfiguration()
	 */
	public Configuration getConfiguration() {
		return myConfiguration;
	}

	/** 
	 * @see ca.neo.util.Configurable#propertyChange(java.lang.String, java.lang.Object)
	 */
	public void propertyChange(String propertyName, Object newValue) throws StructuralException {
		Object[] oldValues = new Object[myNodeTerminations.length];
		
		for (int i = 0; i < myNodeTerminations.length; i++) {
			oldValues[i] = myNodeTerminations[i].getConfiguration().getProperty(propertyName);
			try {
				myNodeTerminations[i].getConfiguration().setProperty(propertyName, newValue);
			} catch (StructuralException e) {
				//roll back changes
				for (int j = 0; j < i; j++) {
					myNodeTerminations[j].getConfiguration().setProperty(propertyName, oldValues[j]); 
				}
				throw new StructuralException(e);
			}
		}
	}

	/**
	 * @see ca.neo.model.Termination#getNode()
	 */
	public Node getNode() {
		return myNode;
	}

}
