package ca.neo.model.nef.impl;

import java.util.Properties;

import ca.neo.dynamics.Integrator;
import ca.neo.dynamics.LinearSystem;
import ca.neo.dynamics.impl.CanonicalModel;
import ca.neo.dynamics.impl.LTISystem;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Node;
import ca.neo.model.Probeable;
import ca.neo.model.RealOutput;
import ca.neo.model.Resettable;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.impl.RealOutputImpl;
import ca.neo.util.MU;
import ca.neo.util.TimeSeries;
import ca.neo.util.impl.TimeSeriesImpl;

/**
 * <p>A Termination of decoded state vectors onto an NEFEnsemble. A DecodedTermination
 * performs a linear transformation on incoming vectors, mapping them into the 
 * space of the NEFEnsemble to which this Termination belongs. A DecodedTermination 
 * also applies linear PSC dynamics (typically exponential decay) to the resulting 
 * vector.</p> 
 * 
 * <p>Non-linear dynamics are not allowed at this level. This is because the vector input 
 * to an NEFEnsemble only has meaning in terms of the decomposition of synaptic weights 
 * into decoding vectors, transformation matrix, and encoding vectors. Linear PSC dynamics
 * actually apply to currents, but if everything is linear we can re-order the dynamics
 * and the encoders for convenience (so that the dynamics seem to operate on the 
 * state vectors). In contrast, non-linear dynamics must be modeled within each Neuron, 
 * because all inputs to a non-linear dynamical process must be taken into account before
 * the effect of any single input is known.</p>
 * 
 * @author Bryan Tripp
 */
