/*
 * Created on 1-Dec-2006
 */
package ca.neo.math.impl;

import Jama.Matrix;
import ca.neo.math.CurveFitter;
import ca.neo.math.Function;
import ca.neo.model.Units;
import ca.neo.plot.Plotter;
import ca.neo.util.TimeSeries1D;
import ca.neo.util.impl.TimeSeries1DImpl;

/**
 * <p>A least-squares polynomial CurveFitter.</p> 
 * 
 * <p>See http://mathworld.wolfram.com/LeastSquaresFittingPolynomial.html </p>  
 * 
 * TODO: write proper tests
 * 
 * @author Bryan Tripp
 */
public class PolynomialCurveFitter implements CurveFitter {

	private int myOrder;
	
	/**
	 * @param order Order of polynomials used to approximate example points 
	 */
	public PolynomialCurveFitter(int order) {
		myOrder = order;
	}

	/**
	 * @see ca.neo.math.CurveFitter#fit(float[], float[])
	 */
	public Function fit(float[] x, float[] y) {		
		if (x.length != y.length) {
			throw new IllegalArgumentException("Arrays x and y must have the same length; we take it that y = f(x)");
		}
		
		Matrix X = new Matrix(x.length, myOrder+1);
		Matrix Y = new Matrix(y.length, 1);
		for (int i = 0; i < x.length; i++) {
			X.set(i, 0, 1d);
			Y.set(i, 0, (double) y[i]);
			
			float xpowj = x[i];
			for (int j = 1; j < myOrder+1; j++) {
				X.set(i, j, (double) xpowj);
				xpowj = xpowj*x[i];
			}
		}
		
		//Note: this is the form given on Mathworld but don't see the advantage
		//Matrix XTX = X.transpose().times(X);
		//Matrix A = XTX.inverse().times(X.transpose()).times(Y);
		
		Matrix A = X.inverse().times(Y);
		
		float[] coefficients = new float[myOrder+1];
		for (int i = 0; i < coefficients.length; i++) {
			coefficients[i] = (float) A.get(i,0);
		}
		
		return new Polynomial(coefficients);
	}

	/**
	 * @return Order of polynomials used to approximate points (eg 1 corresponds to linear 
	 * 		approximation, 2 to quadratic, etc) 
	 */
	public int getOrder() {
		return myOrder;
	}

	@Override
	public CurveFitter clone() throws CloneNotSupportedException {
		return (CurveFitter) super.clone();
	}

	//test code (to move to unit test)
	public static void main(String[] args) {
		PolynomialCurveFitter fitter = new PolynomialCurveFitter(2);
		
		float[] examplex = new float[]{1f, 2f, 3f, 4f};
		float[] exampley = new float[]{3f, 2f, 1f, 2f};
		Function f = fitter.fit(examplex, exampley);
		
		float[] x = new float[50];
		float[] y = new float[50];
		float dx = 0.1f;
		for (int i = 0; i < x.length; i++) {
			x[i] = i*dx;
			y[i] = f.map(new float[]{x[i]});
		}
		
		TimeSeries1D approx = new TimeSeries1DImpl(x, y, Units.UNK);
		TimeSeries1D actual = new TimeSeries1DImpl(examplex, exampley, Units.UNK);
		Plotter.plot(approx, actual, "polynomial");
	}
}
