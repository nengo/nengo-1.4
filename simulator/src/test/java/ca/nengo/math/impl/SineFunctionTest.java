package ca.nengo.math.impl;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class SineFunctionTest {
	@Test
	public void getDimension() {
		SineFunction f = new SineFunction(0.5f);
		assertEquals(1, f.getDimension());
	}
	
	@Test
	public void getOmega() {
		SineFunction f = new SineFunction(0.5f);
		assertEquals(0.5f, f.getOmega(), 0.0f);
	}

	@Test
	public void map() {
		SineFunction f = new SineFunction(0.5f);
		assertEquals(0f, f.map(new float[]{0f}), .00001f);
		assertEquals(1f, f.map(new float[]{(float)Math.PI}), .00001f);
		assertEquals(0.84147f, f.map(new float[]{2f}), .00001f);
	}

	@Test
	public void multiMap() {
		SineFunction f = new SineFunction(0.5f);

		float[] values = f.multiMap(new float[][]{new float[]{3f}, new float[]{-2f}, new float[]{(float)-Math.PI}});
		assertEquals(0.997495f, values[0], .00001f);
		assertEquals(-0.84147f, values[1], .00001f);
		assertEquals(-1f, values[2], .00001f);
	}
}
