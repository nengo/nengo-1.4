package ca.nengo.math.impl;

import static org.junit.Assert.*;
import org.junit.Test;

public class PolynomialTest {
	@Test
	public void testGetDimension() {
		Polynomial f = new Polynomial(new float[]{-1,0,1,2});
		assertEquals(1, f.getDimension());
	}

	@Test
	public void testMap() {
		Polynomial f = new Polynomial(new float[]{-1,0,2,1});
		assertEquals(2, f.map(new float[]{1}), .00001f);
		assertEquals(15, f.map(new float[]{2}), .00001f);
		assertEquals(-1, f.map(new float[]{-2}), .00001f);
		assertEquals(-1, f.map(new float[]{0}), .00001f);
	}

	@Test
	public void testMultiMap() {
		Polynomial f = new Polynomial(new float[]{-1,0,2,-1});

		float[] values = f.multiMap(new float[][]{new float[]{3}, new float[]{-2}});
		assertEquals(-10, values[0], .00001f);
		assertEquals(15, values[1], .00001f);
	}
}
