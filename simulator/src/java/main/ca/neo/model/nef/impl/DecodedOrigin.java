/*
 * Created on 2-Jun-2006
 */
package ca.neo.model.nef.impl;

import ca.neo.math.Function;
import ca.neo.math.LinearApproximator;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.RealOutput;
import ca.neo.model.Resettable;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.SpikeOutput;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.RealOutputImpl;
import ca.neo.model.nef.NEFNode;
import ca.neo.model.neuron.Neuron;
import ca.neo.util.MU;

/**
 * An Origin of functions of the state variables of an NEFEnsemble. 
 * 
 * TODO: how do units fit in. define in constructor? ignore?
 * TODO: user-specified node origin, select nodes make up decoded origin 
 * 
 * @author Bryan Tripp
 */
public class DecodedOrigin implements Origin, Resettable {

	private static final long serialVersionUID = 1L;
	
	private String myName;
	private Node[] myNodes;
	private Function[] myFunctions;
	private float[][] myDecoders;
	private SimulationMode myMode;
	private RealOutput myOutput;

	/**
	 * With this constructor, decoding vectors are generated using default settings. 
	 *  
	 * @param name Name of this Origin
	 * @param nodes Nodes that belong to the NEFEnsemble from which this Origin arises
	 * @param functions Output Functions on the vector that is represented by the NEFEnsemble 
	 * 		(one Function per dimension of output). For example if the Origin is to output 
	 * 		x1*x2, where the ensemble represents [x1 x1], then one 2D function would be 
	 * 		needed in this list. The input dimension of each function must be the same as the 
	 * 		dimension of the state vector represented by this ensemble.  
	 * @throws StructuralException if functions do not all have the same input dimension (we 
	 * 		don't check against the state dimension at this point)
	 */
	public DecodedOrigin(String name, Node[] nodes, Function[] functions, LinearApproximator approximator) 
			throws StructuralException {
		
		checkFunctionDimensions(functions);
		
		myName = name;
		myNodes = nodes;
		myFunctions = functions; 
		myDecoders = findDecoders(nodes, functions, approximator);  
		myMode = SimulationMode.DEFAULT;
		
		reset(false);
	}
	
	/**
	 * With this constructor decoding vectors are specified by the caller. 
	 * 
	 * @param name As in other constructor
	 * @param nodes As in other constructor
	 * @param functions As in other constructor
	 * @param decoders Decoding vectors which are scaled by the main output of each Node, and 
	 * 		then summed, to estimate the same function of the ensembles state vector that is 
	 * 		defined by the 'functions' arg. The 'functions' arg is still needed, because in DIRECT 
	 * 		SimulationMode, these functions are used directly. The 'decoders' arg allows the caller 
	 * 		to provide decoders that are generated with non-default methods or parameters (eg an 
	 * 		unusual number of singular values). Must be a matrix with one row per Node and one 
	 * 		column per function.   
	 * @throws StructuralException If dimensions.length != neurons.length, decoders is not a matrix 
	 * 		(ie all elements with same length), or if the number of columns in decoders is not equal 
	 * 		to the number of functions 
	 */
	public DecodedOrigin(String name, NEFNode[] nodes, Function[] functions, float[][] decoders) throws StructuralException {
		checkFunctionDimensions(functions);
		
		if (!MU.isMatrix(decoders)) {
			throw new StructuralException("Elements of decoders do not all have the same length");
		}
		
		if (decoders[0].length != functions.length) {
			throw new StructuralException("Number of decoding functions and dimension of decoding vectors must be the same");
		}
		
		if (decoders.length != nodes.length) {
			throw new StructuralException("Number of decoding vectors and Neurons must be the same");
		}
			
		myName = name;
		myNodes = nodes;
		myFunctions = functions;
		myDecoders = decoders;
		myMode = SimulationMode.DEFAULT;
		
		reset(false);
	}

	/**
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		myOutput = new RealOutputImpl(new float[myFunctions.length], Units.UNK);
	}

	private static float[][] findDecoders(Node[] nodes, Function[] functions, LinearApproximator approximator)  {
		float[][] result = new float[nodes.length][];
		for (int i = 0; i < result.length; i++) {
			result[i] = new float[functions.length];
		}
		
		for (int j = 0; j < functions.length; j++) {
			float[] coeffs = approximator.findCoefficients(functions[j]);
			for (int i = 0; i < nodes.length; i++) {
				result[i][j] = coeffs[i];
			}
		}
		
		return result;
	}
		
	private static void checkFunctionDimensions(Function[] functions) throws StructuralException {
		int dim = functions[0].getDimension();
		for (int i = 1; i < functions.length; i++) {
			if (functions[i].getDimension() != dim) {
				throw new StructuralException("Functions must all have the same input dimension");
			}
		}
	}
	
	/**
	 * @see ca.neo.model.Origin#getName()
	 */
	public String getName() {
		return myName;
	}

	/**
	 * @see ca.neo.model.Origin#getDimensions()
	 */
	public int getDimensions() {
		return myFunctions.length;
	}
	
	/**
	 * @return Decoding vectors for each Node
	 */
	public float[][] getDecoders() {
		return myDecoders;
	}
	
	/**
	 * @param mode Requested simulation mode 
	 */
	public void setMode(SimulationMode mode) {
		myMode = mode;
	}
	
	/**
	 * @return The mode in which the Ensemble is currently running. 
	 */
	public SimulationMode getMode() {
		return myMode;
	}
	
	/**
	 * Must be called at each time step after Nodes are run and before getValues().  
	 *  
	 * @param state Idealized state (as defined by inputs) which can be fed into (idealized) functions 
	 * 		that make up the Origin, when it is running in DIRECT mode. This is not used in other modes, 
	 * 		and can be null.  
	 * @param stepSize Length of time step, used to calculate magnitude of spike "impulses".  
	 * @throws SimulationException If the given state is not of the expected dimension (ie the input 
	 * 		dimension of the functions provided in the constructor)
	 */
	public void run(float[] state, float stepSize) throws SimulationException {
		if (state != null && state.length != myFunctions[0].getDimension()) {
			throw new SimulationException("A state of dimension " + myFunctions[0].getDimension() + " was expected");
		}		
		
		float[] values = new float[myFunctions.length];
		
		if (myMode == SimulationMode.DIRECT) {
			for (int i = 0; i < values.length; i++) {
				values[i] = myFunctions[i].map(state);
			}
		} else {
			for (int i = 0; i < myNodes.length; i++) {
				try {
					InstantaneousOutput o = myNodes[i].getOrigin(Neuron.AXON).getValues();

					float val = 0; 
					if (o instanceof SpikeOutput) {
						val = ((SpikeOutput) o).getValues()[0] ? 1f / stepSize : 0f;
					} else if (o instanceof RealOutput) {
						val = ((RealOutput) o).getValues()[0];
					} else {
						throw new Error("Neuron output is of type " + o.getClass().getName() 
							+ ". DecodedOrigin can only deal with RealOutput and SpikeOutput, so it apparently has to be updated");
					}
					
					float[] decoder = myDecoders[i];
					for (int j = 0; j < values.length; j++) {
						values[j] += val * decoder[j];
					}
				} catch (StructuralException e) {
					throw new SimulationException(e);
				}				
			}		
		}
		
		myOutput = new RealOutputImpl(values, Units.UNK);
	}

	/**
	 * @see ca.neo.model.Origin#getValues()
	 */
	public InstantaneousOutput getValues() throws SimulationException {
		return myOutput;
	}

}
