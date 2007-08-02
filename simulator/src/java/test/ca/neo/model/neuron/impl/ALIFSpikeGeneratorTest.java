/*
 * Created on 1-Aug-07
 */
package ca.neo.model.neuron.impl;

import ca.neo.TestUtil;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
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

	public static void main(String[] args) {
		ALIFSpikeGeneratorTest test = new ALIFSpikeGeneratorTest();
		try {
			test.testGetAdaptedRate();
		} catch (SimulationException e) {
			e.printStackTrace();
		}
	}
}
