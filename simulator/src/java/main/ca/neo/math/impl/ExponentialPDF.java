/*
 * Created on 19-May-07
 */
package ca.neo.math.impl;

import ca.neo.config.ConfigUtil;
import ca.neo.config.Configuration;
import ca.neo.math.PDF;
import ca.neo.plot.Plotter;

/**
 * A one-dimensional exponential probability density function.
 * 
 * TODO: unit tests
 * TODO: generalize to any function with invertible integral (see numerical recipies in C chapter 7)
 * 
 * @author Bryan Tripp
 */
public class ExponentialPDF extends AbstractFunction implements PDF {

	private static final long serialVersionUID = 1L;

	private float myTau;
	
	public ExponentialPDF(float tau) {
		super(1);
		myTau = tau;
	}
	
	public ExponentialPDF(){
		this(1);
	}
	
	public float getTau() {
		return myTau;
	}
	
	public void setTau(float tau) {
		myTau = tau;
	}

	/**
	 * @see ca.neo.math.impl.AbstractFunction#map(float[])
	 */
	public float map(float[] from) {
		return from[0] >= 0 ? (1f/myTau) * (float) Math.exp(-from[0]/myTau) : 0;
	}

	/**
	 * @see ca.neo.math.PDF#sample()
	 */
	public float[] sample() {
		double x = 0;
		do {
			x = Math.random();
		} while (x == 0);
		
		return new float[]{myTau * (float) -Math.log(x)}; 
	}
	
	//functional test ... 
	public static void main(String[] args) {
		PDF pdf = new ExponentialPDF(.1f);
		Plotter.plot(pdf, 0, .001f, .5f, "aagg");
		
		float binSize = .05f; 
		float[] bins = new float[8];
		for (int i = 0; i < 10000; i++) {
			float foo = pdf.sample()[0];
			int bin = (int) Math.floor(foo / binSize);
			if (bin <= 7) bins[bin] += 1;
		}
		
		Plotter.plot(bins, "bins");
	}

}
