package ca.neo.model.nef.impl;

import Jama.Matrix;

import ca.neo.dynamics.Integrator;
import ca.neo.dynamics.LinearSystem;
import ca.neo.dynamics.impl.CanonicalModel;
import ca.neo.dynamics.impl.LTISystem;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.RealOutput;
import ca.neo.model.Resettable;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
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
 * TODO: test
 * 
 * @author Bryan Tripp
 */
public class DecodedTermination implements Termination, Resettable {

	private static final long serialVersionUID = 1L;
	
	private String myName;
	private float[][] myTransform;
	private LinearSystem[] myDynamics;
	private Integrator myIntegrator;
	private Units[] myNullUnits;
	private float[] myInputValues; 
	private float[] myOutputValues;
	private boolean myTauMutable;
	private float myTau;
	private boolean myModulatory;
	
	/**
	 * @param name The name of this Termination
	 * @param transform A matrix that maps input (which has the dimension of this Termination)  
	 * 		onto the state space represented by the NEFEnsemble to which the Termination belongs
	 * @param dynamics Post-synaptic current dynamics (single-input single-output). Time-varying 
	 * 		dynamics are OK, but non-linear dynamics don't make sense here, because other 
	 * 		Terminations may input onto the same neurons. 
	 * @param integrator Numerical integrator with which to solve dynamics  
	 * @throws StructuralException If dynamics are not SISO or given transform is not a matrix 
	 */
	public DecodedTermination(String name, float[][] transform, LinearSystem dynamics, Integrator integrator) 
			throws StructuralException {
		
		if ( !MU.isMatrix(transform) ) {
			throw new StructuralException("Given transform is not a matrix");
		}
		
		if (dynamics.getInputDimension() != 1 || dynamics.getOutputDimension() != 1) {
			throw new StructuralException("Dynamics must be single-input single-output");
		}
		
		myName = name;
		myTransform = transform;
		myIntegrator = integrator;		
		
		setDynamics(dynamics, transform.length);

		//we save a little time by not reporting units to the dynamical system at each step
		myNullUnits = new Units[dynamics.getInputDimension()];
		
		//PSC time constant can be changed online if dynamics are LTI in controllable-canonical form 
		myTauMutable = (dynamics instanceof LTISystem && CanonicalModel.isControllableCanonical((LTISystem) dynamics)); 
		
		//find PSC time constant (slowest dynamic mode) if applicable 
		if (myTauMutable) {
			double[] eig = new Matrix(MU.convert(dynamics.getA(0f))).eig().getRealEigenvalues();
			
			double slowest = eig[0];
			for (int i = 1; i < eig.length; i++) {
				if (Math.abs(eig[i]) < Math.abs(slowest)) slowest = eig[i];
			}
			
			myTau = -1f / (float) slowest;
		}
		
		myModulatory = false;
	}

	//copies dynamics for to each dimension
	private synchronized void setDynamics(LinearSystem dynamics, int dimension) {
		LinearSystem[] newDynamics = new LinearSystem[dimension];
		for (int i = 0; i < newDynamics.length; i++) {
			try {
				newDynamics[i] = (LinearSystem) dynamics.clone();
				
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

		myInputValues = ((RealOutput) values).getValues();
	}
	
	/**
	 * @param startTime Simulation time at which running is to start
	 * @param endTime Simulation time at which running is to end
	 * @throws SimulationException
	 */
	public void run(float startTime, float endTime) throws SimulationException {
		if (myInputValues == null) {
			throw new SimulationException("Null input values on termination " + myName);
		}
		
		float[] dynamicsInputs = MU.prod(myTransform, myInputValues);
		float[] result = new float[dynamicsInputs.length];
		
		for (int i = 0; i < myDynamics.length; i++) {
			float[] inVal  = new float[]{dynamicsInputs[i]};
			TimeSeries inSeries = new TimeSeriesImpl(new float[]{startTime, endTime}, new float[][]{inVal, inVal}, myNullUnits);
			TimeSeries outSeries = myIntegrator.integrate(myDynamics[i], inSeries);
			result[i] = outSeries.getValues()[outSeries.getValues().length-1][0];
		}
		
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
	public float[] getInput() {
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
		for (int i = 0; i < myDynamics.length; i++) {
			myDynamics[i].setState(new float[myDynamics[i].getState().length]);			
		}
	}

	/**
	 * @see ca.neo.model.Termination#listPropertyNames()
	 */
	public String[] listPropertyNames() {
		if (myTauMutable) {
			return new String[]{Termination.TAU_PSC}; 
		} else {
			return new String[0];
		}
	}

	/**
	 * @see ca.neo.model.Termination#getProperty(java.lang.String)
	 */
	public String getProperty(String name) {
		if (myTauMutable && name.equals(Termination.TAU_PSC)) {
			return String.valueOf(myTau);
		} else if (name.equals(Termination.MODULATORY)) {
			return String.valueOf(myModulatory);
		} else {
			return new String();			
		}
	}

	/**
	 * @see ca.neo.model.Termination#setProperty(java.lang.String, java.lang.String)
	 */
	public void setProperty(String name, String value) throws StructuralException {
		if (name.equals(Termination.TAU_PSC)) {
			if (!myTauMutable) {
				throw new StructuralException("This Termination has immutable dynamics "
					+ "(must be LTI in controllable-canonical form to change time constant online");
			}

			try {
				float tau = Float.parseFloat(value);
				LTISystem dynamics = CanonicalModel.changeTimeConstant((LTISystem) myDynamics[0], tau);
				setDynamics(dynamics, myTransform.length);
			} catch (NumberFormatException e) {
				throw new StructuralException("Error setting property value", e);
			} 				
		} else if (name.equals(Termination.MODULATORY)) {
			myModulatory = (value.equalsIgnoreCase("true"));
		} else {
			throw new StructuralException("Property " + name + " is unknown");			
		}

	}
	
	/**
	 * @return The matrix that maps input (which has the dimension of this Termination)  
	 * 		onto the state space represented by the NEFEnsemble to which the Termination belongs
	 */
	public float[][] getTransform() {
		return myTransform;
	}

}
