/*
 * Created on 2-Jun-2006
 */
package ca.neo.model.nef.impl;

import java.io.File;
import java.io.IOException;

import ca.neo.io.MatlabExporter;
import ca.neo.math.Function;
import ca.neo.math.LinearApproximator;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Node;
import ca.neo.model.Noise;
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
import ca.neo.util.MU;

/**
 * An Origin of functions of the state variables of an NEFEnsemble. 
 * 
 * TODO: how do units fit in. define in constructor? ignore?
 * TODO: user-specified node origin, select nodes make up decoded origin 
 * 
 * @author Bryan Tripp
 */
public class DecodedOrigin implements Origin, Resettable, SimulationMode.ModeConfigurable, Noise.Noisy {

	private static final long serialVersionUID = 1L;
	
	private Node myNode; //parent node
	private String myName;
	private Node[] myNodes;
	private String myNodeOrigin;
	private Function[] myFunctions;
	private float[][] myDecoders;
	private SimulationMode myMode;
	private RealOutput myOutput;
	private Noise myNoise = null;
	private Noise[] myNoises = null;
	private LinearApproximator myApproximator;

	/**
	 * With this constructor, decoding vectors are generated using default settings. 
	 *  
	 * @param Node The parent Node
	 * @param name Name of this Origin
	 * @param nodes Nodes that belong to the NEFEnsemble from which this Origin arises
	 * @param nodeOrigin Name of the Origin on each given node from which output is to be decoded  
	 * @param functions Output Functions on the vector that is represented by the NEFEnsemble 
	 * 		(one Function per dimension of output). For example if the Origin is to output 
	 * 		x1*x2, where the ensemble represents [x1 x1], then one 2D function would be 
	 * 		needed in this list. The input dimension of each function must be the same as the 
	 * 		dimension of the state vector represented by this ensemble.  
	 * @throws StructuralException if functions do not all have the same input dimension (we 
	 * 		don't check against the state dimension at this point)
	 */
	public DecodedOrigin(Node node, String name, Node[] nodes, String nodeOrigin, Function[] functions, LinearApproximator approximator) 
			throws StructuralException {
		
		checkFunctionDimensions(functions);
		
		myNode = node;
		myName = name;
		myNodes = nodes;
		myNodeOrigin = nodeOrigin;
		myFunctions = functions; 
		myDecoders = findDecoders(nodes, functions, approximator);  
		myMode = SimulationMode.DEFAULT;
		myApproximator = approximator;
		
		reset(false);
	}
	
	protected void setDecoders(float[][] decoders) {
		assert MU.isMatrix(decoders);
		assert myDecoders.length == decoders.length;
		assert myDecoders[0].length == decoders[0].length;
		
		myDecoders = decoders;
	}
	
	/**
	 * With this constructor decoding vectors are specified by the caller. 
	 * 
	 * @param Node The parent Node
	 * @param name As in other constructor
	 * @param nodes As in other constructor
	 * @param nodeOrigin Name of the Origin on each given node from which output is to be decoded  
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
	public DecodedOrigin(Node node, String name, Node[] nodes, String nodeOrigin, Function[] functions, float[][] decoders) throws StructuralException {
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
			
		myNode = node;
		myName = name;
		myNodes = nodes;
		myNodeOrigin = nodeOrigin;
		myFunctions = functions;
		myDecoders = decoders;
		myMode = SimulationMode.DEFAULT;
		
		reset(false);
	}
	
	/**
	 * @return Mean-squared error of this origin over points evaluated by the LinearApproximator 
	 */
	public float[] getError() {
		float[] result = new float[getDimensions()];
		
		if (myApproximator != null) {
			MatlabExporter exporter = new MatlabExporter();
			float[][] estimate = MU.transpose(MU.prod(MU.transpose(myApproximator.getValues()), getDecoders()));
			float[][] actuals = new float[myFunctions.length][];
			float[][] errors = new float[myFunctions.length][];
			for (int i = 0; i < myFunctions.length; i++) {
				float[] actual = myFunctions[i].multiMap(myApproximator.getEvalPoints());
				float[] error = MU.difference(estimate[i], actual);
				actuals[i] = actual;
				errors[i] = error;
//				System.out.println(MU.toString(new float[][]{estimate[i]}, 5));
//				System.out.println(MU.toString(new float[][]{actual}, 5));
				result[i] = MU.prod(error, error) / (float) error.length;
			}
			System.out.println(MU.toString(myApproximator.getEvalPoints(), 5));
			exporter.add("actual", actuals);
			exporter.add("error", errors);
			exporter.add("evalPoints", myApproximator.getEvalPoints());
			exporter.add("estimate", estimate);
			try {
				exporter.write(new File("evalpoints.mat"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * @param noise New output noise model (defaults to no noise)
	 */
	public void setNoise(Noise noise) {
		myNoise = noise;
		myNoises = new Noise[getDimensions()];
		for (int i = 0; i < myNoises.length; i++) {
			myNoises[i] = myNoise.clone();
		}
	}
	
	/**
	 * @return Noise with which output of this Origin is corrupted
	 */
	public Noise getNoise() {
		return myNoise;
	}

	/**
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		float time = (myOutput == null) ? 0 : myOutput.getTime();
		myOutput = new RealOutputImpl(new float[myFunctions.length], Units.UNK, time);
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
	 * @param startTime simulation time of timestep onset  
	 * @param endTime simulation time of timestep end  
	 * @throws SimulationException If the given state is not of the expected dimension (ie the input 
	 * 		dimension of the functions provided in the constructor)
	 */
	public void run(float[] state, float startTime, float endTime) throws SimulationException {
		if (state != null && state.length != myFunctions[0].getDimension()) {
			throw new SimulationException("A state of dimension " + myFunctions[0].getDimension() + " was expected");
		}		
		
		float[] values = new float[myFunctions.length];
		float stepSize = endTime - startTime;
		
		if (myMode == SimulationMode.DIRECT) {
			for (int i = 0; i < values.length; i++) {
				values[i] = myFunctions[i].map(state);
			}
		} else {
			for (int i = 0; i < myNodes.length; i++) {
				try {
					InstantaneousOutput o = myNodes[i].getOrigin(myNodeOrigin).getValues();

					float val = 0; 
					if (o instanceof SpikeOutput) {
						val = ((SpikeOutput) o).getValues()[0] ? 1f / stepSize : 0f;
					} else if (o instanceof RealOutput) {
						val = ((RealOutput) o).getValues()[0];
					} else {
						throw new Error("Node output is of type " + o.getClass().getName() 
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
		
		if (myNoise != null) {
			for (int i = 0; i < values.length; i++) {
				values[i] = myNoises[i].getValue(startTime, endTime, values[i]);				
			}
		}
		
		myOutput = new RealOutputImpl(values, Units.UNK, endTime);
	}
	
	/**
	 * @see ca.neo.model.Origin#getValues()
	 */
	public InstantaneousOutput getValues() throws SimulationException {
		return myOutput;
	}
	
	/**
	 * @return List of Functions approximated by this DecodedOrigin
	 */
	protected Function[] getFunctions() {
		return myFunctions;
	}

	/**
	 * @return Name of Node-level Origin on which this DecodedOrigin is based
	 */
	protected String getNodeOrigin() {
		return myNodeOrigin;
	}

	/**
	 * @see ca.neo.model.Origin#getNode()
	 */
	public Node getNode() {
		return myNode;
	}

}
