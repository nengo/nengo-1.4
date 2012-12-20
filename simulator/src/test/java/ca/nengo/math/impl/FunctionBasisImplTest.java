package ca.nengo.math.impl;

import ca.nengo.math.Function;
import ca.nengo.math.FunctionBasis;
import static org.junit.Assert.*;
import org.junit.Test;

public class FunctionBasisImplTest {

	private FunctionBasis myFunctionBasis = new FunctionBasisImpl(
		new Function[]{new MockFunction(-1), new MockFunction(1)}
	);
	
	@Test
	public void testGetDimensions() {
		assertEquals(2, myFunctionBasis.getBasisDimension());
	}

	@Test
	public void testGetFunction() {
		float[] from = new float[]{0, 0};
		assertTrue(myFunctionBasis.getFunction(0).map(from) < 0);
		assertTrue(myFunctionBasis.getFunction(1).map(from) > 0);
	}
	
	private static class MockFunction implements Function {

		private static final long serialVersionUID = 1L;
		
		float myConstantResult;
		
		public MockFunction(float constantResult) {
			myConstantResult = constantResult;
		}
		
		@Override
		public int getDimension() {
			return 1;
		}

		@Override
		public float map(float[] from) {
			return myConstantResult;
		}

		@Override
		public float[] multiMap(float[][] from) {
			throw new RuntimeException("not implemented");
		}
		
		@Override
		public Function clone() throws CloneNotSupportedException {
			return (Function) super.clone();
		}
	}
}
