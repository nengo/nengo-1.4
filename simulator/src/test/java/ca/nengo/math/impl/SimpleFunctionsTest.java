package ca.nengo.math.impl;

import static org.junit.Assert.*;
import org.junit.Test;

public class SimpleFunctionsTest {

	private static float ourTolerance = 1e-10f;
	
	@Test
	public void testSin() {
		assertEquals(-0.54402111088936981340474766185138f, 
				new SimpleFunctions.Sin().map(new float[]{10}), ourTolerance);
	}
	
	@Test
	public void testCos() {
		assertEquals(-0.83907152907645245225886394782406f, 
				new SimpleFunctions.Cos().map(new float[]{10}), ourTolerance);
	}
	
	@Test
	public void testTan() {
		assertEquals(0.64836082745908667125912493300981f, 
				new SimpleFunctions.Tan().map(new float[]{10}), ourTolerance);
	}
	
	@Test
	public void testAsin() {
		assertEquals(0.5236f, 
				new SimpleFunctions.Asin().map(new float[]{.5f}), .0001f);
	}
	
	@Test
	public void testAcos() {
		assertEquals(1.0472f, 
				new SimpleFunctions.Acos().map(new float[]{.5f}), .0001f);
	}
	
	@Test
	public void testAtan() {
		assertEquals(0.4636f, 
				new SimpleFunctions.Atan().map(new float[]{.5f}), .0001f);
	}
	
	@Test
	public void testExp() {
		assertEquals(22026f, 
				new SimpleFunctions.Exp().map(new float[]{10}), 1);
	}
	
	@Test
	public void testLog2() {
		assertEquals(3.3219f, 
				new SimpleFunctions.Log2().map(new float[]{10}), .0001f);
	}
	
	@Test
	public void testLog10() {
		assertEquals(1f, 
				new SimpleFunctions.Log10().map(new float[]{10}), ourTolerance);
	}
	
	@Test
	public void testLn() {
		assertEquals(2.3026f, 
				new SimpleFunctions.Ln().map(new float[]{10}), .0001f);
	}
	
	@Test
	public void testPow() {
		assertEquals(3162.2776601683793319988935444327f, 
				new SimpleFunctions.Pow().map(new float[]{10, 3.5f}), ourTolerance);
	}
	
	@Test
	public void testMax() {
		assertEquals(10f, 
				new SimpleFunctions.Max().map(new float[]{10, 3.5f}), ourTolerance);
	}
	
	@Test
	public void testMin() {
		assertEquals(3.5f, 
				new SimpleFunctions.Min().map(new float[]{10, 3.5f}), ourTolerance);
	}
	
	@Test
	public void testSqrt() {
		assertEquals(3.1622776601683793319988935444327f, 
				new SimpleFunctions.Sqrt().map(new float[]{10}), ourTolerance);
	}
	
}
