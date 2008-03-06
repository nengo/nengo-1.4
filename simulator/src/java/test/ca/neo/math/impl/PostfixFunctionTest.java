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
		PostfixFunction f = new PostfixFunction(new ArrayList(), "", dim);
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
		f = new PostfixFunction(l, "", 0);
		TestUtil.assertClose(5.5f, f.map(new float[0]), .0001f);

		l.clear();
		l.add(new Integer(0));
		f = new PostfixFunction(l, "", 1);
		TestUtil.assertClose(1f, f.map(new float[]{1f}), .0001f);

		l.clear();
		l.add(new Integer(0));
		l.add(new SineFunction(1));
		f = new PostfixFunction(l, "", 1);
		TestUtil.assertClose(0f, f.map(new float[]{(float) Math.PI}), .0001f);
	}

	/*
	 * Test method for 'ca.neo.math.impl.PostfixFunction.multiMap(float[][])'
	 */
	public void testMultiMap() {
		ArrayList l = new ArrayList();
		l.add(new Integer(0));
		PostfixFunction f = new PostfixFunction(l, "", 1);
		float[] values = f.multiMap(new float[][]{new float[]{1f}, new float[]{2f}});
		TestUtil.assertClose(1f, values[0], .0001f);
		TestUtil.assertClose(2f, values[1], .0001f);
	}
	
	public void testClone() throws CloneNotSupportedException {
		PostfixFunction f1 = new PostfixFunction("x0 + x1^2", 2);
		PostfixFunction f2 = (PostfixFunction) f1.clone();
		f2.setExpression("x1"); 
		assertEquals("x0 + x1^2", f1.getExpression());
		assertTrue(f1.map(new float[]{0, 2}) - f2.map(new float[]{0, 2}) > 1);
	}

}
