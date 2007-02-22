/*
 * Created on 31-May-2006
 */
package ca.neo.model.nef.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Jama.Matrix;

import ca.neo.dynamics.LinearSystem;
import ca.neo.dynamics.impl.CanonicalModel;
import ca.neo.dynamics.impl.EulerIntegrator;
import ca.neo.dynamics.impl.LTISystem;
import ca.neo.dynamics.impl.SimpleLTISystem;
import ca.neo.math.ApproximatorFactory;
import ca.neo.math.Function;
import ca.neo.math.LinearApproximator;
import ca.neo.model.Origin;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFNode;
import ca.neo.model.neuron.Neuron;
import ca.neo.model.plasticity.EnsemblePlasticityRule;
import ca.neo.util.MU;

/**
 * Default implementation of NEFEnsemble. 
 * 
 * TODO: links to NEF documentation
 * TODO: allow user to specify radius
 * TODO: test
 * 
 * @author Bryan Tripp
 */
public class NEFEnsembleImpl extends DecodableEnsembleImpl implements NEFEnsemble {

	private static final long serialVersionUID = 1L;
	
	private int myDimension;
	private float[][] myEncoders;
	private Map<String, DecodedTermination> myDecodedTerminations;
	private Map<String, EnsemblePlasticityRule> myPlasticityRules;
	private Map<String, LinearApproximator> myDecodingApproximators;
	private ApproximatorFactory myApproximatorFactory;
	private float[][] myEvalPoints;

	/**
	 * @param name Unique name of Ensemble
	 * @param nodes Nodes that make up the Ensemble
	 * @param encoders List of encoding vectors (one for each node). All must have same length 
	 * @param factory Source of LinearApproximators to use in decoding output
	 * @param evalPoints Vector inputs at which output is found to produce DecodedOrigins   
	 * @throws StructuralException if there	are a different number of Nodes than encoding vectors or if not 
	 * 		all encoders have the same length
	 */
	public NEFEnsembleImpl(String name, NEFNode[] nodes, float[][] encoders, ApproximatorFactory factory, float[][] evalPoints) throws StructuralException {
		super(name, nodes, factory);
		
		if (nodes.length != encoders.length) {
			throw new StructuralException("There are " + nodes.length + " Nodes but " 
					+ encoders.length + " encoding vectors");
		}
		
		for (int i = 1; i < encoders.length; i++) {
			if (encoders[i].length != myDimension) {
				throw new StructuralException("Encoders have different lengths");
			}
		}
		
		myDimension = encoders[0].length;
		myEncoders = encoders;
		myDecodedTerminations = new HashMap<String, DecodedTermination>(10);
		myPlasticityRules = new HashMap<String, EnsemblePlasticityRule>(10);
		myApproximatorFactory = factory;
		myDecodingApproximators = new HashMap<String, LinearApproximator>(10);
		myEvalPoints = evalPoints;
	}
	
	/**
	 * TODO: fix
	 * @param evalPoints Vector points at which to find output (each one must have same dimension as
	 * 		encoder)
	 * @return Output of each Node at each evaluation point (1st dimension corresponds to Node) 
	 * @throws StructuralException If CONSTANT_RATE is not supported by any Node
	 */
	protected float[][] getAllFiringRates(float[][] evalPoints) throws StructuralException {
		NEFNode[] nodes = (NEFNode[]) getNodes();		
		float[][] result = new float[nodes.length][];
		
		for (int i = 0; i < nodes.length; i++) {			
			try {
				result[i] = getFiringRates(i, evalPoints);
			} catch (SimulationException e) {
				throw new Error("Node " + i + " does not have the standard 'AXON' Origin");
			}
		}

		return result;
	}
	
	/**
	 * @param nodeIndex Index of Node for which to find output at various inputs
	 * @param evalPoints Vector points at which to find output (each one must have same dimension as
	 * 		encoder)
	 * @return Output of indexed Node at each evaluation point 
	 * @throws StructuralException If CONSTANT_RATE is not supported by the given Node
	 * @throws SimulationException If the Node does not have an Origin with the standard name Neuron.AXON 
	 */
	protected float[] getFiringRates(int neuronIndex, float[][] evalPoints) 
			throws StructuralException, SimulationException {
		
		float[] result = new float[evalPoints.length];
		
		NEFNode node = (NEFNode) getNodes()[neuronIndex];
		synchronized (node) {
			SimulationMode mode = node.getMode();
			
			node.setMode(SimulationMode.CONSTANT_RATE);
			if ( !node.getMode().equals(SimulationMode.CONSTANT_RATE) ) {
				throw new StructuralException(
					"To find decoders using default methods, all Nodes must support CONSTANT_RATE simulation mode");
			}
			
			for (int i = 0; i < result.length; i++) {
				node.setRadialInput(getRadialInput(evalPoints[i], neuronIndex));
				
				node.run(0f, 0f);
				
				RealOutput output = (RealOutput) node.getOrigin(Neuron.AXON).getValues();
				result[i] = output.getValues()[0];
			}
			
			node.setMode(mode);
		}
		
		return result;
	}

