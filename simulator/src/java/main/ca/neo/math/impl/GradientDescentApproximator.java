package ca.neo.math.impl;

import java.io.Serializable;

import org.apache.log4j.Logger;

import ca.neo.math.ApproximatorFactory;
import ca.neo.math.Function;
import ca.neo.math.LinearApproximator;
import ca.neo.util.MU;

/**
 * A LinearApproximator that searches for coefficients by descending an error gradient. 
 * This method is slower and less powerful than WeightedCostApproximator, but 
 * constraints on coefficients are allowed.  
 *  
 * @author Bryan Tripp 
 */
public class GradientDescentApproximator implements LinearApproximator {

	private static Logger ourLogger = Logger.getLogger(GradientDescentApproximator.class);
	private static final long serialVersionUID = 1L;

	private float[][] myEvalPoints;
	private float[][] myValues;
	private float[] myStartingCoefficients;
	private Constraints myConstraints;
	private int myMaxIterations;
	private float myRate;
	private float myTolerance;
	private boolean myIgnoreBias;
	
	/**
	 * @param evaluationPoints Points at which error is evaluated (should be uniformly 
	 * 		distributed, as the sum of error at these points is treated as an integral
	 * 		over the domain of interest). Examples include vector inputs to an ensemble, 
	 * 		or different points in time	within different simulation regimes.  
	 * @param values The values of whatever functions are being combined, at the 
	 * 		evaluationPoints. Commonly neuron firing rates. The first dimension makes up 
	 * 		the list of functions, and the second the values of these functions at each 
	 * 		evaluation point.
	 * @param constraints Constraints on coefficients
	 * @param ignoreBias If true, bias in constituent and target functions is ignored (resulting 
	 * 		estimate will be biased) 
	 */
	public GradientDescentApproximator(float[][] evaluationPoints, float[][] values, Constraints constraints, boolean ignoreBias) {
		assert MU.isMatrix(evaluationPoints);
		assert MU.isMatrix(values);
		assert evaluationPoints.length == values[0].length;

		myEvalPoints = evaluationPoints;
		myValues = values;
		myConstraints = constraints;
		myMaxIterations = 1000;
		myStartingCoefficients = new float[values.length];
		myRate = 0.5f / (float) myValues.length;
		myTolerance = .000000001f;
		
		myIgnoreBias = ignoreBias;
		if (ignoreBias) {
			for (int i = 0; i < myValues.length; i++) {
				myValues[i] = unbias(myValues[i]);
			}
		}
	}
	
	/**
	 * @return Maximum iterations per findCoefficients(...)
	 */
	public int getMaxIterations() {
		return myMaxIterations;
	}
	
	/**
	 * @param max New maximum number of iterations per findCoefficients(...)
	 */
	public void setMaxIterations(int max) {
		myMaxIterations = max;
	}
	
	/**
	 * @return Target mean-squared error 
	 */
	public float getTolerance()  {
		return myTolerance;
	}
	
	/**
	 * @param tolerance Target mean-squared error 
	 */
	public void setTolerance(float tolerance) {
		myTolerance = tolerance;
	}

	/**
	 * @see ca.neo.math.LinearApproximator#findCoefficients(ca.neo.math.Function)
	 */
	public float[] findCoefficients(Function target) {
		float[] result = new float[myValues.length];
		System.arraycopy(myStartingCoefficients, 0, result, 0, result.length);
		
		float[] targetValues = getTargetValues(target);
		
		boolean stuck = false;
		boolean done = false;
		float[] error = findError(targetValues, result);
		for (int i = 0; i < myMaxIterations && !stuck && !done; i++) {
			for (int j = 0; j < myValues.length; j++) {
				float norm = MU.prod(myValues[j], myValues[j]);
				if (norm > 0) {
					result[j] -= myRate * MU.prod(error, myValues[j]) / norm; //(float) myEvalPoints.length;					
				}
			}
			stuck = myConstraints.correct(result);
			
			error = findError(targetValues, result);
			float mse = MU.prod(error, error) / (float) error.length;
			done = mse < myTolerance;
			ourLogger.debug("Iteration: " + i + "  MSE: " + mse + " Stuck: " + stuck);
		}
		
		return result;
	}
	
	//finds values of target function at eval points
	private float[] getTargetValues(Function target) {
		float[] result = new float[myEvalPoints.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = target.map(myEvalPoints[i]);
		}
		if (myIgnoreBias) result = unbias(result);
		return result;
	}
	
	//finds approximation error with given coefficients and target function values
	//we don't use matrix ops here because a a transpose would be needed
	private float[] findError(float[] target, float[] coefficients) {
		float[] result = new float[target.length];
		for (int i = 0; i < result.length; i++) {
			float estimate = 0f;
			for (int j = 0; j < myValues.length; j++) {
				estimate += myValues[j][i] * coefficients[j];
			}
			result[i] = estimate - target[i];
		}
		return result;
	}
	
	//removes bias
	private float[] unbias(float[] x) {
		float sum = 0;
		for (int i = 0; i < x.length; i++) {
			sum += x[i];
		}
		float bias = sum / (float) x.length;
		
		float[] result = new float[x.length];
		for (int i = 0; i < x.length; i++) {
			result[i] = x[i] - bias;
		}
		
		return result;
	}

	/**
	 * Enforces constraints on coefficients. 
	 * 
	 * TODO: should this be generalized to LinearApproximator? 
	 * 
	 * @author Bryan Tripp 
	 */
	public static interface Constraints extends Serializable {
		
		/**
		 * @param coefficients A set of coefficients which may violate constraints (they 
		 * 		are altered as little as possible by this method so that they satisfy 
		 * 		constraints after the call)
		 * @return True if all coefficients had to be corrected (no further improvement 
		 * 		is possible in the attempted direction)
		 */
		boolean correct(float[] coefficients);
	}
	
	/**
	 * An ApproximatorFactory that produces GradientDescentApproximators. 
	 * 
	 * @author Bryan Tripp
	 */
	public static class Factory implements ApproximatorFactory {

		private static final long serialVersionUID = 1L;
		
		private Constraints myConstraints;
		private boolean myIgnoreBiasFlag;

		/**
		 * @param constraints As in GradientDescentApproximator constructor
		 * @param ignoreBias As in GradientDescentApproximator constructor
		 */
		public Factory(Constraints constraints, boolean ignoreBias) {
			myConstraints = constraints;
			myIgnoreBiasFlag = ignoreBias;
		}
		
		/**
		 * @see ca.neo.math.ApproximatorFactory#getApproximator(float[][], float[][])
		 */
		public LinearApproximator getApproximator(float[][] evalPoints, float[][] values) {
			return new GradientDescentApproximator(evalPoints, values, myConstraints, myIgnoreBiasFlag);
		}
		
	}
}
