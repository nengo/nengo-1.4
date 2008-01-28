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
	private UINetwork networkUI;

	/**
	 * @param name
	 *            Name to be given to this instance
	 * @param network
	 *            Network to be given to NeoGraphics
	 */
	public ExampleRunner(String name, Network network) {
		this.network = network;
		System.out.println("Running example: " + name);

		/**
		 * All UI funcitons and constructors must be invoked from the Swing
		 * Event Thread
		 */
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UINetwork uiNetwork = buildUI();

				doStuff(uiNetwork);
			}
		});
	}

	/**
	 * @param name
	 *            Name to be given to this instance
	 * @param network
	 *            Network to be given to NeoGraphics
	 */
	public ExampleRunner(String name, UINetwork network) {
		this(name, network.getModel());
		this.networkUI = network;
	}

	protected void doStuff(UINetwork network) {

	}

	/**
	 * Builds a NeoGraphics User Interface
	 */
	private UINetwork buildUI() {

		TrackedStatusMsg task;
		task = new TrackedStatusMsg("Creating Model UI");
		if (networkUI == null) {
			NeoGraphics neoGraphics = new NeoGraphics();

			networkUI = new UINetwork(network);
			neoGraphics.addNeoNode(networkUI);
		}

		processNetwork(networkUI);
		task.finished();

		return networkUI;

	}

	protected void processNetwork(UINetwork network) {

	}

}
