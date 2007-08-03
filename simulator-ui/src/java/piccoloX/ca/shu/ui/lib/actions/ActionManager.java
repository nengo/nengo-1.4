package ca.shu.ui.lib.actions;

import java.util.Stack;
import java.util.Vector;

import ca.shu.ui.lib.world.impl.GFrame;

public class ActionManager {

	static final int MAX_NUM_OF_UNDO_ACTIONS = 5;

	Vector<ReversableAction> reversableActions;

	GFrame parent;

	public ActionManager(GFrame parent) {
		super();
		reversableActions = new Vector<ReversableAction>(
				MAX_NUM_OF_UNDO_ACTIONS + 1);
		this.parent = parent;
	}

	public void addReversableAction(ReversableAction action) {
		reversableActions.add(action);

		if (reversableActions.size() > MAX_NUM_OF_UNDO_ACTIONS) {
			ReversableAction reversableAction = reversableActions.remove(0);
			reversableAction.finalizeAction();
		}
		updateParent();
	}

	public void updateParent() {
		parent.reversableActionsUpdated();
	}

	public void undoLastAction() {
		ReversableAction action = reversableActions.remove(reversableActions
				.size() - 1);

		action.undoAction();
		updateParent();
	}

	public String getLastActionDescription() {
		if (hasReversableAction()) {
			return reversableActions.get(reversableActions.size() - 1)
					.getDescription();
		} else {
			return "none";
		}
	}

	public boolean hasReversableAction() {
		if (reversableActions.size() > 0)
			return true;
		else
			return false;

	}

	public void redoLastAction() {
		ReversableAction action = reversableActions.get(reversableActions
				.size() - 1);

		action.doAction();
		addReversableAction(action);
		updateParent();
	}
}
