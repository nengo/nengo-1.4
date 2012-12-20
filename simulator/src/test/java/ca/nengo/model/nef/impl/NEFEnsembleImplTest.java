package ca.nengo.model.nef.impl;

import ca.nengo.math.Function;
import ca.nengo.math.impl.AbstractFunction;
import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.Projection;
import ca.nengo.model.SimulationException;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Units;
import ca.nengo.model.impl.FunctionInput;
import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.model.nef.NEFEnsembleFactory;
import ca.nengo.model.neuron.impl.SpikingNeuron;
import ca.nengo.plot.Plotter;
import ca.nengo.util.MU;
import ca.nengo.util.Probe;
import ca.nengo.util.TimeSeries;
import ca.nengo.util.impl.TimeSeriesImpl;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * TODO: this is a functional test with no failures ... convert to unit test
 * TODO: make sure performance optimization works with inhibitory projections
 */
public class NEFEnsembleImplTest {
	public void functionalTestAddBiasOrigin() throws StructuralException, SimulationException {
		NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();

		boolean regenerate = false;
		NEFEnsemble source = ef.make("source", 300, 1, "nefeTest_source", regenerate);
		NEFEnsemble dest = ef.make("dest", 300, 1, "nefeTest_dest", regenerate);
		
		Function f = new AbstractFunction(1) {
			private static final long serialVersionUID = 1L;
			@Override
			public float map(float[] from) {
				return from[0] - 1;
			}
		};
		FunctionInput input = new FunctionInput("input", new Function[]{f}, Units.UNK);
		
		Network network = new NetworkImpl();
		network.addNode(input);
		network.addNode(source);
		network.addNode(dest);

		source.addDecodedTermination("input", MU.I(1), .005f, false); //OK
		BiasOrigin bo = source.addBiasOrigin(source.getOrigin(NEFEnsemble.X), 200, "interneurons", true); //should have -ve bias decoders
		network.addNode(bo.getInterneurons()); //should be backwards response functions

		network.addProjection(input.getOrigin(FunctionInput.ORIGIN_NAME), source.getTermination("input"));
		network.addProjection(source.getOrigin(NEFEnsemble.X), dest.getTermination("source"));
		network.run(0, 2);
	}
	
	public void functionalTestBiasOriginError() throws StructuralException, SimulationException {
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
		network.addNode(pre);
		
		NEFEnsemble post = ef.make("post", 200, 1, "nefe_post", false);
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
	}
	
	@Test
	public void testClone() throws StructuralException, CloneNotSupportedException {
		NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();
		NEFEnsemble ensemble = ef.make("test", 100, 1);
		long startTime = System.currentTimeMillis();
		ensemble.clone();
		System.out.println(System.currentTimeMillis() - startTime);
	}
	
	public static void main(String[] args) {
		NEFEnsembleImplTest test = new NEFEnsembleImplTest();
		try {
			test.testClone();
		} catch (StructuralException e) {
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testKillNeurons() throws StructuralException
	{
		NEFEnsembleFactoryImpl ef = new NEFEnsembleFactoryImpl();
		NEFEnsembleImpl nef1 = (NEFEnsembleImpl)ef.make("nef1", 1000, 1);
		
		nef1.killNeurons(0.0f,true);
		int numDead = countDeadNeurons(nef1);
		if(numDead != 0) {
			fail("Number of dead neurons outside expected range");
		}
		
		nef1.killNeurons(0.5f,true);
		numDead = countDeadNeurons(nef1);
		if(numDead < 400 || numDead > 600) {
			fail("Number of dead neurons outside expected range");
		}
		
		nef1.killNeurons(1.0f,true);
		numDead = countDeadNeurons(nef1);
		if(numDead != 1000) {
			fail("Number of dead neurons outside expected range");
		}
		
		NEFEnsembleImpl nef2 = (NEFEnsembleImpl)ef.make("nef2", 1, 1);
		nef2.killNeurons(1.0f,true);
		numDead = countDeadNeurons(nef2);
		if(numDead != 0)
			fail("Relay protection did not work");
		nef2.killNeurons(1.0f,false);
		numDead = countDeadNeurons(nef2);
		if(numDead != 1)
			fail("Number of dead neurons outside expected range");

	}
	private int countDeadNeurons(NEFEnsembleImpl pop)
	{
		Node[] neurons = pop.getNodes();
		int numDead = 0;
		
		for(int i = 0; i < neurons.length; i++)
		{
			SpikingNeuron n = (SpikingNeuron)neurons[i];
			if(n.getBias() == 0.0f && n.getScale() == 0.0f)
				numDead++;
		}
		
		return numDead;
	}
	
	@Test
	public void testAddDecodedSignalOrigin() throws StructuralException
	{
		NEFEnsembleFactoryImpl ef = new NEFEnsembleFactoryImpl();
		NEFEnsembleImpl ensemble = (NEFEnsembleImpl)ef.make("test", 5, 1);
		float[][] vals = new float[2][1];
		vals[0][0] = 1;
		vals[1][0] = 1;
		TimeSeriesImpl targetSignal = new TimeSeriesImpl(new float[]{0,1}, vals, new Units[]{Units.UNK});
		TimeSeriesImpl[] evalSignals = new TimeSeriesImpl[1];
		
		//test the per-dimension eval signals
		evalSignals[0] = new TimeSeriesImpl(new float[]{0,1}, vals, new Units[]{Units.UNK});
		ensemble.addDecodedSignalOrigin("test1", targetSignal, evalSignals, "AXON");
		if(ensemble.getOrigin("test1") == null)
			fail("Error creating per-dimension signal origin");
		
		//test the per-node eval signals
		vals[0] = new float[]{1, 1, 1, 1, 1};
		vals[1] = new float[]{1, 1, 1, 1, 1};
		evalSignals[0] = new TimeSeriesImpl(new float[]{0,1}, vals, new Units[]{Units.UNK,Units.UNK,Units.UNK,Units.UNK,Units.UNK});
		ensemble.addDecodedSignalOrigin("test2", targetSignal, evalSignals, "AXON");
		if(ensemble.getOrigin("test2") == null)
			fail("Error creating per-node signal origin");
	}

}
