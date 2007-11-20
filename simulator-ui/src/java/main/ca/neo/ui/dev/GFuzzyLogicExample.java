package ca.neo.ui.dev;

import ca.neo.model.StructuralException;
import ca.neo.ui.ExampleRunner;

/**
 * In this example, an Integrator network is constructed
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

}
