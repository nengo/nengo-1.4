/*
 * Created on 26-Jul-2006
 */
package ca.neo.examples;

import java.util.ArrayList;

import ca.neo.math.ApproximatorFactory;
import ca.neo.math.Function;
import ca.neo.math.LinearApproximator;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.math.impl.GaussianPDF;
import ca.neo.math.impl.IndependentDimensionApproximator;
import ca.neo.math.impl.WeightedCostApproximator;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.EnsembleFactory;
import ca.neo.model.impl.FunctionInput;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFNode;
import ca.neo.model.nef.impl.DecodedOrigin;
import ca.neo.model.nef.impl.NEFEnsembleImpl;
import ca.neo.model.neuron.Neuron;
import ca.neo.plot.Plotter;
import ca.neo.sim.Simulator;
import ca.neo.sim.impl.LocalSimulator;
import ca.neo.util.MU;
import ca.neo.util.Memory;
import ca.neo.util.Probe;
import ca.neo.util.TimeSeries;
import ca.neo.util.VectorGenerator;
import ca.neo.util.impl.RandomHypersphereVG;
import ca.neo.util.impl.Rectifier;

public class InferenceExample {

	public InferenceExample() {
	}
	
	public static Network createNetwork() throws StructuralException {
		EnsembleFactory ef = new EnsembleFactory() {
			public NEFEnsemble construct(String name, Neuron[] neurons, float[][] encoders) throws StructuralException {
				VectorGenerator vg = new RandomHypersphereVG(true, 1, 0);
				int dim = encoders[0].length;
				ApproximatorFactory af = new ApproximatorFactory() {
					public LinearApproximator getApproximator(float[][] evalPoints,	float[][] values) {
						return new WeightedCostApproximator(evalPoints, values, new ConstantFunction(evalPoints[0].length, 1), .1f);
					}
				};				
				return new PDFEnsemble(name, neurons, encoders, af, vg.genVectors(300*dim, dim));
			}			
		};
		ef.setVectorGenerator(new RandomHypersphereVG(true, 1f, 1f));
		
		int dimension = 2; //dimension of each PDF
		
		NEFEnsemble observation = ef.make("observation", 300, dimension, "observation", true); 
		observation.doneOrigins();
		
		System.out.println(MU.toString( ((DecodedOrigin) observation.getOrigin(NEFEnsemble.X)).getDecoders(), 2));
//		NEFEnsemble inference = ef.make("inference", 300, dimension, "inference", false);
//
//		float[][] joint = makeJoint(5);
//		float[] colsum = new float[dimension];
//		for (int i = 0; i < dimension; i++) {
//			for (int j = 0; j < dimension; j++) {
//				colsum[j] += joint[i][j];
//			}
//		}
//		
//		float[][] conditionals = new float[dimension][];
//		for (int i = 0; i < dimension; i++) {
//			conditionals[i] = new float[dimension];
//			for (int j = 0; j < dimension; j++) {
//				conditionals[i][j] = joint[i][j] / colsum[j];
//			}
//		}
//		
		observation.addDecodedTermination("pdf", MU.I(dimension), .005f, false);
//		inference.addDecodedTermination("conditional", conditionals, .005f);
//		
		Function[] inPDF = new Function[] {
//				new ConstantFunction(1, 0f), 
//				new ConstantFunction(1, 0f), 
//				new ConstantFunction(1, .1f), 
//				new ConstantFunction(1, .2f), 
//				new ConstantFunction(1, .3f),				
//				new ConstantFunction(1, .2f), 
//				new ConstantFunction(1, .1f), 
//				new ConstantFunction(1, 0f), 
				new ConstantFunction(1, .1f), 
				new ConstantFunction(1, .2f)				
		};
		FunctionInput in = new FunctionInput("in", inPDF, Units.UNK);

		Network net = new NetworkImpl();
		net.addNode(in);
		net.addNode(observation);
//		net.addNode(inference);
		net.addProjection(in.getOrigin(FunctionInput.ORIGIN_NAME), observation.getTermination("pdf"));
//		net.addProjection(observation.getOrigin(NEFEnsemble.X), inference.getTermination("conditional"));
		
		return net;
	}
	
