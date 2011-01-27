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
import ca.nengo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.nengo.model.nef.impl.NEFEnsembleImpl;
import ca.nengo.model.neuron.impl.SpikingNeuron;
//import ca.nengo.model.neuron.Neuron;
import ca.nengo.util.SpikePattern;
import ca.nengo.util.VisiblyMutable;
import ca.nengo.util.VisiblyMutableUtils;
import junit.framework.TestCase;

public class NetworkImplTest extends TestCase {

	private NetworkImpl myNetwork;

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
	
	public void testKillNeurons() throws StructuralException
	{
		NEFEnsembleFactoryImpl ef = new NEFEnsembleFactoryImpl();
		NEFEnsembleImpl nef1 = (NEFEnsembleImpl)ef.make("nef1", 1000, 1);
		NEFEnsembleImpl nef2 = (NEFEnsembleImpl)ef.make("nef2", 1000, 1);
		NEFEnsembleImpl nef3 = (NEFEnsembleImpl)ef.make("nef3", 1, 1);
		NetworkImpl net = new NetworkImpl();
		
		net.addNode(nef1);
		myNetwork.addNode(net);
		myNetwork.addNode(nef2);
		myNetwork.addNode(nef3);
		
		myNetwork.killNeurons(0.0f,true);
		int numDead = countDeadNeurons(nef1);
		if(numDead != 0)
			fail("Number of dead neurons outside expected range");
		numDead = countDeadNeurons(nef2);
		if(numDead != 0)
			fail("Number of dead neurons outside expected range");
		
		myNetwork.killNeurons(0.5f,true);
		numDead = countDeadNeurons(nef1);
		if(numDead < 400 || numDead > 600)
			fail("Number of dead neurons outside expected range");
		numDead = countDeadNeurons(nef2);
		if(numDead < 400 || numDead > 600)
			fail("Number of dead neurons outside expected range");
		
		myNetwork.killNeurons(1.0f,true);
		numDead = countDeadNeurons(nef1);
		if(numDead != 1000)
			fail("Number of dead neurons outside expected range");
		numDead = countDeadNeurons(nef2);
		if(numDead != 1000)
			fail("Number of dead neurons outside expected range");
		
		numDead = countDeadNeurons(nef3);
		if(numDead != 0)
			fail("Relay protection did not work");
		myNetwork.killNeurons(1.0f,false);
		numDead = countDeadNeurons(nef3);
		if(numDead != 1)
			fail("Number of dead neurons outside expected range");
	}
	private int countDeadNeurons(NEFEnsembleImpl pop)
	{
		Node[] neurons = pop.getNodes();
		int numDead = 0;
		
		for(int i = 0; i < neurons.length; i++)
		{
			SpikingNeuron n = (SpikingNeuron)neurons[i];
			if(n.getBias() == 0.0f && n.getScale() == 0.0f)
				numDead++;
		}
		
		return numDead;
	}
	
	public void testAddNode() throws StructuralException
	{
		Ensemble a = new MockEnsemble("a");
		
		try
		{
			myNetwork.getNode("a");
			fail("Node is present in network when it shouldn't be");
		}
		catch(StructuralException se)
		{
		}
			
		
		myNetwork.addNode(a);
		
		if(myNetwork.getNode("a") != a)
			fail("Ensemble not added correctly");
		
		NetworkImpl b = new NetworkImpl();
		b.setName("b");
		myNetwork.addNode(b);
		
		if(myNetwork.getNode("b") != b)
			fail("Network not added correctly");
		
	}
	
	public void testRemoveNode() throws StructuralException, SimulationException
	{
		Ensemble a = new MockEnsemble("a");
		
		myNetwork.addNode(a);
		if(myNetwork.getNode("a") == null)
			fail("Node not added");
		
		myNetwork.removeNode("a");
		try
		{
			myNetwork.getNode("a");
			fail("Node not removed");
		}
		catch(StructuralException se)
		{
		}
			
		NetworkImpl b = new NetworkImpl();
		b.setName("b");
		myNetwork.addNode(b);
		
		NEFEnsembleFactoryImpl ef = new NEFEnsembleFactoryImpl();
		NEFEnsembleImpl c = (NEFEnsembleImpl)ef.make("c", 10, 1);
		b.addNode(c);
		b.getSimulator().addProbe("c", "X", true);
		
		b.exposeOrigin(c.getOrigin("X"), "exposed");
		
		if(!b.getExposedOriginName(c.getOrigin("X")).equals("exposed"))
			fail("Origin not exposed correctly");
		
		if(myNetwork.getNode("b") == null)
			fail("Network not added");
		
		myNetwork.removeNode("b");
		
		try
		{
			myNetwork.getNode("b");
			fail("Network not removed");
		}
		catch(StructuralException se)
		{
		}
		
		try
		{
			b.getNode("c");
			fail("Ensemble not recursively removed from network");
		}
		catch(StructuralException se)
		{
		}

		if(b.getSimulator().getProbes().length != 0)
			fail("Probes not removed correctly");
		
		if(b.getExposedOriginName(c.getOrigin("X")) != null)
			fail("Origin not unexposed correctly");
	}
	
