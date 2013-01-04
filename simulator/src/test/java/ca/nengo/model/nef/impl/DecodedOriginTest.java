/**
 * 
 */
package ca.nengo.model.nef.impl;

import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.model.nef.impl.DecodedOrigin;
import ca.nengo.model.nef.impl.NEFEnsembleFactoryImpl;
//import ca.nengo.plot.Plotter;
import ca.nengo.util.MU;
import junit.framework.TestCase;

/**
 * Unit tests for DecodedOrigin. 
 * 
 * @author Bryan Tripp
 */
public class DecodedOriginTest extends TestCase {

	private DecodedOrigin myOrigin;
	
	/**
	 * @param arg0
	 */
	public DecodedOriginTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		NEFEnsembleFactoryImpl ef = new NEFEnsembleFactoryImpl();
		NEFEnsemble ensemble = ef.make("test", 100, 1);
		myOrigin = (DecodedOrigin) ensemble.getOrigin(NEFEnsemble.X);
//		Plotter.plot(ensemble, NEFEnsemble.X);
	}

	/**
	 * Test method for {@link ca.nengo.model.nef.impl.DecodedOrigin#getError()}.
	 */
	public void testGetError() {
		System.out.println(MU.toString(new float[][]{myOrigin.getError()}, 10));
	}
	
//	public static void main(String[] args) {
//		DecodedOriginTest test = new DecodedOriginTest("");
//		try {
//			test.setUp();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}