	//random joint distribution composed of 3 Gaussians 
//	public static float[][] makeJoint(int dimension) {
//		float[][] result = new float[dimension][];
//		
//		for (int i = 0; i < dimension; i++) {
//			result[i] = new float[dimension];
//		}
//
//		float integral = 0f;		
//		double increment = 1 / (double) dimension;  
//		for (int i = 0; i < 2; i++) {
//			double meanx = Math.random();
//			double meany = Math.random();
//			double var = Math.random() / 3;
//			
//			GaussianPDF g = new GaussianPDF(0f, (float) var);
//			for (int j = 0; j < dimension; j++) {
//				double x = (double) j * increment + increment/2;
//				for (int k = 0; k < dimension; k++) {
//					double y = (double) k * increment + increment/2;
//					double d = Math.pow((meanx - x)*(meanx - x) + (meany - y)*(meany - y), .5);
//					float p = g.map(new float[]{(float) d});
//					result[j][k] += p;
//					integral += p;
//				}
//			}
//		}
//		
//		JFigGenericFigure fig = new JFigGenericFigure();
//		float[] ordinate = new float[dimension];
//		for (int i = 0; i < dimension; i++) {
//			ordinate[i] = (float) i * (float) increment + (float) increment / 2f;
//		}
//		for (int i = 0; i < dimension; i++) {
//			float[] plot = new float[dimension]; 
//			for (int j = 0; j < dimension; j++) {
//				result[i][j] = result[i][j] / integral;
//				plot[j] = result[i][j]; // + .1f * (float) i;
//			}
//			fig.plot(ordinate, plot, true, false);
//		}
//		fig.show();		
//		System.out.println(MU.toString(result, 2));
//		
//		return result;
//	}
	
	
	public static void main(String[] args) {
		try {
			Network net = createNetwork();
			
			Simulator sim = new LocalSimulator();
			sim.initialize(net);
			
			Probe obsRecorder = sim.addProbe("observation", NEFEnsemble.X, true);
//			Recorder infRecorder = sim.addRecorder("inference", NEFEnsemble.X);
//
			runAndShow(sim, 0f, 1f, .001f, SimulationMode.CONSTANT_RATE, new Probe[]{obsRecorder}, new String[]{"Observation"});

			//			Plotter.plot(ideal);
//			Plotter.plot(actual);
//			Plotter.plot(infRecorder.getData());
			
		} catch (StructuralException e) {
			e.printStackTrace();
		} catch (SimulationException e) {
			e.printStackTrace();
		}
	}
	
	//TODO: put this somewhere nice
	//TODO: why are twice-direct results different?
	//TODO: colours aren't consistently ordered
	public static void runAndShow(Simulator sim, float startTime, float endTime, float timeStep, SimulationMode mode, 
			Probe[] recorders, String[] titles) throws SimulationException {
		
		float tauFilter = .005f;
		
		sim.resetNetwork(false);
		sim.run(startTime, endTime, timeStep, SimulationMode.DIRECT);
		
		TimeSeries[] directResults = new TimeSeries[recorders.length];
		for (int i = 0; i < recorders.length; i++) {
			directResults[i] = recorders[i].getData();
			recorders[i].reset();
		}
		
		sim.resetNetwork(false);
		sim.run(startTime, endTime, timeStep, mode);
		for (int i = 0; i < recorders.length; i++) {
//			Plotter.plot(directResults[i], recorders[i].getData(), tauFilter, titles[i]);
			Plotter.plot(directResults[i], recorders[i].getData(), titles[i]);
			recorders[i].reset();
		}
	}

