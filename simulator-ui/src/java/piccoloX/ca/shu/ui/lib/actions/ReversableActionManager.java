package ca.shu.ui.lib.actions;

import java.util.Vector;

import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.GFrame;

public class ReversableActionManager {

	static final int MAX_NUM_OF_UNDO_ACTIONS = 5;

	int numOfUndoSteps = 0;

	GFrame parent;

	Vector<ReversableAction> reversableActions;

	public ReversableActionManager(GFrame parent) {
		super();
		reversableActions = new Vector<ReversableAction>(
				MAX_NUM_OF_UNDO_ACTIONS + 1);
		this.parent = parent;
	}

	public void addReversableAction(ReversableAction action) {
		reversableActions.add(action);

		while (numOfUndoSteps > 0) {
			reversableActions.remove(reversableActions.size() - 1);
			numOfUndoSteps--;
		}

		if (reversableActions.size() > MAX_NUM_OF_UNDO_ACTIONS) {
			ReversableAction reversableAction = reversableActions.remove(0);
			reversableAction.finalizeAction();
		}
		updateParent();
	}

	public boolean canRedo() {
		if (numOfUndoSteps > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean canUndo() {
		if ((reversableActions.size() - numOfUndoSteps) > 0)
			return true;
		else
			return false;

	}

	public String getRedoActionDescription() {
		if (canRedo()) {
			return reversableActions.get(
					reversableActions.size() - numOfUndoSteps).getDescription();
		} else {
			return "none";
		}
	}

	public String getUndoActionDescription() {
		if (canUndo()) {
			return reversableActions.get(
					reversableActions.size() - 1 - numOfUndoSteps)
					.getDescription();
		} else {
			return "none";
		}
	}

	public void redoAction() {
		ReversableAction action = reversableActions.get(reversableActions
				.size()
				- numOfUndoSteps);

		numOfUndoSteps--;

		action.doActionLater();
		updateParent();
	}

	public void undoAction() {
		if (canUndo()) {

			ReversableAction action = reversableActions.get(reversableActions
					.size()
					- 1 - numOfUndoSteps);
			numOfUndoSteps++;
			action.undoAction();
		} else {
			Util.UserError("Cannot undo anymore steps");
		}

		updateParent();
	}

	public void updateParent() {
		parent.reversableActionsUpdated();
	}
}
