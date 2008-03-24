package ca.nengo.ui.models.nodes;

import ca.nengo.model.neuron.Neuron;
import ca.nengo.ui.models.UINeoNode;
import ca.nengo.ui.models.icons.NodeIcon;

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
		setIcon(new NodeIcon(this));
	}

	@Override
	public String getTypeName() {
		return "Neuron";
	}

}
