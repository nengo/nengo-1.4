package ca.neo.ui;

import ca.neo.math.Function;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.model.Network;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;

/**
 * In this example, an Integrator network is constructed
 * 
 * @author Shu Wu
 * 
 */
public class GIntegratorExample extends ExampleRunner {

	public static Network createNetwork() throws StructuralException {

		Network network = new NetworkImpl();

		Function f = new ConstantFunction(1, 1f);
		// Function f = new SineFunction();
		FunctionInput input = new FunctionInput("input", new Function[] { f },
				Units.UNK);
		network.addNode(input);

		NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();

		NEFEnsemble integrator = ef.make("integrator", 500, 1, "integrator1",
				false);
		network.addNode(integrator);
		integrator.collectSpikes(true);

		float tau = .05f;

		Termination interm = integrator.addDecodedTermination("input",
				new float[][] { new float[] { tau } }, tau, false);
		// Termination interm = integrator.addDecodedTermination("input", new
		// float[][]{new float[]{1f}}, tau);
		network.addProjection(input.getOrigin(FunctionInput.ORIGIN_NAME),
				interm);

		Termination fbterm = integrator.addDecodedTermination("feedback",
				new float[][] { new float[] { 1f } }, tau, false);
		network.addProjection(integrator.getOrigin(NEFEnsemble.X), fbterm);

		// System.out.println("Network creation: " + (System.currentTimeMillis()
		// - start));
		return network;
	}

	public static void main(String[] args) {

		try {
			new GIntegratorExample();
		} catch (StructuralException e) {
			e.printStackTrace();
		}
	}

	public GIntegratorExample() throws StructuralException {
		super("Integrator Example", createNetwork());
	}

}
