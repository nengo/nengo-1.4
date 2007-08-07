package ca.neo.ui;

import ca.neo.examples.InferenceExample;
import ca.neo.model.Network;
import ca.neo.model.StructuralException;
import ca.neo.ui.models.nodes.PNetwork;
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
public class GInferenceExample {

	public static void main(String[] args) {

		NeoGraphics neoGraphics = new NeoGraphics("Inference Example");

		try {
			TrackedMsg task = new TrackedMsg(
					"Creating Inference NEO Network model");
			Network network = InferenceExample.createNetwork();
			task.finished();

			task = new TrackedMsg("Executing Inference Example");
			PNetwork networkUI = new PNetwork(network);

			neoGraphics.addWorldObject(networkUI);

			networkUI.openViewer();
			networkUI.getAndConstructViewer().applyJungLayout(FRLayout.class);
			task.finished();

		} catch (StructuralException e) {
			Util.Error("Could not create network: " + e.toString());
		}
	}

}