	/** 
	 * @see ca.neo.model.nef.NEFEnsemble#getDimension()
	 */
	public int getDimension() {
		return myDimension;
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsemble#getEncoders()
	 */
	public float[][] getEncoders() {
		return myEncoders;
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsemble#addDecodedOrigin(java.lang.String, Function[], String)
	 */
	public Origin addDecodedOrigin(String name, Function[] functions, String nodeOrigin) throws StructuralException {		
		if (!myDecodingApproximators.containsKey(nodeOrigin)) {
			float[][] outputs = getAllFiringRates(myEvalPoints);
			LinearApproximator approximator = myApproximatorFactory.getApproximator(myEvalPoints, outputs);
			myDecodingApproximators.put(nodeOrigin, approximator);
		}		
		
		DecodedOrigin result = new DecodedOrigin(name, (NEFNode[]) getNodes(), functions, myDecodingApproximators.get(nodeOrigin));
		super.addDecodedOrigin(name, result);
		
		return result;
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsemble#addDecodedTermination(java.lang.String, float[][], float, boolean)
	 */
	public Termination addDecodedTermination(String name, float[][] matrix, float tauPSC, boolean isModulatory) 
			throws StructuralException {
		
		float scale = 1 / tauPSC; //output scaling to make impulse integral = 1

		LinearSystem dynamics = new SimpleLTISystem(
				new float[]{-1f/tauPSC}, 
				new float[][]{new float[]{1f}}, 
				new float[][]{new float[]{scale}}, 
				new float[]{0f}, 
				new Units[]{Units.UNK}
		);
		
		EulerIntegrator integrator = new EulerIntegrator(tauPSC / 10f);
		
		DecodedTermination result = new DecodedTermination(name, matrix, dynamics, integrator);
		if (isModulatory) {
			result.getConfiguration().setProperty(Termination.MODULATORY, new Boolean(true));
		} else if (matrix.length != myDimension) {
			throw new StructuralException("Output dimension " + matrix.length + " doesn't equal ensemble dimension " + myDimension);
		}
		myDecodedTerminations.put(name, result);
		
		return result;
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsemble#addDecodedTermination(java.lang.String, float[][], float[], float[], boolean)
	 */
	public Termination addDecodedTermination(String name, float[][] matrix, float[] tfNumerator, float[] tfDenominator, 
			boolean isModulatory) throws StructuralException {
		
		LTISystem dynamics = CanonicalModel.getRealization(tfNumerator, tfDenominator, 0f);
	
		Matrix A = new Matrix(MU.convert(dynamics.getA(0f)));
		double[] eigenvalues = A.eig().getRealEigenvalues();
		double fastest = Math.abs(eigenvalues[0]);
		for (int i = 1; i < eigenvalues.length; i++) {
			if (Math.abs(eigenvalues[i]) > fastest) fastest = Math.abs(eigenvalues[i]);
		}
		
		EulerIntegrator integrator = new EulerIntegrator(1f / (10f * (float) fastest));
		
		DecodedTermination result = new DecodedTermination(name, matrix, dynamics, integrator);
		if (isModulatory) {
			result.getConfiguration().setProperty(Termination.MODULATORY, new Boolean(true));
		} else if (matrix.length != myDimension) {
			throw new StructuralException("Output dimension " + matrix.length + " doesn't equal ensemble dimension " + myDimension);
		}
		myDecodedTerminations.put(name, result);
		
		return result;
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsemble#removeDecodedTermination(java.lang.String)
	 */
	public void removeDecodedTermination(String name) {
		myDecodedTerminations.remove(name);
	}

	/**
	 * @see ca.neo.model.Ensemble#getTerminations()
	 */
	public Termination[] getTerminations() {
		Termination[] decoded = myDecodedTerminations.values().toArray(new Termination[0]);
		Termination[] composites = super.getTerminations();
		
		Termination[] all = new Termination[decoded.length + composites.length];
		System.arraycopy(decoded, 0, all, 0, decoded.length);
		System.arraycopy(composites, 0, all, decoded.length, composites.length);
		
		return all;
	}

	/**
	 * @see ca.neo.model.Ensemble#run(float, float)
	 */
	public void run(float startTime, float endTime) throws SimulationException {
		try {
			float[] state = new float[myDimension];

			//run terminations and sum state ...
			Iterator it = myDecodedTerminations.values().iterator();
			while (it.hasNext()) {
				DecodedTermination t = (DecodedTermination) it.next();
				t.run(startTime, endTime);
				float[] output = t.getOutput();
				
				Boolean isModulatory = (Boolean) t.getConfiguration().getProperty(Termination.MODULATORY);
				if (!isModulatory.booleanValue()) state = MU.sum(state, output);
			}

			if ( getMode().equals(SimulationMode.DIRECT) ) {
				Origin[] origins = getOrigins();
				for (int i = 0; i < origins.length; i++) {
					if (origins[i] instanceof DecodedOrigin) {
						((DecodedOrigin) origins[i]).run(state, endTime - startTime);
					}
				}
				setTime(endTime);
			} else {
				//multiply state by encoders (cosine tuning), set radial input of each Neuron and run ...
				NEFNode[] nodes = (NEFNode[]) getNodes();
				for (int i = 0; i < nodes.length; i++) {
					nodes[i].setRadialInput(getRadialInput(state, i));
				}
				super.run(startTime, endTime);
			}
			learn(endTime - startTime);

		} catch (SimulationException e) {
			e.setEnsemble(getName());
			throw e;
		}
	}
	
	//run ensemble plasticity rules (assume constant input/state over given elapsed time)
	private void learn(float elapsedTime) throws SimulationException {
		Iterator ruleIter = myPlasticityRules.keySet().iterator();
		while (ruleIter.hasNext()) {
			String name = (String) ruleIter.next();
			DecodedTermination termination = (DecodedTermination) myDecodedTerminations.get(name);
			EnsemblePlasticityRule rule = (EnsemblePlasticityRule) myPlasticityRules.get(name);
			
			Iterator termIter = myDecodedTerminations.keySet().iterator();
			while (termIter.hasNext()) {
				DecodedTermination t = (DecodedTermination) myDecodedTerminations.get(termIter.next());
				rule.setTerminationState(t.getName(), t.getOutput());
			}
			
			Origin[] origins = getOrigins();
			for (int i = 0; i < origins.length; i++) {
				if (origins[i] instanceof DecodedOrigin) {
					rule.setOriginState(origins[i].getName(), ((RealOutput) origins[i].getValues()).getValues());
				}
			}
			
			float[][] transform = termination.getTransform();
			float[][] derivative = rule.getDerivative(transform, termination.getInput());
			for (int i = 0; i < transform.length; i++) {
				for (int j = 0; j < transform[i].length; j++) {
					transform[i][j] += derivative[i][j] * elapsedTime;
				}
			}	
		}
	}
	
	/**
	 * @param state State vector 
	 * @param node Node number 
	 * @return Radial input to the given node 
	 */
	private float getRadialInput(float[] state, int node) {
		return MU.prod(state, myEncoders[node]);
	}

	/**
	 * @see ca.neo.model.Node#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		return myDecodedTerminations.containsKey(name) ? myDecodedTerminations.get(name) : super.getTermination(name);
	}
	
	/**
	 * @see ca.neo.model.Ensemble#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
		super.setMode(mode);
		
		Origin[] origins = getOrigins();
		for (int i = 0; i < origins.length; i++) {
			if (origins[i] instanceof DecodedOrigin) {
				((DecodedOrigin) origins[i]).setMode(mode);
			}
		}
	}

	/**
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		super.reset(randomize);
		
		Iterator it = myDecodedTerminations.keySet().iterator();		
		while (it.hasNext()) {
			DecodedTermination t = (DecodedTermination) myDecodedTerminations.get(it.next());
			t.reset(randomize);
		}

		Origin[] origins = getOrigins();
		for (int i = 0; i < origins.length; i++) {
			if (origins[i] instanceof DecodedOrigin) {
				((DecodedOrigin) origins[i]).reset(randomize);
			}
		}
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsemble#setTerminationPlasticityRule(java.lang.String, ca.neo.model.plasticity.EnsemblePlasticityRule)
	 */
	public void setTerminationPlasticityRule(String name, EnsemblePlasticityRule rule) throws StructuralException {
		if (!myDecodedTerminations.containsKey(name)) {
			throw new StructuralException("The DecodedTermination " + name + " does not exist");
		}
		
		myPlasticityRules.put(name, rule);
	}

}
