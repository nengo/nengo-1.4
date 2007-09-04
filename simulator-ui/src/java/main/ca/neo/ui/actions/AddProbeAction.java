package ca.neo.ui.actions;

import java.util.Map.Entry;

import ca.neo.model.SimulationException;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.nodes.widgets.UISimulatorProbe;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Action for adding probes
 * 
 * @author Shu Wu
 */
public class AddProbeAction extends ReversableAction {

	private static final long serialVersionUID = 1;

	private UISimulatorProbe probeCreated;

	private Entry<String, String> state;
	private UINeoNode myNode;

	public AddProbeAction(UINeoNode nodeParent, Entry<String, String> state) {
		super(state.getKey() + " - " + state.getValue());
		this.state = state;
		this.myNode = nodeParent;

	}

	@Override
	protected void action() throws ActionException {

		try {
			probeCreated = myNode.addProbe(state.getKey());
		} catch (SimulationException e) {

			UserMessages.showError("Could not add probe: " + e.toString());
		}

	}

	@Override
	protected void undo() throws ActionException {
		myNode.removeProbe(probeCreated);

	}

}