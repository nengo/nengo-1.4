/*
 * Created on 24-May-2006
 */
package ca.nengo.model.impl;

import java.util.ArrayList;
import java.util.List;

import ca.nengo.model.Ensemble;
import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.SimulationException;
import ca.nengo.model.SimulationMode;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.model.neuron.Neuron;
import ca.nengo.util.SpikePattern;
import ca.nengo.util.VisiblyMutable;
import ca.nengo.util.VisiblyMutableUtils;
import junit.framework.TestCase;

public class NetworkImplTest extends TestCase {

	private Network myNetwork;

	protected void setUp() throws Exception {
		super.setUp();

		myNetwork = new NetworkImpl();
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.NetworkImpl.getNodes()'
	 */
	public void testGetNodes() throws StructuralException {
		Ensemble a = new MockEnsemble("a");
		myNetwork.addNode(a);
		myNetwork.addNode(new MockEnsemble("b"));

		assertEquals(2, myNetwork.getNodes().length);

		try {
			myNetwork.addNode(new MockEnsemble("a"));
			fail("Should have thrown exception due to duplicate ensemble name");
		} catch (StructuralException e) {
		} // exception is expected

		try {
			myNetwork.removeNode("c");
			fail("Should have thrown exception because named ensemble doesn't exist");
		} catch (StructuralException e) {
		} // exception is expected

		myNetwork.removeNode("b");
		assertEquals(1, myNetwork.getNodes().length);
		assertEquals(a, myNetwork.getNodes()[0]);
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.NetworkImpl.getProjections()'
	 */
	public void testGetProjections() throws StructuralException {
		Origin o1 = new ProjectionImplTest.MockOrigin("o1", 1);
		Origin o2 = new ProjectionImplTest.MockOrigin("o2", 1);
		Termination t1 = new ProjectionImplTest.MockTermination("t1", 1);
		Termination t2 = new ProjectionImplTest.MockTermination("t2", 1);
		Termination t3 = new ProjectionImplTest.MockTermination("t3", 2);

		myNetwork.addProjection(o1, t1);
		myNetwork.addProjection(o1, t2);

		assertEquals(2, myNetwork.getProjections().length);

		try {
			myNetwork.addProjection(o2, t1);
			fail("Should have thrown exception because termination t1 already filled");
		} catch (StructuralException e) {
		} // exception is expected

		try {
			myNetwork.addProjection(o1, t3);
			fail("Should have thrown exception because origin and termination have different dimensions");
		} catch (StructuralException e) {
		} // exception is expected

		myNetwork.removeProjection(t2);
		assertEquals(t1, myNetwork.getProjections()[0].getTermination());
	}
	
	public void testNodeNameChange() throws StructuralException {
		MockEnsemble e1 = new MockEnsemble("one");
		myNetwork.addNode(e1);
		
		MockEnsemble e2 = new MockEnsemble("two");
		myNetwork.addNode(e2);
		
		assertTrue(myNetwork.getNode("one") != null);
		
		e1.setName("foo");
		assertTrue(myNetwork.getNode("foo") != null);
		try {
			myNetwork.getNode("one");
			fail("Shouldn't exist any more");
		} catch (StructuralException e) {}
		
		try {
			e2.setName("foo");
			fail("Should have thrown exception on duplicate name");
		} catch (StructuralException e) {}
	}

	private static class MockEnsemble implements Ensemble {

		private static final long serialVersionUID = 1L;

		private String myName;
		private transient List<VisiblyMutable.Listener> myListeners;

		public MockEnsemble(String name) {
			myName = name;
		}

		public String getName() {
			return myName;
		}
		
		public void setName(String name) throws StructuralException {
			VisiblyMutableUtils.nameChanged(this, getName(), name, myListeners);
			myName = name;
		}

		public Node[] getNodes() {
			throw new RuntimeException("not implemented");
		}

		public void addNeuron(Neuron neuron) {
			throw new RuntimeException("not implemented");
		}

		public void removeNeuron(int index) {
			throw new RuntimeException("not implemented");
		}

		public Origin[] getOrigins() {
			throw new RuntimeException("not implemented");
		}

		public Termination[] getTerminations() {
			throw new RuntimeException("not implemented");
		}

		public void setMode(SimulationMode mode) {
			throw new RuntimeException("not implemented");
		}

		public SimulationMode getMode() {
			throw new RuntimeException("not implemented");
		}

		public void run(float startTime, float endTime)
				throws SimulationException {
			throw new RuntimeException("not implemented");
		}

		public void reset(boolean randomize) {
			throw new RuntimeException("not implemented");
		}

		public Origin getOrigin(String name) throws StructuralException {
			throw new RuntimeException("not implemented");
		}

		public Termination getTermination(String name)
				throws StructuralException {
			throw new RuntimeException("not implemented");
		}

		public SpikePattern getSpikePattern() {
			throw new RuntimeException("not implemented");
		}

		public void collectSpikes(boolean collect) {
			throw new RuntimeException("not implemented");
		}

		public String getDocumentation() {
			throw new RuntimeException("not implemented");
		}

		public void setDocumentation(String text) {
			throw new RuntimeException("not implemented");
		}

		public boolean isCollectingSpikes() {
			throw new RuntimeException("not implemented");
		}

		/**
		 * @see ca.nengo.util.VisiblyMutable#addChangeListener(ca.nengo.util.VisiblyMutable.Listener)
		 */
		public void addChangeListener(Listener listener) {
			if (myListeners == null) {
				myListeners = new ArrayList<Listener>(2);
			}
			myListeners.add(listener);
		}

		/**
		 * @see ca.nengo.util.VisiblyMutable#removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)
		 */
		public void removeChangeListener(Listener listener) {
			myListeners.remove(listener);
		}

		@Override
		public Node clone() throws CloneNotSupportedException {
			return (Node) super.clone();
		}

	}

}
