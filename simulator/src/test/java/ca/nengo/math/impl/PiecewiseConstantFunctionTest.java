package ca.nengo.math.impl;

import ca.nengo.math.Function;
import ca.nengo.plot.Plotter;
import static org.junit.Assert.*;
import org.junit.Test;

public class PiecewiseConstantFunctionTest {
	@Test
	public void testGetDimension() {
		PiecewiseConstantFunction f = new PiecewiseConstantFunction(new float[]{-1,0,1}, new float[]{.1f,.2f,.3f,.4f});
		assertEquals(1, f.getDimension());
	}

	@Test
	public void testMap() {
		PiecewiseConstantFunction f = new PiecewiseConstantFunction(new float[]{-1,0,1}, new float[]{.1f,.2f,.3f,.4f});
		assertEquals(.1f, f.map(new float[]{-2.f}), .00001f);
		assertEquals(.2f, f.map(new float[]{-0.5f}), .00001f);
		assertEquals(.3f, f.map(new float[]{0.f}), .00001f);
		assertEquals(.4f, f.map(new float[]{2.f}), .00001f);
	}

	@Test
	public void testMultiMap() {
		PiecewiseConstantFunction f = new PiecewiseConstantFunction(new float[]{-1,0,1}, new float[]{.1f,.2f,.3f,.4f});

		float[] values = f.multiMap(new float[][]{new float[]{3}, new float[]{-0.5f}});
		assertEquals(.4f, values[0], .00001f);
		assertEquals(.2f, values[1], .00001f);
	}

    public static void main(String args[]) {
        Function f = new PiecewiseConstantFunction(new float[0], new float[]{5});
        Plotter.plot(f, -1, .01f, 10, "");
    }
}
