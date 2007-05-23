/*
 * Created on 24-Apr-07
 */
package ca.neo.model.nef.impl;

import ca.neo.math.Function;
import ca.neo.math.impl.AbstractFunction;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.model.Network;
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
import junit.framework.TestCase;

/**
 * Unit tests for NEFEnsembleImpl. 
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
		BiasOrigin bo = source.addBiasOrigin(source.getOrigin(NEFEnsemble.X), 200, true); //should have -ve bias decoders
		network.addNode(bo.getInterneurons()); //should be backwards response functions
//**		bo.getInterneurons().addDecodedTermination("source", MU.I(1), .005f, false);
		
		Plotter.plot(bo.getInterneurons());
		Plotter.plot(bo.getInterneurons(), NEFEnsemble.X);
		
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
		
		Plotter.plot(sourceProbe.getData(), "source");
		Plotter.plot(destProbe.getData(), "dest");
		Plotter.plot(interProbe.getData(), "interneurons");
	}
	
	
	
	public static void main(String[] args) {
		NEFEnsembleImplTest test = new NEFEnsembleImplTest();
		try {
			test.testAddBiasOrigin();
		} catch (StructuralException e) {
			e.printStackTrace();
		} catch (SimulationException e) {
			e.printStackTrace();
		}
	}

}
