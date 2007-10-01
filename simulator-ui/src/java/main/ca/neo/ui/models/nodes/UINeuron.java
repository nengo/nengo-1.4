package ca.neo.ui.models.nodes;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.neo.model.Node;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.icons.NeuronIcon;
import ca.shu.ui.lib.util.UserMessages;

/**
 * UI Wrapper for a Neuron
 * 
 * @author Shu Wu
 * 
 */
public class UINeuron extends UINeoNode {

	private static final long serialVersionUID = 1L;

	public UINeuron() {
		super();
		init();
	}

	public UINeuron(Node model) {
		super(model);
		init();
	}

	private void init() {

		setIcon(new NeuronIcon(this));
	}

	@Override
	protected Object configureModel(PropertySet configuredProperties)
			throws ConfigException {
		throw new NotImplementedException();
	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		UserMessages.showError("not implemented yet");
		return null;

	}

	@Override
	public String getTypeName() {
		return "Neuron";
	}

}
