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
import ca.neo.math.ApproximatorFactory;
import ca.neo.math.Function;
import ca.neo.math.LinearApproximator;
import ca.neo.model.Origin;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.SpikeOutput;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.impl.AbstractEnsemble;
import ca.neo.model.impl.RealOutputImpl;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFNode;
import ca.neo.model.plasticity.PlasticityRule;
import ca.neo.util.MU;
import ca.neo.util.TimeSeries;

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
	
	public static String BIAS_SUFFIX = "_bias";
	public static String INTERNEURON_SUFFIX = "_interneuron";
	
	private int myDimension;
	private float[][] myEncoders;
	private Map<String, DecodedTermination> myDecodedTerminations;
	private Map<String, PlasticityRule> myPlasticityRules;
	private float myPlasticityInterval;
	private float myLastPlasticityTime;	
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
		
		myDimension = encoders[0].length;
		for (int i = 1; i < encoders.length; i++) {
			if (encoders[i].length != myDimension) {
				throw new StructuralException("Encoders have different lengths");
			}
		}		
		myEncoders = encoders;
		
		myDecodedTerminations = new HashMap<String, DecodedTermination>(10);
		myPlasticityRules = new HashMap<String, PlasticityRule>(10);
		myPlasticityInterval = -1;
		myLastPlasticityTime = 0;		
		myApproximatorFactory = factory;
		myDecodingApproximators = new HashMap<String, LinearApproximator>(10);
		myEvalPoints = evalPoints;
	}
	
	/**
	 * @param evalPoints Vector points at which to find output (each one must have same dimension as
	 * 		encoder)
	 * @param origin Name of Origin from which to collect output for each Node
	 * @return Output of each Node at each evaluation point (1st dimension corresponds to Node) 
	 * @throws StructuralException If CONSTANT_RATE is not supported by any Node
	 */
	protected float[][] getConstantOutputs(float[][] evalPoints, String origin) throws StructuralException {
		NEFNode[] nodes = (NEFNode[]) getNodes();		
		float[][] result = new float[nodes.length][];
		
		for (int i = 0; i < nodes.length; i++) {			
			try {
				result[i] = getConstantOutput(i, evalPoints, origin);
			} catch (SimulationException e) {
				throw new StructuralException("Node " + i + " does not have the Origin " + origin);
			}
		}

		return result;
	}
	
	/**
	 * @param nodeIndex Index of Node for which to find output at various inputs
	 * @param evalPoints Vector points at which to find output (each one must have same dimension as
	 * 		encoder)
	 * @param origin Name of Origin from which to collect output
	 * @return Output of indexed Node at each evaluation point 
	 * @throws StructuralException If CONSTANT_RATE is not supported by the given Node
	 * @throws SimulationException If the Node does not have an Origin with the given name  
	 */
	protected float[] getConstantOutput(int nodeIndex, float[][] evalPoints, String origin) 
			throws StructuralException, SimulationException {
		
		float[] result = new float[evalPoints.length];
		
		NEFNode node = (NEFNode) getNodes()[nodeIndex];
		synchronized (node) {
			SimulationMode mode = node.getMode();
			
			node.setMode(SimulationMode.CONSTANT_RATE);
			if ( !node.getMode().equals(SimulationMode.CONSTANT_RATE) ) {
				throw new StructuralException(
					"To find decoders using this method, all Nodes must support CONSTANT_RATE simulation mode");
			}
			
			for (int i = 0; i < result.length; i++) {
				node.setRadialInput(getRadialInput(evalPoints[i], nodeIndex));
				
				node.run(0f, 0f);
				
				RealOutput output = (RealOutput) node.getOrigin(origin).getValues();
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
			float[][] outputs = getConstantOutputs(myEvalPoints, nodeOrigin);
			LinearApproximator approximator = myApproximatorFactory.getApproximator(myEvalPoints, outputs);
			myDecodingApproximators.put(nodeOrigin, approximator);
		}		
		
		DecodedOrigin result = new DecodedOrigin(this, name, (NEFNode[]) getNodes(), nodeOrigin, functions, myDecodingApproximators.get(nodeOrigin));
		super.addDecodedOrigin(name, result);
		
		return result;
	}	

	/**
	 * @see ca.neo.model.nef.NEFEnsemble#addBiasOrigin(ca.neo.model.Origin, int, java.lang.String, boolean)
	 */
	public BiasOrigin addBiasOrigin(Origin existing, int numInterneurons, String name, boolean excitatory) throws StructuralException {
		if ( !(existing instanceof DecodedOrigin) ) {
			throw new StructuralException("A DecodedOrigin is needed to make a BiasOrigin");
		}
		
		DecodedOrigin o = (DecodedOrigin) existing;
		BiasOrigin result = new BiasOrigin(this, name, getNodes(), o.getNodeOrigin(), 
				getConstantOutputs(myEvalPoints, o.getNodeOrigin()), numInterneurons, excitatory);
		
		addDecodedOrigin(result.getName(), result);
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
		
		DecodedTermination result = new DecodedTermination(this, name, matrix, dynamics, integrator);
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
			float passthrough, boolean isModulatory) throws StructuralException {
		
		LTISystem dynamics = CanonicalModel.getRealization(tfNumerator, tfDenominator, passthrough);
	
		Matrix A = new Matrix(MU.convert(dynamics.getA(0f)));
		double[] eigenvalues = A.eig().getRealEigenvalues();
		double fastest = Math.abs(eigenvalues[0]);
		for (int i = 1; i < eigenvalues.length; i++) {
			if (Math.abs(eigenvalues[i]) > fastest) fastest = Math.abs(eigenvalues[i]);
		}
		
		EulerIntegrator integrator = new EulerIntegrator(1f / (10f * (float) fastest));
		
		DecodedTermination result = new DecodedTermination(this, name, matrix, dynamics, integrator);
		if (isModulatory) {
			result.getConfiguration().setProperty(Termination.MODULATORY, new Boolean(true));
		} else if (matrix.length != myDimension) {
			throw new StructuralException("Output dimension " + matrix.length + " doesn't equal ensemble dimension " + myDimension);
		}
		myDecodedTerminations.put(name, result);
		
		return result;
	}
		
	public BiasTermination[] addBiasTerminations(DecodedTermination baseTermination, float interneuronTauPSC, float biasDecoder, float[][] functionDecoders) throws StructuralException {
		float[][] transform = baseTermination.getTransform();
		
		float[] biasEncoders = new float[myEncoders.length];
		for (int j = 0; j < biasEncoders.length; j++) {
			float max = 0;
			for (int i = 0; i < functionDecoders.length; i++) {
				float x = - MU.prod(myEncoders[j], MU.prod(transform, functionDecoders[i])) / biasDecoder;
				if (x > max) max = x;
			}			
			biasEncoders[j] = max;
		}
		
		float baseTauPSC = ((Float) baseTermination.getConfiguration().getProperty(Termination.TAU_PSC)).floatValue();
		EulerIntegrator integrator = new EulerIntegrator(Math.min(interneuronTauPSC, baseTauPSC) / 10f);
		System.out.println("base: " + baseTauPSC + " inter" + interneuronTauPSC + " min: " + Math.min(interneuronTauPSC, baseTauPSC));
		
		float scale = 1 / interneuronTauPSC; //output scaling to make impulse integral = 1		
		LinearSystem interneuronDynamics = new SimpleLTISystem(
				new float[]{-1f/interneuronTauPSC}, 
				new float[][]{new float[]{1f}}, 
				new float[][]{new float[]{scale}}, 
				new float[]{0f}, 
				new Units[]{Units.UNK}
		);
		
		String biasName = baseTermination.getName()+BIAS_SUFFIX;
		String interName = baseTermination.getName()+INTERNEURON_SUFFIX;
		BiasTermination biasTermination = new BiasTermination(this, biasName, baseTermination.getName(), baseTermination.getDynamics(), integrator, biasEncoders, false);
		BiasTermination interneuronTermination = new BiasTermination(this, interName, baseTermination.getName(), interneuronDynamics, integrator, biasEncoders, true);
		
		Boolean modulatory = (Boolean) baseTermination.getConfiguration().getProperty(Termination.MODULATORY);
		biasTermination.getConfiguration().setProperty(Termination.MODULATORY, modulatory);
		interneuronTermination.getConfiguration().setProperty(Termination.MODULATORY, modulatory);
		
		myDecodedTerminations.put(biasName, biasTermination);
		myDecodedTerminations.put(interName, interneuronTermination);
		
		return new BiasTermination[]{biasTermination, interneuronTermination};
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
			Map<String, Float> bias = new HashMap<String, Float>(5);

			//run terminations and sum state ...
			Iterator it = myDecodedTerminations.values().iterator();
			while (it.hasNext()) {
				DecodedTermination t = (DecodedTermination) it.next();
				t.run(startTime, endTime);
				float[] output = t.getOutput();
				
				boolean isModulatory = ((Boolean) t.getConfiguration().getProperty(Termination.MODULATORY)).booleanValue();
				//TODO: handle modulatory bias input
				if (t instanceof BiasTermination) {
					String baseName = ((BiasTermination) t).getBaseTerminationName();
					if (!bias.containsKey(baseName)) bias.put(baseName, new Float(0));					
					if (!isModulatory) bias.put(baseName, new Float(bias.get(baseName).floatValue() + output[0]));
				} else {
					if (!isModulatory) state = MU.sum(state, output);					
				}
				
			}

			if ( getMode().equals(SimulationMode.DIRECT) ) {
				Origin[] origins = getOrigins();
				for (int i = 0; i < origins.length; i++) {
					if (origins[i] instanceof DecodedOrigin) {
						((DecodedOrigin) origins[i]).run(state, startTime, endTime);
					}
				}
				setTime(endTime);
			} else {
				//multiply state by encoders (cosine tuning), set radial input of each Neuron and run ...
				NEFNode[] nodes = (NEFNode[]) getNodes();
				for (int i = 0; i < nodes.length; i++) {
					nodes[i].setRadialInput(getRadialInput(state, i) + getBiasInput(bias, myDecodedTerminations, i));
				}
				super.run(startTime, endTime);
			}
			if (myPlasticityInterval <= 0) {
				learn(startTime, endTime);				
			} else if (endTime >= myLastPlasticityTime + myPlasticityInterval) {
				learn(myLastPlasticityTime, endTime);
				myLastPlasticityTime = endTime;
			}

		} catch (SimulationException e) {
			e.setEnsemble(getName());
			throw e;
		}
	}
	
	// @param bias Bias input (related to avoidance of negative weights with interneurons) 
	private static float getBiasInput(Map<String, Float> bias, Map<String, DecodedTermination> dt, int node) {
		float sumBias = 0;
		Iterator<String> it = bias.keySet().iterator();
		while (it.hasNext()) {
			String baseName = it.next();
			float netBias = bias.get(baseName).floatValue();
			float biasEncoder = ((BiasTermination) dt.get(baseName+BIAS_SUFFIX)).getBiasEncoders()[node];
			sumBias += netBias * biasEncoder;
		}
		return sumBias;
	}
	
	//run ensemble plasticity rules (assume constant input/state over given elapsed time)
	private void learn(float startTime, float endTime) throws SimulationException {
		Iterator ruleIter = myPlasticityRules.keySet().iterator();
		while (ruleIter.hasNext()) {
			String name = (String) ruleIter.next();
			DecodedTermination termination = myDecodedTerminations.get(name);
			PlasticityRule rule = myPlasticityRules.get(name);
			
			Iterator termIter = myDecodedTerminations.keySet().iterator();
			while (termIter.hasNext()) {
				DecodedTermination t = myDecodedTerminations.get(termIter.next());
				rule.setTerminationState(t.getName(), new RealOutputImpl(t.getOutput(), Units.UNK, endTime), endTime);
			}
			
			Origin[] origins = getOrigins();
			for (int i = 0; i < origins.length; i++) {
				if (origins[i] instanceof DecodedOrigin) {
					rule.setOriginState(origins[i].getName(), origins[i].getValues(), endTime);
				}
			}
			
			float[][] transform = termination.getTransform();
			float[][] derivative = rule.getDerivative(transform, termination.getInput(), endTime);
			float scale = (termination.getInput() instanceof SpikeOutput) ? 1 : (endTime - startTime); 
			for (int i = 0; i < transform.length; i++) {
				for (int j = 0; j < transform[i].length; j++) {
					transform[i][j] += derivative[i][j] * scale;
				}
			}	
		}
	}
	
	/**
	 * @param state State vector 
	 * @param node Node number 
	 * @return Radial input to the given node 
	 */
	public float getRadialInput(float[] state, int node) {
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
	 * TODO: should there be a pass-through option as in EnsembleImpl? 
	 * 
	 * @see ca.neo.model.plasticity.Plastic#setPlasticityRule(java.lang.String, ca.neo.model.plasticity.PlasticityRule)
	 */
	public void setPlasticityRule(String terminationName, PlasticityRule rule) throws StructuralException {
		if (myDecodedTerminations.containsKey(terminationName)) {
			myPlasticityRules.put(terminationName, rule);
		} else {
			super.setPlasticityRule(terminationName, rule);
		} 
	}

	/**
	 * @see ca.neo.model.plasticity.Plastic#setPlasticityInterval(float)
	 */
	public void setPlasticityInterval(float time) {
		myPlasticityInterval = time;
	}

	@Override
	public TimeSeries getHistory(String stateName) throws SimulationException {
		DecodedTermination t = myDecodedTerminations.get(stateName);
		if (t == null) {
			return super.getHistory(stateName);			
		} else {
			return t.getHistory(DecodedTermination.OUTPUT);
		}
	}

	@Override
	public Properties listStates() {
		Properties p = super.listStates();
		Iterator<String> it = myDecodedTerminations.keySet().iterator();
		while (it.hasNext()) {
			String termName = it.next();
			p.setProperty(termName, "Output of Termination " + termName);
		}
		return p;
	}
	
	

}
