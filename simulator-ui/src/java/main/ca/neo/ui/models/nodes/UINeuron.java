package ca.neo.ui.models.nodes;

import ca.neo.model.neuron.Neuron;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.icons.NeuronIcon;

/**
 * UI Wrapper for a Neuron
 * 
 * @author Shu Wu
 */
public class UINeuron extends UINeoNode {

	private static final long serialVersionUID = 1L;

	public UINeuron(Neuron model) {
		super(model);
		init();
	}

	private void init() {

		setIcon(new NeuronIcon(this));
	}

	@Override
	public String getTypeName() {
		return "Neuron";
	}

}
