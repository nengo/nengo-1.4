package ca.nengo.math.impl;

import static org.junit.Assert.*;
import org.junit.Test;

public class ConstantFunctionTest {
	@Test
	public void testGetDimension() {
		ConstantFunction f = new ConstantFunction(1, 1f);
		assertEquals(1, f.getDimension());

		f = new ConstantFunction(10, 1f);
		assertEquals(10, f.getDimension());
	}

	@Test
	public void testMap() {
		ConstantFunction f = new ConstantFunction(1, 1f);
		assertEquals(1f, f.map(new float[]{0f}), .00001f);
		assertEquals(1f, f.map(new float[]{1f}), .00001f);
	}

	@Test
	public void testMultiMap() {
		ConstantFunction f = new ConstantFunction(1, 1f);
		float[] result = f.multiMap(new float[][]{new float[]{0f}, new float[]{1f}});
		
		assertEquals(2, result.length);
		assertEquals(1f, result[0], .00001f);
		assertEquals(1f, result[1], .00001f);
	}
}
