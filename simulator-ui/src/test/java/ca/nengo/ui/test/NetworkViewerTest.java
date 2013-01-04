package ca.nengo.ui.test;

import ca.nengo.math.Function;
import ca.nengo.math.impl.ConstantFunction;
import ca.nengo.model.Network;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Units;
import ca.nengo.model.impl.FunctionInput;
import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.ui.actions.RunSimulatorAction;
import ca.nengo.ui.dev.ExampleRunner;
import ca.nengo.ui.models.nodes.UINetwork;

public class NetworkViewerTest extends ExampleRunner {

	public static void main(String[] args) {

		try {
			new NetworkViewerTest();
		} catch (StructuralException e) {
			e.printStackTrace();
		}
	}

	public NetworkViewerTest() throws StructuralException {
		super(CreateNetwork());
	}
	
	
	public static Network CreateNetwork() throws StructuralException {
		NetworkImpl network = new NetworkImpl();
		
		Function f = new ConstantFunction(1, 1f);
		FunctionInput input = new FunctionInput("input", new Function[] { f }, Units.UNK);
		
		network .addNode(input);
		
		return network;
	}

	@Override
	protected void doStuff(UINetwork network) {
		(new RunSimulatorAction("Run", network, 0f, 1f, 0.002f)).doAction();


		
	}

}