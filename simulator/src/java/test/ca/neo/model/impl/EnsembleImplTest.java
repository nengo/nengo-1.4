/*
 * Created on 13-Mar-08
 */
package ca.neo.model.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ca.neo.dynamics.impl.SimpleLTISystem;
import ca.neo.model.ExpandableNode;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.util.MU;
import junit.framework.TestCase;

/**
 * Unit tests for EnsembleImpl. 
 * 
 * @author Bryan Tripp
 */
public class EnsembleImplTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testClone() throws StructuralException, CloneNotSupportedException {
		MockExpandableNode node1 = new MockExpandableNode("1", new Origin[0], 
				new Termination[]{new BasicTermination(null, new SimpleLTISystem(1, 1, 1), null, "existing")});
		MockExpandableNode node2 = new MockExpandableNode("2", new Origin[0], 
				new Termination[]{new BasicTermination(null, new SimpleLTISystem(1, 1, 1), null, "existing")});
		EnsembleImpl ensemble = new EnsembleImpl("ensemble", new Node[]{node1, node2});
		ensemble.addTermination("new", MU.uniform(2, 2, 1), .005f, false);
		
		EnsembleImpl copy = (EnsembleImpl) ensemble.clone();
		assertEquals(2, copy.getTerminations().length);		
		copy.removeTermination("new");		
		assertTrue(copy.getTermination("existing") instanceof EnsembleTermination);
		try {
			copy.removeTermination("existing");
			fail("Should have thrown exception (can't remove non-expanded terminations)");
		} catch (StructuralException e) {
//			e.printStackTrace();
		}
	}
	
	public class MockExpandableNode extends AbstractNode implements ExpandableNode {

		private static final long serialVersionUID = 1L;
		
		private Map<String, Termination> myExpandedTerminations;

		public MockExpandableNode(String name, Origin[] origins, Termination[] terminations) {
			super(name, Arrays.asList(origins), Arrays.asList(terminations));
			myExpandedTerminations = new HashMap<String, Termination>(10);
		}

		public Termination addTermination(String name, float[][] weights, float tauPSC, boolean modulatory) throws StructuralException {
			Termination result = new BasicTermination(this, new SimpleLTISystem(1, 1, 1), null, name);
			myExpandedTerminations.put(name, result);
			return result;
		}

		public int getDimension() {
			return 1;
		}

		public void removeTermination(String name) throws StructuralException {
			myExpandedTerminations.remove(name);
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
		
	}

}
