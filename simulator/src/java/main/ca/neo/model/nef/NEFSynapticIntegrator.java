/*
 * Created on May 18, 2006
 */
package ca.neo.model.nef;

import ca.neo.model.neuron.SynapticIntegrator;

/**
 * <p>A SynapticIntegrator with a distinguished Termination that corresponds 
 * to a sum of synaptic input. This direct channel is used by NEFEnsembles. 
 * NEFEnsembles run more efficiently by combining and filtering inputs at 
 * the Ensemble level, so that these same combinations and filterings need not 
 * be performed multiple times, for each Neuron. Differences in net input to 
 * the different Neurons in an NEFEnsemble are accounted for by encoding vectors
 * (see Eliasmith & Anderson, 2003).</p> 
 * 
 * <p>There can also be additional inputs to an NEFSynapticIntegrator, beyond the 
 * distinguished input. These would reflect neuron-to-neuron connections that are 
 * not modelled under the NEF (e.g. synaptic connections with learned 
 * weights). The manner in which such inputs are combined with each other 
 * and with the distinguished input is determined by the SynapticIntegrator. </p>  
 *  
 * @author Bryan Tripp
 */
public interface NEFSynapticIntegrator extends SynapticIntegrator {

	/**
	 * @param value Value of filtered summary input. This value is typically in the range 
	 * 		[-1 1], and correponds to an inner product of vectors in the space  
	 * 		represented by the NEFEnsemble to which this SynapticIntegrator belongs. 
	 */
	public void setRadialInput(float value);
	
}
