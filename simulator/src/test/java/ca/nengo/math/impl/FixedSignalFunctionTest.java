package ca.nengo.math.impl;

import ca.nengo.math.Function;
import ca.nengo.model.StructuralException;
import static org.junit.Assert.*;
import org.junit.Test;

public class FixedSignalFunctionTest {
	@Test
	public void testMap() throws StructuralException {
		float[][] sig = new float[3][1];
		sig[0][0] = 0;
		sig[1][0] = 1;
		sig[2][0] = 2;
		Function f = new FixedSignalFunction(sig , 0);

		assertEquals(0f, f.map(new float[]{0f}), .00001f);
		assertEquals(1f, f.map(new float[]{0f}), .00001f);
		assertEquals(2f, f.map(new float[]{0f}), .00001f);
		assertEquals(0f, f.map(new float[]{0f}), .00001f);
		assertEquals(1f, f.map(new float[]{0f}), .00001f);
		assertEquals(2f, f.map(new float[]{0f}), .00001f);
	}
}
