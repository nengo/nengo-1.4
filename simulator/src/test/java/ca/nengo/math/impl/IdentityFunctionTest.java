package ca.nengo.math.impl;

import static org.junit.Assert.*;
import org.junit.Test;

public class IdentityFunctionTest {
	@Test
	public void testGetDimension() {
		IdentityFunction f = new IdentityFunction(3, 0);
		assertEquals(3, f.getDimension());
	}

	@Test
	public void testMap() {
		IdentityFunction f = new IdentityFunction(3, 0);
		assertEquals(.1f, f.map(new float[]{.1f, .2f, .3f}), .00001f);

		f = new IdentityFunction(3, 1);
		assertEquals(.2f, f.map(new float[]{.1f, .2f, .3f}), .00001f);
	}

	@Test
	public void testMultiMap() {
		IdentityFunction f = new IdentityFunction(3, 0);

		float[] values = f.multiMap(new float[][]{new float[]{.1f, .2f, .3f}, new float[]{.2f, .3f, .4f}});
		assertEquals(.1f, values[0], .00001f);
		assertEquals(.2f, values[1], .00001f);
	}
}
