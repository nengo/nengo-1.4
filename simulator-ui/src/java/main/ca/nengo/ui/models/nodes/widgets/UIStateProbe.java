/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "UIStateProbe.java". Description: 
"UI Wrapper for a Simulator Probe
  
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

package ca.nengo.ui.models.nodes.widgets;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import ca.nengo.io.MatlabExporter;
import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.Probeable;
import ca.nengo.model.SimulationException;
import ca.nengo.ui.actions.PlotAdvanced;
import ca.nengo.ui.actions.PlotTimeSeries;
import ca.nengo.ui.models.UINeoNode;
import ca.nengo.ui.models.nodes.UIEnsemble;
import ca.nengo.ui.models.tooltips.TooltipBuilder;
import ca.nengo.ui.models.viewers.EnsembleViewer;
import ca.nengo.util.Probe;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.activities.TrackedAction;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;

/**
 * UI Wrapper for a Simulator Probe
 * 
 * @author Shu Wu
 */
public class UIStateProbe extends UIProbe {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Probe createProbe(UINeoNode nodeAttachedTo, String state)
			throws SimulationException {

		/*
		 * Creates the probe
		 */
		Node node = nodeAttachedTo.getModel();
		Probe probe;
		try {
			if (nodeAttachedTo.getParentViewer() instanceof EnsembleViewer) {
				EnsembleViewer ensembleViewer = (EnsembleViewer) nodeAttachedTo.getParentViewer();

				UIEnsemble ensemble = ensembleViewer.getViewerParent();
				Network network = ensemble.getNetworkParent().getModel();

				probe = network.getSimulator().addProbe(ensemble.getName(), (Probeable) node,
						state, true);

				nodeAttachedTo.showPopupMessage("Probe (" + state + ") added to Simulator");

			} else {
				probe = nodeAttachedTo.getNetworkParent().getSimulator().addProbe(node.getName(),
						state, true);
			}
		} catch (SimulationException exception) {
			// nodeAttachedTo.popupTransientMsg("Could not add Probe (" + state
			// + ") added to Simulator");
			throw exception;
		}
		return probe;
	}

	public UIStateProbe(UINeoNode nodeAttachedTo, Probe probeModel) {
		super(nodeAttachedTo, probeModel);
	}

	public UIStateProbe(UINeoNode nodeAttachedTo, String state) throws SimulationException {
		super(nodeAttachedTo, createProbe(nodeAttachedTo, state));
	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);

		menu.addSection("Probe");
		MenuBuilder plotMenu = menu.addSubMenu("plot");
		plotMenu.addAction(new PlotTimeSeries(getModel().getData(), getName()));
		plotMenu.addAction(new PlotAdvanced(getModel().getData(), getName()));

		MenuBuilder exportMenu = menu.addSubMenu("export data");
		exportMenu.addAction(new ExportToMatlabAction());

	}

	@Override
	public void doubleClicked() {
		(new PlotTimeSeries(getModel().getData(), getName())).doAction();
	}

	@Override
	protected void constructTooltips(TooltipBuilder tooltips) {
		super.constructTooltips(tooltips);
		tooltips.addProperty("Attached to", getModel().getStateName());
	}

	@Override
	protected void prepareToDestroyModel() {
		try {
			getProbeParent().getNetworkParent().getSimulator().removeProbe(getModel());
			getProbeParent().showPopupMessage("Probe removed from Simulator");
		} catch (SimulationException e) {
			UserMessages.showError("Could not remove probe: " + e.getMessage());
		}

		super.prepareToDestroyModel();
	}

	/**
	 * @param name
	 *            prefix of the fileName to be exported to
	 * @throws IOException
	 */
	public void exportToMatlab(String name) {
		MatlabExporter me = new MatlabExporter();
		me.add(getName(), getModel().getData());
		try {
			me.write(new File(name + ".mat"));
		} catch (IOException e) {
			UserMessages.showError("Could not export file: " + e.toString());
		}
	}

	@Override
	public Probe getModel() {
		return (Probe) super.getModel();
	}

	@Override
	public String getTypeName() {
		return "State Probe";
	}

	@Override
	public void modelUpdated() {
		setName(((Probe) getModel()).getStateName());
	}

	/**
	 * Action for exporting to MatLab
	 * 
	 * @author Shu Wu
	 */
	class ExportToMatlabAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		String name;

		public ExportToMatlabAction() {
			super("Matlab");
		}

		@Override
		protected void action() throws ActionException {

			name = JOptionPane.showInputDialog("Enter name of file to export to: ");

			(new TrackedAction("Exporting to matlab") {

				private static final long serialVersionUID = 1L;

				@Override
				protected void action() throws ActionException {
					exportToMatlab(name);
				}

			}).doAction();

		}
	}
}
