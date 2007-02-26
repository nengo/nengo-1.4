/*
 * Created on 13-Jun-2006
 */
package ca.neo.model.impl;


import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import ca.neo.io.FileManager;
import ca.neo.math.ApproximatorFactory;
import ca.neo.math.Function;
import ca.neo.math.LinearApproximator;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.math.impl.IdentityFunction;
import ca.neo.math.impl.IndicatorPDF;
import ca.neo.math.impl.WeightedCostApproximator;
import ca.neo.model.Node;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFNode;
import ca.neo.model.nef.impl.NEFEnsembleImpl;
import ca.neo.model.neuron.Neuron;
import ca.neo.model.neuron.impl.LIFNeuronFactory;
import ca.neo.util.VectorGenerator;
import ca.neo.util.impl.RandomHypersphereVG;

/**
 * Produces Ensembles and groups of SpikingNeurons either with default or custom-configured 
 * parameters.  
 * 
 * TODO: test
 * TODO: clean up 
 * 
 * @author Bryan Tripp
 */
public class EnsembleFactory {
	
	private static Logger ourLogger = Logger.getLogger(EnsembleFactory.class);
	
	private NodeFactory myDefaultNeuronFactory;
	private VectorGenerator myVectorGenerator;
	private File myDatabase;
	
	/**
	 * Creates an EnsembleFactory that uses default ConstructorSpecs to create SynapticIntegrators 
	 * and SpikeGenerators, and a default VectorGenerator to create encoding vectors. These can 
	 * all be changed with the corresponding setters. 
	 */
	public EnsembleFactory() {		
		myDefaultNeuronFactory 
			= new LIFNeuronFactory(.02f, .002f, new IndicatorPDF(200f, 400f), new IndicatorPDF(-.9f, .9f));
		
		setVectorGenerator(new RandomHypersphereVG(true, 1f, 0f));
		setDatabase(FileManager.getDefaultLocation()); 
	}
	
	/**
	 * @return Directory for saving / loading ensembles
	 */
	public File getDatabase() {
		return myDatabase;
	}
	
	/**
 	 * @param database New directory for saving / loading ensembles
	 */
	public void setDatabase(File database) {
		if ( !database.isDirectory() ) {
			throw new IllegalArgumentException("Database must be a file directory");
		}
		
		myDatabase = database;
	}
	
	/**
	 * @param factory NeuronFactory to set as default 
	 */
	public void setDefaultNeuronFactory(NodeFactory factory) {
		myDefaultNeuronFactory = factory;
	}
	
	/**
	 * 
	 * @return The NeuronFactory that is used by default, ie unless a factory is specified in the call to make() 
	 */
	public NodeFactory getDefaultNeuronFactory() {
		return myDefaultNeuronFactory;
	}
	
	/**
 	 * @param generator The generator of encoding vectors 
	 */
	public void setVectorGenerator(VectorGenerator generator) {
		myVectorGenerator = generator;
	}
	
	/**
	 * @return The generator of encoding vectors
	 */
	public VectorGenerator getVectorGenerator() {
		return myVectorGenerator;
	}
 	
	/**
	 * Uses the default NeuronFactory. 
	 * 
	 * @param name Name of the NEFEnsemble
	 * @param n Number of neurons in the ensemble
	 * @param dim Dimension of the ensemble. 
	 * @return NEFEnsemble containing Neurons generated with the default NeuronFactory   
	 * @throws StructuralException if there is any error attempting to create the ensemble
	 */
	public NEFEnsemble make(String name, int n, int dim) throws StructuralException {
		return doMake(name, n, dim, myDefaultNeuronFactory, myVectorGenerator);
	}
	
