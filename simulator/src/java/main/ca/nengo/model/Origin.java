/*
 * Created on May 16, 2006
 */
package ca.nengo.model;

import java.io.Serializable;

/**
 * <p>An source of information in a circuit model. Origins arise from Ensembles, 
 * ExternalInputs, and individual Neurons (although the latter Origins are mainly used 
 * internally within Ensembles, ie an Ensemble typically combines Neuron Origins into 
 * Ensemble Origins).</p>
 * 
 * <p>An Origin object will often correspond loosely to the anatomical origin of a neural 
 * projection in the brain. However, there is not a strict correspondance. In particular, 
 * an Origin object may relate specifically to a particular decoding of 
 * activity in an Ensemble. For example, suppose a bundle of axons bifurcates and 
 * terminates in two places. This would be modelled with two Origin objects if the 
 * postsynaptic Ensembles received different functions of the variables represented by the 
 * presynaptic Ensemble. So, an Origin is best thought about as a source of information 
 * in a certain form, rather than an anatomical source of axons.</p>  
 *    
 * @author Bryan Tripp
 */
public interface Origin extends Serializable, Cloneable {

	/**
	 * @return Name of this Origin (unique in the scope of a source of Origins, eg a Neuron or 
	 * 		Ensemble) 
	 */
	public String getName();
	
	/**
	 * @return Dimensionality of information coming from this Origin (eg number of 
	 * 		axons, or dimension of decoded function of variables represented by the 
	 * 		Ensemble) 
	 */
	public int getDimensions();
	
	/**
	 * @return Instantaneous output from this Origin.
	 * @throws SimulationException if there is any problem retrieving values  
	 */
	public InstantaneousOutput getValues() throws SimulationException;
	
	/**
	 * @return The Node to which the Origin belongs
	 */
	public Node getNode();
	
	public Origin clone() throws CloneNotSupportedException;
	
}
