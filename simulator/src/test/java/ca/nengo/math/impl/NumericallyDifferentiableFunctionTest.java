package ca.nengo.math.impl;

import ca.nengo.math.Function;
import static org.junit.Assert.*;
import org.junit.Test;

public class NumericallyDifferentiableFunctionTest {
	@Test
	public void testGetDimension() {
		SigmoidFunction f = new SigmoidFunction();
		NumericallyDifferentiableFunction wrap = new NumericallyDifferentiableFunction(f, 0, 0.1f);
		assertEquals(f.getDimension(), wrap.getDimension());
		assertEquals(1, wrap.getDimension());
	}

	@Test
	public void testMap() {
		SigmoidFunction f = new SigmoidFunction();
		NumericallyDifferentiableFunction wrap = new NumericallyDifferentiableFunction(f, 0, 0.1f);
		
		assertEquals(wrap.map(new float[]{0}), f.map(new float[]{0}), 0.0f);
		assertEquals(wrap.map(new float[]{3}), f.map(new float[]{3}), 0.0f);
		assertEquals(wrap.map(new float[]{100}), f.map(new float[]{100}), 0.0f);
	}

	@Test
	public void testMultiMap() {
		SigmoidFunction f = new SigmoidFunction();
		NumericallyDifferentiableFunction wrap = new NumericallyDifferentiableFunction(f, 0, 0.1f);

		float[] values = f.multiMap(new float[][]{new float[]{3}, new float[]{-2}});
		float[] newVals = wrap.multiMap(new float[][]{new float[]{3}, new float[]{-2}});
		assertEquals(values[0], newVals[0], 0.0f);
		assertEquals(values[1], newVals[1], 0.0f);
	}
	
	@Test
	public void testGetDerivative() {
		SigmoidFunction f = new SigmoidFunction(-1f,0.5f,1f,2f);
		Function g = f.getDerivative();
		NumericallyDifferentiableFunction wrap = new NumericallyDifferentiableFunction(f, 0, 0.01f);
		Function gWrap = wrap.getDerivative();
		
		assertEquals(gWrap.getDimension(), g.getDimension());
		assertEquals(gWrap.map(new float[]{0f}), g.map(new float[]{0f}), .0001f);
		assertEquals(gWrap.map(new float[]{-1f}), g.map(new float[]{-1f}), .0001f);
		assertEquals(gWrap.map(new float[]{3f}), g.map(new float[]{3f}), .0001f);
	}

}
