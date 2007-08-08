package ca.neo.ui;

import ca.neo.examples.FuzzyLogicExample;
import ca.neo.model.StructuralException;

/**
 * In this example, the FuzzyLogic network is constructed from an existing
 * Network Model
 * 
 * @author Shu
 * 
 */
public class GFuzzyLogicExample extends ExampleRunner {

	public GFuzzyLogicExample() throws StructuralException {
		super("Fuzzy Logic Example", FuzzyLogicExample.createNetwork());
	}

	public static void main(String[] args) {
		try {
			new GFuzzyLogicExample();
		} catch (StructuralException e) {

			e.printStackTrace();
		}
	}

}
