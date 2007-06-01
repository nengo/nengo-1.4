/*
 * Created on May 18, 2006
 */
package ca.neo.model;

import java.io.Serializable;

import ca.neo.util.Configurable;

/**
 * <p>An destination for information in a circuit model. A Termination is normally associated 
 * with a neural Ensemble or an individual Neuron, although other terminations could be modelled 
 * (eg muscles).</p>
 * 
 * <p>Terminations onto neural Ensembles can be modelled in two ways. First, a Termination can 
 * model a set of axons that end at an Ensemble. In this case the dimension of the Termination 
 * equals the number of axons. Associated with each Neuron in the Ensemble will be synaptic weights
 * (possibly zero) corresponding to each axon (i.e. each dimension of the Termination).<p>
 * 
 * <p>Alternatively, in a connection between two NEFEnsembles, a termination may have a smaller 
 * number of dimensions that summarize activity in all the axons. In this case, each dimension 
 * of the termination corresponds to a dimension of a represented vector or function. Synaptic 
 * weights are not stored anywhere explicitly. Synaptic weights are instead decomposed into 
 * decoding vectors, a transformation matrix, and encoding vectors. The decoding vectors are 
 * associated with the sending Ensemble. The encoding vectors are associated with the receiving 
 * ensemble. The transformation matrix is a property of the projection, but it happens that we 
 * keep it with the receiving Ensemble, for various reasons. See Eliasmith & Anderson, 2003 for 
 * related theory.</p> 
 * 
 * <P>Note that in each case, a corresponding Origin and Termination have the same dimensionality, 
 * and that this is the dimensionality associated with the Origin. The receiving Ensemble is responsible 
 * for the weight matrix in the first case, and for the transformation matrix in the second case, 
 * which transform inputs into dimensions that the receiving Ensemble can use.</p>
 * 
 * <p>Note also that the second method is more efficient when the number of neurons in each ensemble 
 * is much larger than the number of dimensions in represented variables, as is typical.</p>
 * 
 * TODO: should probably extract properties-related methods into another interface (Configurable?)
 *   possibly supporting types 
 *   
 * @author Bryan Tripp
 */
public interface Termination extends Configurable, Serializable {

	/**
	 * Standard name of the post-synaptic current time constant property (most Terminations  
	 * have this property). 
	 */
	public static final String TAU_PSC = "tauPSC";
	
	/**
	 * A modulatory termination does not induce current directly but may influence membrane properties or 
	 * excitability: Boolean(true) means modulatory; Boolean(false) means not modulatory.  
	 */
	public static final String MODULATORY = "MODULATORY";
	
	/**
	 * Standard name of synaptic weights property (a float[][]) 
	 */
	public static final String WEIGHTS = "WEIGHTS";
	
	/**
	 * A property value for Terminations that are composed of multiple underlying 
	 * Terminations. This property value indicates that different underlying Terminations 
	 * report different values for the requested property.  
	 */
	public static final String MIXED_VALUE = "MIXED VALUE";
	
	/**
	 * @return Name of this Termination (unique in the scope of the object the which the Termination 
	 * 		is connected, eg the Neuron or Ensemble).  
	 */
	public String getName();
	
	/**
	 * @return Dimensionality of information entering this Termination (eg number of 
	 * 		axons, or dimension of decoded function of variables represented by sending 
	 * 		Ensemble) 
	 */
	public int getDimensions();
	
	/**
	 * @param values InstantaneousOutput (eg from another Ensemble) to apply to this Termination.
	 * @throws SimulationException if the given values have the wrong dimension 
	 */
	public void setValues(InstantaneousOutput values) throws SimulationException;
	
}
