/*
 * Created on 24-May-2006
 */
package ca.nengo.model.impl;

import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.Projection;
import ca.nengo.model.SimulationException;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.impl.ProjectionImpl;
import junit.framework.TestCase;

/**
 * Unit tests for ProjectionImpl. 
 * 
 * @author Bryan Tripp
 */
public class ProjectionImplTest extends TestCase {

	private Projection myProjection;
	private Origin myOrigin;
	private Termination myTermination;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		myOrigin = new MockOrigin("mock origin", 1);
		myTermination = new MockTermination("mock termination", 1);
		myProjection = new ProjectionImpl(myOrigin, myTermination, null);		
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.ProjectionImpl.getOrigin()'
	 */
	public void testGetOrigin() {
		assertEquals(myOrigin, myProjection.getOrigin());
	}

	/*
	 * Test method for 'ca.bpt.cn.model.impl.ProjectionImpl.getTermination()'
	 */
	public void testGetTermination() {
		assertEquals(myTermination, myProjection.getTermination());
	}
	
	public static class MockOrigin implements Origin {

		private static final long serialVersionUID = 1L;
		
		private String myName;
		private int myDimensions;
		
		public MockOrigin(String name, int dimensions) {
			myName = name;
			myDimensions = dimensions;
			
		}
		
		public String getName() {
			return myName;
		}
		
		public void setName(String name) {
			myName = name;
		}

		public int getDimensions() {
			return myDimensions;
		}
		
		public void setDimensions(int dim) {
			myDimensions = dim;
		}

		public InstantaneousOutput getValues() {
			throw new RuntimeException("not implemented");
		}

		public Node getNode() {
			return null;
		}

		@Override
		public Origin clone() throws CloneNotSupportedException {
			return (Origin) super.clone();
		}
		
	}
	
	public static class MockTermination implements Termination {

		private static final long serialVersionUID = 1L;
		
		private String myName;
		private int myDimensions;

		public MockTermination(String name, int dimensions) {
			myName = name;
			myDimensions = dimensions;
		}
		
		public String getName() {
			return myName;
		}

		public int getDimensions() {
			return myDimensions;
		}

		public void setValues(InstantaneousOutput values) throws SimulationException {
			throw new RuntimeException("not implemented");
		}

		public void propertyChange(String propertyName, Object newValue) {
			throw new RuntimeException("not implemented");
		}

		public Node getNode() {
			return null;
		}

		public boolean getModulatory() {
			return false;
		}

		public float getTau() {
			return 0;
		}

		public void setModulatory(boolean modulatory) {
		}

		public void setTau(float tau) throws StructuralException {
		}

		/**
		 * @see ca.nengo.model.Resettable#reset(boolean)
		 */
		public void reset(boolean randomize) {
		}

		@Override
		public Termination clone() throws CloneNotSupportedException {
			return (Termination) super.clone();
		}
		
	}

}
