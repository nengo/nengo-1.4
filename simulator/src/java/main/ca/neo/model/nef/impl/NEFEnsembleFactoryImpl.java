/*
 * Created on 4-Mar-07
 */
package ca.neo.model.nef.impl;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import ca.neo.io.FileManager;
import ca.neo.math.ApproximatorFactory;
import ca.neo.math.Function;
import ca.neo.math.impl.IdentityFunction;
import ca.neo.math.impl.IndicatorPDF;
import ca.neo.math.impl.WeightedCostApproximator;
import ca.neo.model.Node;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.impl.NodeFactory;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.NEFNode;
import ca.neo.model.neuron.Neuron;
import ca.neo.model.neuron.impl.LIFNeuronFactory;
import ca.neo.util.VectorGenerator;
import ca.neo.util.impl.RandomHypersphereVG;

/**
 * Default implementation of NEFEnsembleFactory. 
 * 
 * @author Bryan Tripp
 */
public class NEFEnsembleFactoryImpl implements NEFEnsembleFactory {

	private static Logger ourLogger = Logger.getLogger(NEFEnsembleFactoryImpl.class);
	
	private ApproximatorFactory myApproximatorFactory;
	private VectorGenerator myEncoderFactory;
	private VectorGenerator myEvalPointFactory;
	private NodeFactory myNodeFactory;
	private File myDatabase;	
	
	public NEFEnsembleFactoryImpl() {
		myApproximatorFactory = new WeightedCostApproximator.Factory(0.1f);
		myEncoderFactory = new RandomHypersphereVG(true, 1f, 0f);
		myEvalPointFactory = new RandomHypersphereVG(false, 1f, 0f);
		myNodeFactory = new LIFNeuronFactory(.02f, .002f, new IndicatorPDF(200f, 400f), new IndicatorPDF(-.9f, .9f));
	}
	
	/**
	 * @see ca.neo.model.nef.NEFEnsembleFactory#getApproximatorFactory()
	 */
	public ApproximatorFactory getApproximatorFactory() {
		return myApproximatorFactory;
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsembleFactory#getEncoderFactory()
	 */
	public VectorGenerator getEncoderFactory() {
		return myEncoderFactory;
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsembleFactory#getEvalPointFactory()
	 */
	public VectorGenerator getEvalPointFactory() {
		return myEvalPointFactory;
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsembleFactory#getNodeFactory()
	 */
	public NodeFactory getNodeFactory() {
		return myNodeFactory;
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsembleFactory#make(java.lang.String, int, int)
	 */
	public NEFEnsemble make(String name, int n, int dim) throws StructuralException {
		return doMake(name, n, dim);
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsembleFactory#make(java.lang.String, int, int, java.lang.String, boolean)
	 */
	public NEFEnsemble make(String name, int n, int dim, String storageName, boolean overwrite) throws StructuralException {
		NEFEnsemble result = null;
		
		File ensembleFile = new File(myDatabase, storageName + "." + FileManager.ENSEMBLE_EXTENSION);
		
		FileManager fm = new FileManager();
		if (!overwrite && ensembleFile.exists() && ensembleFile.canRead()) {
			try {
				result = (NEFEnsemble) fm.load(ensembleFile);
			} catch (Exception e) {
				ourLogger.error("Failed to load file " + ensembleFile.getAbsolutePath() + ". New ensemble will be created.", e);
			}
		}
		
		if (result == null) {
			result = doMake(name, n, dim);
			
			try {
				fm.save(result, ensembleFile);
			} catch (IOException e) {
				ourLogger.error("Failed to save file " + ensembleFile.getAbsolutePath(), e);
			}
		}
		
		return result;
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsembleFactory#setApproximatorFactory(ca.neo.math.ApproximatorFactory)
	 */
	public void setApproximatorFactory(ApproximatorFactory factory) {
		myApproximatorFactory = factory;
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsembleFactory#setEncoderFactory(ca.neo.util.VectorGenerator)
	 */
	public void setEncoderFactory(VectorGenerator factory) {
		myEncoderFactory = factory;
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsembleFactory#setEvalPointFactory(ca.neo.util.VectorGenerator)
	 */
	public void setEvalPointFactory(VectorGenerator factory) {
		myEvalPointFactory = factory;
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsembleFactory#setNodeFactory(ca.neo.model.impl.NodeFactory)
	 */
	public void setNodeFactory(NodeFactory factory) {
		myNodeFactory = factory;
	}
	
	//common make(...) implementation 
	private NEFEnsemble doMake(String name, int n, int dim) 
			throws StructuralException {
		
		NEFNode[] nodes = new NEFNode[n];
		
		for (int i = 0; i < n; i++) {
			Node node = myNodeFactory.make("node" + i);
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

		float[][] encoders = myEncoderFactory.genVectors(n, dim);
		
		ApproximatorFactory af = new WeightedCostApproximator.Factory(0.1f);
		float[][] evalPoints = getEvalPointFactory().genVectors(getNumEvalPoints(dim), dim);
		
		NEFEnsemble result = construct(name, nodes, encoders, af, evalPoints);
		
		addDefaultOrigins(result);
		
		return result;
	}

	/**
	 * This method is exposed so that it can be over-ridden to change behaviour. 
	 * 
	 * @param dim the dimension of the state represented by an Ensemble
	 * @return The number of points at which to approximate decoded functions  
	 */
	protected int getNumEvalPoints(int dim) {
		int[] pointsPerDim = new int[]{300, 1000}; 
		return (dim < pointsPerDim.length) ? pointsPerDim[dim] : dim*500;		
	}
	
	/**
	 * This method is exposed so that it can be over-ridden to change behaviour. 
	 * 
	 * @param name Name of new Ensemble 
	 * @param nodes Nodes that make up Ensemble 
	 * @param encoders Encoding vector for each Node
	 * @param af Factory that produces LinearApproximators for decoding Ensemble output
	 * @param evalPoints States at which Node output is evaluated for decoding purposes
	 * @return New NEFEnsemble with given parameters
	 * @throws StructuralException 
	 */
	protected NEFEnsemble construct(String name, NEFNode[] nodes, float[][] encoders, ApproximatorFactory af, float[][] evalPoints) 
			throws StructuralException {
		return new NEFEnsembleImpl(name, nodes, encoders, af, evalPoints);
	}

	/**
	 * Adds standard decoded Origins to the given NEFEnsemble 
	 * 
	 * This method is exposed so that it can be over-ridden to change behaviour.
	 * 
	 * @param ensemble A new NEFEnsemble 
	 * @throws StructuralException
	 */
	protected void addDefaultOrigins(NEFEnsemble ensemble) throws StructuralException {
		Function[] functions = new Function[ensemble.getDimension()];
		for (int i = 0; i < functions.length; i++) {
			functions[i] = new IdentityFunction(ensemble.getDimension(), i);
		}		

		ensemble.addDecodedOrigin(NEFEnsemble.X, functions, Neuron.AXON);
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
		
}
