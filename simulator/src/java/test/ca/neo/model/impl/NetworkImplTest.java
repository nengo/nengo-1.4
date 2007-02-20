/*
 * Created on 24-May-2006
 */
package ca.neo.model.impl;

import org.apache.commons.lang.NotImplementedException;

import ca.neo.model.Ensemble;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.SimulationException;
import ca.neo.model.SimulationMode;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.model.neuron.Neuron;
import ca.neo.util.SpikePattern;
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
		} catch (StructuralException e) {} //exception is expected
		
		try {
			myNetwork.removeNode("c");
			fail("Should have thrown exception because named ensemble doesn't exist");
		} catch (StructuralException e) {} //exception is expected

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
		} catch (StructuralException e) {} //exception is expected
		
		try {
			myNetwork.addProjection(o1, t3);
			fail("Should have thrown exception because origin and termination have different dimensions");
		} catch (StructuralException e) {} //exception is expected
		
		myNetwork.removeProjection(t2);
		assertEquals(t1, myNetwork.getProjections()[0].getTermination());
	}
	
	private static class MockEnsemble implements Ensemble {

		private static final long serialVersionUID = 1L;
		
		private String myName;
		
		public MockEnsemble(String name) {
			myName = name;
		}
		
		public String getName() {
			return myName;
		}

		public Node[] getNodes() {
			throw new NotImplementedException("not implemented");
		}
		
		public void addNeuron(Neuron neuron) {
			throw new NotImplementedException("not implemented");
		}

		public void removeNeuron(int index) {
			throw new NotImplementedException("not implemented");
		}

		public Origin[] getOrigins() {
			throw new NotImplementedException("not implemented");
		}

		public Termination[] getTerminations() {
			throw new NotImplementedException("not implemented");
		}

		public void setMode(SimulationMode mode) {
			throw new NotImplementedException("not implemented");
		}

		public SimulationMode getMode() {
			throw new NotImplementedException("not implemented");
		}

		public void run(float startTime, float endTime) throws SimulationException {
			throw new NotImplementedException("not implemented");
		}

		public void reset(boolean randomize) {
			throw new NotImplementedException("not implemented");
		}

		public Origin getOrigin(String name) throws StructuralException {
			throw new NotImplementedException("not implemented");
		}

		public Termination getTermination(String name) throws StructuralException {
			throw new NotImplementedException("not implemented");
		}

		public SpikePattern getSpikePattern() {
			throw new NotImplementedException("not implemented");
		}

		public void collectSpikes(boolean collect) {
			throw new NotImplementedException("not implemented");
		}

	}
	
}