	/**
	 * An ensemble class with rectified vector components along axes (OK for linear transformations 
	 * on PDFs, not OK for general functions or non-linear transformations). 
	 * 
	 * @author Bryan Tripp
	 */
	public static class PDFEnsemble extends NEFEnsembleImpl {

		public PDFEnsemble(String name, Neuron[] neurons, float[][] encoders, ApproximatorFactory af, float[][] evalPoints) throws StructuralException {			
			super(name, (NEFNode[]) neurons, encoders, af, evalPoints);
		}

		public LinearApproximator getApproximator() throws StructuralException {
			System.out.println(MU.toString(getEncoders(), 2));
			
			int numEvalPoints = 300;
			
//			VectorGenerator vg = new Rectifier(new RandomHypersphereVG(false, 1f, 0f));
			VectorGenerator vg = new RandomHypersphereVG(false, 1f, 0f);
			float[][] evalPoints = vg.genVectors(numEvalPoints, 1);
//			float[][] evalPoints = vg.genVectors(numEvalPoints, getDimension());
			
			Neuron[] neurons = (Neuron[]) getNodes();	
			
//			//copy eval points to every dimension, so each neuron sees them on its own dimension
//			int dim = getDimension();
//			float[][] ep = new float[evalPoints.length][];
//			for (int i = 0; i < ep.length; i++) {
//				ep[i] = new float[dim];
//				for (int j = 0; j < dim; j++) {
//					ep[i][j] = evalPoints[i][0];
//				}
//			}
			
			//TODO: weights are high with on-axis encoders -- compare to one axis at a time -- not much different 
//			float[][] values = new float[neurons.length][];
//			for (int i = 0; i < values.length; i++) {			
//				try {
//					values[i] = getFiringRates(i, ep);
//				} catch (SimulationException e) {
//					throw new Error("Neuron " + i + " does not have the standard 'AXON' Origin");
//				}
//			}				
//			return new WeightedCostApproximator(evalPoints, values, new ConstantFunction(evalPoints[0].length, 1), .1f); 

			float[] ep = new float[evalPoints.length];
			for (int i = 0; i < ep.length; i++) {
				ep[i] = evalPoints[i][0];
			}
			float[][] values = getValues(neurons, getEncoders(), ep);
			float[][] encoders = getEncoders();
			int[] dimensions = new int[encoders.length];
			for (int i = 0; i < encoders.length; i++) {
				dimensions[i] = getNonZeroDimension(encoders[i]);
			}
			return new IndependentDimensionApproximator(ep, values, dimensions, getDimension(), new ConstantFunction(1, 1f), .1f);
		}
		
		private float[][] getValues(Neuron[] neurons, float[][] encoders, float[] evalPoints) throws StructuralException {//, SimulationException {
			
			float[][] result = new float[neurons.length][];			
			for (int i = 0; i < neurons.length; i++) {
				int dim = getNonZeroDimension(encoders[i]);
				float[][] evalPointsND = new float[encoders.length][];
				for (int j = 0; j < encoders.length; j++) {
					evalPointsND[j] = new float[getDimension()];
					evalPointsND[j][dim] = evalPoints[j];
				}
				try {
					result[i] = getFiringRates(i, evalPointsND);
				} catch (SimulationException e) {
					throw new Error("Neuron " + i + " does not have the standard 'AXON' Origin");
				}
			}
			
			return result;
		}
		
//		private static float[][] project(float[][] evalPoints, int index, int dim) {
//			float[][] result = new float[evalPoints.length][];
//			
//			for (int i = 0; i < evalPoints.length; i++) {
//				result[i] = new float[dim];
//				result[i][index] = evalPoints[i][0];
//			}
//			
//			return result;
//		}
		
		private static int getNonZeroDimension(float[] encoder) {
			for (int i = 0; i < encoder.length; i++) {
				if (Math.abs(encoder[i]) > 0) return i;
			}
			throw new IllegalArgumentException("All entries are zero");
		}
			

		
	}

}
