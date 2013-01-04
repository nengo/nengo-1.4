package ca.nengo.model.impl;

import ca.nengo.math.Function;
import ca.nengo.math.impl.IdentityFunction;
import ca.nengo.model.Ensemble;
import ca.nengo.model.InstantaneousOutput;
import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.Origin;
import ca.nengo.model.Projection;
import ca.nengo.model.SimulationException;
import ca.nengo.model.SimulationMode;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import ca.nengo.model.Units;
import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.model.nef.NEFEnsembleFactory;
import ca.nengo.model.nef.impl.BiasOrigin;
import ca.nengo.model.nef.impl.BiasTermination;
import ca.nengo.model.nef.impl.DecodedOrigin;
import ca.nengo.model.nef.impl.DecodedTermination;
import ca.nengo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.nengo.util.DataUtils;
import ca.nengo.util.MU;
import ca.nengo.util.Probe;
import static org.junit.Assert.*;
import org.junit.Test;

public class ProjectionImplTest {

	private Origin myOrigin = new MockOrigin("mock origin", 1);
	private Termination myTermination = new MockTermination("mock termination", 1);
	private Projection myProjection = new ProjectionImpl(myOrigin, myTermination, null);

	@Test
	public void testGetOrigin() {
		assertEquals(myOrigin, myProjection.getOrigin());
	}

	@Test
	public void testGetTermination() {
		assertEquals(myTermination, myProjection.getTermination());
	}

	@Test
	public void testAddBias2D() throws StructuralException, SimulationException {
		Network network = new NetworkImpl();
		FunctionInput input = new FunctionInput("input", new Function[]{new IdentityFunction(1, 0)}, Units.UNK);
		network.addNode(input);
		NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();
		int n = 300;
		NEFEnsemble pre = ef.make("pre", n, 2);
		pre.addDecodedTermination("input", MU.uniform(2, 1, 1), .005f, false);
		network.addNode(pre);
		network.addProjection(input.getOrigin(FunctionInput.ORIGIN_NAME), pre.getTermination("input"));
		NEFEnsemble post = ef.make("post", n, 2);
		network.addNode(post);
		post.addDecodedTermination("input", MU.I(2), .01f, false);
		Projection p = network.addProjection(pre.getOrigin(NEFEnsemble.X), post.getTermination("input"));

		DecodedOrigin o = (DecodedOrigin) pre.getOrigin(NEFEnsemble.X);
		DecodedTermination t = (DecodedTermination) post.getTermination("input");
		float[][] directWeights = MU.prod(post.getEncoders(), MU.prod(t.getTransform(), MU.transpose(o.getDecoders())));
		System.out.println("Direct weights: " + MU.min(directWeights) + " to " + MU.max(directWeights));

		Probe probe = network.getSimulator().addProbe(post.getName(), NEFEnsemble.X, true);
		network.setMode(SimulationMode.CONSTANT_RATE);
		network.run(-1.5f, 1);
		network.setMode(SimulationMode.DEFAULT);
		float[] reference = MU.transpose(DataUtils.filter(probe.getData(), .01f).getValues())[0];

		network.run(-1.5f, 1);
		float[] mixed = MU.transpose(DataUtils.filter(probe.getData(), .01f).getValues())[0];
		getError(reference, mixed);

		p.addBias(300, .005f, .01f, true, false);
		BiasOrigin bo = (BiasOrigin) pre.getOrigin("post:input");
		BiasTermination bt = (BiasTermination) post.getTermination("input:bias");
		assertTrue(MU.min(getNetWeights(directWeights, bo, bt)) > -1e-10);
		network.run(-1.5f, 1);
		p.removeBias();

		p.addBias(300, .005f, .01f, true, true);
		bo = (BiasOrigin) pre.getOrigin("post:input");
		bt = (BiasTermination) post.getTermination("input:bias");
		assertTrue(MU.min(getNetWeights(directWeights, bo, bt)) > -1e-10);
		network.run(-1.5f, 1);
		p.removeBias();
	}

	private static float getError(float[] reference, float[] data) {
		int start = Math.round(reference.length/5); //avoid transient in error calculation
		int length = reference.length - start;
		float[] difference = new float[length];
		System.arraycopy(MU.difference(data, reference), start, difference, 0, length);

		float result = MU.variance(difference, 0);
		System.out.println("error" + result);
		return result;
	}

	private static float[][] getNetWeights(float[][] directWeights, BiasOrigin bo, BiasTermination bt) {
		float[][] biasWeights = MU.prod(MU.transpose(new float[][]{bt.getBiasEncoders()}), MU.transpose(bo.getDecoders()));
		System.out.println("Bias weights: " + MU.min(biasWeights) + " to " + MU.max(biasWeights));

		float[][] netWeights = MU.sum(directWeights, biasWeights);
		System.out.println("Net weights: " + MU.min(netWeights) + " to " + MU.max(netWeights) + " mean " + MU.mean(netWeights));

		return netWeights;
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
		
		public  void setValues(InstantaneousOutput val) {
			throw new RuntimeException("not implemented");
		}


		public Node getNode() {
			return null;
		}
		
		public void setRequiredOnCPU(boolean val){
		}
		    
		public boolean getRequiredOnCPU(){
		    return false;
		}

		@Override
		public Origin clone() throws CloneNotSupportedException {
			return (Origin) super.clone();
		}
		
		public Origin clone(Ensemble ensemble) throws CloneNotSupportedException {
			return this.clone();
		}
	}

	public static class MockTermination implements Termination {

		private static final long serialVersionUID = 1L;

		private final String myName;
		private final int myDimensions;

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
		
		public InstantaneousOutput getInput() {
			throw new RuntimeException("not implemented");
		}

		/**
		 * @see ca.nengo.model.Resettable#reset(boolean)
		 */
		public void reset(boolean randomize) {
		}

		@Override
		public MockTermination clone() throws CloneNotSupportedException {
			return this.clone(null);
		}
		
		public MockTermination clone(Node node) throws CloneNotSupportedException {
			return (MockTermination)super.clone();
		}

	}

	public static void main(String[] args) {
		ProjectionImplTest test = new ProjectionImplTest();
		try {
			test.testAddBias2D();
		} catch (StructuralException e) {
			e.printStackTrace();
		} catch (SimulationException e) {
			e.printStackTrace();
		}
	}
}
