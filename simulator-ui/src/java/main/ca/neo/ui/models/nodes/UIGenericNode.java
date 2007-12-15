package ca.neo.ui.models.nodes;

import ca.neo.model.Node;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.models.UINeoNode;

/**
 * Generic wrapper for any NEO Node. Used when no there is no specialized
 * wrapper.
 * 
 * @author Shu Wu
 */
public class UIGenericNode extends UINeoNode {

	private static final long serialVersionUID = 1L;

	public UIGenericNode(Node model) {
		super(model);
	}

	@Override
	protected Object configureModel(PropertySet configuredProperties)
			throws ConfigException {
		throw new UnsupportedOperationException();
	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTypeName() {
		return "Generic Node";
	}

}
