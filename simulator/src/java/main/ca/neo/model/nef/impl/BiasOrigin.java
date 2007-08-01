/*
 * Created on 23-Apr-07
 */
package ca.neo.model.nef.impl;

import ca.neo.math.Function;
import ca.neo.math.PDF;
import ca.neo.math.impl.AbstractFunction;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.math.impl.GradientDescentApproximator;
import ca.neo.math.impl.IndicatorPDF;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.neuron.Neuron;
import ca.neo.model.neuron.impl.LIFNeuronFactory;
import ca.neo.util.VectorGenerator;
import ca.neo.util.impl.RandomHypersphereVG;
import ca.neo.util.impl.Rectifier;

/**
 * <p>Part of a projection in which each of the Nodes making up an Ensemble is a source of only excitatory or inhibitory 
 * connections.</p> 
 * 
 * <p>The theory is presented in Parisien, Anderson & Eliasmith (2007).</p>
 *  
 * <p>Such a projection includes a "base" DecodedOrigin and DecodedTermination (a projection between these may have weights
 * of mixed sign). The projection is expanded with a BiasOrigin a pair of BiasTerminations, and a new NEFEnsemble of 
 * interneurons. The make weight signs uniform, a projection is established between the BiasOrigin and BiasTermination, 
 * in parallel with the original projection. The effective synaptic weights that arise from the combination of these two 
 * projections are of uniform sign. However, the post-synaptic Ensemble receives extra bias current as a result. This bias 
 * current is cancelled by a projection from the BiasOrigin through the interneurons, to a second BiasTermination.</p> 
 *    
 * TODO: account for transformations in the Termination, which can change sign and magnitude of weights
 * 
 * @author Bryan Tripp
 */
public class BiasOrigin extends DecodedOrigin {
	
	private static final long serialVersionUID = 1L;		
	
	private NEFEnsemble myInterneurons;
	
	public BiasOrigin(Node node, String name, Node[] nodes, String nodeOrigin, float[][] constantOutputs, int numInterneurons, boolean excitatory) throws StructuralException {
		super(node, name, nodes, nodeOrigin, new Function[]{new ConstantFunction(0, 0f)}, getUniformBiasDecoders(constantOutputs, excitatory));
		//note above ConstantFunction has dimension 0, but the input dimension isn't checked
		
		myInterneurons = createInterneurons(name + "_interneurons", numInterneurons, excitatory);
	}
	
	private static float[][] getUniformBiasDecoders(float[][] constantOutputs, boolean excitatory) {
		float[][] result = new float[constantOutputs.length][];
		float decoder = getBiasDecoder(constantOutputs, excitatory);
		for (int i = 0; i < result.length; i++) {
			result[i] = new float[]{decoder};
		}
		return result;
	}
	
	private static float getBiasDecoder(float[][] constantOutputs, boolean excitatory) {
		//iterate over evaluation points to find max of sum(constantOutputs)
		float max = 0;
		for (int i = 0; i < constantOutputs[0].length; i++) { 
			float sum = 0;
			for (int j = 0; j < constantOutputs.length; j++) {
				sum += constantOutputs[j][i];
			}
			if (sum > max) max = sum;
		}
		
		return excitatory ? 1f / max : -1f / max; //this makes the bias function peak at 1 (or -1) 
	}
	
	private NEFEnsemble createInterneurons(String name, int num, boolean excitatoryProjection) throws StructuralException {
		NEFEnsembleFactory ef = null;
		if (excitatoryProjection) {
			ef = new NEFEnsembleFactoryImpl();
		} else {
			ef = new NEFEnsembleFactoryImpl() {
				protected void addDefaultOrigins(NEFEnsemble ensemble) throws StructuralException {
					Function f = new AbstractFunction(1) {
						private static final long serialVersionUID = 1L;
						public float map(float[] from) {
							return 1f + from[0];
						}
					};
					ensemble.addDecodedOrigin(NEFEnsemble.X, new Function[]{f}, Neuron.AXON);
				}
			};
		}
		//TODO: handle additional bias in inhibitory case 
		
		new NEFEnsembleFactoryImpl();
		ef.setEncoderFactory(new Rectifier(ef.getEncoderFactory()));
		ef.setEvalPointFactory(new BiasedVG(new RandomHypersphereVG(false, 0.5f, 0f), 0, excitatoryProjection ? 1f : -1f));
		
		PDF interceptPDF = excitatoryProjection ? new IndicatorPDF(-.1f, .99f) : new IndicatorPDF(-.99f, .1f);
		PDF maxRatePDF = excitatoryProjection ? new IndicatorPDF(200f, 500f) : new IndicatorPDF(400f, 800f);
		ef.setNodeFactory(new LIFNeuronFactory(.02f, .001f, maxRatePDF, interceptPDF));
		ef.setApproximatorFactory(new GradientDescentApproximator.Factory(new CoefficientsPositive(), false));
		return ef.make(name, num, 1);
	}

	/**
	 * @return An ensemble of interneurons through which this Origin must project (in parallel with its 
	 * 		direct projection) to compensate for the bias introduced by making all weights the same sign.  
	 */
	public NEFEnsemble getInterneurons() {
		return myInterneurons;
	}

	/**
	 * Forces all decoding coefficients to be >= 0. 
	 * 
	 * @author Bryan Tripp
	 */
	private static class CoefficientsPositive implements GradientDescentApproximator.Constraints {
		
		private static final long serialVersionUID = 1L;
		
		/**
		 * @see ca.neo.math.impl.GradientDescentApproximator.Constraints#correct(float[])
		 */
		public boolean correct(float[] coefficients) {
			boolean allCorrected = true;
			for (int i = 0; i < coefficients.length; i++) {
				if (coefficients[i] < 0) {
					coefficients[i] = 0;
				} else {
					allCorrected = false;
				}
			}
			return allCorrected;
		}
	}

	/**
	 * Adds a specified bias to a specified dimension of vectors that are made by an underlying generator. 
	 *  
	 * @author Bryan Tripp
	 */
	private static class BiasedVG implements VectorGenerator {
		
		private VectorGenerator myVG;
		private int myDim;
		private float myBias;
		
		public BiasedVG(VectorGenerator vg, int dim, float bias) {
			myVG = vg;
			myDim = dim;
			myBias = bias;
		}

		/**
		 * @see ca.neo.util.VectorGenerator#genVectors(int, int)
		 */
		public float[][] genVectors(int number, int dimension) {
			float[][] result = myVG.genVectors(number, dimension);
			for (int i = 0; i < result.length; i++) {
				result[i][myDim] += myBias;
			}
			return result;
		}
	}

}