public class DecodedTermination implements Termination, Resettable, Probeable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of Probeable output state. 
	 */
	public static final String OUTPUT = "output";
	
	private Node myNode;
	private String myName;
	private int myOutputDimension;
	private float[][] myTransform;
	private LinearSystem myDynamicsTemplate;
	private LinearSystem[] myDynamics;
	private Integrator myIntegrator;
	private Units[] myNullUnits;	
	private RealOutput myInputValues; 
	private float myTime;
	private float[] myOutputValues;	
	private boolean myTauMutable;	
	private DecodedTermination myScalingTermination;
	private float[] myStaticBias;	
	private float myTau;
	private boolean myModulatory;
	
	/**
	 * @param Node The parent Node
	 * @param name The name of this Termination
	 * @param transform A matrix that maps input (which has the dimension of this Termination)  
	 * 		onto the state space represented by the NEFEnsemble to which the Termination belongs
	 * @param dynamics Post-synaptic current dynamics (single-input single-output). Time-varying 
	 * 		dynamics are OK, but non-linear dynamics don't make sense here, because other 
	 * 		Terminations may input onto the same neurons. 
	 * @param integrator Numerical integrator with which to solve dynamics  
	 * @throws StructuralException If dynamics are not SISO or given transform is not a matrix 
	 */
	public DecodedTermination(Node node, String name, float[][] transform, LinearSystem dynamics, Integrator integrator) 
			throws StructuralException {
		
		if (dynamics.getInputDimension() != 1 || dynamics.getOutputDimension() != 1) {
			throw new StructuralException("Dynamics must be single-input single-output");
		}
		
		myOutputDimension = transform.length;
		setTransform(transform);
		
		myNode = node;
		myName = name;
		myIntegrator = integrator;		
		
		//we save a little time by not reporting units to the dynamical system at each step
		myNullUnits = new Units[dynamics.getInputDimension()];
		myOutputValues = new float[transform.length];
		
		setDynamics(dynamics);
		myScalingTermination = null;
	}
	
	//copies dynamics for to each dimension
	private synchronized void setDynamics(int dimension) {
		LinearSystem[] newDynamics = new LinearSystem[dimension];
		for (int i = 0; i < newDynamics.length; i++) {
			try {
				newDynamics[i] = (LinearSystem) myDynamicsTemplate.clone();
				
				//maintain state if there is state
				if (myDynamics != null && myDynamics[i] != null) {
					newDynamics[i].setState(myDynamics[i].getState());
				}
			} catch (CloneNotSupportedException e) {
				throw new Error("The clone() operation is not supported by the given dynamics object");
			}
		}
		
		myDynamics = newDynamics;
	}

	/**
	 * @param bias Intrinsic bias that is added to inputs to this termination 
	 */
	public void setStaticBias(float[] bias) {
		if (bias.length != myTransform.length) {
			throw new IllegalArgumentException("Bias must have length " + myTransform.length);
		}
		myStaticBias = bias;
	}
	
	/**
	 * @return Static bias vector (a copy)
	 */
	public float[] getStaticBias() {
		float[] result = new float[myStaticBias.length];
		System.arraycopy(myStaticBias, 0, result, 0, result.length);
		return result;
	}
	
	/**
	 * @param values Only RealOutput is accepted. 
	 * 
	 * @see ca.neo.model.Termination#setValues(ca.neo.model.InstantaneousOutput)
	 */
	public void setValues(InstantaneousOutput values) throws SimulationException {
		if (values.getDimension() != getDimensions()) {
			throw new SimulationException("Dimension of input (" + values.getDimension() 
					+ ") does not equal dimension of this Termination (" + getDimensions() + ")");
		}
		
		if ( !(values instanceof RealOutput) ) {
			throw new SimulationException("Only real-valued input is accepted at a DecodedTermination");
		}

		RealOutput ro = (RealOutput) values;
		myInputValues = new RealOutputImpl(MU.sum(ro.getValues(), myStaticBias), ro.getUnits(), ro.getTime());
	}
	
	/**
	 * @param startTime Simulation time at which running is to start
	 * @param endTime Simulation time at which running is to end
	 * @throws SimulationException
	 */
	public void run(float startTime, float endTime) throws SimulationException {
		if (myDynamics == null) {
			setDynamics(myOutputDimension);
		}
		
		if (myInputValues == null) {
			throw new SimulationException("Null input values on termination " + myName);
		}

		float[][] transform = getTransform();
		if (myScalingTermination != null) {
			float scale = myScalingTermination.getOutput()[0];
			transform = MU.prod(transform, scale);
		}
		float[] dynamicsInputs = MU.prod(transform, myInputValues.getValues());
		float[] result = new float[dynamicsInputs.length];
		
		for (int i = 0; i < myDynamics.length; i++) {
			float[] inVal  = new float[]{dynamicsInputs[i]};
			TimeSeries inSeries = new TimeSeriesImpl(new float[]{startTime, endTime}, new float[][]{inVal, inVal}, myNullUnits);
			TimeSeries outSeries = myIntegrator.integrate(myDynamics[i], inSeries);
			result[i] = outSeries.getValues()[outSeries.getValues().length-1][0];
		}
		
		myTime = endTime;
		myOutputValues = result;
	}
	
	/**
	 * This method should be called after run(...). 
	 * 
	 * @return Output of dynamical system -- of interest at end of run(...)
	 */
	public float[] getOutput() {
		return myOutputValues;
	}
	
	/**
	 * @return Latest input to Termination (pre transform and dynamics)
	 */
	public RealOutput getInput() {
		return myInputValues;
	}

	/**
	 * @see ca.neo.model.Termination#getName()
	 */
	public String getName() {
		return myName;
	}

	/**
	 * @see ca.neo.model.Termination#getDimensions()
	 */
	public int getDimensions() {
		return myTransform[0].length;
	}

	/**
	 * @see ca.neo.model.Resettable#reset(boolean)
	 */
	public void reset(boolean randomize) {
		for (int i = 0; myDynamics != null && i < myDynamics.length; i++) {
			myDynamics[i].setState(new float[myDynamics[i].getState().length]);			
		}
		myInputValues = null;
	}

	/**
	 * @return The matrix that maps input (which has the dimension of this Termination)  
	 * 		onto the state space represented by the NEFEnsemble to which the Termination belongs
	 */
	public float[][] getTransform() {
		return MU.clone(myTransform);
	}
	
	/**
	 * @param transform New transform
	 * @throws StructuralException If the transform is not a matrix or has the wrong size
	 */
	public void setTransform(float[][] transform) throws StructuralException {
		if ( !MU.isMatrix(transform) ) {
			throw new StructuralException("Given transform is not a matrix");
		}
		if (transform.length != myOutputDimension) {
			throw new StructuralException("This transform must have " + myOutputDimension + " rows");
		}

		myTransform = transform;
		
		if  (myStaticBias == null) {
			myStaticBias = new float[transform[0].length];					
		} else {
			float[] newStaticBias = new float[transform[0].length];
			System.arraycopy(myStaticBias, 0, newStaticBias, 0, Math.min(myStaticBias.length, newStaticBias.length));
			myStaticBias = newStaticBias;
		}
		
		if (myDynamics != null && myDynamics.length != transform.length) {
			setDynamics(transform.length);
		}
	}
	
	public void setScaling(DecodedTermination t) {
		myScalingTermination = t;
	}
	
	public DecodedTermination getScaling() {
		return myScalingTermination;
	}

	/**
	 * @return The dynamics that govern each dimension of this Termination. Changing the properties 
	 * 		of the return value will change dynamics of all dimensions, effective next run time. 
	 */
	public LinearSystem getDynamics() {
		myDynamics = null; //caller may change properties so we'll have to re-clone at next run
		return myDynamicsTemplate;
	}
	
	/**
	 * @param dynamics New dynamics for each dimension of this Termination (effective immediately). 
	 * 		This method uses a clone of the given dynamics.  
	 */
	public void setDynamics(LinearSystem dynamics) {
		try {
			myDynamicsTemplate = (LinearSystem) dynamics.clone();
			setDynamics(myOutputDimension);

			//PSC time constant can be changed online if dynamics are LTI in controllable-canonical form 
			myTauMutable = (dynamics instanceof LTISystem && CanonicalModel.isControllableCanonical((LTISystem) dynamics)); 
			
			//find PSC time constant (slowest dynamic mode) if applicable 
			if (dynamics instanceof LTISystem) {
				myTau = CanonicalModel.getDominantTimeConstant((LTISystem) dynamics); 
			} else {
				myTau = 0;
			}
			
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return Slowest time constant of dynamics, if dynamics are LTI, otherwise 0
	 */
	public float getTau() {
		return myTau;
	}
	
	/**
	 * @param tau New time constant to replace current slowest time constant of dynamics
	 * @throws StructuralException if the dynamics of this Termination are not LTI in controllable
	 * 		canonical form
	 */
	public void setTau(float tau) throws StructuralException {
		if (!myTauMutable) {
			throw new StructuralException("This Termination has immutable dynamics "
				+ "(must be LTI in controllable-canonical form to change time constant online");
		}
		
		setDynamics(CanonicalModel.changeTimeConstant((LTISystem) myDynamicsTemplate, tau));
	}

	/**
	 * @see ca.neo.model.Termination#getModulatory()
	 */
	public boolean getModulatory() {
		return myModulatory;
	}

	/**
	 * @see ca.neo.model.Termination#setModulatory(boolean)
	 */
	public void setModulatory(boolean modulatory) {
		myModulatory = modulatory;
	}

	/** 
	 * @see ca.neo.model.Probeable#getHistory(java.lang.String)
	 */
	public TimeSeries getHistory(String stateName) throws SimulationException {
		if (stateName.equals(OUTPUT)) {
			return new TimeSeriesImpl(new float[]{myTime}, 
					new float[][]{myOutputValues}, Units.uniform(Units.UNK, myOutputValues.length));
		} else {
			throw new SimulationException("The state '" + stateName + "' is unknown");
		}
	}

	/** 
	 * @see ca.neo.model.Probeable#listStates()
	 */
	public Properties listStates() {
		Properties p = new Properties();
		p.setProperty(OUTPUT, "Output of the termination, after static transform and dynamics");
		return p;
	}

	/**
	 * @see ca.neo.model.Termination#getNode()
	 */
	public Node getNode() {
		return myNode;
	}
	
	protected void setNode(Node node) {
		myNode = node;
	}

	@Override
	public Termination clone() throws CloneNotSupportedException {
		try {
			DecodedTermination result = (DecodedTermination) super.clone();
			result.setTransform(MU.clone(myTransform));
			result.setDynamics((LinearSystem) myDynamicsTemplate.clone());
			result.myIntegrator = myIntegrator.clone();
			if (myInputValues != null) result.myInputValues = (RealOutput) myInputValues.clone();
			if (myOutputValues != null) result.myOutputValues = myOutputValues.clone();
			result.myScalingTermination = myScalingTermination; //refer to same copy
			result.myStaticBias = myStaticBias.clone();
			return result;
		} catch (StructuralException e) {
			throw new CloneNotSupportedException("Problem trying to clone: " + e.getMessage());
		}
	}

}
