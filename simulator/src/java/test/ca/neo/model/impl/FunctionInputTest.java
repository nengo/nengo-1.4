/*
 * Created on 24-Jul-2006
 */
package ca.neo.model.impl;

import ca.neo.math.Function;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import junit.framework.TestCase;

public class FunctionInputTest extends TestCase {

	/*
	 * Test method for 'ca.neo.model.impl.FunctionInput.getName()'
	 */
	public void testGetName() throws StructuralException {
		String name = "test";
		FunctionInput input = new FunctionInput(name, new Function[]{new ConstantFunction(1, 1f)}, Units.UNK);
		assertEquals(name, input.getName());
	}

	/*
	 * Test method for 'ca.neo.model.impl.FunctionInput.getDimensions()'
	 */
	public void testGetDimensions() throws StructuralException {
		FunctionInput input = new FunctionInput("test", new Function[]{new ConstantFunction(1, 1f)}, Units.UNK);
		assertEquals(1, input.getDimensions());

		input = new FunctionInput("test", new Function[]{new ConstantFunction(1, 1f), new ConstantFunction(1, 1f)}, Units.UNK);
		assertEquals(2, input.getDimensions());		
		
		try {
			input = new FunctionInput("test", new Function[]{new ConstantFunction(2, 1f)}, Units.UNK);
			fail("Should have thrown exception due to 2-D function");
		} catch (Exception e) {} //exception is expected
	}

	/*
	 * Test method for 'ca.neo.model.impl.FunctionInput.getValues()'
	 */
	public void testGetValues() throws StructuralException, SimulationException {
		FunctionInput input = new FunctionInput("test", new Function[]{new ConstantFunction(1, 1f), new ConstantFunction(1, 2f)}, Units.UNK);
		assertEquals(2, input.getValues().getDimension());
		assertEquals(2, ((RealOutput) input.getValues()).getValues().length);
		
		input.run(0f, 1f);
		assertEquals(2, input.getValues().getDimension());
		assertEquals(2, ((RealOutput) input.getValues()).getValues().length);
		float value = ((RealOutput) input.getValues()).getValues()[0];
		assertTrue(value > .9f);
		value = ((RealOutput) input.getValues()).getValues()[1];
		assertTrue(value > 1.9f);
	}

	/*
	 * Test method for 'ca.neo.model.impl.FunctionInput.getMode()'
	 */
	public void testGetMode() throws StructuralException {
		FunctionInput input = new FunctionInput("test", new Function[]{new ConstantFunction(1, 1f)}, Units.UNK);
		assertEquals(SimulationMode.DEFAULT, input.getMode());
	}

	/*
	 * Test method for 'ca.neo.model.impl.FunctionInput.getHistory(String)'
	 */
	public void testGetHistory() throws StructuralException, SimulationException {
		FunctionInput input = new FunctionInput("test", new Function[]{new ConstantFunction(1, 1f), new ConstantFunction(1, 2f)}, Units.UNK);
		assertEquals(1, input.listStates().size());
		assertTrue(input.listStates().get("input") != null);
		
		assertEquals(1, input.getHistory("input").getValues().length);
		assertTrue(input.getHistory("input").getValues()[0][0] < .5f);
		
		input.run(0f, 1f);
		
		assertEquals(1, input.getHistory("input").getValues().length);
		assertTrue(input.getHistory("input").getValues()[0][0] > .5f);
	}

}
