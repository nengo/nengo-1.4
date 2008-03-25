package ca.nengo.ui.dev;

import ca.nengo.model.StructuralException;
import ca.nengo.ui.models.nodes.UINetwork;

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
		super(FuzzyLogicExample.createNetwork());
	}

	@Override
	protected void doStuff(UINetwork network) {
		// (new RunSimulatorAction("Run", network)).doAction();
	}

}
