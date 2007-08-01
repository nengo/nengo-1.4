package ca.neo.ui;

import ca.neo.math.Function;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.model.impl.FunctionInput;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.ui.models.wrappers.PDecodedTermination;
import ca.neo.ui.models.wrappers.PFunctionInput;
import ca.neo.ui.models.wrappers.PNEFEnsemble;
import ca.neo.ui.models.wrappers.PNetwork;

/**
 * TODO: unfinished
 * 
 * @author Shu Wu
 * 
 */
public class GIntegratorExample {
	static final float tau = .05f;

	public static void main(String[] args) {

		NeoGraphics ui = new NeoGraphics("Integrator Workspace");

		/*
		 * Creates the ui object containing the network model
		 */
		PNetwork network = new PNetwork("Integrator");

		/*
		 * Adds the toplevel network into the Graphics workspace
		 */
		ui.addObject(network);

		/*
		 * Creates function input
		 */
		Function f = new ConstantFunction(1, 1f);
		PFunctionInput input = new PFunctionInput("input", f);

		/*
		 * Create "integrator" ensemble
		 */
		PNEFEnsemble integrator = new PNEFEnsemble("integrator", 100, 1,
				"integrator1");
		integrator.setCollectingSpikes(true);

		integrator.showAllOrigins();
		PDecodedTermination interm = integrator.addDecodedTermination("input",
				tau, false);
		PDecodedTermination fbterm = integrator.addDecodedTermination(
				"feedback", 1f, false);

		/*
		 * Add the nodes to the network
		 */

		network.addNode(input);

		integrator.setOffset(150, 0); // change the position of the integrator
		network.addNode(integrator);

		/*
		 * Add connections
		 */
		input.getOrigin(FunctionInput.ORIGIN_NAME).connectTo(interm);
		integrator.getOrigin(NEFEnsemble.X).connectTo(fbterm);

		/*
		 * Opens up the network viewer which shows the nodes inside the network
		 */
		network.showNodes();

	}
}
