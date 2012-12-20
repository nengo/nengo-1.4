package ca.nengo.model.impl;

import ca.nengo.model.SpikeOutput;
import ca.nengo.model.Units;
import static org.junit.Assert.*;
import org.junit.Test;

public class SpikeOutputImplTest {
	private SpikeOutput mySpikeOutput = new SpikeOutputImpl(new boolean[]{true}, Units.SPIKES, 0);

	@Test
	public void testGetValues() {
		assertEquals(1, mySpikeOutput.getValues().length);
		assertEquals(true, mySpikeOutput.getValues()[0]);
	}

	@Test
	public void testGetUnits() {
		assertEquals(Units.SPIKES, mySpikeOutput.getUnits());
	}

	@Test
	public void testGetDimension() {
		assertEquals(1, mySpikeOutput.getDimension());
	}
}
