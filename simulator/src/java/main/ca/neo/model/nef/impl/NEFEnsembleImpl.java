/*
 * Created on 31-May-2006
 */
package ca.neo.model.nef.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import Jama.Matrix;

import ca.neo.dynamics.LinearSystem;
import ca.neo.dynamics.impl.CanonicalModel;
import ca.neo.dynamics.impl.EulerIntegrator;
import ca.neo.dynamics.impl.LTISystem;
import ca.neo.dynamics.impl.SimpleLTISystem;
import ca.neo.math.Function;
import ca.neo.math.LinearApproximator;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.math.impl.WeightedCostApproximator;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Origin;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.impl.AbstractEnsemble;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFNode;
import ca.neo.model.neuron.Neuron;
import ca.neo.model.neuron.SpikeOutput;
import ca.neo.model.neuron.impl.SpikePatternImpl;
import ca.neo.model.plasticity.EnsemblePlasticityRule;
import ca.neo.util.MU;
import ca.neo.util.TimeSeries;
import ca.neo.util.VectorGenerator;
import ca.neo.util.impl.RandomHypersphereVG;
import ca.neo.util.impl.TimeSeriesImpl;

/**
 * Default implementation of NEFEnsemble. 
 * 
 * TODO: links to NEF documentation
 * TODO: allow user to specify radius
 * TODO: test
 * 
 * @author Bryan Tripp
 */
public class NEFEnsembleImpl extends AbstractEnsemble implements NEFEnsemble {

	private static final long serialVersionUID = 1L;
	
	private int myDimension;
	private float[][] myEncoders;
	private Map myDecodedOrigins;
	private Map myDecodedTerminations;
	private Map myPlasticityRules;
	private Origin[] myNodeOrigins;
	private Termination[] myNodeTerminations;
	private LinearApproximator myDecodingApproximator;
	private float myTime; //used to support Probeable
	
	/**
	 * @param name Unique name of Ensemble
	 * @param nodes Nodess that make up the Ensemble
	 * @param encoders List of encoding vectors (one for each node). All must have same length 
	 * @throws StructuralException if there	are a different number of Nodes than encoding vectors, if not 
	 * 		all encoders have the same length, or if there is a problem setting up the LinearApproximator 
	 */
	public NEFEnsembleImpl(String name, NEFNode[] nodes, float[][] encoders) throws StructuralException {
		super(name, nodes);
		init(name, nodes, encoders);
		setApproximator();
	}
	
	/**
	 * @param name Unique name of Ensemble
	 * @param nodes Nodes that make up the Ensemble
	 * @param encoders List of encoding vectors (one for each Node). All must have same length
	 * @param setApproximator 
	 * @throws StructuralException if there	are a different number of Nodes than encoding vectors, if not 
	 * 		all encoders have the same length, or if there is a problem setting up the LinearApproximator 
	 */
	public NEFEnsembleImpl(String name, NEFNode[] nodes, float[][] encoders, boolean setApproximator) throws StructuralException {
		super(name, nodes);
		init(name, nodes, encoders);
		if (setApproximator) setApproximator();
	}
	
	private void init(String name, NEFNode[] nodes, float[][] encoders) throws StructuralException {
		if (nodes.length != encoders.length) {
			throw new StructuralException("There are " + nodes.length + " Nodes but " 
					+ encoders.length + " encoding vectors");
		}
		
		myDimension = encoders[0].length;
		
		for (int i = 1; i < encoders.length; i++) {
			if (encoders[i].length != myDimension) {
				throw new StructuralException("Encoders have different lengths");
			}
		}
		
		myEncoders = encoders;
		
		myDecodedOrigins = new HashMap(10);
		myDecodedTerminations = new HashMap(10);
		myPlasticityRules = new HashMap(10);
		
		myNodeOrigins = findOrigins(nodes);
		myNodeTerminations = findTerminations(nodes);
		
		myTime = 0f;		
	}
		
	protected void setApproximator() throws StructuralException {
		myDecodingApproximator = getApproximator();		
	}
	
	/**
	 * Uses a WeightedCostApproximator with flat cost function by default -- override to 
	 * change this.  
	 * 
	 * @return LinearApproximator used for finding decoders  
	 * @throws StructuralException if not all Neurons support CONSTANT_RATE SimulationMode
	 */
	public LinearApproximator getApproximator() throws StructuralException {

		//TODO: what is a good rule here? do we need to stick to axes for higher dimensions? 
		int[] evalPointsByDimension = new int[] {0, 300, 1000, 5000}; 	
		int numEvalPoints = myDimension <= 3 ? evalPointsByDimension[myDimension] : 300 * myDimension;
		
		VectorGenerator vg = new RandomHypersphereVG(false, 1f, 0f);
		float[][] evalPoints = vg.genVectors(numEvalPoints, myDimension);		
		float[][] values = getAllFiringRates(evalPoints);
		
		return new WeightedCostApproximator(evalPoints, values, new ConstantFunction(evalPoints[0].length, 1), .1f); 
	}
	
