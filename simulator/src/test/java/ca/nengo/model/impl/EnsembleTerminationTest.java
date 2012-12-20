package ca.nengo.model.impl;

import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import static org.junit.Assert.*;
import org.junit.Test;

public class EnsembleTerminationTest {

	private static float ourTau = .005f;
	private static float ourTolerance = 1e-5f;
	
	private Termination[] myNodeTerminations = new Termination[10];
	private EnsembleTermination myEnsembleTermination;
	
	public EnsembleTerminationTest() throws Exception {
		for (int i = 0; i < myNodeTerminations.length; i++)
			myNodeTerminations[i] = new LinearExponentialTermination(null, ""+i, new float[]{1}, ourTau);
		myEnsembleTermination = new EnsembleTermination(null, "test", myNodeTerminations);
	}

	@Test
	public void testSetModulatory() {
		assertFalse(myEnsembleTermination.getModulatory());
		myNodeTerminations[0].setModulatory(true);
		assertFalse(myEnsembleTermination.getModulatory());
		
		myEnsembleTermination.setModulatory(true);
		assertTrue(myEnsembleTermination.getModulatory());
		assertTrue(myNodeTerminations[0].getModulatory());
		assertTrue(myNodeTerminations[1].getModulatory());
	}

	@Test
	public void testSetTau() throws StructuralException {
		assertEquals(ourTau, myEnsembleTermination.getTau(), ourTolerance);
		myNodeTerminations[0].setTau(ourTau*2);
		assertTrue(myEnsembleTermination.getTau() > ourTau*1.01f);
		
		myEnsembleTermination.setTau(ourTau*2);
		assertEquals(ourTau*2, myEnsembleTermination.getTau(), ourTolerance);
		assertEquals(ourTau*2, myNodeTerminations[0].getTau(), ourTolerance);
		assertEquals(ourTau*2, myNodeTerminations[0].getTau(), ourTolerance);
	}
}
