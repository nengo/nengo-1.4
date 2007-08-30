package ca.shu.ui.lib.actions;

import java.util.Vector;

import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.AppFrame;

/**
 * Manages reversable actions
 * 
 * @author Shu Wu
 */
public class ReversableActionManager {

	/**
	 * Max number of undo steps to reference
	 */
	static final int MAX_NUM_OF_UNDO_ACTIONS = 5;

	/**
	 * Number of undo steps that have been taken
	 */
	private int undoStepCount = 0;

	private AppFrame parent;

	/**
	 * A collection of reversable actions
	 */
	private Vector<ReversableAction> reversableActions;

	/**
	 * Create a new reversable action manager
	 * 
	 * @param parent
	 *            Application parent of this manager
	 */
	public ReversableActionManager(AppFrame parent) {
		super();
		reversableActions = new Vector<ReversableAction>(
				MAX_NUM_OF_UNDO_ACTIONS + 1);
		this.parent = parent;
	}

	/**
	 * @param action
	 *            Action to add
	 */
	public void addReversableAction(ReversableAction action) {
		reversableActions.add(action);

		while (undoStepCount > 0) {
			reversableActions.remove(reversableActions.size() - 1);
			undoStepCount--;
		}

		if (reversableActions.size() > MAX_NUM_OF_UNDO_ACTIONS) {
			ReversableAction reversableAction = reversableActions.remove(0);
			reversableAction.finalizeAction();
		}
		updateParent();
	}

	/**
	 * @return True, if an action can be redone
	 */
	public boolean canRedo() {
		if (undoStepCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return True, if an action can be undone
	 */
	public boolean canUndo() {
		if ((reversableActions.size() - undoStepCount) > 0)
			return true;
		else
			return false;

	}

	/**
	 * @return Description of the action that can be redone
	 */
	public String getRedoActionDescription() {
		if (canRedo()) {
			return reversableActions.get(
					reversableActions.size() - undoStepCount).getDescription();
		} else {
			return "none";
		}
	}

	/**
	 * @return Description of the action that can be undone
	 */
	public String getUndoActionDescription() {
		if (canUndo()) {
			return reversableActions.get(
					reversableActions.size() - 1 - undoStepCount)
					.getDescription();
		} else {
			return "none";
		}
	}

	/**
	 * Redo the focused action
	 */
	public void redoAction() {
		ReversableAction action = reversableActions.get(reversableActions
				.size()
				- undoStepCount);

		undoStepCount--;

		action.doActionLater();
		updateParent();
	}

	/**
	 * Undo the focused action
	 */
	public void undoAction() {
		if (canUndo()) {

			ReversableAction action = reversableActions.get(reversableActions
					.size()
					- 1 - undoStepCount);
			undoStepCount++;
			action.undoAction();
		} else {
			Util.UserError("Cannot undo anymore steps");
		}

		updateParent();
	}

	/**
	 * Updates the application parent that reversable actions have changed
	 */
	private void updateParent() {
		parent.reversableActionsUpdated();
	}
}
