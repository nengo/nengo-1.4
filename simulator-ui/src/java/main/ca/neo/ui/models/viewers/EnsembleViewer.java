package ca.neo.ui.models.viewers;

import ca.neo.model.Ensemble;
import ca.neo.model.Node;
import ca.neo.model.neuron.Neuron;
import ca.neo.ui.models.nodes.PEnsemble;
import ca.neo.ui.models.nodes.PNeuron;
import ca.shu.ui.lib.util.Util;

public class EnsembleViewer extends NodeViewer {

	public EnsembleViewer(PEnsemble ensembleUI) {
		super(ensembleUI);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void updateNodesFromModel() {

		Node[] nodes = getModel().getNodes();

		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			if (node instanceof Neuron) {
				Neuron neuron = (Neuron) node;

				PNeuron neuronUI = new PNeuron(neuron);

				addNodeToViewer(neuronUI, false, false, false);
			} else {
				Util.Error("Unsupported node type "
						+ node.getClass().getSimpleName()
						+ " in EnsembleViewer");
			}

		}

	}

	@Override
	public Ensemble getModel() {

		return (Ensemble) super.getModel();
	}

	@Override
	public void applyDefaultLayout() {
		applySquareLayout();

	}

}
