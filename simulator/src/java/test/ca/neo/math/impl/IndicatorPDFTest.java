/*
 * Created on 13-Jun-2006
 */
package ca.neo.math.impl;

import ca.neo.math.PDF;
import junit.framework.TestCase;

/**
 * Unit tests for IndicatorPDF. 
 * 
 * @author Bryan Tripp
 */
public class IndicatorPDFTest extends TestCase {

	/*
	 * Test method for 'ca.neo.math.impl.IndicatorPDF.sample()'
	 */
	public void testSample() {
		PDF pdf = new IndicatorPDF(-1, 1);
		
		for (int i = 0; i < 10; i++) {
			float[] s = pdf.sample();
			assertEquals(1, s.length);
			assertTrue(s[0] > -1 && s[0] < 1);
		}
	}

	/*
	 * Test method for 'ca.neo.math.impl.IndicatorPDF.getDimension()'
	 */
	public void testGetDimension() {
		PDF pdf = new IndicatorPDF(-1, 1);
		assertEquals(1, pdf.getDimension());
	}

	/*
	 * Test method for 'ca.neo.math.impl.IndicatorPDF.map(float[])'
	 */
	public void testMap() {
		PDF pdf = new IndicatorPDF(-1, 1);
		assertClose(0f, pdf.map(new float[]{-1.5f}));
		assertClose(.5f, pdf.map(new float[]{-0.5f}));
		assertClose(.5f, pdf.map(new float[]{0.5f}));
		assertClose(0f, pdf.map(new float[]{1.5f}));
	}

	/*
	 * Test method for 'ca.neo.math.impl.IndicatorPDF.multiMap(float[][])'
	 */
	public void testMultiMap() {
		PDF pdf = new IndicatorPDF(-1, 1);
		float[] result = pdf.multiMap(new float[][]{new float[]{0f}, new float[]{2f}});
		
		assertEquals(2, result.length);
		assertClose(.5f, result[0]);
		assertClose(0f, result[1]);
	}
	
	//b is within .01 of a
	private static void assertClose(float a, float b) {
		assertTrue(b > a-.01);
		assertTrue(b < a+.01);
	}

}
