/*
 * Created on 15-Nov-07
 */
package ca.nengo.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.SimulationException;
import ca.nengo.model.Termination;
import ca.nengo.model.Units;
import ca.nengo.model.impl.AbstractEnsemble;
import ca.nengo.model.impl.AbstractNode;
import ca.nengo.model.impl.BasicOrigin;
import ca.nengo.util.ScriptGenException;
import junit.framework.TestCase;

/**
 * Unit tests for AbstractEnsemble. 
 * 
 * @author Bryan Tripp
 */
public class AbstractEnsembleTest extends TestCase {

	public void testFindCommon1DOrigins() {
		Origin one = new BasicOrigin(null, "2D", 2, Units.UNK);
		Origin two = new BasicOrigin(null, "unique", 1, Units.UNK);
		Origin three = new BasicOrigin(null, "shared1", 1, Units.UNK);
		Origin four = new BasicOrigin(null, "shared2", 1, Units.UNK);
		
		List<Origin> shared = new ArrayList<Origin>(3);
		shared.add(one);
		shared.add(three);
		shared.add(four);
		
		List<Origin> notshared = new ArrayList<Origin>(4);
		notshared.add(one);
		notshared.add(three);
		notshared.add(four);
		notshared.add(two);
		
		Node[] nodes = new Node[3];
		nodes[0] = new AbstractNode("a", shared, new ArrayList<Termination>(1)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void run(float startTime, float endTime)
					throws SimulationException {}

			@Override
			public void reset(boolean randomize) {}

			public Node[] getChildren() {
				return new Node[0];
			}

			public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException {
				return "";
			}};		
		nodes[1] = new AbstractNode("b", shared, new ArrayList<Termination>(1)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void run(float startTime, float endTime)
					throws SimulationException {}

			@Override
			public void reset(boolean randomize) {}

			public Node[] getChildren() {
				return new Node[0];
			}

			public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException {
				return "";
			}};		
		nodes[2] = new AbstractNode("c", notshared, new ArrayList<Termination>(1)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void run(float startTime, float endTime)
					throws SimulationException {}

			@Override
			public void reset(boolean randomize) {}

			public Node[] getChildren() {
				return new Node[0];
			}

			public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException {
				return "";
			}};
		
		List<String> origins = AbstractEnsemble.findCommon1DOrigins(nodes);
		assertEquals(2, origins.size());
		assertTrue(origins.contains(three.getName()));
		assertTrue(origins.contains(four.getName()));
	}

}
