package ca.nengo.math.impl;

import static org.junit.Assert.*;
import org.junit.Test;

public class SigmoidFunctionTest {
	@Test
	public void testGetDimension() {
		SigmoidFunction f = new SigmoidFunction();
		assertEquals(1, f.getDimension());
		f = new SigmoidFunction(0f,1f,2f,3f);
		assertEquals(1, f.getDimension());
	}

	@Test
	public void testMap() {
		SigmoidFunction f = new SigmoidFunction();
		assertEquals(0.5f, f.map(new float[]{0}), .00001f);
		assertEquals(1f, f.map(new float[]{100}), .00001f);
		assertEquals(0.952574f, f.map(new float[]{3}), .00001f);
		f = new SigmoidFunction(0f,1f,2f,3f);
		assertEquals(2f, f.map(new float[]{-100}), .00001f);
		assertEquals(2.5f, f.map(new float[]{0}), .00001f);
		assertEquals(2.960834f, f.map(new float[]{.8f}), .00001f);
	}

	@Test
	public void testMultiMap() {
		SigmoidFunction f = new SigmoidFunction();

		float[] values = f.multiMap(new float[][]{new float[]{3}, new float[]{-2}});
		assertEquals(0.952574f, values[0], .00001f);
		assertEquals(0.1192029f, values[1], .00001f);
	}
	
	@Test
	public void testGetDerivative() {
		SigmoidFunction f = new SigmoidFunction(-1f,0.5f,1f,2f);
		AbstractFunction g = (AbstractFunction)f.getDerivative();
		
		assertEquals(1, g.getDimension());
		assertEquals(0.209987f, g.map(new float[]{0f}), .00001f);
		assertEquals(0.5f, g.map(new float[]{-1f}), .00001f);
		assertEquals(0.000670475f, g.map(new float[]{3f}), .00001f);
	}

}
