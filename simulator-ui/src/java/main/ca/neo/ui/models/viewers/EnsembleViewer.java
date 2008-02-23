package ca.neo.ui.models.viewers;

import ca.neo.model.Ensemble;
import ca.neo.model.Node;
import ca.neo.model.Probeable;
import ca.neo.model.neuron.Neuron;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.nodes.UIEnsemble;
import ca.neo.ui.models.nodes.UINeuron;
import ca.neo.util.Probe;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;

/**
 * Viewer for peeking into an Ensemble
 * 
 * @author Shu
 */
public class EnsembleViewer extends NodeViewer {

	private static final long serialVersionUID = 1L;

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
