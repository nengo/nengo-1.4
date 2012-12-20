package ca.nengo.model.nef.impl;

import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.util.MU;
import org.junit.Test;

public class DecodedOriginTest {
	private DecodedOrigin myOrigin;
	
	public DecodedOriginTest() throws Exception {
		NEFEnsembleFactoryImpl ef = new NEFEnsembleFactoryImpl();
		NEFEnsemble ensemble = ef.make("test", 100, 1);
		myOrigin = (DecodedOrigin) ensemble.getOrigin(NEFEnsemble.X);
	}

	@Test
	public void testGetError() {
		System.out.println(MU.toString(new float[][]{myOrigin.getError()}, 10));
	}
}
