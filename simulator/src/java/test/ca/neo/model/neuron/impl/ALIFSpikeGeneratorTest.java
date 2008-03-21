/*
 * Created on 1-Aug-07
 */
package ca.neo.model.neuron.impl;

import ca.neo.TestUtil;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.SpikeOutput;
import ca.neo.util.TimeSeries;
import junit.framework.TestCase;

/**
 * Unit tests for ALIFSpikeGenerator. 
 * 
 * @author Bryan Tripp
 */
public class ALIFSpikeGeneratorTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetOnsetRate() throws SimulationException {
		float I = 10;		
		ALIFSpikeGenerator g1 = new ALIFSpikeGenerator(.002f, .02f, .2f, .1f);
		float rate = run(g1, .001f, 1, I);
		TestUtil.assertClose(rate, g1.getOnsetRate(I), .5f);

		ALIFSpikeGenerator g2 = new ALIFSpikeGenerator(.001f, .01f, .1f, .2f);
		rate = run(g2, .001f, 1, I);
		TestUtil.assertClose(rate, g2.getOnsetRate(I), .5f);
	}
	
	public void testGetAdaptedRate() throws SimulationException {
		float I = 10;
		ALIFSpikeGenerator g1 = new ALIFSpikeGenerator(.002f, .02f, .2f, .1f);
		float rate = run(g1, .001f, 1000, I);
		TestUtil.assertClose(rate, g1.getAdaptedRate(I), .5f);
		
		ALIFSpikeGenerator g2 = new ALIFSpikeGenerator(.002f, .02f, .1f, .2f);
		rate = run(g2, .001f, 1000, I);
		TestUtil.assertClose(rate, g2.getAdaptedRate(I), .75f);

		//TODO: are these too far off (~ 0.75%)?
		
		ALIFSpikeGenerator g3 = new ALIFSpikeGenerator(.001f, .01f, .1f, .2f);
		rate = run(g3, .001f, 1000, I);
		TestUtil.assertClose(rate, g3.getAdaptedRate(I), 1.5f);
		
		I = 15;
		rate = run(g3, .001f, 1000, I);
		TestUtil.assertClose(rate, g3.getAdaptedRate(I), 2f);
	}

	//returns final firing rate
	private static float run(ALIFSpikeGenerator generator, float dt, int steps, float current) throws SimulationException {
		generator.setMode(SimulationMode.RATE);
		
		for (int i = 0; i < steps; i++) {
			generator.run(new float[]{i*dt, (i+1)*dt}, new float[]{current, current});
		}
		
		TimeSeries history = generator.getHistory("rate");
		return history.getValues()[0][0];
	}

	public void testRun() throws SimulationException {
		float maxTimeStep = .0005f;
		float[] current = new float[]{0f, 2f, 5f};
		float[] tauRC = new float[]{0.01f, .02f};
		float[] tauRef = new float[]{.001f, .002f};
		float[] tauN = new float[]{0.1f};

		ALIFSpikeGenerator sg = new ALIFSpikeGenerator(maxTimeStep, tauRC[0], tauRef[0], tauN[0]);
		assertSpikesCloseToRate(sg, current[0], 1);
		assertSpikesCloseToRate(sg, current[1], 5);
		assertSpikesCloseToRate(sg, current[2], 44);
				
		sg = new ALIFSpikeGenerator(maxTimeStep, tauRC[0], tauRef[1], tauN[0]);
		assertSpikesCloseToRate(sg, current[0], 1);
		assertSpikesCloseToRate(sg, current[1], 4);
		assertSpikesCloseToRate(sg, current[2], 44);
				
		sg = new ALIFSpikeGenerator(maxTimeStep, tauRC[1], tauRef[0], tauN[0]);
		assertSpikesCloseToRate(sg, current[0], 1);
		assertSpikesCloseToRate(sg, current[1], 2);
		assertSpikesCloseToRate(sg, current[2], 10);

		sg = new ALIFSpikeGenerator(maxTimeStep, tauRC[1], tauRef[1], tauN[0]);
		assertSpikesCloseToRate(sg, current[0], 1);
		assertSpikesCloseToRate(sg, current[1], 1);
		assertSpikesCloseToRate(sg, current[2], 10);
	}

	
	
	private static void assertSpikesCloseToRate(ALIFSpikeGenerator sg, float current, float tolerance) throws SimulationException {
		float stepSize = .001f;
		int steps = 1000;
		sg.setMode(SimulationMode.RATE);
		sg.reset(false);
		float rate = ((RealOutput) sg.run(new float[1], new float[]{current})).getValues()[0];
		rate=rate*steps*stepSize;
		
		int spikeCount = 0;
		sg.setMode(SimulationMode.DEFAULT);
		sg.reset(false);
		for (int i = 0; i < steps; i++) {
			boolean spike = ((SpikeOutput) sg.run(new float[]{stepSize * (float) i, stepSize * (float) (i+1)}, 
					new float[]{current, current})).getValues()[0];
			if (spike) {
				spikeCount++;
			}
		}
		
		System.out.println(spikeCount + " spikes in simulation, " + rate + " expected");
		assertTrue(spikeCount + " spikes in simulation, " + rate + " expected", 
				spikeCount > rate-tolerance && spikeCount < rate+tolerance);
	}
	
	
	
	public static void main(String[] args) {
		ALIFSpikeGeneratorTest test = new ALIFSpikeGeneratorTest();
		try {
			test.testGetAdaptedRate();
		} catch (SimulationException e) {
			e.printStackTrace();
		}
	}
}


