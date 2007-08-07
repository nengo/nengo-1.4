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
import ca.neo.ui.models.nodes.PNetwork;
import ca.shu.ui.lib.objects.widgets.TrackedMsg;
import ca.shu.ui.lib.util.Util;

/**
 * DEFUNCT: Originally, in this example, the integrator network is constructed
 * by piece-by-piece through the construction of individual UI wrapper objects
 * of NEO node. However, this method is not recommended, as many of the UI
 * functions cannot be safely called from the main thread):
 * 
 * Users should either create Network models and pass it into the UI, or create
 * the model from the Graphical interface itself
 * 
 * @author Shu Wu
 * 
 */
public class GIntegratorExample {
	static final float tau = .05f;

	public static void main(String[] args) {

		NeoGraphics neoGraphics = new NeoGraphics("Integrator Example");

		try {
			TrackedMsg task = new TrackedMsg(
					"Creating Integrator NEO Network model");
			Network network = createNetwork();
			task.finished();

			task = new TrackedMsg("Creating Integrator Model UI");
			PNetwork networkUI = new PNetwork(network);

			neoGraphics.addWorldObject(networkUI);

			networkUI.openViewer();

			task.finished();

		} catch (StructuralException e) {
			Util.Error("Could not create network: " + e.toString());
		}
	}

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

}
