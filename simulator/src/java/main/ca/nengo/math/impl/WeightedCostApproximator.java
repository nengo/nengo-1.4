/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "WeightedCostApproximator.java". Description: 
"A LinearApproximator in which error is evaluated at a fixed set of points, and  
  the cost function that is minimized is a weighted integral of squared error"

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
 * Created on 5-Jun-2006
 */
package ca.nengo.math.impl;

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

import java.util.Random;

import org.apache.log4j.Logger;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import ca.nengo.math.ApproximatorFactory;
import ca.nengo.math.Function;
import ca.nengo.math.LinearApproximator;
import ca.nengo.util.MU;
import ca.nengo.util.Memory;

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
	private float[][] myNoisyValues;
	private Function myCostFunction;
	private boolean myQuiet;
	
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
	public WeightedCostApproximator(float[][] evaluationPoints, float[][] values, Function costFunction, float noise, int nSV, boolean quiet) {
		assert MU.isMatrix(evaluationPoints);
		assert MU.isMatrix(values);
		assert evaluationPoints.length == values[0].length;
		
		myEvalPoints = evaluationPoints;
		myValues = MU.clone(values);
		myNoisyValues = MU.clone(values);
		myQuiet = quiet;
		float absNoiseSD = addNoise(myNoisyValues, noise);
		
		myCostFunction = costFunction;
		
		if(!myQuiet)
			Memory.report("before gamma");
		
		double[][] gamma = findGamma();
		if(!myQuiet)
			Memory.report("before inverse");
		myGammaInverse = pseudoInverse(gamma, absNoiseSD*absNoiseSD, nSV);
		if(!myQuiet)
			Memory.report("after inverse");
		
		//testPlot(evaluationPoints, values);
	}
	public WeightedCostApproximator(float[][] evaluationPoints, float[][] values, Function costFunction, float noise, int nSV) {
		this(evaluationPoints, values, costFunction, noise, nSV, false);
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

	/**
	 * @see ca.nengo.math.LinearApproximator#getEvalPoints()
	 */
	public float[][] getEvalPoints() {
		return myEvalPoints;
	}

	/**
	 * @see ca.nengo.math.LinearApproximator#getValues()
	 */
	public float[][] getValues() {
		return myValues;
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
		double[][] result=null;
		
		Random random=new Random();
		
		Runtime runtime=Runtime.getRuntime();
		int hashCode=java.util.Arrays.hashCode(matrix);
		try {
			// TODO: separate this out into a helper method, so we can do this sort of thing for other calculations as well
			String parent=System.getProperty("user.dir");
			java.io.File path=new java.io.File(parent,"external");
			String filename="matrix_"+random.nextLong();
			
			java.io.File file=new java.io.File(path,filename);
			if (file.canRead()) file.delete();
			java.io.File file2=new java.io.File(path,filename+".inv");
			if (file2.canRead()) file2.delete();
			
			java.nio.channels.FileChannel channel=new java.io.RandomAccessFile(file,"rw").getChannel();			
			java.nio.ByteBuffer buffer=channel.map(java.nio.channels.FileChannel.MapMode.READ_WRITE, 0, matrix.length*matrix.length*4);	
			buffer.order(java.nio.ByteOrder.BIG_ENDIAN);
			for (int i=0; i<matrix.length; i++) {
				for (int j=0; j<matrix.length; j++) {
					buffer.putFloat((float)(matrix[i][j]));
				}
			}
			channel.force(true);
            channel.close();
            
			if (System.getProperty("os.name").startsWith("Windows")) {
				Process process=runtime.exec("cmd /c pseudoInverse.bat "+filename+" "+filename+".inv"+" "+minSV+" "+nSV,null,path);
				process.waitFor();
			} else {
				Process process=runtime.exec("."+java.io.File.separatorChar+"pseudoInverse "+filename+" "+filename+".inv"+" "+minSV+" "+nSV,null,path);
				process.waitFor();
			}
			
			channel=new java.io.RandomAccessFile(file2,"r").getChannel();			
			buffer=channel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, matrix.length*matrix.length*4);			
			double[][] inv=new double[matrix.length][];
			for (int i=0; i<matrix.length; i++) {
				double[] row=new double[matrix.length];
				for (int j=0; j<matrix.length; j++) {
					row[j]=buffer.getFloat();
				}
				inv[i]=row;
			}
			result=inv;

			file=new java.io.File(path,filename);
			if (file.canRead()) file.delete();
			file2=new java.io.File(path,filename+".inv");
			if (file2.canRead()) file2.delete();
			
            // Close all file handles
            channel.close();
		} catch (java.io.IOException e) {
			//e.printStackTrace();
            System.err.println("WeightedCostApproximator.psuedoInverse() - IO Exception: " + e);
		} catch (InterruptedException e) {
            System.err.println("WeightedCostApproximator.psuedoInverse() - Interrupted: " + e);
			//e.printStackTrace();		
		} catch (Exception e){
            System.err.println("WeightedCostApproximator.psuedoInverse() - Gen Exception: " + e);
            //e.printStackTrave();
        }
				
		if (result==null) {
			
			Matrix m = new Matrix(matrix);		
			SingularValueDecomposition svd = m.svd();
			Matrix sInv = svd.getS().inverse();
	
			int i = 0; 
			while (i < svd.getS().getRowDimension() && svd.getS().get(i, i) > minSV && (nSV <= 0 || i < nSV)) i++;
	
			if(!myQuiet)
				ourLogger.info("Using " + i + " singular values for pseudo-inverse");
			
			for (int j = i; j < matrix.length; j++) {
				sInv.set(j, j, 0d);
			}
			
			result = svd.getV().times(sInv).times(svd.getU().transpose()).getArray();
						
		}
		
		return result;
	}
	
	/**
	 * <p>This implementation is adapted from Eliasmith & Anderson, 2003, appendix A.</p> 
	 * 
	 * <p>It solves PHI = GAMMA" UPSILON, where " denotes pseudoinverse, UPSILON_i = < cost(x) x a_i(x) >,  
	 * and GAMMA_ij = < cost(x) a_i(x) a_j(x) >. <> denotes integration (the sum over eval points). </p>  
	 * 
	 * @see ca.nengo.math.LinearApproximator#findCoefficients(ca.nengo.math.Function)
	 */
	public float[] findCoefficients(Function target) {
		float[] targetValues = new float[myEvalPoints.length];
		for (int i = 0; i < targetValues.length; i++) {
			targetValues[i] = target.map(myEvalPoints[i]);
		}
		
		float[] upsilon = new float[myNoisyValues.length];
		for (int i = 0; i < myNoisyValues.length; i++) {
			for (int j = 0; j < myEvalPoints.length; j++) {
				upsilon[i] += myNoisyValues[i][j] * targetValues[j] * myCostFunction.map(myEvalPoints[j]);
			}
			upsilon[i] = upsilon[i] / myEvalPoints.length;
		}
		
		float[] result = new float[myNoisyValues.length];
		for (int i = 0; i < myNoisyValues.length; i++) {
			for (int j = 0; j < myNoisyValues.length; j++) {
				result[i] += myGammaInverse[i][j] * upsilon[j];
			}
		}
		
		return result;
	}
	
	private double[][] findGamma() {
		double[][] result = new double[myNoisyValues.length][];
		
		for (int i = 0; i < result.length; i++) {
			result[i] = new double[myNoisyValues.length];
			for (int j = 0; j < result[i].length; j++) {
				for (int k = 0; k < myEvalPoints.length; k++) {
					result[i][j] += myNoisyValues[i][k] * myNoisyValues[j][k] * myCostFunction.map(myEvalPoints[k]);						
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
		result.myNoisyValues = MU.clone(myNoisyValues);
		
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
		private boolean myQuiet;
		
		/**
		 * @param noise Random noise to add to component functions (proportion of largest value over all functions) 
		 */
		public Factory(float noise) {
			this(noise, -1, false);
		}
		
		public Factory(float noise, boolean quiet) {
			this(noise, -1, quiet);
		}

		public Factory(float noise, int NSV) {
			this(noise, NSV, false);
		}
		
		public Factory(float noise, int NSV, boolean quiet) {
			myNoise = noise;
			myNSV = NSV;
			myQuiet = quiet;
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
		 * @return Maximum number of singular values to use in pseudoinverse of correlation matrix (zero or less means 
		 * 		use as many as possible to a threshold magnitude determined by noise).   
		 */
		public int getNSV() {
			return myNSV;
		}

		/**
		 * @param nSV Maximum number of singular values to use in pseudoinverse of correlation matrix (zero or less means 
		 * 		use as many as possible to a threshold magnitude determined by noise).
		 */
		public void setNSV(int nSV) {
			myNSV = nSV;
		}
		
		/**
		 * @return Whether or not information will be printed out to console during make process.
		 * 
		 */
		public boolean getQuiet() {
			return(myQuiet);
		}
		
		/**
		 * @param quiet Controls whether or not information will be printed out to console during make process.
		 * 
		 */
		public void setQuiet(boolean quiet) {
			myQuiet = quiet;
		}
		
		
		/**
		 * @see ca.nengo.math.ApproximatorFactory#getApproximator(float[][], float[][])
		 */
		public LinearApproximator getApproximator(float[][] evalPoints, float[][] values) {
			return new WeightedCostApproximator(evalPoints, values, getCostFunction(evalPoints[0].length), myNoise, myNSV, myQuiet);
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
