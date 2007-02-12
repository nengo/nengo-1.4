/*
 * Created on 25-Jul-2006
 */
package ca.neo.math.impl;

import java.util.ArrayList;

import ca.neo.TestUtil;

import junit.framework.TestCase;

/**
 * Unit tests for PostfixFunction. 
 *  
 * @author Bryan Tripp
 */
public class PostfixFunctionTest extends TestCase {

	/*
	 * Test method for 'ca.neo.math.impl.PostfixFunction.getDimension()'
	 */
	public void testGetDimension() {
		int dim = 10;
		PostfixFunction f = new PostfixFunction(new ArrayList(), dim);
		assertEquals(dim, f.getDimension());
	}

	/*
	 * Test method for 'ca.neo.math.impl.PostfixFunction.map(float[])'
	 */
	public void testMap() {

		//some basic tests follow, and a more exhaustive list is included in DefaultFunctionInterpreterTest 
		PostfixFunction f = null; 
		
		ArrayList l = new ArrayList();
		l.add(new Float(5.5f));
		f = new PostfixFunction(l, 0);
		TestUtil.assertClose(5.5f, f.map(new float[0]), .0001f);

		l.clear();
		l.add(new Integer(0));
		f = new PostfixFunction(l, 1);
		TestUtil.assertClose(1f, f.map(new float[]{1f}), .0001f);

		l.clear();
		l.add(new Integer(0));
		l.add(new SineFunction());
		f = new PostfixFunction(l, 1);
		TestUtil.assertClose(0f, f.map(new float[]{(float) Math.PI}), .0001f);
	}

	/*
	 * Test method for 'ca.neo.math.impl.PostfixFunction.multiMap(float[][])'
	 */
	public void testMultiMap() {
		ArrayList l = new ArrayList();
		l.add(new Integer(0));
		PostfixFunction f = new PostfixFunction(l, 1);
		float[] values = f.multiMap(new float[][]{new float[]{1f}, new float[]{2f}});
		TestUtil.assertClose(1f, values[0], .0001f);
		TestUtil.assertClose(2f, values[1], .0001f);
	}

}
