package ca.neo.examples;

import ca.neo.math.Function;
import ca.neo.math.FunctionInterpreter;
import ca.neo.math.impl.AbstractFunction;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.math.impl.DefaultFunctionInterpreter;
import ca.neo.model.Network;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.neo.model.neuron.Neuron;
import ca.neo.sim.Simulator;
import ca.neo.sim.impl.LocalSimulator;
import ca.neo.util.Probe;

/**
 * Fuzzification is implemented as a function transformation. Inference is done with 
 * multidimensional ensembles from which norms and conorms are decoded. Composition 
 * is done by projecting high-dimensional fuzzy consequents additively onto the 
 * same ensemble, from which the mode is selected by lateral inhibition. 
 * 
 * @author Bryan Tripp
 */
public class FuzzyLogicExample {

	public static Network createNetwork() throws StructuralException {
		Network net = new NetworkImpl();
		
		//Rules: 
		// 1) if A and (B or C) then 1 
		// 2) if D then 2
		FunctionInterpreter fi = new DefaultFunctionInterpreter();
		
		Function[] functions = new Function[]{
				fi.parse("x0 < .2", 1),
				new ConstantFunction(1, .5f),
				new ConstantFunction(1, .2f),
				new ConstantFunction(1, .3f)
		};
		FunctionInput in = new FunctionInput("input", functions, Units.UNK);
		
		NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();
		
		NEFEnsemble A = ef.make("A", 100, 1, "A", false); 
		NEFEnsemble B = ef.make("B", 100, 1, "B", false); 
		NEFEnsemble C = ef.make("C", 100, 1, "C", false); 
		NEFEnsemble D = ef.make("D", 100, 1, "D", false);
		
		NEFEnsemble rule1a = ef.make("rule1a", 500, 2, "rule1a", false);
		NEFEnsemble rule1b = ef.make("rule1b", 500, 2, "rule1b", false);
		NEFEnsemble rule2 = ef.make("rule2", 200, 1, "rule2", false);
		rule1a.addDecodedOrigin("OR", new Function[]{new MAX(2)}, Neuron.AXON);
		rule1b.addDecodedOrigin("AND", new Function[]{new MIN(2)}, Neuron.AXON);
		rule1a.doneOrigins();
		rule1b.doneOrigins();
		rule2.doneOrigins();
		
		NEFEnsemble output = ef.make("output", 500, 5, "fuzzyoutput", false);		
		
		net.addNode(in);
		net.addNode(A);
		net.addNode(B);
		net.addNode(C);
		net.addNode(D);
		net.addNode(rule1a);
		net.addNode(rule1b);
		net.addNode(rule2);
		net.addNode(output);
		
		A.addDecodedTermination("in", new float[][]{new float[]{1f, 0f, 0f, 0f}}, .005f, false);
		B.addDecodedTermination("in", new float[][]{new float[]{0f, 1f, 0f, 0f}}, .005f, false);
		C.addDecodedTermination("in", new float[][]{new float[]{0f, 0f, 1f, 0f}}, .005f, false);
		D.addDecodedTermination("in", new float[][]{new float[]{0f, 0f, 0f, 1f}}, .005f, false);
		
		net.addProjection(in.getOrigin(FunctionInput.ORIGIN_NAME), A.getTermination("in"));
		net.addProjection(in.getOrigin(FunctionInput.ORIGIN_NAME), B.getTermination("in"));
		net.addProjection(in.getOrigin(FunctionInput.ORIGIN_NAME), C.getTermination("in"));
		net.addProjection(in.getOrigin(FunctionInput.ORIGIN_NAME), D.getTermination("in"));
		
		rule1a.addDecodedTermination("B", new float[][]{new float[]{1f}, new float[]{0f}}, .005f, false);
		rule1a.addDecodedTermination("C", new float[][]{new float[]{0f}, new float[]{1f}}, .005f, false);
		rule1b.addDecodedTermination("A", new float[][]{new float[]{1f}, new float[]{0f}}, .005f, false);
		rule1b.addDecodedTermination("B or C", new float[][]{new float[]{0f}, new float[]{1f}}, .005f, false);
		rule2.addDecodedTermination("D", new float[][]{new float[]{1f}}, .005f, false);
		
		net.addProjection(B.getOrigin(NEFEnsemble.X), rule1a.getTermination("B"));
		net.addProjection(C.getOrigin(NEFEnsemble.X), rule1a.getTermination("C"));
		net.addProjection(A.getOrigin(NEFEnsemble.X), rule1b.getTermination("A"));
		net.addProjection(rule1a.getOrigin("OR"), rule1b.getTermination("B or C"));
		net.addProjection(D.getOrigin(NEFEnsemble.X), rule2.getTermination("D"));

		output.addDecodedTermination("rule1", new float[][]{new float[]{.4f}, new float[]{.3f}, new float[]{.2f}, new float[]{.1f}, new float[]{0f}}, .005f, false);
		output.addDecodedTermination("rule2", new float[][]{new float[]{0f}, new float[]{.1f}, new float[]{.2f}, new float[]{.3f}, new float[]{.4f}}, .005f, false);
		
		net.addProjection(rule1b.getOrigin("AND"), output.getTermination("rule1"));
		net.addProjection(rule2.getOrigin(NEFEnsemble.X), output.getTermination("rule2"));
		
		float neg = -.3f;
		float pos = .9f;
		float[][] m = new float[][]{
				new float[]{pos, neg, neg, neg, neg}, 
				new float[]{neg, pos, neg, neg, neg}, 
				new float[]{neg, neg, pos, neg, neg}, 
				new float[]{neg, neg, neg, pos, neg}, 
				new float[]{neg, neg, neg, neg, pos}, 
		};
		output.addDecodedTermination("recurrent", m, .005f, false);
		
		Function[] clipped = new Function[]{
				new Clip(5, 0, 0f, 1f), 
				new Clip(5, 1, 0f, 1f), 
				new Clip(5, 2, 0f, 1f), 
				new Clip(5, 3, 0f, 1f), 
				new Clip(5, 4, 0f, 1f) 
		};
		output.addDecodedOrigin("recurrent", clipped, Neuron.AXON);
		
		net.addProjection(output.getOrigin("recurrent"), output.getTermination("recurrent"));
		
		return net;
	}
	
