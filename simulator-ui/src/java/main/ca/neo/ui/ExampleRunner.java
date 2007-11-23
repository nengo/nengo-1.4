package ca.neo.ui;

import javax.swing.SwingUtilities;

import ca.neo.model.Network;
import ca.neo.ui.models.nodes.UINetwork;
import ca.shu.ui.lib.objects.activities.TrackedStatusMsg;

/**
 * Used to conveniently create a NeoGraphics instance with an existing Network
 * model
 * 
 * @author Shu Wu
 */
public class ExampleRunner {
	private Network network;
	private String networkName;
	private UINetwork networkUI;

	/**
	 * @param name
	 *            Name to be given to this instance
	 * @param network
	 *            Network to be given to NeoGraphics
	 */
	public ExampleRunner(String name, Network network) {
		this.networkName = name;
		this.network = network;

		/**
		 * All UI funcitons and constructors must be invoked from the Swing
		 * Event Thread
		 */
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				buildUI();
				
				doStuff();
			}
		});
	}

	protected void doStuff() {
		
	}
	
	/**
	 * Builds a NeoGraphics User Interface
	 */
	private void buildUI() {

		NeoGraphics neoGraphics = new NeoGraphics(networkName);

		TrackedStatusMsg task = new TrackedStatusMsg(
				"Creating NEO Network model");

		task.finished();

		task = new TrackedStatusMsg("Creating Model UI");
		networkUI = new UINetwork(network);
		neoGraphics.addNeoNode(networkUI);

		processNetwork(networkUI);
		task.finished();

	}

	protected void processNetwork(UINetwork network) {

	}

	public UINetwork getNetworkUI() {
		return networkUI;
	}

}
