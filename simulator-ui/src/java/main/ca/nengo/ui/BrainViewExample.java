package ca.nengo.ui;

import ca.nengo.model.StructuralException;
import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.ui.models.nodes.UINetwork;

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
		super(new NetworkImpl());
	}

	@Override
	protected void processNetwork(UINetwork network) {
		network.closeViewer();
		network.createBrainViewer();
	}

}
