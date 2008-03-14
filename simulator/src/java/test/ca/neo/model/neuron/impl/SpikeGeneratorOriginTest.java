package ca.neo.model.neuron.impl;

import org.apache.commons.lang.NotImplementedException;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.SpikeOutput;
import ca.neo.model.Units;
import ca.neo.model.impl.RealOutputImpl;
import ca.neo.model.impl.SpikeOutputImpl;
import ca.neo.model.neuron.Neuron;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.model.neuron.impl.SpikeGeneratorOrigin;
import junit.framework.TestCase;

/**
 * Unit tests for SpikeGeneratorOrigin. 
 * 
 * @author Bryan Tripp
 */
public class SpikeGeneratorOriginTest extends TestCase {

	private SpikeGeneratorOrigin myOrigin;
	private MockSpikeGenerator myGenerator;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		myGenerator = new MockSpikeGenerator();
		myOrigin = new SpikeGeneratorOrigin(null, myGenerator);
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.SpikeGeneratorOrigin.getName()'
	 */
	public void testGetName() {
		assertEquals(Neuron.AXON, myOrigin.getName());
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.SpikeGeneratorOrigin.getDimensions()'
	 */
	public void testGetDimensions() {
		assertEquals(1, myOrigin.getDimensions());
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.SpikeGeneratorOrigin.getValues()'
	 */
	public void testGetValues() throws SimulationException {
		myGenerator.setNextOutput(true, 1f);
		
		myOrigin.run(new float[]{0f}, new float[]{0f});
		InstantaneousOutput output = myOrigin.getValues();
		assertTrue(output instanceof SpikeOutput);
		assertEquals(1, output.getDimension());
		assertEquals(Units.SPIKES, output.getUnits());
		assertEquals(1, ((SpikeOutput) output).getValues().length);
		assertEquals(true, ((SpikeOutput) output).getValues()[0]);
		
		myGenerator.setMode(SimulationMode.CONSTANT_RATE);
		
		myOrigin.run(new float[]{0f}, new float[]{0f});
		output = myOrigin.getValues();
		assertTrue(output instanceof RealOutput);
		assertEquals(1, output.getDimension());
		assertEquals(Units.SPIKES_PER_S, output.getUnits());
		assertEquals(1, ((RealOutput) output).getValues().length);
		assertTrue(((RealOutput) output).getValues()[0] > .99f);		
	}
	
	private static class MockSpikeGenerator implements SpikeGenerator {

		private static final long serialVersionUID = 1L;
		
		private boolean myNextSpikeOutput;
		private float myNextRateOutput;
		private SimulationMode myMode = SimulationMode.DEFAULT;
		
		public void setNextOutput(boolean nextSpikeOutput, float nextRateOutput) {
			myNextSpikeOutput = nextSpikeOutput;
			myNextRateOutput = nextRateOutput;
		}
		
		public InstantaneousOutput run(float[] time, float[] current) {
			if (myMode.equals(SimulationMode.DEFAULT)) {
				return new SpikeOutputImpl(new boolean[]{myNextSpikeOutput}, Units.SPIKES, 0);
			} else {
				return new RealOutputImpl(new float[]{myNextRateOutput}, Units.SPIKES_PER_S, 0);
			}
		}

		public void reset(boolean randomize) {
			throw new NotImplementedException("not implemented");
		}

		public SimulationMode getMode() {
			return myMode;
		}

		public void setMode(SimulationMode mode) {
			myMode = mode;
		}

		@Override
		public SpikeGenerator clone() throws CloneNotSupportedException {
			return (SpikeGenerator) super.clone();
		}
		
	}

}
