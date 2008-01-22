package ca.shu.ui.lib.actions;

import java.util.Collection;

import ca.shu.ui.lib.world.WorldObject;

public class RemoveObjectsAction extends StandardAction {

	private static final long serialVersionUID = 1L;
	private Collection<WorldObject> objectsToRemove;

	public RemoveObjectsAction(Collection<WorldObject> objectsToRemove,
			String actionName) {
		super(actionName);
		this.objectsToRemove = objectsToRemove;
	}

	@Override
	protected void action() throws ActionException {
		for (WorldObject wo : objectsToRemove) {
			wo.destroy();
		}

	}

}
