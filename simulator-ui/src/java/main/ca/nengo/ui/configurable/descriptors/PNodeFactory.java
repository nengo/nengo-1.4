package ca.nengo.ui.configurable.descriptors;

import ca.nengo.model.impl.NodeFactory;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.PropertyInputPanel;
import ca.nengo.ui.configurable.panels.NodeFactoryPanel;

public class PNodeFactory extends Property {

	private static final long serialVersionUID = 1L;

	public PNodeFactory(String name) {
		super(name);
	}

	@Override
	protected PropertyInputPanel createInputPanel() {
		return new NodeFactoryPanel(this);
	}

	@Override
	public Class<NodeFactory> getTypeClass() {
		return NodeFactory.class;
	}

	@Override
	public String getTypeName() {
		return "Node Factory";
	}

}
