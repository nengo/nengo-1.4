package ca.nengo.model.impl;

import java.util.Properties;

import ca.nengo.model.Probeable;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SimulationException;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Units;
import ca.nengo.model.impl.ProbeableOrigin;
import ca.nengo.util.TimeSeries;
import ca.nengo.util.impl.TimeSeries1DImpl;
import junit.framework.TestCase;

/**
 * Unit tests for Probeable Origin. 
 * 
 * @author Bryan Tripp
 */
public class ProbeableOriginTest extends TestCase {

	/*
	 * Test method for 'ca.bpt.cn.model.impl.ProbeableOrigin.getName()'
	 */
	public void testGetName() throws StructuralException {
		String name = "test";
		ProbeableOrigin origin = new ProbeableOrigin(null, new MockProbeable(new float[0]), "x", 0, name);
		assertEquals(name, origin.getName());
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.ProbeableOrigin.getDimensions()'
	 */
	public void testGetDimensions() throws StructuralException {
		ProbeableOrigin origin = new ProbeableOrigin(null, new MockProbeable(new float[0]), "x", 0, "test");
		assertEquals(1, origin.getDimensions());
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.ProbeableOrigin.getValues()'
	 */
	public void testGetValues() throws StructuralException, SimulationException {
//		MockProbeable p = new MockProbeable(new float[0]);
//		ProbeableOrigin origin = new ProbeableOrigin(p, "x", 0, "test");
//		
//		try {
//			origin.getValues();
//			fail("Should have thrown exception because state x TimeSeries has length 0");
//		} catch (SimulationException e) {} //exception is expected
		
		//test that last TimeSeries value is returned, etc.
		MockProbeable p = new MockProbeable(new float[]{-1f, 1f});
		ProbeableOrigin origin = new ProbeableOrigin(null, p, "x", 0, "test");
		
		assertEquals(Units.UNK, origin.getValues().getUnits());
		assertTrue(origin.getValues() instanceof RealOutput);
		assertEquals(1, ((RealOutput) origin.getValues()).getDimension());
		assertTrue(((RealOutput) origin.getValues()).getValues()[0] > 0);
	}
	
	public void testConstructor() {
		try {
			new ProbeableOrigin(null, new MockProbeable(new float[0]), "y", 0, "test");
			fail("Should have thrown exception because state y doesn't exist");
		} catch (StructuralException e) {} //exception is expected
	}

	private static class MockProbeable implements Probeable {

		private float[] myConstantValues;
		
		public MockProbeable(float[] constantValues) {
			myConstantValues = constantValues;
		}
		
		public TimeSeries getHistory(String stateName) throws SimulationException {
			if (!stateName.equals("x")) {
				throw new SimulationException("No such state");
			}
			
			return new TimeSeries1DImpl(new float[myConstantValues.length], myConstantValues, Units.UNK);
		}

		public Properties listStates() {
			Properties result = new Properties();
			result.setProperty("x", "example state");
			return result;
		}
	}
}
