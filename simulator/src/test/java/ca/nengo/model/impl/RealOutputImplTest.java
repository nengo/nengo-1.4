package ca.nengo.model.impl;

import ca.nengo.model.RealOutput;
import ca.nengo.model.Units;
import static org.junit.Assert.*;
import org.junit.Test;

public class RealOutputImplTest {

	private RealOutput myRealOutput = new RealOutputImpl(new float[]{1f}, Units.SPIKES_PER_S, 0);

	@Test
	public void testGetValues() {
		assertEquals(1, myRealOutput.getValues().length);
		float val = myRealOutput.getValues()[0];
		assertTrue(val > .99f && val < 1.01f);
	}

	@Test
	public void testGetUnits() {
		assertEquals(Units.SPIKES_PER_S, myRealOutput.getUnits());
	}

	@Test
	public void testGetDimension() {
		assertEquals(1, myRealOutput.getDimension());
	}
}
