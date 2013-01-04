package ca.nengo.util.impl;

import static org.junit.Assert.*;
import org.junit.Test;

public class SpikePatternImplTest {
	@Test
	public void getNumNeurons() {
		SpikePatternImpl sp = new SpikePatternImpl(10);
		assertEquals(10, sp.getNumNeurons());
	}

	@Test
	public void getSpikeTimes() {
		SpikePatternImpl sp = new SpikePatternImpl(2);
		
		for (int i = 0; i < 150; i++) { //important to test more than initial buffer size of 100
			if (i < 50) sp.addSpike(0, (float) i);
			sp.addSpike(1, (float) i);
		}
		
		assertEquals(50, sp.getSpikeTimes(0).length);
		assertEquals(150, sp.getSpikeTimes(1).length);
		
		float[] times = sp.getSpikeTimes(1);
		for (int i = 0; i < times.length; i++) {
			assertTrue(times[i] > (float) i - .0001f);
			assertTrue(times[i] < (float) i + .0001f);
		}
	}

}
