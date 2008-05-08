/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "NEFEnsembleImpl.java". Description: 
"Default implementation of NEFEnsemble"

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
 * Created on 31-May-2006
 */
package ca.nengo.model.nef.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import Jama.Matrix;

import ca.nengo.dynamics.LinearSystem;
import ca.nengo.dynamics.impl.CanonicalModel;
import ca.nengo.dynamics.impl.EulerIntegrator;
import ca.nengo.dynamics.impl.LTISystem;
import ca.nengo.dynamics.impl.SimpleLTISystem;
import ca.nengo.math.ApproximatorFactory;
import ca.nengo.math.Function;
import ca.nengo.math.LinearApproximator;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SimulationException;
import ca.nengo.model.SimulationMode;
import ca.nengo.model.SpikeOutput;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.Units;
import ca.nengo.model.impl.RealOutputImpl;
import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.model.nef.NEFNode;
import ca.nengo.model.plasticity.PlasticityRule;
import ca.nengo.util.MU;
import ca.nengo.util.TimeSeries;

/**
 * Default implementation of NEFEnsemble. 
 * 
 * TODO: links to NEF documentation
 * TODO: test
 * 
 * @author Bryan Tripp
 */
public class NEFEnsembleImpl extends DecodableEnsembleImpl implements NEFEnsemble {

	private static final long serialVersionUID = 1L;
	
	public static String BIAS_SUFFIX = ":bias";
	public static String INTERNEURON_SUFFIX = ":interneuron";
	
	private int myDimension;
	private float[][] myEncoders;
	private Map<String, DecodedTermination> myDecodedTerminations;
	private Map<String, PlasticityRule> myPlasticityRules;
	private float myPlasticityInterval;
	private float myLastPlasticityTime;	
	private Map<String, LinearApproximator> myDecodingApproximators;	
	private boolean myReuseApproximators;
	private float[][] myUnscaledEvalPoints;
	private float[][] myEvalPoints;	
	private float[] myRadii;
	private float[] myInverseRadii;
	private boolean myRadiiAreOne;

	/**
	 * @param name Unique name of Ensemble
	 * @param nodes Nodes that make up the Ensemble
	 * @param encoders List of encoding vectors (one for each node). All must have same length 
	 * @param factory Source of LinearApproximators to use in decoding output
	 * @param evalPoints Vector inputs at which output is found to produce DecodedOrigins   
	 * @throws StructuralException if there	are a different number of Nodes than encoding vectors or if not 
	 * 		all encoders have the same length
	 */
	public NEFEnsembleImpl(String name, NEFNode[] nodes, float[][] encoders, ApproximatorFactory factory, float[][] evalPoints, float[] radii) 
			throws StructuralException {
		
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
		myDecodingApproximators = new HashMap<String, LinearApproximator>(10);
		myReuseApproximators = true;		
		myUnscaledEvalPoints = evalPoints;
		setRadii(radii);
	}
	
	/**
	 * @see ca.nengo.model.nef.NEFEnsemble#getRadii()
	 */
	public float[] getRadii() {
		return myRadii.clone();
	}
	
	/**
	 * @param radii A list of radii of encoded area along each dimension; uniform 
	 * 		radius along each dimension can be specified with a list of length 1
	 */
	public void setRadii(float[] radii) {
		if (radii.length != getDimension() && radii.length != 1) {
			throw new IllegalArgumentException("radius vector must have length " + getDimension() 
					+ " or 1 (for uniform radius)");
		}
		
		if (radii.length == 1 && getDimension() != 1) {
			float uniformRadius = radii[0];
			radii = MU.uniform(1, getDimension(), uniformRadius)[0];			
		}
		
		myRadii = radii;
		
		myInverseRadii = new float[radii.length];
		myRadiiAreOne = true;
		for (int i = 0; i < radii.length; i++) {
			myInverseRadii[i] = 1f / radii[i];
			if (Math.abs(radii[i]-1f) > 1e-10) myRadiiAreOne = false;
		}
		
		myEvalPoints = new float[myUnscaledEvalPoints.length][];
		for (int i = 0; i < myUnscaledEvalPoints.length; i++) {
			myEvalPoints[i] = new float[myUnscaledEvalPoints[i].length];
			for (int j = 0; j < myUnscaledEvalPoints[i].length; j++) {
				myEvalPoints[i][j] = myUnscaledEvalPoints[i][j] * myRadii[j];
			}
		}
		
		myDecodingApproximators.clear();
	}
	
