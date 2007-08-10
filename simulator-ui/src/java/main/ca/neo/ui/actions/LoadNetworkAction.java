package ca.neo.ui.actions;

import ca.neo.model.Network;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.nodes.PNetwork;
import ca.shu.ui.lib.util.Util;

public class LoadNetworkAction extends LoadObjectAction {

	private static final long serialVersionUID = 1L;
	INodeContainer nodeContainer;

	public LoadNetworkAction(String actionName, INodeContainer nodeContainer) {
		super("Load network from file", actionName);
		this.nodeContainer = nodeContainer;
	}

	@Override
	protected void processObject(Object objLoaded) {
		if (objLoaded instanceof Network) {

			PNetwork networkUI = new PNetwork((Network) objLoaded);
			nodeContainer.addNeoNode(networkUI);

		} else {
			Util.Error("Could not load Network file");
		}

	}

}