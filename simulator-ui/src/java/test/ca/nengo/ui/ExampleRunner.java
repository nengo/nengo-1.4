/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "ExampleRunner.java". Description: 
"Used to conveniently create a NeoGraphics instance with an existing Network
  model
  
  @author Shu Wu"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU 
Public License license (the GPL License), in which case the provisions of GPL 
License are applicable  instead of those above. If you wish to allow use of your 
version of this file only under the terms of the GPL License and not to allow 
others to use your version of this file under the MPL, indicate your decision 
by deleting the provisions above and replace  them with the notice and other 
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
*/

package ca.nengo.ui;

import javax.swing.SwingUtilities;

import ca.nengo.model.Network;
import ca.nengo.ui.NengoGraphics;
import ca.nengo.ui.lib.objects.activities.TrackedStatusMsg;
import ca.nengo.ui.models.nodes.UINetwork;

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
