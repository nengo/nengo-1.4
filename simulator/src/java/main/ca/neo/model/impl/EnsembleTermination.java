/*
 * Created on 31-May-2006
 */
package ca.neo.model.impl;

import java.util.HashSet;
import java.util.Set;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;

/**
 * A Termination that is composed of Terminations onto multiple Neurons. 
 * The dimensions of the Terminations onto each Neuron must be the same.
 * Physically, this corresponds roughly to a set of n axons passing into 
 * a neuron pool. Each Neuron in the pool receives synaptic connections 
 * from as many as n of these neurons (zero weight is equivalent to no 
 * connection). Sometimes we deal with this set of axons only in terms 
 * of the branches they send to one specific Neuron (a Neuron-level Termination)
 * but here we deal with all branches (an Emsemble-level Termination). 
 * In either case the spikes transmitted by the axons are the same.  
 *  
 * @author Bryan Tripp
 */
public class EnsembleTermination implements Termination {

	private static final long serialVersionUID = 1L;
	
	private String myName;
	private Termination[] myNeuronTerminations;
	private String[] myPropertyNames;
	
	public EnsembleTermination(String name, Termination[] neuronTerminations) throws StructuralException {
		checkSameDimension(neuronTerminations, name);
		
		myName = name;
		myNeuronTerminations = neuronTerminations;
		myPropertyNames = findSharedProperties(neuronTerminations);
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
		Set result = copyIntoSet(terminations[0].listPropertyNames());		
		
		for (int i = 1; i < terminations.length; i++) {
			Set comparison = copyIntoSet(terminations[i].listPropertyNames());
			result.retainAll(comparison);
		}
		
		return (String[]) result.toArray(new String[0]);
	}
	
	//used above
	private static Set copyIntoSet(Object[] things) {
		Set result = new HashSet(things.length * 2);
		for (int i = 0; i < things.length; i++) {
			result.add(things[i]);
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
		return myNeuronTerminations[0].getDimensions();
	}

	/**
	 * @see ca.neo.model.Termination#setValues(ca.neo.model.InstantaneousOutput)
	 */
	public void setValues(InstantaneousOutput values) throws SimulationException {
		if (values.getDimension() != getDimensions()) {
			throw new SimulationException("Input to this Termination must have dimension " + getDimensions());
		}
		
		for (int i = 0; i < myNeuronTerminations.length; i++) {
			myNeuronTerminations[i].setValues(values);
		}
	}

	/**
	 * @return Properties that are shared by all Neuron Terminations that underlie 
	 * 		this Termination 
	 * 
	 * @see ca.neo.model.Termination#listPropertyNames()
	 */
	public String[] listPropertyNames() {
		return myPropertyNames;
	}

	/**
	 * @return Value of given property, if value is the same for all underlying Neuron 
	 * 		Terminations, or Termination.MIXED_VALUE if underlying Terminations report
	 * 		differing values, or null if the property is not shared by all underlying 
	 * 		Terminations. 
	 * @see ca.neo.model.Termination#getProperty(java.lang.String)
	 */
	public String getProperty(String name) {
		String result = null;
		
		if (knownProperty(name)) {
			result = myNeuronTerminations[0].getProperty(name);
			boolean mixed = false;
			for (int i = 1; i < myNeuronTerminations.length && !mixed; i++) {
				if ( !myNeuronTerminations[i].getProperty(name).equals(result) ) {
					mixed = true;
				}
			}
			if (mixed) {
				result = Termination.MIXED_VALUE;
			}
		}
		
		return result;
	}

	/**
	 * @see ca.neo.model.Termination#setProperty(java.lang.String, java.lang.String)
	 */
	public void setProperty(String name, String value) throws StructuralException {
		if (knownProperty(name)) {
			String[] oldValues = new String[myNeuronTerminations.length];
			
			for (int i = 0; i < myNeuronTerminations.length; i++) {
				oldValues[i] = myNeuronTerminations[i].getProperty(name);
				try {
					myNeuronTerminations[i].setProperty(name, value);
				} catch (StructuralException e) {
					//roll back changes
					for (int j = 0; j < i; j++) {
						myNeuronTerminations[j].setProperty(name, oldValues[j]); 
					}
					throw new StructuralException(e);
				}
			}
		}
	}
	
	//true if given property is in myPropertyNames list 
	private boolean knownProperty(String propertyName) {
		boolean known = false;
		for (int i = 0; i < myPropertyNames.length && !known; i++) {
			if (myPropertyNames[i].equals(propertyName)) {
				known = true;
			}
		}
		return known;
	}

}
