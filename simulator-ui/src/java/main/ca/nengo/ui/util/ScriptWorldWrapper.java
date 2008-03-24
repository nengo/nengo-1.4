package ca.nengo.ui.util;

import ca.nengo.model.Node;
import ca.nengo.ui.NengoGraphics;
import ca.nengo.ui.models.NodeContainer.ContainerException;

public class ScriptWorldWrapper {
	private NengoGraphics nengoGraphics;

	public ScriptWorldWrapper(NengoGraphics neoGraphics) {
		super();
		this.nengoGraphics = neoGraphics;
	}

	public void add(Node node) throws ContainerException {
		nengoGraphics.addNodeModel(node);
	}

	public void add(Node node, double posX, double posY) throws ContainerException {
		nengoGraphics.addNodeModel(node, posX, posY);
	}

	public void remove(Node node) {
		nengoGraphics.removeNodeModel(node);
	}
}
