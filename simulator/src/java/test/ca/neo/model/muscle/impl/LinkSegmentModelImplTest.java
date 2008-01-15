/*
 * Created on 5-Apr-07
 */
package ca.neo.model.muscle.impl;

import ca.neo.config.Configuration;
import ca.neo.dynamics.impl.AbstractDynamicalSystem;
import ca.neo.model.muscle.LinkSegmentModel;
import junit.framework.TestCase;

/**
 * Unit test for LinkSegmentModel. 
 * 
 * TODO: finish
 * 
 * @author Bryan Tripp
 */
public class LinkSegmentModelImplTest extends TestCase {

	public void testRun() {
//		LinkSegmentModel model = new LinkSegmentModelImpl("upper limb", null, .001f);
		
	}
	
	private static class UpperLimbDynamics extends AbstractDynamicalSystem {

		public UpperLimbDynamics() {
			super(new float[]{0, 0});
		}		
		
		public Configuration getConfiguration() {
			return null;
		}


		public float[] f(float t, float[] u) {
			return null;
		}

		public float[] g(float t, float[] u) {
			return null;
		}

		/**
		 * shoulder flexor; shoulder extensor; elbow flexor; elbow extensor 
		 */
		public int getInputDimension() {
			return 4;
		}

		/**
		 * shoulder angle from vertical; elbow angle from straight 
		 */
		public int getOutputDimension() {
			return 2;
		}
		
	}
	
	public static void main(String[] args) {
		(new LinkSegmentModelImplTest()).testRun();
	}

}
