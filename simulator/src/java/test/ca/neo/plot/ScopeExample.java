/*
 * Created on 11-Feb-08
 */
package ca.neo.plot;

import ca.neo.math.Function;
import ca.neo.math.FunctionBasis;
import ca.neo.math.impl.FunctionBasisImpl;
import ca.neo.math.impl.GaussianPDF;
import ca.neo.math.impl.PostfixFunction;
import ca.neo.math.impl.SigmoidFunction;
import ca.neo.model.Network;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.util.Probe;

/**
 * Example data for scope display. 
 * 
 * @author Bryan Tripp
 */
public class ScopeExample {

	public static void main(String[] args) {
		
		try {
			Network network = new NetworkImpl();
			
			//this "function input" is Probeable ... 		
			Function f1 = new PostfixFunction("sin(x0)", 1);
			Function f2 = new PostfixFunction("sin(x0)^2", 1);
			String name = "functions of time";
			FunctionInput fi = new FunctionInput(name, new Function[]{f1, f2}, Units.uAcm2);
			network.addNode(fi);
			
			//we can add a probe to it and run the simulator ... 
			Probe p = network.getSimulator().addProbe(name, FunctionInput.STATE_NAME, true);			
			network.run(0, 10);
			
			//... and plot the probed data from the simulation ... 
			Plotter.plot(p.getData(), "function output");
			
			//now here are a couple of function bases ... 
			Function g1 = new GaussianPDF(0, 1);
			Function g2 = new GaussianPDF(0.5f, 1);			
			FunctionBasis gaussianBasis = new FunctionBasisImpl(new Function[]{g1, g2});

			Function s1 = new SigmoidFunction(0, 1, 0, 1);
			Function s2 = new SigmoidFunction(0.5f, -1, 0, 1);
			FunctionBasis sigmoidBasis = new FunctionBasisImpl(new Function[]{s1, s2});
			
			//here is a plot of the probed vector X the gaussian basis (value at time 4.5s) ... 			
			gaussianBasis.setCoefficients(p.getData().getValues()[4500]);
			Plotter.plot(gaussianBasis, -3, .001f, 3, "gaussian basis plot");
			
			//here is a plot of the probed vector X the sigmoid basis (value at time 0.5s) ... 						
			sigmoidBasis.setCoefficients(p.getData().getValues()[500]);
			Plotter.plot(sigmoidBasis, -3, .001f, 3, "sigmoid basis plot");
						
		} catch (StructuralException e) {
			e.printStackTrace();
		} catch (SimulationException e) {
			e.printStackTrace();
		}
	}
}
