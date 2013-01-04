/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "EnsembleViewer.java". Description: 
"Viewer for peeking into an Ensemble
  
  @author Shu"

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

package ca.nengo.ui.models.viewers;

import ca.nengo.model.Ensemble;
import ca.nengo.model.Node;
import ca.nengo.model.Network;
import ca.nengo.model.Probeable;
import ca.nengo.model.neuron.Neuron;
import ca.nengo.ui.lib.util.UserMessages;
import ca.nengo.ui.lib.util.Util;
import ca.nengo.ui.models.UINeoNode;
import ca.nengo.ui.models.nodes.UIEnsemble;
import ca.nengo.ui.models.nodes.UINeuron;
import ca.nengo.ui.models.nodes.UINetwork;
import ca.nengo.util.Probe;

/**
 * Viewer for peeking into an Ensemble
 * 
 * @author Shu
 */
public class EnsembleViewer extends NodeViewer {

	/**
	 * @param ensembleUI
	 *            Parent Ensemble UI Wrapper
	 */
	public EnsembleViewer(UIEnsemble ensembleUI) {
		super(ensembleUI);
	}

	@Override
	public void applyDefaultLayout() {
		if (getUINodes().size() == 0)
			return;

		applySortLayout(SortMode.BY_NAME);
	}

	@Override
	public Ensemble getModel() {

		return (Ensemble) super.getModel();
	}

	@Override
	public UIEnsemble getViewerParent() {
		return (UIEnsemble) super.getViewerParent();
	}

	@Override
	public void updateViewFromModel(boolean isFirstUpdate) {
		getGround().clearLayer();

		Node[] nodes = getModel().getNodes();

		/*
		 * Construct Neurons
		 */
		for (Node node : nodes) {
			if (node instanceof Neuron) {
				Neuron neuron = (Neuron) node;

				UINeuron neuronUI = new UINeuron(neuron);

				addUINode(neuronUI, false, false);
			} else if (node instanceof Ensemble) {
				Ensemble ensemble = (Ensemble)node;
				UIEnsemble ensembleUI = new UIEnsemble(ensemble);
				addUINode(ensembleUI,false,false);
				
			} else if (node instanceof Network) {
				Network network = (Network)node;
				UINetwork networkUI = new UINetwork(network);
				addUINode(networkUI, false, false);
			} else {
				UserMessages.showError("Unsupported node type " + node.getClass().getSimpleName()
						+ " in EnsembleViewer");
			}
		}

		if (getViewerParent().getNetworkParent() != null) {
			/*
			 * Construct probes
			 */
			Probe[] probes = getViewerParent().getNetworkParent().getSimulator().getProbes();

			for (Probe probe : probes) {
				Probeable target = probe.getTarget();

				if (!(target instanceof Node)) {
					UserMessages.showError("Unsupported target type for probe");
				} else {

					if (probe.isInEnsemble() && probe.getEnsembleName() == getModel().getName()) {
						Node node = (Node) target;

						UINeoNode nodeUI = getUINode(node);
						nodeUI.showProbe(probe);
					}
				}

			}
		}
	}

	@Override
	protected void removeChildModel(Node node) {
		Util.Assert(false, "Cannot remove model");
	}

	@Override
	protected boolean canRemoveChildModel(Node node) {
		return false;
	}

}
