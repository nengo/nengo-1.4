package ca.neo.ui.configurable.descriptors;

import ca.neo.model.impl.NodeFactory;
import ca.neo.ui.configurable.Property;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.panels.NodeFactoryPanel;

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
