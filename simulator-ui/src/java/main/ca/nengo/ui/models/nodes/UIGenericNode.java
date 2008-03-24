package ca.nengo.ui.models.nodes;

import ca.nengo.model.Node;
import ca.nengo.ui.models.UINeoNode;
import ca.nengo.ui.models.icons.NodeIcon;

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
		setIcon(new NodeIcon(this));
	}

	@Override
	public String getTypeName() {
		return "Generic Node";
	}

}
