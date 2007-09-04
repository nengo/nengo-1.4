package ca.neo.ui;

import ca.neo.model.StructuralException;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.ui.models.nodes.UINetwork;

/**
 * In this example, an Integrator network is constructed
 * 
 * @author Shu Wu
 */
public class BrainViewExample extends ExampleRunner {

	public static void main(String[] args) {

		try {
			new BrainViewExample();
		} catch (StructuralException e) {
			e.printStackTrace();
		}
	}

	public BrainViewExample() throws StructuralException {
		super("Brain View Example (under construction)", new NetworkImpl());
	}

	@Override
	protected void processNetwork(UINetwork network) {
		network.closeViewer();
		network.openBrainViewer();
	}

}
