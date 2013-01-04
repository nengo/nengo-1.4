package ca.nengo.util.impl;

import ca.nengo.util.VectorGenerator;
import org.junit.Test;
import static org.junit.Assert.*;

public class RandomHypersphereVGTest {
	@Test
	public void genVectors() {
		VectorGenerator vg = null; 
		
		vg = new RandomHypersphereVG(true, 1f, 1f);
		float[][] v = vg.genVectors(10, 3);
		for (int i = 0; i < v.length; i++) {
			float radius = (float) Math.pow(v[i][0]*v[i][0] + v[i][1]*v[i][1] + v[i][2]*v[i][2], .5);
			assertEquals(1f, radius, .0001f);
		}

		vg = new RandomHypersphereVG(true, 1f, 0f);
		v = vg.genVectors(10, 3);
		for (int i = 0; i < v.length; i++) {
			float radius = (float) Math.pow(v[i][0]*v[i][0] + v[i][1]*v[i][1] + v[i][2]*v[i][2], .5);
			assertEquals(1f, radius, .0001f);
		}

		vg = new RandomHypersphereVG(true, 1f, .5f);
		v = vg.genVectors(10, 3);
		for (int i = 0; i < v.length; i++) {
			float radius = (float) Math.pow(v[i][0]*v[i][0] + v[i][1]*v[i][1] + v[i][2]*v[i][2], .5);
			assertEquals(1f, radius, .0001f);
		}
		
		vg = new RandomHypersphereVG(false, 1f, 1f);
		v = vg.genVectors(10, 3);
		for (int i = 0; i < v.length; i++) {
			float radius = (float) Math.pow(v[i][0]*v[i][0] + v[i][1]*v[i][1] + v[i][2]*v[i][2], .5);
			assertTrue(radius < 1f);
		}
		
		vg = new RandomHypersphereVG(true, 2f, 1f);
		v = vg.genVectors(10, 3);
		for (int i = 0; i < v.length; i++) {
			float radius = (float) Math.pow(v[i][0]*v[i][0] + v[i][1]*v[i][1] + v[i][2]*v[i][2], .5);
			assertEquals(2f, radius, .0001f);
		}

		vg = new RandomHypersphereVG(true, 1f, 1f);
		v = vg.genVectors(10, 3);
		for (int i = 0; i < v.length; i++) {
			assertEquals(1f, Math.abs(v[i][0]) + Math.abs(v[i][1]) + Math.abs(v[i][2]), .0001f);
		}
	}
}
