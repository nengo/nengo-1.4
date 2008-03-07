/*
 * Created on 5-Jun-2006
 */
package ca.neo.math.impl;

//import java.awt.BorderLayout;
//
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.data.xy.XYSeries;
//import org.jfree.data.xy.XYSeriesCollection;

import org.apache.log4j.Logger;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import ca.neo.math.ApproximatorFactory;
import ca.neo.math.Function;
import ca.neo.math.LinearApproximator;
import ca.neo.util.MU;
import ca.neo.util.Memory;

/**
 * <p>A LinearApproximator in which error is evaluated at a fixed set of points, and  
 * the cost function that is minimized is a weighted integral of squared error.</p>  
 * 
 * <p>Uses the Moore-Penrose pseudoinverse.</p>
 *  
 * TODO: test
 * 
 * @author Bryan Tripp
 */
public class WeightedCostApproximator implements LinearApproximator {

	private static Logger ourLogger = Logger.getLogger(WeightedCostApproximator.class);
	private static final long serialVersionUID = 1L;
	
	private float[][] myEvalPoints;
	private float[][] myValues;
	private Function myCostFunction;
	
	private double[][] myGammaInverse; 
	
	/**
	 * @param evaluationPoints Points at which error is evaluated (should be uniformly 
	 * 		distributed, as the sum of error at these points is treated as an integral
	 * 		over the domain of interest). Examples include vector inputs to an ensemble, 
	 * 		or different points in time	within different simulation regimes.  
	 * @param values The values of whatever functions are being combined, at the 
	 * 		evaluationPoints. Commonly neuron firing rates. The first dimension makes up 
	 * 		the list of functions, and the second the values of these functions at each 
	 * 		evaluation point. 
	 * @param costFunction A cost function that weights squared error over the domain of 
	 * 		evaluation points
	 * @param noise Standard deviation of Gaussian noise to add to values (to reduce 
	 * 		sensitivity to simulation noise) as a proportion of the maximum absolute 
	 * 		value over all values  
	 */
	public WeightedCostApproximator(float[][] evaluationPoints, float[][] values, Function costFunction, float noise, int nSV) {
		assert MU.isMatrix(evaluationPoints);
		assert MU.isMatrix(values);
		assert evaluationPoints.length == values[0].length;

		myEvalPoints = evaluationPoints;
		myValues = values;
		float absNoiseSD = addNoise(myValues, noise);
		
		myCostFunction = costFunction;
		
		Memory.report("before gamma");
		
		double[][] gamma = findGamma();
		Memory.report("before inverse");
		myGammaInverse = pseudoInverse(gamma, absNoiseSD*absNoiseSD, nSV);
		Memory.report("after inverse");
		
		//testPlot(evaluationPoints, values);
	}
	
	private float addNoise(float[][] values, float noise) {
		float maxValue = 0f;
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[i].length; j++) {
				if (Math.abs(values[i][j]) > maxValue) maxValue = Math.abs(values[i][j]); 				
			}
		}

		float SD = noise * maxValue;
		GaussianPDF pdf = new GaussianPDF(0f, SD*SD);
		
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[i].length; j++) {
				values[i][j] += pdf.sample()[0];
			}
		}
		
		return SD;
	}
	
