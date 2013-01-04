package ca.nengo.util.impl;

import ca.nengo.model.Units;
import ca.nengo.util.TimeSeries1D;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TimeSeries1DImplTest {
	private TimeSeries1D myTimeSeries =
		new TimeSeries1DImpl(
			new float[]{0f, 1f},
			new float[]{1.1f, 2.1f},
			Units.UNK
		);
	
	@Test(expected=IllegalArgumentException.class)
	public void constructor() {
		new TimeSeries1DImpl(new float[]{0f}, new float[]{0f, 1f}, Units.UNK);
	}

	@Test
	public void getTimes() {
		assertEquals(2, myTimeSeries.getTimes().length);
		assertTrue(myTimeSeries.getTimes()[1] > 0);
	}

	@Test
	public void getValues() {
		assertEquals(2, myTimeSeries.getValues().length);
		assertTrue(myTimeSeries.getValues1D()[1] > 2);		
	}

	@Test
	public void getUnits() {
		assertEquals(Units.UNK, myTimeSeries.getUnits1D());
	}
}
