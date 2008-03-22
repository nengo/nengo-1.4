package ca.neo.ui.util;

import ca.neo.model.Node;
import ca.neo.ui.NengoGraphics;
import ca.neo.ui.models.NodeContainer.ContainerException;

public class ScriptWorldWrapper {
	private NengoGraphics neoGraphics;

	public ScriptWorldWrapper(NengoGraphics neoGraphics) {
		super();
		this.neoGraphics = neoGraphics;
	}

	public void add(Node node) throws ContainerException {
		neoGraphics.addNodeModel(node);
	}

	public void add(Node node, double posX, double posY) throws ContainerException {
		neoGraphics.addNodeModel(node, posX, posY);
	}

	public void remove(Node node) {
		neoGraphics.removeNodeModel(node);
	}
}