	/**
	 * Loads an NEFEnsemble, or creates and saves it.
	 *   
	 * @param name Name of the NEFEnsemble
	 * @param n Number of neurons in the ensemble
	 * @param dim Dimension of the ensemble. 
	 * @param fileName Name of file to load from / save to
	 * @param overwrite If false, loads the ensemble if it exists in the factory's current database. 
	 * 		If true, creates a new ensemble regardless and overwrites any existing ensemble in the 
	 * 		database. 
	 * @return Either new NEFEnsemble generated according to specs and with default NeuronFactory, or 
	 * 		an ensemble loaded from disk   
	 * @throws StructuralException if there is any error attempting to create the ensemble
	 */
	public NEFEnsemble make(String name, int n, int dim, String fileName, boolean overwrite) throws StructuralException {
		NEFEnsemble result = null;
		
		File ensembleFile = new File(myDatabase, fileName + "." + FileManager.ENSEMBLE_EXTENSION);
		
		FileManager fm = new FileManager();
		if (!overwrite && ensembleFile.exists() && ensembleFile.canRead()) {
			try {
				result = (NEFEnsemble) fm.load(ensembleFile);
			} catch (Exception e) {
				ourLogger.error("Failed to load file " + ensembleFile.getAbsolutePath() + ". New ensemble will be created.", e);
			}
		}
		
		if (result == null) {
			result = doMake(name, n, dim, myDefaultNeuronFactory, myVectorGenerator);
			
			try {
				fm.save(result, ensembleFile);
			} catch (IOException e) {
				ourLogger.error("Failed to save file " + ensembleFile.getAbsolutePath(), e);
			}
		}
		
		return result;
	}
	
	/**
	 * Uses a user-specified NeuronFactory. 
	 * 
	 * @param name Name of the NEFEnsemble
	 * @param n Number of neurons in the ensemble
	 * @param dim Dimension of the ensemble.
	 * @param factory NeuronFactory with which Neurons are to be created 
	 * @return NEFEnsemble containing Neurons generated with the given NeuronFactory   
	 * @throws StructuralException if there is any error attempting to create the ensemble
	 */
	public NEFEnsemble make(String name, int n, int dim, NodeFactory factory) throws StructuralException {
		return doMake(name, n, dim, factory, myVectorGenerator);
	}
	
	//common make(...) implementation 
	private NEFEnsemble doMake(String name, int n, int dim, NodeFactory factory, VectorGenerator generator) 
			throws StructuralException {
		
		NEFNode[] nodes = new NEFNode[n];
		
		for (int i = 0; i < n; i++) {
			Node node = factory.make("neuron" + i);
			if ( !(node instanceof NEFNode) ) {
				throw new StructuralException("Nodes must be NEFNodes");
			}
			nodes[i] = (NEFNode) node;
			
			nodes[i].setMode(SimulationMode.CONSTANT_RATE);
			if ( !nodes[i].getMode().equals(SimulationMode.CONSTANT_RATE) ) {
				throw new StructuralException("Neurons in an NEFEnsemble must support CONSTANT_RATE mode");
			}
			
			nodes[i].setMode(SimulationMode.DEFAULT);
		}

		float[][] encoders = generator.genVectors(n, dim);
		
		NEFEnsemble result = construct(name, nodes, encoders);
		
		addDefaultOrigins(result);
		
		return result;
	}
	
	//TODO: document as strategy
	public NEFEnsemble construct(String name, NEFNode[] nodes, float[][] encoders) throws StructuralException {
		VectorGenerator vg = new RandomHypersphereVG(false, 1, 0);
		int dim = encoders[0].length;
		ApproximatorFactory af = new WeightedCostApproximator.Factory(0.1f);
		return new NEFEnsembleImpl(name, nodes, encoders, af, vg.genVectors(dim*300, dim));
	}
	
	//TODO: document as strategy
	public void addDefaultOrigins(NEFEnsemble ensemble) throws StructuralException {
		Function[] functions = new Function[ensemble.getDimension()];
		for (int i = 0; i < functions.length; i++) {
			functions[i] = new IdentityFunction(ensemble.getDimension(), i);
		}		

		ensemble.addDecodedOrigin(NEFEnsemble.X, functions, Neuron.AXON);
	}

}
