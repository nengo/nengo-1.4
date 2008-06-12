/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "NEFEnsembleFactoryImpl.java". Description: 
"Default implementation of NEFEnsembleFactory"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU 
Public License license (the GPL License), in which case the provisions of GPL 
License are applicable  instead of those above. If you wish to allow use of your 
version of this file only under the terms of the GPL License and not to allow 
others to use your version of this file under the MPL, indicate your decision 
by deleting the provisions above and replace  them with the notice and other 
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
*/

/*
 * Created on 4-Mar-07
 */
package ca.nengo.model.nef.impl;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import ca.nengo.io.FileManager;
import ca.nengo.math.ApproximatorFactory;
import ca.nengo.math.Function;
import ca.nengo.math.impl.IdentityFunction;
import ca.nengo.math.impl.IndicatorPDF;
import ca.nengo.math.impl.WeightedCostApproximator;
import ca.nengo.model.Node;
import ca.nengo.model.SimulationMode;
import ca.nengo.model.StructuralException;
import ca.nengo.model.impl.NodeFactory;
import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.model.nef.NEFEnsembleFactory;
import ca.nengo.model.nef.NEFNode;
import ca.nengo.model.neuron.Neuron;
import ca.nengo.model.neuron.impl.LIFNeuronFactory;
import ca.nengo.util.MU;
import ca.nengo.util.VectorGenerator;
import ca.nengo.util.impl.RandomHypersphereVG;

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
	 * @see ca.nengo.model.nef.NEFEnsembleFactory#getApproximatorFactory()
	 */
	public ApproximatorFactory getApproximatorFactory() {
		return myApproximatorFactory;
	}

	/**
	 * @see ca.nengo.model.nef.NEFEnsembleFactory#getEncoderFactory()
	 */
	public VectorGenerator getEncoderFactory() {
		return myEncoderFactory;
	}

	/**
	 * @see ca.nengo.model.nef.NEFEnsembleFactory#getEvalPointFactory()
	 */
	public VectorGenerator getEvalPointFactory() {
		return myEvalPointFactory;
	}

	/**
	 * @see ca.nengo.model.nef.NEFEnsembleFactory#getNodeFactory()
	 */
	public NodeFactory getNodeFactory() {
		return myNodeFactory;
	}

	/**
	 * @see ca.nengo.model.nef.NEFEnsembleFactory#make(java.lang.String, int, int)
	 */
	public NEFEnsemble make(String name, int n, int dim) throws StructuralException {
		float[] radii = MU.uniform(1, dim, 1)[0];
		return doMake(name, n, radii);
	}

	/**
	 * @see ca.nengo.model.nef.NEFEnsembleFactory#make(java.lang.String, int, float[])
	 */
	public NEFEnsemble make(String name, int n, float[] radii) throws StructuralException {
		return doMake(name, n, radii);
	}

	/**
	 * @see ca.nengo.model.nef.NEFEnsembleFactory#make(java.lang.String, int, int, java.lang.String, boolean)
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
			float[] radii = MU.uniform(1, dim, 1)[0];
			result = doMake(name, n, radii);
			
			try {
				fm.save(result, ensembleFile);
			} catch (IOException e) {
				ourLogger.error("Failed to save file " + ensembleFile.getAbsolutePath(), e);
			}
		}
		
		return result;
	}

	/**
	 * @see ca.nengo.model.nef.NEFEnsembleFactory#setApproximatorFactory(ca.nengo.math.ApproximatorFactory)
	 */
	public void setApproximatorFactory(ApproximatorFactory factory) {
		myApproximatorFactory = factory;
	}

	/**
	 * @see ca.nengo.model.nef.NEFEnsembleFactory#setEncoderFactory(ca.nengo.util.VectorGenerator)
	 */
	public void setEncoderFactory(VectorGenerator factory) {
		myEncoderFactory = factory;
	}

	/**
	 * @see ca.nengo.model.nef.NEFEnsembleFactory#setEvalPointFactory(ca.nengo.util.VectorGenerator)
	 */
	public void setEvalPointFactory(VectorGenerator factory) {
		myEvalPointFactory = factory;
	}

	/**
	 * @see ca.nengo.model.nef.NEFEnsembleFactory#setNodeFactory(ca.nengo.model.impl.NodeFactory)
	 */
	public void setNodeFactory(NodeFactory factory) {
		myNodeFactory = factory;
	}
	
	//common make(...) implementation 
	private NEFEnsemble doMake(String name, int n, float[] radii) throws StructuralException {
		
		int dim = radii.length;
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
		float[][] evalPoints = getEvalPointFactory().genVectors(getNumEvalPoints(dim), dim);
		NEFEnsemble result = construct(name, nodes, encoders, myApproximatorFactory, evalPoints, radii);
		
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
		int[] pointsPerDim = new int[]{0, 300, 1000}; 
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
	 * @param radii Radius of encoded area in each dimension
	 * @return New NEFEnsemble with given parameters
	 * @throws StructuralException 
	 */
	protected NEFEnsemble construct(String name, NEFNode[] nodes, float[][] encoders, ApproximatorFactory af, float[][] evalPoints, float[] radii) 
			throws StructuralException {
		return new NEFEnsembleImpl(name, nodes, encoders, af, evalPoints, radii);
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
