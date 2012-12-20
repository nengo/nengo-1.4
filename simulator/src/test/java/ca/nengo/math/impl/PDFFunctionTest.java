package ca.nengo.math.impl;

import ca.nengo.math.Function;
import ca.nengo.model.StructuralException;
import static org.junit.Assert.*;
import org.junit.Test;

public class PDFFunctionTest {
	@Test
	public void testMap() throws StructuralException {
		Function f = new PDFFunction(new IndicatorPDF(0,10));
		
		int[] counts = new int[10];
		for(int i=0; i < 10000; i++)
			counts[(int)f.map(new float[]{0f})]++;
		for(int i=0; i < 10; i++)
			assertEquals(1000, counts[i], 100);
	}
}
