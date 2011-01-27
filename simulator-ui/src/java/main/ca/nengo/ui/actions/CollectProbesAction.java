package ca.nengo.ui.actions;

//import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.ui.lib.actions.ActionException;
import ca.nengo.ui.lib.actions.StandardAction;
import ca.nengo.ui.models.nodes.UINetwork;

public class CollectProbesAction extends StandardAction {
	
	private static final long serialVersionUID = 1L;
	
	UINetwork myNetwork;
	
	public CollectProbesAction(UINetwork uinetwork)
	{
		super("Collect probes from subnetworks", "Collect subnetwork probes", false);
		myNetwork = uinetwork;
	}

	@Override
	protected void action() throws ActionException 
	{
		myNetwork.getModel().collectAllProbes();
		myNetwork.showPopupMessage("Probes collected");
	}

}
