/*
 * Created on 26-May-2006
 */
package ca.neo.model.impl;

import org.apache.log4j.Logger;

import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.impl.LinearExponentialTermination;
import ca.neo.model.impl.RealOutputImpl;
import junit.framework.TestCase;

/**
 * Unit tests for LinearExponentialTermination. 
 * 
 * @author Bryan Tripp
 */
public class LinearExponentialTerminationTest extends TestCase {

	private static Logger ourLogger = Logger.getLogger(LinearExponentialTerminationTest.class);
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.LinearExponentialTermination.getName()'
	 */
	public void testGetName() {
		String name = "test";
		LinearExponentialTermination let = new LinearExponentialTermination(null, name, new float[0], 0f);
		assertEquals(name, let.getName());
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.LinearExponentialTermination.getDimensions()'
	 */
	public void testGetDimensions() {
		LinearExponentialTermination let = new LinearExponentialTermination(null, "test", new float[1], 0f);
		assertEquals(1, let.getDimensions());
		let = new LinearExponentialTermination(null, "test", new float[2], 0f);
		assertEquals(2, let.getDimensions());
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.LinearExponentialTermination.getProperty(String)'
	 */
	public void testGetProperty() throws StructuralException {
		LinearExponentialTermination let = new LinearExponentialTermination(null, "test", new float[1], 1.5f);
		
		assertEquals(3, let.getConfiguration().listPropertyNames().length);
		assertEquals(Termination.TAU_PSC, let.getConfiguration().listPropertyNames()[0]);
		
		Float tau = (Float) let.getConfiguration().getProperty(Termination.TAU_PSC);
		assertTrue(tau.floatValue() > 1.49 && tau.floatValue() < 1.51);
		
		try {
			let.getConfiguration().setProperty("unknown_property", "1");
			fail("Should have thrown exception due to unknown property name");
		} catch (StructuralException e) {} //exception is expected

		try {
			let.getConfiguration().setProperty(Termination.TAU_PSC, "not a float");
			fail("Should have thrown exception due to bad property value");
		} catch (StructuralException e) {} //exception is expected
		
		let.getConfiguration().setProperty(Termination.TAU_PSC, new Float(2.5f));
		tau = (Float) let.getConfiguration().getProperty(Termination.TAU_PSC);
		assertTrue(tau.floatValue() > 2.49 && tau.floatValue() < 2.51);
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.LinearExponentialTermination.reset(boolean)'
	 */
	public void testReset() throws SimulationException {
		LinearExponentialTermination let = new LinearExponentialTermination(null, "test", new float[]{2f}, 1f);
		let.setValues(new RealOutputImpl(new float[]{1f}, Units.ACU, 0));
		
		float current = let.updateCurrent(false, 1f, 0f);
		assertTrue(current > 1.99f);
		
		let.reset(false);
		
		current = let.updateCurrent(false, 0, 0);
		assertTrue(current < .01f);	
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.LinearExponentialTermination.setValues(InstantaneousOutput)'
	 */
	public void testSetValues() throws SimulationException {
		LinearExponentialTermination let = new LinearExponentialTermination(null, "test", new float[]{1f, 2f, 3f}, 1f);
		
		try {
			let.setValues(new SpikeOutputImpl(new boolean[]{true}, Units.SPIKES, 0));
			fail("Should have thrown exception because dimension of input is 1 (should be 3)");
		} catch (SimulationException e) {} //exception is expected
		
		let.setValues(new SpikeOutputImpl(new boolean[]{true, false, true}, Units.SPIKES, 0));
		float current = let.updateCurrent(true, 0, 0);
		assertClose(4f, current, .01f);
		
		let.reset(false);
		
		let.setValues(new RealOutputImpl(new float[]{1f, .1f, .01f}, Units.SPIKES_PER_S, 0));
		current = let.updateCurrent(false, 1, 0);
		assertClose(1.23f, current, .001f);
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.LinearExponentialTermination.updateCurrent(boolean, float, float)'
	 */
	public void testUpdateCurrent() throws SimulationException {
		float tol = .0001f;
		float tauPSC = .01f;
		LinearExponentialTermination let = new LinearExponentialTermination(null, "test", new float[]{1f}, tauPSC);		
		assertClose(0, let.updateCurrent(false, 0, 0), tol);
		
		let.setValues(new SpikeOutputImpl(new boolean[]{false}, Units.SPIKES, 0));
		assertClose(0, let.updateCurrent(true, 0, 0), tol);
		
		let.setValues(new RealOutputImpl(new float[]{0f}, Units.SPIKES_PER_S, 0));
		assertClose(0, let.updateCurrent(false, 1f, 0), tol);
		
		let.setValues(new SpikeOutputImpl(new boolean[]{true}, Units.SPIKES, 0));
		assertClose(1f/tauPSC, let.updateCurrent(true, 0, 0), tol);
		assertClose(0, let.updateCurrent(false, 0, tauPSC), tol); //which illustrates that we need time steps << tauPSC
		
		let.reset(false);
		
		//decay a spike to 0
		let.setValues(new SpikeOutputImpl(new boolean[]{true}, Units.SPIKES, 0));
		float current = let.updateCurrent(true, 0, 0);
		for (int i = 0; i < 150; i++) {
			current = let.updateCurrent(false, 0, tauPSC/10f);
		}
		ourLogger.debug("current: " + current);
		assertClose(0f, current, tol);
		
		//low-pass filter constant rate input
		let.setValues(new RealOutputImpl(new float[]{10f}, Units.SPIKES_PER_S, 0));
		current = 0;
		for (int i = 0; i < 120; i++) {
			float lastCurrent = current;
			current = let.updateCurrent(false, tauPSC/10f, tauPSC/10f);
			if (i % 20 == 0) {
				assertTrue(current > lastCurrent);
			}
		}
		ourLogger.debug("current: " + current);
		assertClose(10f, current, tol);		
	}

	//approximate assertEquals for floats
	private void assertClose(float target, float value, float tolerance) {
		assertTrue(value > target - tolerance && value < target + tolerance);
	}

}
