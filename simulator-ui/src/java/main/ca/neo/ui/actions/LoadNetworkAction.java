package ca.neo.ui.actions;

import ca.neo.model.Network;
import ca.neo.ui.models.nodes.PNetwork;
import ca.shu.ui.lib.util.Util;

public abstract class LoadNetworkAction extends LoadObjectAction {

	private static final long serialVersionUID = 1L;

	public LoadNetworkAction(String actionName) {
		super("Load network from file", actionName);
	}

	@Override
	protected void processObject(Object objLoaded) {
		if (objLoaded instanceof Network) {
			
			PNetwork networkUI = new PNetwork((Network) objLoaded);
			gotNetwork(networkUI);
			
			

		} else {
			Util.Error("Could not load Network file");
		}

	}

	protected abstract void gotNetwork(PNetwork network);
	
}