package ca.nengo.model.impl;

import ca.nengo.model.StructuralException;
import junit.framework.TestCase;

public class PlasticEnsembleTerminationTest extends TestCase {

	public void testGetTransform() throws StructuralException {
		float[][] transform = new float[10][];
		for(int i = 0; i < transform.length; i++)
			transform[i] = new float[]{1.0f, 1.0f, 1.0f};
		
		LinearExponentialTermination[] nodeterms = new LinearExponentialTermination[10];
		for(int i = 0; i < nodeterms.length; i++)
			nodeterms[i] = new LinearExponentialTermination(null, null, transform[i], 0.0f);
		
		
		PlasticEnsembleTermination term = new PlasticEnsembleTermination(null, null, nodeterms);
		float[][] rettransform = term.getTransform();
		
		assertTrue(rettransform.length == transform.length);
		
		for(int i = 0; i < transform.length; i++)
		{
			assertTrue(rettransform[i].length == transform[i].length);
			for(int j = 0; j < transform[i].length; j++)
				assertTrue(rettransform[i][j] == transform[i][j]);
		}
	}

	public void testSetTransform() throws StructuralException{
		float[][] transform = new float[10][];
		float[][] newtransform = new float[10][];
		for(int i = 0; i < transform.length; i++)
		{
			transform[i] = new float[]{1.0f, 1.0f, 1.0f};
			newtransform[i] = new float[]{0.0f, 0.0f, 0.0f};
		}
			
		
		LinearExponentialTermination[] nodeterms = new LinearExponentialTermination[10];
		for(int i = 0; i < nodeterms.length; i++)
			nodeterms[i] = new LinearExponentialTermination(null, null, transform[i], 0.0f);
		
		
		PlasticEnsembleTermination term = new PlasticEnsembleTermination(null, null, nodeterms);
		term.setTransform(newtransform);
		
		float[][] rettransform = term.getTransform();
		
		assertTrue(rettransform.length == newtransform.length);
		
		for(int i = 0; i < newtransform.length; i++)
		{
			assertTrue(rettransform[i].length == newtransform[i].length);
			for(int j = 0; j < newtransform[i].length; j++)
				assertTrue(rettransform[i][j] == newtransform[i][j]);
		}
	}

}