	/**
	 * Note: by-products of decoding are sometimes cached, so if these are changed it may be 
	 * necessary to call setReuseApproximators(false) for the change to take effect. 
	 * 
	 * @param evalPoints Points in the encoded space at which node outputs are evaluated for 
	 * 		establishing new DecodedOrigins. 
	 */
	public void setEvalPoints(float[][] evalPoints) {
		if (!MU.isMatrix(evalPoints) || evalPoints[0].length != getDimension()) {
			throw new IllegalArgumentException("Expected eval points of length " 
					+ getDimension() + " (was " + evalPoints[0].length + ")");
		}
		
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
	 * @see ca.nengo.model.nef.NEFEnsemble#getDimension()
	 */
	public int getDimension() {
		return myDimension;
	}

	/**
	 * @see ca.nengo.model.nef.NEFEnsemble#getEncoders()
	 */
	public float[][] getEncoders() {
		return MU.clone(myEncoders);
	}
	
	/**
	 * @param encoders New encoding vectors (row per Node)
	 */
	public void setEncoders(float[][] encoders) {
		assert MU.isMatrix(encoders);
		assert encoders.length == getNodes().length;
		assert encoders[0].length == getDimension();
		
		myEncoders = encoders;
	}
	
	/**
	 * @return True if LinearApproximators for a Node Origin are re-used for decoding multiple decoded Origins. 
	 */
	public boolean getReuseApproximators() {
		return myReuseApproximators;
	}
	
	/**
	 * @param reuse True if LinearApproximators for a Node Origin are re-used for decoding multiple decoded Origins.
	 */
	public void setReuseApproximators(boolean reuse) {
		myReuseApproximators = reuse;
	}

	/**
	 * @see ca.nengo.model.nef.NEFEnsemble#addDecodedOrigin(java.lang.String, Function[], String)
	 */
	public Origin addDecodedOrigin(String name, Function[] functions, String nodeOrigin) throws StructuralException {		
		if (!myReuseApproximators || !myDecodingApproximators.containsKey(nodeOrigin)) {
			float[][] outputs = getConstantOutputs(myEvalPoints, nodeOrigin);
			LinearApproximator approximator = getApproximatorFactory().getApproximator(myEvalPoints, outputs);
			myDecodingApproximators.put(nodeOrigin, approximator);
		}		
		
		DecodedOrigin result = new DecodedOrigin(this, name, (NEFNode[]) getNodes(), nodeOrigin, functions, myDecodingApproximators.get(nodeOrigin));
		result.setMode(getMode());
		super.addDecodedOrigin(name, result);
		
		return result;
	}	

	/**
	 * @see ca.nengo.model.nef.NEFEnsemble#addBiasOrigin(ca.nengo.model.Origin, int, java.lang.String, boolean)
	 */
	public BiasOrigin addBiasOrigin(Origin existing, int numInterneurons, String name, boolean excitatory) throws StructuralException {
		if ( !(existing instanceof DecodedOrigin) ) {
			throw new StructuralException("A DecodedOrigin is needed to make a BiasOrigin");
		}
		
		DecodedOrigin o = (DecodedOrigin) existing;
		BiasOrigin result = new BiasOrigin(this, name, getNodes(), o.getNodeOrigin(), 
				getConstantOutputs(myEvalPoints, o.getNodeOrigin()), numInterneurons, excitatory);
		result.setMode(getMode());
		addDecodedOrigin(result.getName(), result);
		return result;
	}
	
	/**
	 * @see ca.nengo.model.nef.NEFEnsemble#addDecodedTermination(java.lang.String, float[][], float, boolean)
	 */
	public Termination addDecodedTermination(String name, float[][] matrix, float tauPSC, boolean isModulatory) 
			throws StructuralException {
		
		if (myDecodedTerminations.containsKey(name)) {
			throw new StructuralException("The ensemble already contains a termination named " + name);
		}
		
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
			result.setModulatory(isModulatory);
		} else if (matrix.length != myDimension) {
			throw new StructuralException("Output dimension " + matrix.length + " doesn't equal ensemble dimension " + myDimension);
		}
		myDecodedTerminations.put(name, result);
		fireVisibleChangeEvent();
		return result;
	}

