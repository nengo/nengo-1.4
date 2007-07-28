package ca.neo.ui;

import ca.neo.math.Function;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.ui.models.viewers.NetworkView;
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

		PNEFEnsemble newEnsemble = new PNEFEnsemble("My Ensemble", 100, 1,
				"ensemble1");

		newEnsemble.showAllOrigins();
		newEnsemble.addDecodedTermination("input", tau, false);

		/*
		 * Add the nodes to the network
		 */

		network.addNode(input);

		newEnsemble.setOffset(150, 0);
		network.addNode(newEnsemble);

		/*
		 * Opens up the network viewer which shows the nodes inside the network
		 */
		network.showNodes();

	}
}
