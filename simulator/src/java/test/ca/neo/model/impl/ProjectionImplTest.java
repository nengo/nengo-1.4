/*
 * Created on 24-May-2006
 */
package ca.neo.model.impl;

import org.apache.commons.lang.NotImplementedException;

import ca.neo.config.ConfigUtil;
import ca.neo.config.Configurable;
import ca.neo.config.impl.ConfigurationImpl;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.Projection;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.impl.ProjectionImpl;
import ca.neo.util.Configuration;
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
	
	public static class MockOrigin implements Origin, Configurable {

		private static final long serialVersionUID = 1L;
		
		private String myName;
		private int myDimensions;
		private ca.neo.config.Configuration myConfiguration;
		
		public MockOrigin(String name, int dimensions) {
			myName = name;
			myDimensions = dimensions;
			
			myConfiguration = ConfigUtil.defaultConfiguration(this);
			((ConfigurationImpl) myConfiguration).removeProperty("node");
		}
		
		public MockOrigin() {
			this("mock", 1);
		}

		/**
		 * @see ca.neo.config.Configurable#getConfiguration()
		 */
		public ca.neo.config.Configuration getConfiguration() {
			return myConfiguration;
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
			throw new NotImplementedException("not implemented");
		}

		public Node getNode() {
			return null;
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
			throw new NotImplementedException("not implemented");
		}

		public Configuration getConfiguration() {
			throw new NotImplementedException("not implemented");
		}

		public void propertyChange(String propertyName, Object newValue) {
			throw new NotImplementedException("not implemented");
		}

		public Node getNode() {
			return null;
		}
		
	}

}
