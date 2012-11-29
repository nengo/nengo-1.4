/*
 * Created on 13-Mar-08
 */
package ca.nengo.model.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;
import ca.nengo.dynamics.impl.SimpleLTISystem;
import ca.nengo.model.ExpandableNode;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.util.MU;
import ca.nengo.util.ScriptGenException;

/**
 * Unit tests for EnsembleImpl.
 *
 * @author Bryan Tripp
 */
public class EnsembleImplTest extends TestCase {

	@Override
    protected void setUp() throws Exception {
		super.setUp();
	}

	public void testClone() throws StructuralException, CloneNotSupportedException {
        System.out.println("EnsembleImplTest");
		MockExpandableNode node1 = new MockExpandableNode("1", new Origin[0],
				new Termination[]{new BasicTermination(null, new SimpleLTISystem(1, 1, 1), null, "existing")});
		MockExpandableNode node2 = new MockExpandableNode("2", new Origin[0],
				new Termination[]{new BasicTermination(null, new SimpleLTISystem(1, 1, 1), null, "existing")});
		EnsembleImpl ensemble = new EnsembleImpl("ensemble", new Node[]{node1, node2});
		ensemble.addTermination("new", MU.uniform(2, 2, 1), .005f, false);

		EnsembleImpl copy = (EnsembleImpl) ensemble.clone();
        System.out.println("Termination Length");
        System.out.println( copy.getTerminations().length);
		assertEquals(2, copy.getTerminations().length);
		copy.removeTermination("new");
        System.out.println("Termination Name");
        System.out.println( copy.getTermination("existing").getClass().getName());
		assertTrue(copy.getTermination("existing") instanceof EnsembleTermination);
//		try {
//			copy.removeTermination("existing");
//			fail("Should have thrown exception (can't remove non-expanded terminations)");
//		} catch (StructuralException e) {
//			e.printStackTrace();
//		}
	}

	public class MockExpandableNode extends AbstractNode implements ExpandableNode {

		private static final long serialVersionUID = 1L;

		private final Map<String, Termination> myExpandedTerminations;

		public MockExpandableNode(String name, Origin[] origins, Termination[] terminations) {
			super(name, Arrays.asList(origins), Arrays.asList(terminations));
			myExpandedTerminations = new LinkedHashMap<String, Termination>(10);
		}

		public Termination addTermination(String name, float[][] weights, float tauPSC, boolean modulatory) throws StructuralException {
			Termination result = new BasicTermination(this, new SimpleLTISystem(1, 1, 1), null, name);
			myExpandedTerminations.put(name, result);
			return result;
		}

		public int getDimension() {
			return 1;
		}

		public Termination removeTermination(String name) throws StructuralException {
			return myExpandedTerminations.remove(name);
		}

		@Override
		public Termination getTermination(String name) throws StructuralException {
			if (myExpandedTerminations.containsKey(name)) {
				return myExpandedTerminations.get(name);
			} else {
				return super.getTermination(name);
			}
		}

		@Override
		public Termination[] getTerminations() {
			Termination[] result = new Termination[super.getTerminations().length + myExpandedTerminations.size()];
			int i = 0;
			for (Termination t : myExpandedTerminations.values()) {
				result[i++] = t;
			}
			System.arraycopy(super.getTerminations(), 0, result, i++, super.getTerminations().length);

			return result;
		}

		@Override
		public void run(float startTime, float endTime) {
        System.out.println("EnsembleImplTestRun");	}

		@Override
		public void reset(boolean randomize) {}

		public Node[] getChildren() {
			// TODO Auto-generated method stub
			return null;
		}

		public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
