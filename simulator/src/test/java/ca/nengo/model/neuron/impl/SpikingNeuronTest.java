package ca.nengo.model.neuron.impl;

import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.RealOutput;
import ca.nengo.model.SimulationException;
import ca.nengo.model.SimulationMode;
import ca.nengo.model.SpikeOutput;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Units;
import ca.nengo.model.impl.RealOutputImpl;
import ca.nengo.model.neuron.ExpandableSynapticIntegrator;
import ca.nengo.model.neuron.SpikeGenerator;
import org.apache.log4j.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

public class SpikingNeuronTest {
	private static Logger ourLogger = Logger.getLogger(SpikingNeuronTest.class);
	
	private ExpandableSynapticIntegrator myIntegrator = new LinearSynapticIntegrator(.001f, Units.ACU);
	private SpikeGenerator myGenerator = new LIFSpikeGenerator(.001f, .01f, .001f);
	private SpikingNeuron myNeuron = new SpikingNeuron(myIntegrator, myGenerator, 1, 0, "test");
	
	@Test
	public void testGetHistory() throws SimulationException {
		myNeuron.getHistory("I");
		myNeuron.getHistory("V");
		
		try {
			myNeuron.getHistory("foo");
			fail("Should have thrown exception due to nonexistent state 'foo'");
		} catch (SimulationException e) {} //exception is expected
	}	

	@Test
	public void testGetOrigins() {
		assertEquals(2, myNeuron.getOrigins().length);
		assertTrue(myNeuron.getOrigins()[0] instanceof SpikeGeneratorOrigin);
	}

	@Test
	public void testGetMode() {
		assertEquals(SimulationMode.DEFAULT, myNeuron.getMode());
		
		myNeuron.setMode(SimulationMode.PRECISE);
		assertEquals(SimulationMode.PRECISE, myNeuron.getMode());
		
		myNeuron.setMode(SimulationMode.CONSTANT_RATE);
		assertEquals(SimulationMode.CONSTANT_RATE, myNeuron.getMode());
	}

	@Test
	public void testRun() throws StructuralException, SimulationException {
		myIntegrator.addTermination("test", new float[]{1}, .005f, false);
		myIntegrator.getTerminations()[0].setValues(new RealOutputImpl(new float[]{5}, Units.SPIKES_PER_S, 0));
		
		myNeuron.run(0, .005f);
		InstantaneousOutput output = myNeuron.getOrigins()[0].getValues();
		assertTrue(output instanceof SpikeOutput);
		assertTrue(((SpikeOutput) output).getValues()[0] == false);
		
		myNeuron.run(0, .005f);
		output = myNeuron.getOrigins()[0].getValues();
		assertTrue(((SpikeOutput) output).getValues()[0] == true);
		
		myNeuron.setMode(SimulationMode.CONSTANT_RATE);
		myNeuron.run(0, .01f);
		output = myNeuron.getOrigins()[0].getValues();
		assertTrue(output instanceof RealOutput);
		assertTrue(((RealOutput) output).getValues()[0] > 100);
		ourLogger.info(((RealOutput) output).getValues()[0]);
	}
}
