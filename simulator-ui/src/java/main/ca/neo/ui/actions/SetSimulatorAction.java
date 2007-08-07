package ca.neo.ui.actions;

import ca.neo.model.Network;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;

public class SetSimulatorAction extends StandardAction {
	Network network;

	public SetSimulatorAction(String actionName, Network network) {
		super("Set simulator", actionName);
		this.network = network;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void action() throws ActionException {
		// TODO Auto-generated method stub

	}

}