	private static class MIN extends AbstractFunction {

		private static final long serialVersionUID = 1L;

		public MIN(int dim) {
			super(dim);
		}

		public float map(float[] from) {
			float result = from[0];
			
			for (int i = 1; i < from.length; i++) {
				if (from[i] < result) result = from[i];
			}
			
			return result;
		}
	}
	
	private static class MAX extends AbstractFunction {

		private static final long serialVersionUID = 1L;

		public MAX(int dim) {
			super(dim);
		}

		public float map(float[] from) {
			float result = from[0];
			
			for (int i = 1; i < from.length; i++) {
				if (from[i] > result) result = from[i];
			}
			
			return result;
		}
	}
	
	private static class Clip extends AbstractFunction {

		private int myFromDim;
		private float myMin;
		private float myMax;
		
		public Clip(int dim, int fromDim, float min, float max) {
			super(dim);
			myFromDim = fromDim;
			myMin = min;
			myMax = max;
		}
		
		public float map(float[] from) {
			float result = from[myFromDim];
			if (result < myMin) {
				result = myMin;
			} else if (result > myMax){
				result = myMax;
			}
			
			return result;
		}
	}
	
	
	public static void main(String[] args) {
		try {
			Network net = createNetwork();
			
			Simulator sim = new LocalSimulator();
			sim.initialize(net);
			
			//Recorder B = sim.addRecorder("B", NEFEnsemble.X);
			Probe rule1a = sim.addProbe("rule1a", "OR", false);
			Probe rule1b = sim.addProbe("rule1b", "AND", false);
			Probe rule2 = sim.addProbe("rule2", NEFEnsemble.X, false);
			Probe output = sim.addProbe("output", "recurrent", false);
			
			InferenceExample.runAndShow(sim, 0f, .5f, .001f, SimulationMode.DEFAULT, 
					new Probe[]{rule1a, rule1b, rule2, output}, new String[]{"Rule 1a", "Rule 1b", "Rule 2", "Output"});
			
		} catch (StructuralException e) {
			e.printStackTrace();
		} catch (SimulationException e) {
			e.printStackTrace();
		}
	}
	
}