	/**
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
//				float radialInput = MU.prod(encoder, evalPoints[i]);
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
	 * @see ca.neo.model.nef.NEFEnsemble#addDecodedOrigin(java.lang.String, Function[])
	 */
	public Origin addDecodedOrigin(String name, Function[] functions) throws StructuralException {
		Origin result = new DecodedOrigin(name, (NEFNode[]) getNodes(), functions, myDecodingApproximator);
		myDecodedOrigins.put(name, result);
		
		return result;
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsemble#doneOrigins()
	 */
	public void doneOrigins() {
		myDecodingApproximator = null;
	}
	
	/**
	 * @see ca.neo.model.nef.NEFEnsemble#removeDecodedOrigin(java.lang.String)
	 */
	public void removeDecodedOrigin(String name) {
		myDecodedOrigins.remove(name);
	}

	/**
	 * @see ca.neo.model.nef.NEFEnsemble#getDecoders(java.lang.String)
	 */
	public float[][] getDecoders(String name) {
		return ((DecodedOrigin) myDecodedOrigins.get(name)).getDecoders();
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
		
		Termination result = new DecodedTermination(name, matrix, dynamics, integrator);
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
		
		Termination result = new DecodedTermination(name, matrix, dynamics, integrator);
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
	 * @see ca.neo.model.Ensemble#getOrigins()
	 */
	public Origin[] getOrigins() {
		Origin[] decoded = (Origin[]) myDecodedOrigins.values().toArray(new Origin[0]);
		
		Origin[] all = new Origin[decoded.length + myNodeOrigins.length];
		System.arraycopy(decoded, 0, all, 0, all.length);
		System.arraycopy(myNodeOrigins, 0, all, decoded.length, myNodeOrigins.length);
		
		return all;
	}

	/**
	 * @see ca.neo.model.Ensemble#getTerminations()
	 */
	public Termination[] getTerminations() {
		Termination[] decoded = (Termination[]) myDecodedTerminations.values().toArray(new Termination[0]);
		
		Termination[] all = new Termination[decoded.length + myNodeTerminations.length];
		System.arraycopy(decoded, 0, all, 0, all.length);
		System.arraycopy(myNodeTerminations, 0, all, decoded.length, myNodeTerminations.length);
		
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

			//run nodes ... 
			SpikePatternImpl pattern = (myCollectSpikesFlag) ? (SpikePatternImpl) getSpikePattern() : null;
			
			if ( !getMode().equals(SimulationMode.DIRECT) ) {
				//multiply state by encoders (cosine tuning), set radial input of each Neuron and run ...
				NEFNode[] nodes = (NEFNode[]) getNodes();
				for (int i = 0; i < nodes.length; i++) {
					nodes[i].setRadialInput(getRadialInput(state, i));
					
					nodes[i].run(startTime, endTime);

					if (myCollectSpikesFlag) {
						try {
							InstantaneousOutput output = nodes[i].getOrigin(Neuron.AXON).getValues();
							if (output instanceof SpikeOutput && ((SpikeOutput) output).getValues()[0]) {
								pattern.addSpike(i, endTime);
							}											
						} catch (StructuralException e) {
							throw new SimulationException("Ensemble has been set to collect spikes, but not all components have Origin Neuron.AXON", e);
						}
					}				
				}
			}
			
			//run origins ...
			it = myDecodedOrigins.values().iterator();
			while (it.hasNext()) {
				DecodedOrigin o = (DecodedOrigin) it.next();
				o.run(state, endTime - startTime);
			}
			
			learn(endTime - startTime);

			myTime = endTime;
			
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
			
			Iterator originIter = myDecodedOrigins.keySet().iterator();
			while (originIter.hasNext()) {
				DecodedOrigin o = (DecodedOrigin) myDecodedOrigins.get(originIter.next());
				rule.setOriginState(o.getName(), ((RealOutput) o.getValues()).getValues());
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
	protected float getRadialInput(float[] state, int node) {
		return MU.prod(state, myEncoders[node]);
	}

	/**
	 * @see ca.neo.model.Probeable#getHistory(java.lang.String)
	 */
	public TimeSeries getHistory(String stateName) throws SimulationException {
		TimeSeries result = null;
		
		Origin origin = (Origin) myDecodedOrigins.get(stateName);
		
		if (origin != null) {
			float[] vals = ((RealOutput) origin.getValues()).getValues();
			Units[] units = new Units[vals.length];
			for (int i = 0; i < vals.length; i++) {
				units[i] = origin.getValues().getUnits();
			}
			result = new TimeSeriesImpl(new float[]{myTime}, new float[][]{vals}, units);
		} else {
			throw new SimulationException("Output function " + stateName + " is unknown");
		}
		
		return result;
	}

	/**
	 * @see ca.neo.model.Probeable#listStates()
	 */
	public Properties listStates() {
		Properties result = new Properties();
		
		Iterator it = myDecodedOrigins.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next().toString();
			result.setProperty(name, "Function of NEFEnsemble state"); //TODO: could put function.toString() here
		}
		
		return result;
	}

	/**
	 * @see ca.neo.model.Ensemble#getOrigin(java.lang.String)
	 */
	public Origin getOrigin(String name) throws StructuralException {
		Origin result = null;
		
		result = (Origin) myDecodedOrigins.get(name);
		
		for (int i = 0; i < myNodeOrigins.length && result == null; i++) {
			if (myNodeOrigins[i].getName().equals(name)) {
				result = myNodeOrigins[i];
			}
		}
		
		return result;
	}

	/**
	 * @see ca.neo.model.Ensemble#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		Termination result = null;
		
		result = (Termination) myDecodedTerminations.get(name);
		
		for (int i = 0; i < myNodeTerminations.length && result == null; i++) {
			if (myNodeTerminations[i].getName().equals(name)) {
				result = myNodeTerminations[i];
			}
		}
		
		return result;
	}
	
	/**
	 * @see ca.neo.model.Ensemble#setMode(ca.neo.model.SimulationMode)
	 */
	public void setMode(SimulationMode mode) {
		super.setMode(mode);
		
		if (myDecodedOrigins != null) {
			Iterator it = myDecodedOrigins.values().iterator();
			while (it.hasNext()) {
				DecodedOrigin o = (DecodedOrigin) it.next();
				o.setMode(mode);
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

		it = myDecodedOrigins.keySet().iterator();		
		while (it.hasNext()) {
			DecodedOrigin o = (DecodedOrigin) myDecodedOrigins.get(it.next());
			o.reset(randomize);
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
