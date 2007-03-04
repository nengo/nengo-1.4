/*
 * Created on 20-Feb-07
 */
package ca.neo.model.nef;

import ca.neo.math.ApproximatorFactory;
import ca.neo.model.StructuralException;
import ca.neo.model.impl.NodeFactory;
import ca.neo.util.VectorGenerator;

/**
 * Provides a convenient and configurable way to create NEFEnsembles. 
 *  
 * @author Bryan Tripp
 */
public interface NEFEnsembleFactory {

	/**
	 * @return The NodeFactory used to create Nodes that make up new Ensembles
	 */
	public NodeFactory getNodeFactory();

	/**
	 * @param factory NodeFactory to be used to create Nodes that make up new Ensembles
	 */
	public void setNodeFactory(NodeFactory factory);

	/**
	 * @return The VectorGenerator used to create encoding vectors that are associated with 
	 * 		each Node in a new Ensemble
	 */
	public VectorGenerator getEncoderFactory();
	
	/**
	 * @param factory A VectorGenerator to be used to create encoding vectors that are associated 
	 * 		with each Node in a new Ensemble
	 */
	public void setEncoderFactory(VectorGenerator factory);
	
	/**
	 * @return The VectorGenerator used to generate the vector states at which decoding functions are 
	 * 		evaluated
	 */
	public VectorGenerator getEvalPointFactory();

	/**
	 * @param factory A VectorGenerator to be used to generate the vector states at which decoding 
	 * 		functions are evaluated
	 */
	public void setEvalPointFactory(VectorGenerator factory);

	/**
	 * @return The factory that creates LinearApproximators used in decoding ensemble output  
	 */
	public ApproximatorFactory getApproximatorFactory();
	
	/**
	 * @param factory A factory for creating the LinearApproximators used in decoding ensemble output 
	 */
	public void setApproximatorFactory(ApproximatorFactory factory);	
	
	/**
	 * @param name Name of the NEFEnsemble
	 * @param n Number of neurons in the ensemble
	 * @param dim Dimension of the ensemble. 
	 * @return NEFEnsemble containing Neurons generated with the default NeuronFactory   
	 * @throws StructuralException if there is any error attempting to create the ensemble
	 */
	public NEFEnsemble make(String name, int n, int dim) throws StructuralException;
	
	/**
	 * Loads an NEFEnsemble, or creates and saves it.
	 *   
	 * @param name Name of the NEFEnsemble
	 * @param n Number of neurons in the ensemble
	 * @param dim Dimension of the ensemble. 
	 * @param storageName Name for storage (eg filename, db key; may have to be more fully qualified than 
	 * 		name param, if ensembles belonging to multiple networks are stored in the same place)
	 * @param overwrite If false, loads the ensemble if it can be found in storage. 
	 * 		If true, creates a new ensemble regardless and overwrites any existing ensemble. 
	 * @return Either new NEFEnsemble generated according to specs and with default NeuronFactory, or 
	 * 		a previously-created ensemble loaded from storage   
	 * @throws StructuralException if there is any error attempting to create the ensemble
	 */
	public NEFEnsemble make(String name, int n, int dim, String storageName, boolean overwrite) throws StructuralException;	
	
}
