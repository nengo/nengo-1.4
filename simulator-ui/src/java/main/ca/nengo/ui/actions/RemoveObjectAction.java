package ca.nengo.ui.actions;

import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.world.WorldObject;

/**
 * Action for removing this UI Wrapper and the model
 * 
 * @author Shu Wu
 */
public class RemoveObjectAction extends StandardAction {

	private static final long serialVersionUID = 1L;
	WorldObject objectToRemove;

	public RemoveObjectAction(String actionName, WorldObject objectToRemove) {
		super("Remove " + objectToRemove.getName(), actionName);
		this.objectToRemove = objectToRemove;
	}

	@Override
	protected void action() throws ActionException {
		objectToRemove.destroy();
		objectToRemove = null;
	}

}