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
	private Origin[] myNeuronOrigins;
	private Termination[] myNeuronTerminations;
	private LinearApproximator myDecodingApproximator;
	private float myTime; //used to support Probeable
	
	/**
	 * @param name Unique name of Ensemble
	 * @param neurons Neurons that make up the Ensemble
	 * @param encoders List of encoding vectors (one for each neuron). All must have same length 
	 * @throws StructuralException if any of the given Neurons does not have an NEFSynapticIntegrator, if there 
	 * 		are a different number of Neurons than encoding vectors, if not all encoders have the same length, 
	 * 		or if there is a problem setting up the LinearApproximator 
	 */
	public NEFEnsembleImpl(String name, Neuron[] neurons, float[][] encoders) throws StructuralException {
		super(name, neurons);
		init(name, neurons, encoders);
		setApproximator();
	}
	
	/**
	 * @param name Unique name of Ensemble
	 * @param neurons Neurons that make up the Ensemble
	 * @param encoders List of encoding vectors (one for each neuron). All must have same length 
	 * @throws StructuralException if any of the given Neurons does not have an NEFSynapticIntegrator, if there 
	 * 		are a different number of Neurons than encoding vectors, if not all encoders have the same length, 
	 * 		or if there is a problem setting up the LinearApproximator 
	 */
	public NEFEnsembleImpl(String name, Neuron[] neurons, float[][] encoders, boolean setApproximator) throws StructuralException {
		super(name, neurons);
		init(name, neurons, encoders);
		if (setApproximator) setApproximator();
	}
	
	private void init(String name, Neuron[] neurons, float[][] encoders) throws StructuralException {
		for (int i = 0; i < neurons.length; i++) {
			if ( !(neurons[i] instanceof NEFNode) ) {
				throw new StructuralException("All Neurons in an NEFEnsemble must have an NEFSynapticIntegrator");
			}
		}
		
		if (neurons.length != encoders.length) {
			throw new StructuralException("There are " + neurons.length + " neurons but " 
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
		
		myNeuronOrigins = findOrigins(neurons);
		myNeuronTerminations = findTerminations(neurons);
		
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
	 * @param evalPoints Vector points at which to find firing rated (each one must have same dimension as
	 * 		encoder)
	 * @return Firing rates of each neuron at each evaluation point (1st dimension corresponds to neuron) 
	 * @throws StructuralException If CONSTANT_RATE is not supported by any Neuron
	 */
	protected float[][] getAllFiringRates(float[][] evalPoints) throws StructuralException {
		Neuron[] neurons = getNeurons();		
		float[][] result = new float[neurons.length][];
		
		for (int i = 0; i < neurons.length; i++) {			
			try {
				result[i] = getFiringRates(i, evalPoints);
			} catch (SimulationException e) {
				throw new Error("Neuron " + i + " does not have the standard 'AXON' Origin");
			}
		}

		return result;
	}
	
	/**
	 * @param neuronIndex Index of neuron for which to find firing rates at various inputs
	 * @param evalPoints Vector points at which to find firing rate (each one must have same dimension as
	 * 		encoder)
	 * @return Firing rate of indexed neuron at each evaluation point 
	 * @throws StructuralException If CONSTANT_RATE is not supported by the given Neuron
	 * @throws SimulationException If the Neuron does not have an Origin with the standard name "AXON" 
	 */
	protected float[] getFiringRates(int neuronIndex, float[][] evalPoints) 
			throws StructuralException, SimulationException {
		
		float[] result = new float[evalPoints.length];
		
		Neuron neuron = getNeurons()[neuronIndex];
		synchronized (neuron) {
			SimulationMode mode = neuron.getMode();
			
			neuron.setMode(SimulationMode.CONSTANT_RATE);
			if ( !neuron.getMode().equals(SimulationMode.CONSTANT_RATE) ) {
				throw new StructuralException(
					"To find decoders using default methods, all Neurons must support CONSTANT_RATE simulation mode");
			}
			
			for (int i = 0; i < result.length; i++) {
//				float radialInput = MU.prod(encoder, evalPoints[i]);
				((NEFNode) neuron).setRadialInput(getRadialInput(evalPoints[i], neuronIndex));
				
				neuron.run(0f, 0f);
				
				RealOutput firingRate = (RealOutput) neuron.getOrigin(Neuron.AXON).getValues();
				result[i] = firingRate.getValues()[0];
			}
			
			neuron.setMode(mode);
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
//		for (int i = 0; i < functions.length; i++) {
//			if (functions[i].getDimension() != getDimension()) {
//				throw new StructuralException("Functions must all have input dimension " + getDimension());
//			}
//		}
		
		Origin result = new DecodedOrigin(name, getNeurons(), functions, myDecodingApproximator);
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
		
		Origin[] all = new Origin[decoded.length + myNeuronOrigins.length];
		System.arraycopy(decoded, 0, all, 0, all.length);
		System.arraycopy(myNeuronOrigins, 0, all, decoded.length, myNeuronOrigins.length);
		
		return all;
	}

	/**
	 * @see ca.neo.model.Ensemble#getTerminations()
	 */
	public Termination[] getTerminations() {
		Termination[] decoded = (Termination[]) myDecodedTerminations.values().toArray(new Termination[0]);
		
		Termination[] all = new Termination[decoded.length + myNeuronTerminations.length];
		System.arraycopy(decoded, 0, all, 0, all.length);
		System.arraycopy(myNeuronTerminations, 0, all, decoded.length, myNeuronTerminations.length);
		
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

			//run neurons ... 
			SpikePatternImpl pattern = null;
			if (myCollectSpikesFlag) pattern = (SpikePatternImpl) getSpikePattern();
			
			if ( !getMode().equals(SimulationMode.DIRECT) ) {
				//multiply state by encoders (cosine tuning), set radial input of each Neuron and run ...
				Neuron[] neurons = getNeurons();
				for (int i = 0; i < neurons.length; i++) {
					((NEFNode) neurons[i]).setRadialInput(getRadialInput(state, i));
					
					neurons[i].run(startTime, endTime);

					if (myCollectSpikesFlag) {
						try {
							InstantaneousOutput output = neurons[i].getOrigin(Neuron.AXON).getValues();
							if (output instanceof SpikeOutput && ((SpikeOutput) output).getValues()[0]) {
								pattern.addSpike(i, endTime);
							}											
						} catch (StructuralException e) {
							throw new SimulationException(e);
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
	 * @param neuron Neuron number 
	 * @return Radial input to the given neuron 
	 */
	protected float getRadialInput(float[] state, int neuron) {
		return MU.prod(state, myEncoders[neuron]);
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
		
		for (int i = 0; i < myNeuronOrigins.length && result == null; i++) {
			if (myNeuronOrigins[i].getName().equals(name)) {
				result = myNeuronOrigins[i];
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
		
		for (int i = 0; i < myNeuronTerminations.length && result == null; i++) {
			if (myNeuronTerminations[i].getName().equals(name)) {
				result = myNeuronTerminations[i];
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
