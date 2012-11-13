/**
 * 
 */
package ca.nengo.model.nef.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.nengo.TestUtil;
import ca.nengo.math.Function;
import ca.nengo.math.impl.PostfixFunction;
import ca.nengo.model.SimulationMode;
import ca.nengo.model.StructuralException;
import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.model.nef.NEFEnsembleFactory;
import ca.nengo.model.neuron.Neuron;
import ca.nengo.util.MU;

/**
 * Unit tests for NEFUtil.
 * 
 * @author Bryan Tripp
 */
public class NEFUtilTest {

	/**
	 * Test method for {@link ca.nengo.model.nef.impl.NEFUtil#getOutput(ca.nengo.model.nef.impl.DecodedOrigin, float[][], ca.nengo.model.SimulationMode)}.
	 * @throws StructuralException 
	 */
	@Test
	public void testGetOutput() throws StructuralException {
		NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();
		NEFEnsemble ensemble = ef.make("test", 70, new float[]{1, 1, 1});
		DecodedOrigin origin = (DecodedOrigin) ensemble.addDecodedOrigin("test", 
				new Function[]{new PostfixFunction("x1", 3), new PostfixFunction("x2", 3)}, Neuron.AXON);
		
		float[][] input = MU.uniform(500, 3, 1);
		float[][] directOutput = NEFUtil.getOutput(origin, input, SimulationMode.DIRECT);
		float[][] constantOutput = NEFUtil.getOutput(origin, input, SimulationMode.CONSTANT_RATE);
		float[][] defaultOutput = NEFUtil.getOutput(origin, input, SimulationMode.DEFAULT);		
//		Plotter.plot(MU.transpose(defaultOutput)[0], "");

		TestUtil.assertClose(directOutput[0][0], constantOutput[0][0], .5f);
		float[] d0 = MU.transpose(defaultOutput)[0];
		TestUtil.assertClose(constantOutput[0][0], MU.mean(d0), .02f);
		assertTrue(Math.sqrt(MU.variance(d0, MU.mean(d0))) > .01);
	}

}
