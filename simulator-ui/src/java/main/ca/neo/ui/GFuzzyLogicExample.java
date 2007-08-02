package ca.neo.ui;

import ca.neo.examples.FuzzyLogicExample;
import ca.neo.model.StructuralException;
import ca.neo.ui.models.nodes.PNetwork;
import ca.shu.ui.lib.util.Util;

/**
 * In this example, the FuzzyLogic network is constructed from an existing
 * Network Model
 * 
 * @author Shu
 * 
 */
public class GFuzzyLogicExample {
	public static void main(String[] args) {
		NeoGraphics neoGraphics = new NeoGraphics("FuzzyLogic Example");

		try {
			PNetwork networkUI = new PNetwork(FuzzyLogicExample.createNetwork());

			neoGraphics.addWorldObject(networkUI);

			networkUI.openNetworkViewer();

		} catch (StructuralException e) {
			Util.Error("Could not create network: " + e.toString());
		}
	}

}
