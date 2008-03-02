package ca.neo.ui.util;

import ca.neo.model.Node;
import ca.neo.ui.NengoGraphics;

public class ScriptWorldWrapper {
	private NengoGraphics neoGraphics;
	
	public ScriptWorldWrapper(NengoGraphics neoGraphics) {
		super();
		this.neoGraphics = neoGraphics;
	}

	public void add(Node node) {
		neoGraphics.addNodeModel(node);
	}

	public void add(Node node, double posX, double posY) {
		neoGraphics.addNodeModel(node, posX, posY);
	}

	public void remove(Node node) {
		neoGraphics.removeNodeModel(node);
	}
}
