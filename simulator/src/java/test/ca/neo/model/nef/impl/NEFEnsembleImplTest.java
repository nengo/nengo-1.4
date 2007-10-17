/*
 * Created on 24-Apr-07
 */
package ca.neo.model.nef.impl;

import ca.neo.TestUtil;
import ca.neo.math.Function;
import ca.neo.math.impl.AbstractFunction;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.model.Network;
import ca.neo.model.Projection;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.plot.Plotter;
import ca.neo.util.MU;
import ca.neo.util.Probe;
import ca.neo.util.TimeSeries;
import ca.neo.util.impl.TimeSeriesImpl;
import junit.framework.TestCase;

/**
 * Unit tests for NEFEnsembleImpl. 
 * 
 * TODO: this is a functional test with no failures ... convert to unit test
 * TODO: make sure performance optimization works with inhibitory projections
 * 
 * @author Bryan Tripp
 */
public class NEFEnsembleImplTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testAddBiasOrigin() throws StructuralException, SimulationException {
		NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();

		boolean regenerate = false;
		NEFEnsemble source = ef.make("source", 300, 1, "nefeTest_source", regenerate);
		NEFEnsemble dest = ef.make("dest", 300, 1, "nefeTest_dest", regenerate);
		
		Function f = new AbstractFunction(1) {
			private static final long serialVersionUID = 1L;
			public float map(float[] from) {
				return from[0] - 1;
			}
		};
		FunctionInput input = new FunctionInput("input", new Function[]{f}, Units.UNK);
		FunctionInput zero = new FunctionInput("zero", new Function[]{new ConstantFunction(1, 0f)}, Units.UNK);
		
		Network network = new NetworkImpl();
		network.addNode(input);
		network.addNode(source);
		network.addNode(dest);

		source.addDecodedTermination("input", MU.I(1), .005f, false); //OK
		BiasOrigin bo = source.addBiasOrigin(source.getOrigin(NEFEnsemble.X), 200, "interneurons", true); //should have -ve bias decoders
		network.addNode(bo.getInterneurons()); //should be backwards response functions
//**		bo.getInterneurons().addDecodedTermination("source", MU.I(1), .005f, false);
		
//		Plotter.plot(bo.getInterneurons());
//		Plotter.plot(bo.getInterneurons(), NEFEnsemble.X);
		
		DecodedTermination t = (DecodedTermination) dest.addDecodedTermination("source", MU.I(1), .005f, false);
//**		BiasTermination[] bt = dest.addBiasTerminations(t, .002f, bo.getDecoders()[0][0], ((DecodedOrigin) source.getOrigin(NEFEnsemble.X)).getDecoders());
//**		bt[1].setStaticBias(-1); //creates intrinsic current needed to counteract interneuron activity at 0
		
		float[][] weights = MU.prod(dest.getEncoders(), MU.transpose(((DecodedOrigin) source.getOrigin(NEFEnsemble.X)).getDecoders()));
//*		float[][] biasEncoders = MU.transpose(new float[][]{bt[0].getBiasEncoders()});
//*		float[][] biasDecoders = MU.transpose(bo.getDecoders());
//*		float[][] weightBiases = MU.prod(biasEncoders, biasDecoders);
//*		float[][] biasedWeights = MU.sum(weights, weightBiases);
//		Plotter.plot(weights[0], "some weights");
//		Plotter.plot(biasedWeights[0], "some biased weights");
//		Plotter.plot(weights[1], "some more weights");
		
//		Plotter.plot(bt[0].getBiasEncoders(), "bias decoders");
		
		network.addProjection(input.getOrigin(FunctionInput.ORIGIN_NAME), source.getTermination("input"));
		network.addProjection(source.getOrigin(NEFEnsemble.X), dest.getTermination("source"));
//*		network.addProjection(bo, bo.getInterneurons().getTermination("source"));
//*		network.addProjection(bo, bt[0]);
//*		network.addProjection(bo.getInterneurons().getOrigin(NEFEnsemble.X), bt[1]);
//		network.addProjection(zero.getOrigin(FunctionInput.ORIGIN_NAME), bt[1]);
		
