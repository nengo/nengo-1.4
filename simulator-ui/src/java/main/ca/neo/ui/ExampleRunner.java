package ca.neo.ui;

import javax.swing.SwingUtilities;

import ca.neo.model.Network;
import ca.neo.ui.models.nodes.PNetwork;
import ca.shu.ui.lib.objects.widgets.TrackedMsg;

public class ExampleRunner {
	String networkName;
	Network network;

	public ExampleRunner(String networkName, Network exampleNetwork) {
		this.networkName = networkName;
		this.network = exampleNetwork;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				buildUI();
			}
		});
	}

	protected void buildUI() {

		NeoGraphics neoGraphics = new NeoGraphics(networkName);

		TrackedMsg task = new TrackedMsg("Creating NEO Network model");

		task.finished();

		task = new TrackedMsg("Creating Model UI");
		PNetwork networkUI = new PNetwork(network);

		neoGraphics.addWorldObject(networkUI);

		networkUI.openViewer();

		task.finished();

	}

}