	/**
	 * @see ca.nengo.model.nef.NEFEnsemble#addDecodedTermination(java.lang.String, float[][], float[], float[], float, boolean)
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
			result.setModulatory(isModulatory);
		} else if (matrix.length != myDimension) {
			throw new StructuralException("Output dimension " + matrix.length + " doesn't equal ensemble dimension " + myDimension);
		}
		myDecodedTerminations.put(name, result);
		fireVisibleChangeEvent();		
		return result;
	}

	/**
	 * @see ca.nengo.model.nef.NEFEnsemble#addBiasTerminations(ca.nengo.model.nef.impl.DecodedTermination, float, float[][], float[][])
	 */
	public BiasTermination[] addBiasTerminations(DecodedTermination baseTermination, float interneuronTauPSC, float[][] biasDecoders, float[][] functionDecoders) throws StructuralException {
		float[][] transform = baseTermination.getTransform();
		
		float[] biasEncoders = new float[myEncoders.length];
		for (int j = 0; j < biasEncoders.length; j++) {
			float max = 0;
			for (int i = 0; i < functionDecoders.length; i++) {
				float x = - MU.prod(myEncoders[j], MU.prod(transform, functionDecoders[i])) / biasDecoders[i][0];
				if (x > max) max = x;
			}			
			biasEncoders[j] = max;
		}
		
		float baseTauPSC = baseTermination.getTau();
		EulerIntegrator integrator = new EulerIntegrator(Math.min(interneuronTauPSC, baseTauPSC) / 10f);
		
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
		
		BiasTermination biasTermination = null;
		try {
			LinearSystem baseDynamics = (LinearSystem) baseTermination.getDynamics().clone();
			biasTermination = new BiasTermination(this, biasName, baseTermination.getName(), baseDynamics, integrator, biasEncoders, false);
		} catch (CloneNotSupportedException e) {
			throw new StructuralException("Can't clone dynamics for bias termination", e);
		}
		BiasTermination interneuronTermination = new BiasTermination(this, interName, baseTermination.getName(), interneuronDynamics, integrator, biasEncoders, true);
		
		biasTermination.setModulatory(baseTermination.getModulatory());
		interneuronTermination.setModulatory(baseTermination.getModulatory());
		
		myDecodedTerminations.put(biasName, biasTermination);
		myDecodedTerminations.put(interName, interneuronTermination);

		fireVisibleChangeEvent();
		
		return new BiasTermination[]{biasTermination, interneuronTermination};
	}

	/**
	 * @see ca.nengo.model.nef.NEFEnsemble#removeDecodedTermination(java.lang.String)
	 */
	public void removeDecodedTermination(String name) {
		myDecodedTerminations.remove(name);
		fireVisibleChangeEvent();
	}

