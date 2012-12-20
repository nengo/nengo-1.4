package ca.nengo.math.impl;

import static org.junit.Assert.*;
import org.junit.Test;

public class LinearFunctionTest {
	@Test
	public void testClone() throws CloneNotSupportedException {
		float[] map = new float[]{1, 1};
		LinearFunction f = new LinearFunction(map, 0, true);
		LinearFunction f1 = (LinearFunction) f.clone();
		f.getMap()[0] = 2;
		f.setBias(1);
		f.setRectified(false);
		
		assertTrue(f1.getMap()[0] < 1.5f);
		assertTrue(f1.getBias() < .5f);
		assertTrue(f1.getRectified());
	}
}
