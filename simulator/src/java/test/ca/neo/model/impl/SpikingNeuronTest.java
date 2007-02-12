/*
 * Created on 29-May-2006
 */
package ca.neo.model.impl;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.RealOutputImpl;
import ca.neo.model.neuron.ExpandableSynapticIntegrator;
import ca.neo.model.neuron.SpikeGenerator;
import ca.neo.model.neuron.SpikeOutput;
import ca.neo.model.neuron.impl.LIFSpikeGenerator;
import ca.neo.model.neuron.impl.LinearSynapticIntegrator;
import ca.neo.model.neuron.impl.SpikeGeneratorOrigin;
import ca.neo.model.neuron.impl.SpikingNeuron;
import junit.framework.TestCase;

/**
 * Unit tests for SpikingNeuron. 
 *  
 * @author Bryan Tripp
 */
public class SpikingNeuronTest extends TestCase {

	private ExpandableSynapticIntegrator myIntegrator;
	private SpikeGenerator myGenerator;
	private SpikingNeuron myNeuron;
	
	protected void setUp() throws Exception {
		super.setUp();
		myIntegrator = new LinearSynapticIntegrator(1, 0, .001f, Units.ACU);
		myGenerator = new LIFSpikeGenerator(.001f, .01f, .001f);
		myNeuron = new SpikingNeuron(myIntegrator, myGenerator);		
	}
	
	/*
	 * Test method for 'ca.bpt.cn.model.impl.SpikingNeuron.getHistory(String)'
	 */
	public void testGetHistory() throws SimulationException {
		myNeuron.getHistory("I");
		myNeuron.getHistory("V");
		
		try {
			myNeuron.getHistory("foo");
			fail("Should have thrown exception due to nonexistent state 'foo'");
		} catch (SimulationException e) {} //exception is expected
	}	

	/*
	 * Test method for 'ca.bpt.cn.model.impl.SpikingNeuron.getIntegrator()'
	 */
	public void testGetIntegrator() {
		assertEquals(myIntegrator, myNeuron.getIntegrator());
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.SpikingNeuron.getOrigins()'
	 */
	public void testGetOrigins() {
		assertEquals(1, myNeuron.getOrigins().length);
		assertTrue(myNeuron.getOrigins()[0] instanceof SpikeGeneratorOrigin);
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.SpikingNeuron.getMode()'
	 */
	public void testGetMode() {
		assertEquals(SimulationMode.DEFAULT, myNeuron.getMode());
		
		myNeuron.setMode(SimulationMode.PRECISE);
		assertEquals(SimulationMode.DEFAULT, myNeuron.getMode());
		
		myNeuron.setMode(SimulationMode.CONSTANT_RATE);
		assertEquals(SimulationMode.CONSTANT_RATE, myNeuron.getMode());
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.SpikingNeuron.run(float, float)'
	 */
	public void testRun() throws StructuralException, SimulationException {
		myIntegrator.addTermination("test", new float[]{1}, .005f);
		myIntegrator.getTerminations()[0].setValues(new RealOutputImpl(new float[]{5}, Units.SPIKES_PER_S));
		
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
		System.out.println(((RealOutput) output).getValues()[0]);
	}

}
