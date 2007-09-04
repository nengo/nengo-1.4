package ca.neo.ui;

import ca.neo.examples.FuzzyLogicExample;
import ca.neo.model.StructuralException;

/**
 * TODO: Under Construction
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
		super("Fuzzy Logic Example", FuzzyLogicExample.createNetwork());
	}

}
