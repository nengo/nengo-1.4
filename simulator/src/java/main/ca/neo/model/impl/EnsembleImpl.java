/*
 * Created on 31-May-2006
 */
package ca.neo.model.impl;

import java.util.ArrayList;
import java.util.List;

import ca.neo.model.ExpandableEnsemble;
import ca.neo.model.Origin;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.neuron.ExpandableSynapticIntegrator;
import ca.neo.model.neuron.Neuron;

/**
 * <p>Default implementation of Ensemble.</p>
 * 
 * <p>Origins or Terminations can be set up on Neurons before they are grouped into an 
 * Ensemble. After Neurons are added to an Ensemble, no Origins or Terminations should 
 * be added to them directly. Terminations can be added with EnsembleImpl.addTermination(...) 
 * If a Termination is added directly to a Neuron after the Neuron is added to the 
 * Ensemble, the Termination will not appear in Ensemble.getTerminations()</p>  
 * 
 * TODO: test
 * 
 * @author Bryan Tripp
 */
public class EnsembleImpl extends AbstractEnsemble implements ExpandableEnsemble {

	private static final long serialVersionUID = 1L;
	
	private Neuron[] myExpandableNeurons;
	private Origin[] myOrigins;
	private Termination[] myTerminations;
	
	/**
	 * @param name Name of Ensemble
	 * @param neurons Neurons that make up the Ensemble
	 * @throws StructuralException if the given Neurons contain Terminations with the same 
	 * 		name but different dimensions
	 */
	public EnsembleImpl(String name, Neuron[] neurons) throws StructuralException {
		super(name, neurons);
		
		myExpandableNeurons = findExpandable(neurons);
		myOrigins = findOrigins(neurons);
		myTerminations = findTerminations(neurons);
	}

	//finds neurons with expandable synaptic integrators 
	private static Neuron[] findExpandable(Neuron[] neurons) {
		ArrayList result = new ArrayList(neurons.length * 2);
		
		for (int i = 0; i < neurons.length; i++) {
			if (neurons[i].getIntegrator() instanceof ExpandableSynapticIntegrator) {
				result.add(neurons[i]);
			}
		}
		
		return (Neuron[]) result.toArray(new Neuron[0]);
	}
	
	public Origin[] getOrigins() {
		return myOrigins;
	}

	/**
	 * @see ca.neo.model.Ensemble#getTerminations()
	 */
	public Termination[] getTerminations() {
		return myTerminations;
	}

	/**
	 * This Ensemble does not support SimulationMode.DIRECT. 
	 * 
	 * @see ca.neo.model.Ensemble#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
		if (mode.equals(SimulationMode.DIRECT)) {
			mode = mode.getFallbackMode();
		}
		
		super.setMode(mode);
	}

	/**
	 * @see ca.neo.model.ExpandableEnsemble#addTermination(java.lang.String, float[][], float)
	 */
	public synchronized Termination addTermination(String name, float[][] weights, float tauPSC) throws StructuralException {
		if (myExpandableNeurons.length != weights.length) {
			throw new StructuralException(weights.length + " sets of weights given for " 
					+ myExpandableNeurons.length + " expandable neurons");
		}
		
		int dimension = weights[0].length;
		
		Termination[] components = new Termination[myExpandableNeurons.length];
		for (int i = 0; i < myExpandableNeurons.length; i++) {
			if (weights[i].length != dimension) {
				throw new StructuralException("Equal numbers of weights are needed for termination onto each neuron");
			}
			
			ExpandableSynapticIntegrator integrator = (ExpandableSynapticIntegrator) myExpandableNeurons[i].getIntegrator();
			components[i] = integrator.addTermination(name, weights[i], tauPSC);
		}
		
		Termination result = new EnsembleTermination(name, components);
		
		Termination[] newTerminations = new Termination[myTerminations.length + 1];
		System.arraycopy(myTerminations, 0, newTerminations, 0, myTerminations.length);
		newTerminations[myTerminations.length] = result;
		myTerminations = newTerminations;
		
		return result;
	}

	/**
	 * @see ca.neo.model.ExpandableEnsemble#removeTermination(java.lang.String)
	 */
	public synchronized void removeTermination(String name) {
		List newTerminations = new ArrayList(myTerminations.length * 2);
		
		for (int i = 0; i < myTerminations.length; i++) {
			if (!myTerminations[i].getName().equals(name)) {
				newTerminations.add(myTerminations[i]);
			}
		}
		
		myTerminations = (Termination[]) newTerminations.toArray(new Termination[0]);
	}

	/**
	 * @see ca.neo.model.ExpandableEnsemble#getExpandableNeurons()
	 */
	public Neuron[] getExpandableNeurons() {
		return myExpandableNeurons;
	}

	/**
	 * @see ca.neo.model.Ensemble#getOrigin(java.lang.String)
	 */
	public Origin getOrigin(String name) throws StructuralException {
		Origin result = null;
		
		for (int i = 0; i < myOrigins.length && result == null; i++) {
			if (myOrigins[i].getName().equals(name)) {
				result = myOrigins[i];
			}
		}
		
		return result;
	}

	/**
	 * @see ca.neo.model.Ensemble#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		Termination result = null;
		
		for (int i = 0; i < myTerminations.length && result == null; i++) {
			if (myTerminations[i].getName().equals(name)) {
				result = myTerminations[i];
			}
		}
		
		return result;
	}

}