	/**
	 * @see ca.nengo.model.Ensemble#getTerminations()
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
	 * @see ca.nengo.model.Ensemble#run(float, float)
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
				
				boolean isModulatory = t.getModulatory();
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
				Node[] nodes = getNodes();
				for (int i = 0; i < nodes.length; i++) {
					((NEFNode) nodes[i]).setRadialInput(getRadialInput(state, i) + getBiasInput(bias, myDecodedTerminations, i));
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
		//scale state to unit circle if necessary
		if (!myRadiiAreOne) state = MU.prodElementwise(state, myInverseRadii);
		return MU.prod(state, myEncoders[node]);
	}
	
	/**
	 * @see ca.nengo.model.Node#getTermination(java.lang.String)
	 */
	public Termination getTermination(String name) throws StructuralException {
		return myDecodedTerminations.containsKey(name) ? myDecodedTerminations.get(name) : super.getTermination(name);
	}
	
	/**
	 * @see ca.nengo.model.Ensemble#setMode(ca.nengo.model.SimulationMode)
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
	 * @see ca.nengo.model.Resettable#reset(boolean)
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
	 * @see ca.nengo.model.plasticity.Plastic#setPlasticityRule(java.lang.String, ca.nengo.model.plasticity.PlasticityRule)
	 */
	public void setPlasticityRule(String terminationName, PlasticityRule rule) throws StructuralException {
		if (myDecodedTerminations.containsKey(terminationName)) {
			myPlasticityRules.put(terminationName, rule);
		} else {
			super.setPlasticityRule(terminationName, rule);
		} 
	}

	/**
	 * @see ca.nengo.model.plasticity.Plastic#setPlasticityInterval(float)
	 */
	public void setPlasticityInterval(float time) {
		myPlasticityInterval = time;
	}
	
	@Override
	public float getPlasticityInterval() {
		return myPlasticityInterval;
	}

	@Override
	public PlasticityRule getPlasticityRule(String terminationName) throws StructuralException {
		if (myDecodedTerminations.containsKey(terminationName)) {
			return myPlasticityRules.get(terminationName);
		} else {
			return super.getPlasticityRule(terminationName);			
		}
	}

	@Override
	public String[] getPlasticityRuleNames() {
		String[] thisNames = myDecodedTerminations.keySet().toArray(new String[0]);
		String[] superNames = super.getPlasticityRuleNames();
		String[] result = new String[thisNames.length + superNames.length];
		System.arraycopy(thisNames, 0, result, 0, thisNames.length);
		System.arraycopy(superNames, 0, result, thisNames.length, superNames.length);
		return result;
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

	public NEFEnsemble clone() throws CloneNotSupportedException {
		NEFEnsembleImpl result = (NEFEnsembleImpl) super.clone();
		
		result.myEncoders = MU.clone(myEncoders);

		result.myDecodedTerminations = new HashMap<String, DecodedTermination>(10);
		result.myPlasticityRules = new HashMap<String, PlasticityRule>(10);
		for (String key : myDecodedTerminations.keySet()) {
			DecodedTermination t = (DecodedTermination) myDecodedTerminations.get(key).clone(); 
			t.setNode(result);
			result.myDecodedTerminations.put(key, t);
			
			PlasticityRule r = myPlasticityRules.get(key);
			if (r != null) {
				result.myPlasticityRules.put(key, r.clone());
			}
		}		
		
		//change scaling terminations references to the new copies 
		for (String key : result.myDecodedTerminations.keySet()) {
			DecodedTermination t = result.myDecodedTerminations.get(key);
			if (t.getScaling() != null) {
				t.setScaling(result.myDecodedTerminations.get(t.getScaling().getName()));
			}			
		}

		result.myDecodingApproximators = new HashMap<String, LinearApproximator>(5);
		result.myEncoders = MU.clone(myEncoders);
		result.myEvalPoints = MU.clone(myEvalPoints);
		result.myInverseRadii = myInverseRadii.clone();
		result.myRadii = myRadii.clone();
		result.myUnscaledEvalPoints = MU.clone(myUnscaledEvalPoints);
		return result;
	}
}
