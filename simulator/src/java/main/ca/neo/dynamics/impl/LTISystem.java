/*
 * Created on 1-Jun-2006
 */
package ca.neo.dynamics.impl;

import ca.neo.dynamics.LinearSystem;
import ca.neo.model.Units;
import ca.neo.util.MU;

/**
 * A linear time-invariant dynamical system model in state-space form. Such a system 
 * can be defined in terms of the four matrices that must be provided in the contructor. 
 *  
 * TODO: test
 * 
 * @author Bryan Tripp
 */
public class LTISystem implements LinearSystem {

	private static final long serialVersionUID = 1L;
	
	private float[][] A;
	private float[][] B;
	private float[][] C;
	private float[][] D;
	private float[] x;
	private Units[] myOutputUnits;
	
	/**
	 * Each argument is an array of arrays that represents a matrix. The first 
	 * dimension represents the matrix row and the second the matrix column, so  
	 * that A_ij corresponds to A[i-1][j-1] (since arrays are indexed from 0).
	 * 
	 * The matrices must have valid dimensions for a state-space model: A must be 
	 * n x n; B must be n x p; C must be q x n; and D must be q x p.  
	 *  
	 * @param A Dynamics matrix
	 * @param B Input matrix
	 * @param C Output matrix
	 * @param D Passthrough matrix
	 * @param x0 Initial state
	 * @param outputUnits Units in which each dimension of the output are expressed 
	 */
	public LTISystem(float[][] A, float[][] B, float[][] C, float[][] D, float[] x0, Units[] outputUnits) {
		checkIsStateModel(A, B, C, D, x0);
		if (outputUnits.length != C.length) {
			throw new IllegalArgumentException("Units needed for each output");
		}

		this.A = A;
		this.B = B;
		this.C = C;
		this.D = D;
		this.x = x0;
		this.myOutputUnits = outputUnits;
	}
	
	//checks that matrices have the dimensions that form a valid state model 
	private static void checkIsStateModel(float[][] A, float[][] B, float[][] C, float[][] D, float[] x) {
		checkIsMatrix(A);
		checkIsMatrix(B);
		checkIsMatrix(C);
		checkIsMatrix(D);

		checkSameDimension(A.length, A[0].length, "A matrix must be square");
		checkSameDimension(x.length, A.length, "A matrix must be nXn, where n is length of state vector");
		checkSameDimension(A.length, B.length, "Numbers of rows in A and B must be the same");
		checkSameDimension(A[0].length, C[0].length, "Numbers of columns in A and C must be the same");
		checkSameDimension(D.length, C.length, "Numbers of rows in C and D must be the same");
		checkSameDimension(D[0].length, B[0].length, "Numbers of columns in B and D must be the same");
	}
	
	private static void checkIsMatrix(float[][] matrix) {
		if (!MU.isMatrix(matrix)) {
			throw new IllegalArgumentException("Not all matrix rows have the same length");
		}
	}
	
	private static void checkSameDimension(int one, int two, String message) {
		if (one != two) {
			throw new IllegalArgumentException(message);
		}
	}
	
	/**
	 * @return Ax + Bu
	 * 
	 * @see ca.neo.dynamics.DynamicalSystem#f(float[], float, float[])
	 */
	public float[] f(float t, float[] u) {
		assert u.length == getInputDimension();		
		
		return A1x1plusA2x2(A, x, B, u);
	}

	/**
	 * @return Cx + Du
	 * 
	 * @see ca.neo.dynamics.DynamicalSystem#g(float[], float, float[])
	 */
	public float[] g(float t, float[] u) {
		assert u.length == getInputDimension(); 
		
		return A1x1plusA2x2(C, x, D, u);
	}
	
	//does not check dimensions -- we leave this to prior assertion of dimensionsOK(...) 
	private static float[] A1x1plusA2x2(float[][] A1, float[] x1, float[][] A2, float[] x2) {
		float[] result = new float[A1.length];
		
		for (int i = 0; i < A1.length; i++) {
			for (int j = 0; j < A1[0].length; j++) {
				result[i] += A1[i][j] * x1[j];
			}
			for (int j = 0; j < A2[0].length; j++) {
				result[i] += A2[i][j] * x2[j];
			}
		}
		
		return result;
	}
	
	/**
	 * @see ca.neo.dynamics.DynamicalSystem#getState()
	 */
	public float[] getState() {
		return x;
	}
	
	/**
	 * @see ca.neo.dynamics.DynamicalSystem#setState(float[])
	 */
	public void setState(float[] state) {
		assert state.length == x.length;
		
		x = state;
	}

	/**
	 * @see ca.neo.dynamics.DynamicalSystem#getInputDimension()
	 */
	public int getInputDimension() {
		return B[0].length;
	}

	/**
	 * @see ca.neo.dynamics.DynamicalSystem#getOutputDimension()
	 */
	public int getOutputDimension() {
		return C.length;
	}

	/**
	 * @see ca.neo.dynamics.DynamicalSystem#getOutputUnits(int)
	 */
	public Units getOutputUnits(int outputDimension) {
		return myOutputUnits[outputDimension];
	}

	/**
	 * @see ca.neo.dynamics.LinearSystem#getA(float)
	 */
	public float[][] getA(float t) {
		return MU.clone(A);
	}

	/**
	 * @see ca.neo.dynamics.LinearSystem#getB(float)
	 */
	public float[][] getB(float t) {
		return MU.clone(B);
	}

	/**
	 * @see ca.neo.dynamics.LinearSystem#getC(float)
	 */
	public float[][] getC(float t) {
		return MU.clone(C);
	}

	/**
	 * @see ca.neo.dynamics.LinearSystem#getD(float)
	 */
	public float[][] getD(float t) {
		return MU.clone(D);
	}

	/**
	 * @see ca.neo.dynamics.DynamicalSystem#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		LTISystem result = (LTISystem) super.clone();
		
		float[] state = new float[result.getState().length];
		System.arraycopy(result.getState(), 0, state, 0, state.length);
		result.setState(state);
		
		return result;
	}
	
}
