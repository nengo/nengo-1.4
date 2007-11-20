package ca.neo.ui.dev;

import ca.neo.math.Function;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.model.Network;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;
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
