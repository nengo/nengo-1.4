package ca.neo.ui;

import ca.neo.examples.InferenceExample;
import ca.neo.model.StructuralException;

/**
 * In this example, the FuzzyLogic network is constructed from an existing
 * Network Model
 * 
 * @author Shu
 * 
 */
public class GInferenceExample extends ExampleRunner {

	public static void main(String[] args) {
		try {
			new GInferenceExample();
		} catch (StructuralException e) {
			e.printStackTrace();
		}
	}

	public GInferenceExample() throws StructuralException {
		super("Inference Example", InferenceExample.createNetwork());

	}

}
