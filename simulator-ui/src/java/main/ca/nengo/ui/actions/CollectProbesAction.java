package ca.nengo.ui.actions;

import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.ui.lib.actions.ActionException;
import ca.nengo.ui.lib.actions.StandardAction;

public class CollectProbesAction extends StandardAction {
	
	private static final long serialVersionUID = 1L;
	
	NetworkImpl myNetwork;
	
	public CollectProbesAction(NetworkImpl network)
	{
		super("Collect probes from subnetworks", "Collect subnetwork probes", false);
		myNetwork = network;
	}

	@Override
	protected void action() throws ActionException 
	{
		myNetwork.collectAllProbes();
	}

}
