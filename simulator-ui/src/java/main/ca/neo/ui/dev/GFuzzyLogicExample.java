package ca.neo.ui.dev;

import ca.neo.model.StructuralException;
import ca.neo.ui.ExampleRunner;
import ca.neo.ui.models.nodes.UINetwork;

/**
 * In this example, a Fuzzy Logic network is constructed
 * 
 * @author Shu Wu
 */
public class GFuzzyLogicExample extends ExampleRunner {

	public static void main(String[] args) {

		try {
			new GFuzzyLogicExample();
		} catch (StructuralException e) {
			e.printStackTrace();
		}
	}

	public GFuzzyLogicExample() throws StructuralException {
		super("FuzzyLogic Example", FuzzyLogicExample.createNetwork());
	}

	@Override
	protected void doStuff(UINetwork network) {
		// (new RunSimulatorAction("Run", network)).doAction();
	}

}
