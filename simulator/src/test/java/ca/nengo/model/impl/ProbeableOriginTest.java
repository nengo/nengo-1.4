package ca.nengo.model.impl;

import ca.nengo.model.Probeable;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SimulationException;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Units;
import ca.nengo.util.TimeSeries;
import ca.nengo.util.impl.TimeSeries1DImpl;
import java.util.Properties;
import static org.junit.Assert.*;
import org.junit.Test;

public class ProbeableOriginTest {
	@Test
	public void testGetName() throws StructuralException {
		String name = "test";
		ProbeableOrigin origin = new ProbeableOrigin(null, new MockProbeable(new float[0]), "x", 0, name);
		assertEquals(name, origin.getName());
	}

	@Test
	public void testGetDimensions() throws StructuralException {
		ProbeableOrigin origin = new ProbeableOrigin(null, new MockProbeable(new float[0]), "x", 0, "test");
		assertEquals(1, origin.getDimensions());
	}

	@Test
	public void testGetValues() throws StructuralException, SimulationException {
		//test that last TimeSeries value is returned, etc.
		MockProbeable p = new MockProbeable(new float[]{-1f, 1f});
		ProbeableOrigin origin = new ProbeableOrigin(null, p, "x", 0, "test");
		
		assertEquals(Units.UNK, origin.getValues().getUnits());
		assertTrue(origin.getValues() instanceof RealOutput);
		assertEquals(1, ((RealOutput) origin.getValues()).getDimension());
		assertTrue(((RealOutput) origin.getValues()).getValues()[0] > 0);
	}
	
	@Test
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
