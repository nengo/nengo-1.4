/*
 * Created on 29-May-2006
 */
package ca.neo.model.impl;

import ca.neo.model.SimulationException;
import ca.neo.model.neuron.impl.LIFSpikeGenerator;
import ca.neo.util.TimeSeries;
import ca.neo.util.TimeSeries1D;
import junit.framework.TestCase;

/**
 * Unit tests for LIFSpikeGenerator. 
 *  
 * @author Bryan Tripp
 */
public class LIFSpikeGeneratorTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.LIFSpikeGenerator.getHistory(String)'
	 */
	public void testGetHistory() throws SimulationException {
		LIFSpikeGenerator sg = new LIFSpikeGenerator(.0005f, .02f, .002f);
		TimeSeries history = sg.getHistory("V");
		assertTrue(history instanceof TimeSeries1D);
		assertEquals(0, history.getTimes().length);
		assertEquals(0, history.getValues().length);
		
		sg.run(new float[]{0f, .002f}, new float[]{1f, 1f});
		history = sg.getHistory("V");
		assertEquals(4, history.getTimes().length);
		assertEquals(4, history.getValues().length);
		assertTrue(history.getTimes()[1] > history.getTimes()[0]);
		assertTrue(history.getValues()[1][0] > history.getValues()[0][0]);

		try {
			sg.getHistory("X");
			fail("Should have thrown exception");
		} catch (SimulationException e) {} //exception is expected
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.LIFSpikeGenerator.reset(boolean)'
	 */
	public void testReset() throws SimulationException {
		float initialVoltage = .2f;
		LIFSpikeGenerator sg = new LIFSpikeGenerator(.0005f, .02f, .002f, initialVoltage);
		sg.run(new float[]{0f, .005f}, new float[]{1f, 1f});
		float[] voltage1 = ((TimeSeries1D) sg.getHistory("V")).getValues1D();

		sg.run(new float[]{0f, .005f}, new float[]{1f, 1f});
		float[] voltage2 = ((TimeSeries1D) sg.getHistory("V")).getValues1D();
		
		sg.reset(false);
		
		sg.run(new float[]{0f, .005f}, new float[]{1f, 1f});
		float[] voltage3 = ((TimeSeries1D) sg.getHistory("V")).getValues1D();
		
		assertBetween(voltage1[0], .2f, .23f);
		assertBetween(voltage2[0], .37f, .4f);
		assertBetween(voltage3[0], .2f, .23f);
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.LIFSpikeGenerator.runConstantRate(float, float)'
	 */
	public void testRunConstantRate() {
		float maxTimeStep = .0005f;
		float[] current = new float[]{0f, 2f, 5f};
		float[] tauRC = new float[]{0.01f, .02f};
		float[] tauRef = new float[]{.001f, .002f};

		LIFSpikeGenerator sg = new LIFSpikeGenerator(maxTimeStep, tauRC[0], tauRef[0]);
		assertBetween(sg.runConstantRate(0f, current[0]), -.001f, .001f);
		assertBetween(sg.runConstantRate(0f, current[1]), 126f, 127f);
		assertBetween(sg.runConstantRate(0f, current[2]), 309f, 310f);
				
		sg = new LIFSpikeGenerator(maxTimeStep, tauRC[0], tauRef[1]);
		assertBetween(sg.runConstantRate(0f, current[0]), -.001f, .001f);
		assertBetween(sg.runConstantRate(0f, current[1]), 111f, 112f);
		assertBetween(sg.runConstantRate(0f, current[2]), 236f, 237f);
				
		sg = new LIFSpikeGenerator(maxTimeStep, tauRC[1], tauRef[0]);
		assertBetween(sg.runConstantRate(0f, current[0]), -.001f, .001f);
		assertBetween(sg.runConstantRate(0f, current[1]), 67f, 68f);
		assertBetween(sg.runConstantRate(0f, current[2]), 183f, 184f);				

		sg = new LIFSpikeGenerator(maxTimeStep, tauRC[1], tauRef[1]);
		assertBetween(sg.runConstantRate(0f, current[0]), -.001f, .001f);
		assertBetween(sg.runConstantRate(0f, current[1]), 63f, 64f);
		assertBetween(sg.runConstantRate(0f, current[2]), 154f, 155f);
	}
	
	/*
	 * Test method for 'ca.bpt.cn.model.impl.LIFSpikeGenerator.run(float[], float[])'
	 */
	public void testRun() throws SimulationException {
		float maxTimeStep = .0005f;
		float[] current = new float[]{0f, 2f, 5f};
		float[] tauRC = new float[]{0.01f, .02f};
		float[] tauRef = new float[]{.001f, .002f};

		LIFSpikeGenerator sg = new LIFSpikeGenerator(maxTimeStep, tauRC[0], tauRef[0]);
		assertSpikesCloseToRate(sg, current[0], 1);
		assertSpikesCloseToRate(sg, current[1], 3);
		assertSpikesCloseToRate(sg, current[2], 3);
				
		sg = new LIFSpikeGenerator(maxTimeStep, tauRC[0], tauRef[1]);
		assertSpikesCloseToRate(sg, current[0], 1);
		assertSpikesCloseToRate(sg, current[1], 4);
		assertSpikesCloseToRate(sg, current[2], 1);
				
		sg = new LIFSpikeGenerator(maxTimeStep, tauRC[1], tauRef[0]);
		assertSpikesCloseToRate(sg, current[0], 1);
		assertSpikesCloseToRate(sg, current[1], 1);
		assertSpikesCloseToRate(sg, current[2], 10);

		sg = new LIFSpikeGenerator(maxTimeStep, tauRC[1], tauRef[1]);
		assertSpikesCloseToRate(sg, current[0], 1);
		assertSpikesCloseToRate(sg, current[1], 1);
		assertSpikesCloseToRate(sg, current[2], 10);
	}

	private static void assertBetween(float value, float low, float high) {
		assertTrue(value + " is out of range", value > low && value < high);
	}
	
	private static void assertSpikesCloseToRate(LIFSpikeGenerator sg, float current, float tolerance) throws SimulationException {
		float stepSize = .001f;
		int steps = 1000;
		float rate = sg.runConstantRate(0f, current);
		
		int spikeCount = 0;
		for (int i = 0; i < steps; i++) {
			boolean spike = sg.run(new float[]{stepSize * (float) i, stepSize * (float) (i+1)}, 
					new float[]{current, current});
			
			if (spike) {
				spikeCount++;
			}
		}
		
		assertTrue(spikeCount + " spikes in simulation, " + rate + " expected", 
				spikeCount > rate-tolerance && spikeCount < rate+tolerance);
	}

}
