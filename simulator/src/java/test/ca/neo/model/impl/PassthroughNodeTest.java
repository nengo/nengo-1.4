/*
 * Created on 3-Aug-07
 */
package ca.neo.model.impl;

import java.util.HashMap;
import java.util.Map;

import ca.neo.TestUtil;
import ca.neo.model.RealOutput;
import ca.neo.model.SimulationException;
import ca.neo.model.SpikeOutput;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.util.MU;
import junit.framework.TestCase;

/**
 * Unit tests for PassthroughNode. 
 * 
 * @author Bryan Tripp
 */
public class PassthroughNodeTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testRun() throws SimulationException, StructuralException {
		PassthroughNode node1 = new PassthroughNode("test", 2);
		node1.getTermination(PassthroughNode.TERMINATION).setValues(
				new SpikeOutputImpl(new boolean[]{true, false}, Units.UNK, 0));
		node1.run(0, .01f);
		SpikeOutput out1 = (SpikeOutput) node1.getOrigin(PassthroughNode.ORIGIN).getValues();
		assertEquals(true, out1.getValues()[0]);
		assertEquals(false, out1.getValues()[1]);
		
		Map<String, float[][]> terminations2 = new HashMap<String, float[][]>(10);
		terminations2.put("a", MU.I(2));
		terminations2.put("b", MU.I(2));
		PassthroughNode node2 = new PassthroughNode("test2", 2, terminations2);
		node2.getTermination("a").setValues(new RealOutputImpl(new float[]{10, 5}, Units.UNK, 0));
		node2.getTermination("b").setValues(new RealOutputImpl(new float[]{1, 0}, Units.UNK, 0));
		node2.run(0, .01f);
		RealOutput out2 = (RealOutput) node2.getOrigin(PassthroughNode.ORIGIN).getValues();
		TestUtil.assertClose(11, out2.getValues()[0], .001f);
		TestUtil.assertClose(5, out2.getValues()[1], .001f);
		
		Map<String, float[][]> terminations3 = new HashMap<String, float[][]>(10);
		terminations3.put("a", new float[][]{new float[]{1, -1}});
		PassthroughNode node3 = new PassthroughNode("test3", 1, terminations3);
		node3.getTermination("a").setValues(new RealOutputImpl(new float[]{10, 3}, Units.UNK, 0));
		node3.run(0, .01f);
		RealOutput out3 = (RealOutput) node3.getOrigin(PassthroughNode.ORIGIN).getValues();
		TestUtil.assertClose(7, out3.getValues()[0], .001f);
	}

}
