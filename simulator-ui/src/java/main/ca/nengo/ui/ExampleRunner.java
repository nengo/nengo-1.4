package ca.nengo.ui;

import javax.swing.SwingUtilities;

import ca.nengo.model.Network;
import ca.nengo.ui.models.nodes.UINetwork;
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
	public ExampleRunner(Network network) {
		this.network = network;
		System.out.println("Running example: " + network.getName());

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
	public ExampleRunner(UINetwork network) {
		this( network.getModel());
		this.networkUI = network;
	}

	protected void doStuff(UINetwork network) {

	}

	/**
	 * Builds a NeoGraphics User Interface
	 */
	private UINetwork buildUI() {
		NengoGraphics nengoGraphics = new NengoGraphics();

		TrackedStatusMsg task;
		task = new TrackedStatusMsg("Creating Model UI");
		if (networkUI == null) {

			networkUI = new UINetwork(network);
			nengoGraphics.getWorld().getGround().addChild(networkUI);
			networkUI.openViewer();
		}

		processNetwork(networkUI);
		task.finished();

		return networkUI;

	}

	protected void processNetwork(UINetwork network) {

	}

}
