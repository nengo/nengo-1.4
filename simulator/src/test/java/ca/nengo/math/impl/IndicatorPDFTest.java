package ca.nengo.math.impl;

import ca.nengo.math.PDF;
import static org.junit.Assert.*;
import org.junit.Test;

public class IndicatorPDFTest {
	@Test
	public void testSample() {
		PDF pdf = new IndicatorPDF(-1, 1);
		
		for (int i = 0; i < 10; i++) {
			float[] s = pdf.sample();
			assertEquals(1, s.length);
			assertTrue(s[0] > -1 && s[0] < 1);
		}
		
		pdf = new IndicatorPDF(0, 0);
		assertEquals(0f, pdf.sample()[0], 0.0f);
	}

	@Test
	public void testGetDimension() {
		PDF pdf = new IndicatorPDF(-1, 1);
		assertEquals(1, pdf.getDimension());
	}

	@Test
	public void testMap() {
		PDF pdf = new IndicatorPDF(-1, 1);
		assertEquals(0f, pdf.map(new float[]{-1.5f}), 0.1f);
		assertEquals(.5f, pdf.map(new float[]{-0.5f}), 0.1f);
		assertEquals(.5f, pdf.map(new float[]{0.5f}), 0.1f);
		assertEquals(0f, pdf.map(new float[]{1.5f}), 0.1f);
		
		pdf = new IndicatorPDF(5, 5);
		assertEquals(Float.POSITIVE_INFINITY, pdf.map(new float[]{5}), 0.0f);
	}

	@Test
	public void testMultiMap() {
		PDF pdf = new IndicatorPDF(-1, 1);
		float[] result = pdf.multiMap(new float[][]{new float[]{0f}, new float[]{2f}});
		
		assertEquals(2, result.length);
		assertEquals(.5f, result[0], 0.1f);
		assertEquals(0f, result[1], 0.1f);
	}
}
