package ca.nengo.math.impl;

import static org.junit.Assert.*;
import org.junit.Test;

public class FourierFunctionTest {
	@Test
	public void testFourierFunctionFloatArrayFloatArrayFloatArray() {
		FourierFunction f = new FourierFunction(new float[]{1.5f}, new float[]{5f}, new float[]{.2f});
		assertClose(0.0000f, f.map(new float[]{.2f}));
		assertClose(1.5451f, f.map(new float[]{1.5f}));
		assertClose(2.9389f, f.map(new float[]{2.8f}));
	}

	/**
	 * TODO:  Make this self-verifying
	 */
	@Test
	public void testFourierFunctionFloatFloatFloat() {
		int n = 100;
		float[][] from = new float[n][];
		float[] from2 = new float[n];
		for (int i = 0; i < 100; i++) {
			float x = (float) i / (float) n;
			from[i] = new float[]{x};
			from2[i] = x;
		}
	}

	@Test
	public void testGetDimension() {
		FourierFunction f = new FourierFunction(new float[]{1f}, new float[]{1f}, new float[]{0f});
		assertEquals(1, f.getDimension());
	}

	@Test
	public void testMultiMap() {
		FourierFunction f = new FourierFunction(new float[]{1f}, new float[]{1f}, new float[]{0f});

		float[] from1 = new float[]{.5f};
		float val1 = f.map(from1);
		float[] from2 = new float[]{.6f};
		float val2 = f.map(from2);
		
		float[] vals = f.multiMap(new float[][]{from1, from2});
		assertClose(vals[0], val1);
		assertClose(vals[1], val2);
	}
	
	private void assertClose(float a, float b) {
		float tolerance = .0001f;
		if (a > b + tolerance || a < b - tolerance) {
			fail("Values " + a + " and " + b + " are not close enough");
		}
	}
	
	@Test
	public void testClone() throws CloneNotSupportedException {
		FourierFunction f1 = new FourierFunction(new float[]{1.5f}, new float[]{5f}, new float[]{.2f});
		FourierFunction f2 = (FourierFunction) f1.clone();
		f2.setFrequencies(new float[][]{new float[]{2f}});
		assertClose(0.0000f, f1.map(new float[]{.2f}));
		assertClose(1.5451f, f1.map(new float[]{1.5f}));
		assertClose(2.9389f, f1.map(new float[]{2.8f}));
	}
}