	public void testExposeOrigin() throws StructuralException
	{
		NEFEnsembleFactoryImpl ef = new NEFEnsembleFactoryImpl();
		NEFEnsembleImpl a = (NEFEnsembleImpl)ef.make("a", 10, 1);
		
		myNetwork.addNode(a);
		
		myNetwork.exposeOrigin(a.getOrigin("X"), "test");
		
		try
		{
			myNetwork.getOrigin("test");
		}
		catch(StructuralException se)
		{
			fail("Origin not exposed");
		}
		
		if(myNetwork.getExposedOriginName(a.getOrigin("X")) != "test")
			fail("Origin not exposed with correct name");
		
		myNetwork.removeNode("a");
	}
	
	public void testHideOrigin() throws StructuralException
	{
		NEFEnsembleFactoryImpl ef = new NEFEnsembleFactoryImpl();
		NEFEnsembleImpl a = (NEFEnsembleImpl)ef.make("a", 10, 1);
		
		myNetwork.addNode(a);
		
		myNetwork.exposeOrigin(a.getOrigin("X"), "test");
		
		myNetwork.hideOrigin("test");
		
		if(myNetwork.getExposedOriginName(a.getOrigin("X")) != null)
			fail("Origin name not removed");
		
		try
		{
			myNetwork.getOrigin("test");
			fail("Origin not removed");
		}
		catch(StructuralException se)
		{
		}
		
		myNetwork.removeNode("a");
	}
	
	public void testChanged() throws StructuralException
	{
		NetworkImpl b = new NetworkImpl();
		b.setName("b");
		myNetwork.addNode(b);
		
		NEFEnsembleFactoryImpl ef = new NEFEnsembleFactoryImpl();
		NEFEnsembleImpl a = (NEFEnsembleImpl)ef.make("a", 10, 1);
		b.addNode(a);
		
		b.exposeOrigin(a.getOrigin("X"), "exposed");
		
		NEFEnsembleImpl c = (NEFEnsembleImpl)ef.make("c", 10, 1);
		float[][] tmp = new float[1][1];
		tmp[0][0] = 1;
		c.addDecodedTermination("in", tmp, 0.007f, false);
		myNetwork.addNode(c);
		
		myNetwork.addProjection(b.getOrigin("exposed"), c.getTermination("in"));
		
		if(myNetwork.getProjections().length != 1)
			fail("Projection not created properly");
		
		b.hideOrigin("exposed");
		
		if(myNetwork.getProjections().length != 0)
			fail("Projection not removed");
		
		myNetwork.removeNode("b");
		myNetwork.removeNode("c");
	}
	
	public void testGetNodeTerminations() throws StructuralException
	{
		NetworkImpl net = new NetworkImpl();
		
		if(net.getNodeTerminations().size() != 0)
			fail("Network has terminations when it shouldn't");
		
		NEFEnsembleFactoryImpl ef = new NEFEnsembleFactoryImpl();
		NEFEnsembleImpl a = (NEFEnsembleImpl)ef.make("a", 10, 1);
		float[][] tmp = new float[1][1];
		tmp[0][0] = 1;
		a.addDecodedTermination("in", tmp, 0.007f, false);
		
		net.addNode(a);
		
		if(net.getNodeTerminations().size() != 1)
			fail("Network hasn't found node termination");
	}
	
	public void testGetNodeOrigins() throws StructuralException
	{
		NetworkImpl net = new NetworkImpl();
		
		if(net.getNodeOrigins().size() != 0)
			fail("Network has origins when it shouldn't");
		
		NEFEnsembleFactoryImpl ef = new NEFEnsembleFactoryImpl();
		NEFEnsembleImpl a = (NEFEnsembleImpl)ef.make("a", 10, 1);
		
		net.addNode(a);
		
		if(net.getNodeOrigins().size() != a.getOrigins().length)
			fail("Network hasn't found node origin");
		
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

//		public void addNeuron(Neuron neuron) {
//			throw new RuntimeException("not implemented");
//		}

//		public void removeNeuron(int index) {
//			throw new RuntimeException("not implemented");
//		}

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

		public void redefineNodes(Node[] nodes) {
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
