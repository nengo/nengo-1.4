/*
 * Created on 29-Jan-2007
 */
package ca.neo.model.plasticity;

import ca.neo.math.Function;

/**
 * Specifies how a synaptic weight is modified as a function of presynaptic and postsynaptic
 * spike times/rates and optionally additional modulatory inputs.
 * 
 * Modulatory influences are identified by name. This name must match the name of a termination
 * onto the SynapticIntegrator (eg "dopamine"). 
 *
 * A SynapticPlasticityRule can implement getRateFunction(), getTimingFunction(), or both.
 * 
 * The getRateFunction() is used when either the presynaptic neuron, the postsynaptic neuron, 
 * or both are running in rate mode. It is also used if getTimingFunction() returns null.
 * 
 * A SynapticPlasticityRule may also implement getTimingFunction(). If implemented, this function 
 * is used only when both pre and postsynaptic neurons are running in a spiking mode. 
 * 
 * Note that a rate function can include factors that do not depend on either the presynaptic or 
 * postsynaptic firing rate, for example decay of weights over time.
 * 
 * TODO: max time step, temporal window 
 * 
 * @author Bryan Tripp
 */
public interface SynapticPlasticityRule {

	/**
	 * @return Ordered list of the names of terminations that have a modulatory influence
	 * 		on plasticity 
	 */
	public String[] getModulatoryInputs();

	/**
	 * @return A function dw/dt = f(r_pre, r_post, w, m), ie the absolute change in synaptic 
	 * 		weight per second in terms of presynaptic firing rate, postsynaptic firing rate,
	 *   	the synaptic weight, and additional modulatory inputs. The dimension of this 
	 *   	function must be (3+getModulatoryInputs().length).  
	 */
	public Function getRateFunction();

	/**
	 * TODO: should we use membrane potential here? 
	 * @return A function dw = f(t_pre - t_post, w, m), ie the absolute change in synaptic 
	 * 		weight per presynaptic spike in terms of the time difference between pre and post
	 * 		synaptic spikes, the synaptic weight, and additional modulatory inputs. The dimension 
	 *  	of this function must be (3+getModulatoryInputs().length). If null, getRateFunction()
	 *  	is used even if neurons are running in a spiking mode. 
	 */
	public Function getTimingFunction();
	
}
