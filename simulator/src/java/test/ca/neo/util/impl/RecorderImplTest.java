/*
 * Created on 24-May-2006
 */
package ca.neo.util.impl;

import java.util.Properties;

import ca.neo.model.Probeable;
import ca.neo.model.SimulationException;
import ca.neo.model.Units;
import ca.neo.util.MU;
import ca.neo.util.Recorder;
import ca.neo.util.TimeSeries;
import ca.neo.util.impl.RecorderImpl;
import ca.neo.util.impl.TimeSeries1DImpl;
import junit.framework.TestCase;

/**
 * Unit tests for RecorderImpl. 
 * 
 * @author Bryan Tripp
 */
public class RecorderImplTest extends TestCase {

	private Recorder myRecorder;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		myRecorder = new RecorderImpl();
	}

	/*
	 * Test method for 'ca.bpt.cn.util.impl.RecorderImpl.getData()'
	 */
	public void testGetData() throws SimulationException {
		try {
			myRecorder.connect(new MockProbeable(1f), "y");
			fail("Should have thrown exception because state y does not exist");
		} catch (SimulationException e) {} //exception is expected
		
		myRecorder.connect(new MockProbeable(1f), "x");
		myRecorder.collect(1);
		
		TimeSeries ts = myRecorder.getData();
		assertEquals(1, ts.getValues().length);
		assertTrue(ts.getValues()[0][0] > 0);
		assertEquals(Units.UNK, ts.getUnits()[0]);
		
		myRecorder.collect(1);
		assertEquals(2, myRecorder.getData().getValues().length);
		
		myRecorder.reset();
		assertEquals(0, myRecorder.getData().getValues().length);
	}
	
	public void testSamplingRate() throws SimulationException {
		myRecorder.connect(new MockProbeable(1f), "x");
		myRecorder.setSamplingRate(100);
		
		myRecorder.collect(0f);
		myRecorder.collect(.005f);
		myRecorder.collect(.01f);
		myRecorder.collect(.015f);
		
		TimeSeries ts = myRecorder.getData();
		assertEquals(2, ts.getValues().length);		
	}
 
	private static class MockProbeable implements Probeable {

		private float myConstantValue;
		
		public MockProbeable(float constantValue) {
			myConstantValue = constantValue;
		}
		
		public TimeSeries getHistory(String stateName) throws SimulationException {
			if (!stateName.equals("x")) {
				throw new SimulationException("No such state");
			}
			
			return new TimeSeries1DImpl(new float[]{0}, new float[]{myConstantValue}, Units.UNK);
		}

		public Properties listStates() {
			Properties result = new Properties();
			result.setProperty("x", "example state");
			return result;
		}
		
	}
}
