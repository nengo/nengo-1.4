package ca.neo.ui.models;

import ca.neo.model.Node;

public abstract class PModelNode extends PModelConfigurable {
	public Node getNode() {
		return (Node) getModel();
	}
}
