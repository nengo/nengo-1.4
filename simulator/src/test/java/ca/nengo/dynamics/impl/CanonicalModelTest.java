package ca.nengo.dynamics.impl;

import ca.nengo.model.Units;
import static org.junit.Assert.*;
import org.junit.Test;

public class CanonicalModelTest {
	@Test
	public void testGetRealization() {
		LTISystem system = CanonicalModel.getRealization(new float[]{0f, 1f}, new float[]{10f, 100f}, 1f);
		
		float[][] A = system.getA(0f);
		assertEquals(0f, A[0][0], 0.001f);
		assertEquals(1f, A[0][1], 0.001f);
		assertEquals(-100f, A[1][0], 0.001f);
		assertEquals(-10f, A[1][1], 0.001f);

		float[][] B = system.getB(0f);
		assertEquals(0f, B[0][0], 0.001f);
		assertEquals(1f, B[1][0], 0.001f);

		float[][] C = system.getC(0f);
		assertEquals(1f, C[0][0], 0.001f);
		assertEquals(0f, C[0][1], 0.001f);

		float[][] D = system.getD(0f);
		assertEquals(1f, D[0][0], 0.001f);
	}
	
	@Test
	public void testIsControllableCanonical() {
		LTISystem system = CanonicalModel.getRealization(new float[]{0f, 1f}, new float[]{10f, 100f}, 1f);
		assertTrue(CanonicalModel.isControllableCanonical(system));
		
		system = new SimpleLTISystem(
				new float[]{-1f, -1f}, 
				new float[][]{new float[]{0f}, new float[]{1f}},
				new float[][]{new float[]{1f, 1f}}, 				
				new float[2], 
				new Units[]{Units.UNK}
		);
		assertFalse(CanonicalModel.isControllableCanonical(system));
		
		system = new LTISystem(
				new float[][]{new float[]{0f, 1f}, new float[]{1f, 2f}}, 
				new float[][]{new float[]{1f}, new float[]{1f}},
				new float[][]{new float[]{1f, 1f}}, 				
				new float[][]{new float[]{0f}},
				new float[2], 
				new Units[]{Units.UNK}
		);
		assertFalse(CanonicalModel.isControllableCanonical(system));
		
	}

	@Test
	public void testChangeTimeConstant() {
		//distinct roots
		LTISystem system = CanonicalModel.getRealization(new float[]{0f, 1f}, new float[]{30f, 200f}, 1f);
		system = CanonicalModel.changeTimeConstant(system, 1f/5f);
		float[][] A = system.getA(0f);
		assertEquals(-100f, A[1][0], 0.001f);
		assertEquals(-25f, A[1][1], 0.001f);
		
		//complex conjugate roots
		system = CanonicalModel.getRealization(new float[]{0f, 1f}, new float[]{2f, 2f}, 1f);
		system = CanonicalModel.changeTimeConstant(system, 1f/5f);
		A = system.getA(0f);
		assertEquals(-26f, A[1][0], 0.001f);
		assertEquals(-10f, A[1][1], 0.001f);
	}
}