		Probe sourceProbe = network.getSimulator().addProbe("source", NEFEnsemble.X, true);
		Probe destProbe = network.getSimulator().addProbe("dest", NEFEnsemble.X, true);
		Probe interProbe = network.getSimulator().addProbe("source_X_bias_interneurons", NEFEnsemble.X, true);
		
		network.run(0, 2);
		
//		Plotter.plot(sourceProbe.getData(), "source");
//		Plotter.plot(destProbe.getData(), "dest");
//		Plotter.plot(interProbe.getData(), "interneurons");
	}
	
	public void testBiasOriginError() throws StructuralException, SimulationException {
		float tauPSC = .01f;
		
		Network network = new NetworkImpl();
		
		Function f = new AbstractFunction(1) {
			private static final long serialVersionUID = 1L;
			public float map(float[] from) {
				return from[0] - 1;
			}
		};
		
		FunctionInput input = new FunctionInput("input", new Function[]{f}, Units.UNK);
		network.addNode(input);
		
		NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();
		NEFEnsemble pre = ef.make("pre", 400, 1, "nefe_pre", false);
		pre.addDecodedTermination("input", MU.I(1), tauPSC, false);
		DecodedOrigin baseOrigin = (DecodedOrigin) pre.getOrigin(NEFEnsemble.X);
		network.addNode(pre);
		
		NEFEnsemble post = ef.make("post", 200, 1, "nefe_post", false);
		DecodedTermination baseTermination = (DecodedTermination) post.addDecodedTermination("pre", MU.I(1), tauPSC, false);
		network.addNode(post);
		
		network.addProjection(input.getOrigin(FunctionInput.ORIGIN_NAME), pre.getTermination("input"));
		Projection projection = network.addProjection(pre.getOrigin(NEFEnsemble.X), post.getTermination("pre"));
		
		Probe pPost = network.getSimulator().addProbe("post", NEFEnsemble.X, true);
		network.run(0, 2);
		TimeSeries ideal = pPost.getData();
		Plotter.plot(pPost.getData(), .005f, "mixed weights result");		
		
		//remove negative weights ... 
		System.out.println("Minimum weight without bias: " + MU.min(projection.getWeights()));
		projection.addBias(100, .005f, tauPSC, true, false);
		System.out.println("Minimum weight with bias: " + MU.min(projection.getWeights()));
		pPost.reset();
		network.run(0, 2);
		TimeSeries diff = new TimeSeriesImpl(ideal.getTimes(), MU.difference(ideal.getValues(), pPost.getData().getValues()), ideal.getUnits()); 
		Plotter.plot(diff, .01f, "positive weights");
		
		projection.removeBias();
		projection.addBias(100, tauPSC/5f, tauPSC, true, true);
		pPost.reset();
		Probe pInter = network.getSimulator().addProbe("post:pre:interneurons", NEFEnsemble.X, true);
		network.run(0, 2);
		diff = new TimeSeriesImpl(ideal.getTimes(), MU.difference(ideal.getValues(), pPost.getData().getValues()), ideal.getUnits()); 
		Plotter.plot(diff, .01f, "positive weights optimized");
		Plotter.plot(pInter.getData(), .01f, "interneurons");

		
		
//		//remove negative weights ... 
//		BiasOrigin bo = pre.addBiasOrigin(baseOrigin, 100, "interneurons", true);
//		BiasTermination[] bt = post.addBiasTerminations(baseTermination, tauPSC, bo.getDecoders()[0][0], baseOrigin.getDecoders());
//		DecodedTermination it = (DecodedTermination) bo.getInterneurons().addDecodedTermination("bias", MU.I(1), tauPSC/5f, false);
//		network.addNode(bo.getInterneurons());
//		network.addProjection(bo, bt[0]);
//		network.addProjection(bo, bo.getInterneurons().getTermination("bias"));
//		network.addProjection(bo.getInterneurons().getOrigin(NEFEnsemble.X), bt[1]);
//		Plotter.plot(MU.transpose(bo.getDecoders())[0], "bias decoders");
////		Plotter.plot(bo.getInterneurons(), NEFEnsemble.X);
//		
//		pPost.reset();
//		network.run(0, 2);
//		TimeSeries diff = new TimeSeriesImpl(ideal.getTimes(), MU.difference(ideal.getValues(), pPost.getData().getValues()), ideal.getUnits()); 
//		Plotter.plot(diff, .01f, "positive weights");
////		Plotter.plot(ideal, pPost.getData(), .005f, "positive weights result");
//		
//		//narrow bias range ... 
////		Plotter.plot(pre, bo.getName());
//		float[][] baseWeights = MU.prod(post.getEncoders(), MU.prod(baseTermination.getTransform(), MU.transpose(baseOrigin.getDecoders())));
//		float[] encodersBeforeTweak = findBiasEncoders(baseWeights, MU.transpose(bo.getDecoders())[0]);
//		bo.optimizeDecoders(baseWeights, bt[0].getBiasEncoders());
////		Plotter.plot(pre, bo.getName());
//		float[] encodersAfterTweak = findBiasEncoders(baseWeights, MU.transpose(bo.getDecoders())[0]);
//		TestUtil.assertClose(MU.sum(MU.difference(encodersBeforeTweak, encodersAfterTweak)), 0, .0001f);
//		Plotter.plot(MU.transpose(bo.getDecoders())[0], "narrow bias decoders");
//		
//		pPost.reset();		
//		network.run(0, 2);
//		diff = new TimeSeriesImpl(ideal.getTimes(), MU.difference(ideal.getValues(), pPost.getData().getValues()), ideal.getUnits()); 
//		Plotter.plot(diff, .01f, "narrowed bias"); 		
////		Plotter.plot(ideal, pPost.getData(), .005f, "narrowed bias result");
//		
//		//optimize interneuron range ... 
//		float[] range = bo.getRange();
//		System.out.println(range[0] + " to " + range[1]);
//		range[0] = range[0] - .25f * (range[1] - range[0]); //avoid distorted area near zero in interneurons 
//		it.setStaticBias(new float[]{-range[0]});
//		it.getTransform()[0][0] = 1f / (range[1] - range[0]);
//		bt[1].setStaticBias(new float[]{range[0]/(range[1] - range[0])});
//		bt[1].getTransform()[0][0] = -(range[1] - range[0]);		
//		
//		pPost.reset();
//		network.run(0, 2);
//		diff = new TimeSeriesImpl(ideal.getTimes(), MU.difference(ideal.getValues(), pPost.getData().getValues()), ideal.getUnits()); 
//		Plotter.plot(diff, .01f, "optimized interneuron"); 				
////		Plotter.plot(ideal, pPost.getData(), .005f, "optimized interneuron result");
//		
////		Probe pBias = network.getSimulator().addProbe("pre", bo.getName(), true);
////		Probe pInter = network.getSimulator().addProbe(bo.getInterneurons().getName(), NEFEnsemble.X, true);
////		Probe pBT0 = network.getSimulator().addProbe("post", bt[0].getName(), true);
////		Probe pBT1 = network.getSimulator().addProbe("post", bt[1].getName(), true);
////		Probe pT = network.getSimulator().addProbe("post", "pre", true);
////		
////		network.run(0, 2);
////		Plotter.plot(pPost.getData(), .005f, "post");
////		Plotter.plot(pBias.getData(), .005f, "bias");
////		Plotter.plot(pInter.getData(), .005f, "interneurons");
////		Plotter.plot(pBT0.getData(), .005f, "BT0");
////		Plotter.plot(pBT1.getData(), .005f, "BT1");
////		Plotter.plot(pT.getData(), .005f, "base termination");
	}
	
	private float[] findBiasEncoders(float[][] baseWeights, float[] biasDecoders) {
		float[] biasEncoders = new float[baseWeights.length];
		
		for (int j = 0; j < biasEncoders.length; j++) {
			float max = 0;
			for (int i = 0; i < biasDecoders.length; i++) {
				float x = - baseWeights[j][i] / biasDecoders[i];
				if (x > max) max = x;
			}			
			biasEncoders[j] = max;
		}
		
		return biasEncoders;
	}
	
	public static void main(String[] args) {
		NEFEnsembleImplTest test = new NEFEnsembleImplTest();
		try {
//			test.testAddBiasOrigin();
			test.testBiasOriginError();
		} catch (StructuralException e) {
			e.printStackTrace();
		} catch (SimulationException e) {
			e.printStackTrace();
		}
	}

}
