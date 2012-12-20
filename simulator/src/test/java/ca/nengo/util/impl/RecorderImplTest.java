package ca.nengo.util.impl;

import ca.nengo.model.Probeable;
import ca.nengo.model.SimulationException;
import ca.nengo.model.Units;
import ca.nengo.util.Probe;
import ca.nengo.util.TimeSeries;
import java.util.Properties;
import static org.junit.Assert.*;
import org.junit.Test;

public class RecorderImplTest {
	private Probe myRecorder = new ProbeImpl();
	
	@Test(expected=SimulationException.class)
	public void expected() throws SimulationException {
		myRecorder.connect(new MockProbeable(1f), "y", true);
	}
	
	/*
	 * Test method for 'ca.bpt.cn.util.impl.RecorderImpl.getData()'
	 */
	@Test
	public void getData() throws SimulationException {
		myRecorder.connect(new MockProbeable(1f), "x", true);
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
	
	@Test
	public void samplingRate() throws SimulationException {
		myRecorder.connect(new MockProbeable(1f), "x", true);
		myRecorder.setSamplingRate(100);
		
		myRecorder.collect(0f);
		myRecorder.collect(.005f);
		myRecorder.collect(.01f);
		myRecorder.collect(.015f);
		
		TimeSeries ts = myRecorder.getData();
		assertEquals(2, ts.getValues().length);		
	}
	
	@Test
	public void retention() throws SimulationException {
		myRecorder.connect(new MockProbeable(1f), "x", true);
		myRecorder.collect(1);
		myRecorder.collect(2);
		
		TimeSeries ts = myRecorder.getData();
		assertEquals(2, ts.getValues().length);

		myRecorder.connect(new MockProbeable(1f), "x", false);
		myRecorder.collect(1);
		myRecorder.collect(2);
		
		ts = myRecorder.getData();
		assertEquals(1, ts.getValues().length);
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