//	private static void testPlot(float[][] evaluationPoints, float[][] values) {
//		XYSeriesCollection dataset = new XYSeriesCollection();
//		for (int i = 0; i < values.length; i++) {
//			XYSeries series = new XYSeries("" + i);
//			for (int j = 0; j < evaluationPoints.length; j++) {
//				series.add(evaluationPoints[j][0], values[i][j]);
//			}
//			dataset.addSeries(series);
//		}
//		JFreeChart chart = ChartFactory.createXYLineChart(
//				"Approximator Activities",
//				"X", 
//				"Firing Rate (spikes/s)", 
//				dataset, 
//				PlotOrientation.VERTICAL, 
//				false, false, false
//		);
//		JPanel panel = new ChartPanel(chart);
//		JFrame frame = new JFrame("Approximator");
//		frame.getContentPane().add(panel, BorderLayout.CENTER);
//        frame.pack();
//        frame.setVisible(true);		
//	}
	
	/**
	 * Override this method to use a different pseudoinverse implementation (eg clustered).
	 *    
	 * @param matrix Any matrix
	 * @param minSV Hint as to smallest singular value to use 
	 * @param nSV Max number of singular values to use
	 * @return The pseudoinverse of the given matrix
	 */
	public double[][] pseudoInverse(double[][] matrix, float minSV, int nSV) {
		double[][] result;
		
		Matrix m = new Matrix(matrix);
		SingularValueDecomposition svd = m.svd();
		Matrix sInv = svd.getS().inverse();

		int i = 0; 
		while (i < svd.getS().getRowDimension() && svd.getS().get(i, i) > minSV && (nSV <= 0 || i < nSV)) i++;

		ourLogger.info("Using " + i + " singular values for pseudo-inverse");
		
		for (int j = i; j < matrix.length; j++) {
			sInv.set(j, j, 0d);
		}
		
		result = svd.getV().times(sInv).times(svd.getU().transpose()).getArray();
		
		return result;
	}
	
	/**
	 * <p>This implementation is adapted from Eliasmith & Anderson, 2003, appendix A.</p> 
	 * 
	 * <p>It solves PHI = GAMMA" UPSILON, where " denotes pseudoinverse, UPSILON_i = < cost(x) x a_i(x) >,  
	 * and GAMMA_ij = < cost(x) a_i(x) a_j(x) >. <> denotes integration (the sum over eval points). </p>  
	 * 
	 * @see ca.neo.math.LinearApproximator#findCoefficients(ca.neo.math.Function)
	 */
	public float[] findCoefficients(Function target) {
		float[] targetValues = new float[myEvalPoints.length];
		for (int i = 0; i < targetValues.length; i++) {
			targetValues[i] = target.map(myEvalPoints[i]);
		}
		
		float[] upsilon = new float[myValues.length];
		for (int i = 0; i < myValues.length; i++) {
			for (int j = 0; j < myEvalPoints.length; j++) {
				upsilon[i] += myValues[i][j] * targetValues[j] * myCostFunction.map(myEvalPoints[j]);
			}
			upsilon[i] = upsilon[i] / myEvalPoints.length;
		}
		
		float[] result = new float[myValues.length];
		for (int i = 0; i < myValues.length; i++) {
			for (int j = 0; j < myValues.length; j++) {
				result[i] += myGammaInverse[i][j] * upsilon[j];
			}
		}
		
		return result;
	}
	
	private double[][] findGamma() {
		double[][] result = new double[myValues.length][];
		
		for (int i = 0; i < result.length; i++) {
			result[i] = new double[myValues.length];
			for (int j = 0; j < result[i].length; j++) {
				for (int k = 0; k < myEvalPoints.length; k++) {
					result[i][j] += myValues[i][k] * myValues[j][k] * myCostFunction.map(myEvalPoints[k]);						
				}
				result[i][j] = result[i][j] / myEvalPoints.length;
			}
		}
		
		return result;
	}
	
	@Override
	public LinearApproximator clone() throws CloneNotSupportedException {
		WeightedCostApproximator result = (WeightedCostApproximator) super.clone();
		
		result.myCostFunction = myCostFunction.clone();
		result.myEvalPoints = MU.clone(myEvalPoints);
		result.myValues = MU.clone(myValues);
		
		result.myGammaInverse = new double[myGammaInverse.length][];
		for (int i = 0; i < myGammaInverse.length; i++) {
			result.myGammaInverse[i] = myGammaInverse[i].clone();
		}
		
		return result;
	}

	/**
	 * An ApproximatorFactory that produces WeightedCostApproximators. 
	 * 
	 * @author Bryan Tripp
	 */
	public static class Factory implements ApproximatorFactory {

		private static final long serialVersionUID = -3390244062379730498L;
		
		private float myNoise;
		private int myNSV;
		
		/**
		 * @param noise Random noise to add to component functions (proportion of largest value over all functions) 
		 */
		public Factory(float noise) {
			myNoise = noise;
			myNSV = -1;
		}
		
		/**
		 * @return Random noise to add to component functions (proportion of largest value over all functions)
		 */
		public float getNoise() {
			return myNoise;
		}

		/**
		 * @param noise Random noise to add to component functions (proportion of largest value over all functions)
		 */
		public void setNoise(float noise) {
			myNoise = noise;
		}
		
		/**
		 * @return Maximum number of singular values to use in psuedoinverse of correlation matrix (zero or less means 
		 * 		use as many as possible to a threshold magnitude determined by noise).   
		 */
		public int getNSV() {
			return myNSV;
		}

		/**
		 * @param nSV Maximum number of singular values to use in psuedoinverse of correlation matrix (zero or less means 
		 * 		use as many as possible to a threshold magnitude determined by noise).
		 */
		public void setNSV(int nSV) {
			myNSV = nSV;
		}

		/**
		 * @see ca.neo.math.ApproximatorFactory#getApproximator(float[][], float[][])
		 */
		public LinearApproximator getApproximator(float[][] evalPoints, float[][] values) {
			return new WeightedCostApproximator(evalPoints, values, getCostFunction(evalPoints[0].length), myNoise, myNSV);
		}

		/**
		 * Note: override to use non-uniform error weighting. 
		 * 
		 * @param dimension Dimension of the function to be approximated
		 * @return A function over the input space that defines relative importance of error at each point (defaults 
		 * 		to a ConstantFunction) 
		 */
		public Function getCostFunction(int dimension) {
			return new ConstantFunction(dimension, 1); 
		}

		@Override
		public ApproximatorFactory clone() throws CloneNotSupportedException {
			return (ApproximatorFactory) super.clone();
		}
		
	}
	
}
