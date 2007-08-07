package ca.neo.ui;

import ca.neo.examples.FuzzyLogicExample;
import ca.neo.model.Network;
import ca.neo.model.StructuralException;
import ca.neo.ui.models.nodes.PNetwork;
import ca.neo.ui.models.viewers.NodeViewer;
import ca.shu.ui.lib.objects.widgets.TrackedMsg;
import ca.shu.ui.lib.util.Util;
import edu.uci.ics.jung.visualization.FRLayout;

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
			TrackedMsg task = new TrackedMsg(
					"Creating FuzzyLogic NEO Network model");
			Network network = FuzzyLogicExample.createNetwork();
			task.finished();

			task = new TrackedMsg("Creating FuzzyLogic Model UI");
			PNetwork networkUI = new PNetwork(network);

			neoGraphics.addWorldObject(networkUI);

			NodeViewer viewer = networkUI.openViewer();

			task.finished();

		} catch (StructuralException e) {
			Util.Error("Could not create network: " + e.toString());
		}
	}

}
