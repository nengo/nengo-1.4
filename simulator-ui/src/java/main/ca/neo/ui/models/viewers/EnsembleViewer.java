package ca.neo.ui.models.viewers;

import ca.neo.model.Ensemble;
import ca.neo.model.Node;
import ca.neo.model.Probeable;
import ca.neo.model.neuron.Neuron;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.nodes.UIEnsemble;
import ca.neo.ui.models.nodes.UINeuron;
import ca.neo.util.Probe;
import ca.shu.ui.lib.util.Util;

/**
 * Viewer for peeking into Ensembles
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
	public void updateViewFromModel() {
		removeAllNeoNodes();

		Node[] nodes = getModel().getNodes();

		/*
		 * Construct Neurons
		 */
		for (Node node : nodes) {
			if (node instanceof Neuron) {
				Neuron neuron = (Neuron) node;

				UINeuron neuronUI = new UINeuron(neuron);

				addNeoNode(neuronUI, false, false, false);
			} else {
				Util.UserError("Unsupported node type "
						+ node.getClass().getSimpleName()
						+ " in EnsembleViewer");
			}

		}

		if (getViewerParent().getParentNetwork() != null) {
			/*
			 * Construct probes
			 */
			Probe[] probes = getViewerParent().getParentNetwork()
					.getSimulator().getProbes();

			for (Probe probe : probes) {
				Probeable target = probe.getTarget();

				if (!(target instanceof Node)) {
					Util.UserError("Unsupported target type for probe");
				} else {

					if (probe.isInEnsemble()
							&& probe.getEnsembleName() == getModel().getName()) {
						Node node = (Node) target;

						UINeoNode nodeUI = getNode(node.getName());
						nodeUI.showProbe(probe);
					}
				}

			}
		}

	}

}
