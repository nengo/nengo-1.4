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
import ca.neo.ui.models.nodes.PFunctionInput;
import ca.neo.ui.models.nodes.PNEFEnsemble;
import ca.neo.ui.models.nodes.PNetwork;
import ca.neo.ui.models.nodes.connectors.PTermination;

/**
 * In this example, the integrator network is constructed by piece-by-piece
 * through the construction of individual UI wrapper objects of NEO node
 * 
 * @author Shu Wu
 * 
 */
public class GIntegratorExample {
	static final float tau = .05f;

	public static void main(String[] args) throws StructuralException {

		NeoGraphics graphicsWorkspace = new NeoGraphics("Integrator");

		/*
		 * Creates the ui object containing the network model
		 */

		NetworkImpl network = new NetworkImpl();
		network.setName("integrator");

		PNetwork networkUI = new PNetwork(network);

		/*
		 * Adds the toplevel network into the Graphics workspace
		 */
		graphicsWorkspace.addWorldObject(networkUI);

		/*
		 * Creates function input
		 */
		Function f = new ConstantFunction(1, 1f);
		FunctionInput input = new FunctionInput("input", new Function[] { f },
				Units.UNK);
		PFunctionInput inputUI = new PFunctionInput(input);

		/*
		 * Create "integrator" ensemble
		 */
		NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();

		NEFEnsemble integrator = ef.make("integrator", 500, 1, "integrator1",
				false);
		PNEFEnsemble integratorUI = new PNEFEnsemble(integrator);

		integratorUI.setCollectingSpikes(true);

		integratorUI.showAllOrigins();

		/*
		 * Add decoded origins to integrator
		 */
		Termination interm = integrator.addDecodedTermination("input",
				new float[][] { new float[] { tau } }, tau, false);

		Termination fbterm = integrator.addDecodedTermination("feedback",
				new float[][] { new float[] { 1f } }, tau, false);

		PTermination intermUI = integratorUI.showTermination(interm.getName());
		PTermination fbtermUI = integratorUI.showTermination(fbterm.getName());

		/*
		 * Add the nodes to the network
		 */

		// change the position of the integrator in the UI
		integratorUI.setOffset(150, 0);

		networkUI.addNode(inputUI);
		networkUI.addNode(integratorUI);

		/*
		 * Add connections
		 */
		inputUI.getOrigin(FunctionInput.ORIGIN_NAME).connectTo(intermUI);
		integratorUI.getOrigin(NEFEnsemble.X).connectTo(fbtermUI);

		/*
		 * Opens up the network viewer
		 */
		networkUI.openViewer();

	}
}
