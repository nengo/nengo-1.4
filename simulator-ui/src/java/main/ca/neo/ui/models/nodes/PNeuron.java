package ca.neo.ui.models.nodes;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.neo.model.Node;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.icons.NeuronIcon;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;

public class PNeuron extends PNeoNode {

	public PNeuron() {
		super();
		init();
	}

	public PNeuron(Node model) {
		super(model);
		init();
	}

	/**
	 * Initializes the PNetwork
	 */
	private void init() {

		setIcon(new NeuronIcon(this));
	}

	private static final long serialVersionUID = 1L;

	@Override
	public String getTypeName() {
		return "Neuron";
	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		throw new NotImplementedException();

	}

}
