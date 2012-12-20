package ca.nengo.ui.test;

import ca.nengo.model.StructuralException;
import ca.nengo.ui.NengoGraphics;
import ca.nengo.ui.actions.RunSimulatorAction;
import ca.nengo.ui.dev.ExampleRunner;
import ca.nengo.ui.dev.FuzzyLogicExample;
import ca.nengo.ui.models.nodes.UINetwork;

/**
 * Creates a Fuzzy Network, and runs it for 1 second
 */
public class DataViewerTest2 extends ExampleRunner {

	public static void main(String[] args) {

		try {
			new DataViewerTest2();
		} catch (StructuralException e) {
			e.printStackTrace();
		}
	}

	public DataViewerTest2() throws StructuralException {
		super(FuzzyLogicExample.createNetwork());
	}

	@Override
	protected void doStuff(UINetwork network) {
		(new RunSimulatorAction("Run", network, 0f, 1f, 0.002f)).doAction();
		NengoGraphics.getInstance().setDataViewerPaneVisible(true);
	}

}
