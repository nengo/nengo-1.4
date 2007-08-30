package ca.shu.ui.lib.actions;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.exceptions.ActionException;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;

/**
 * An action that can be undo and redoed
 * 
 * @author Shu
 * 
 */
public abstract class ReversableAction extends StandardAction {

	public ReversableAction(String description) {
		super(description);
	}

	public ReversableAction(String description, String actionName) {
		super(description, actionName);
	}

	@Override
	public boolean canBeUndone() {
		return true;
	}

	public void undoAction() {
		if (!isActionCompleted()) {
			Util.UserError("Action was never done, so it can't be undone");
			return;
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					undo();
				} catch (ActionException e) {
					processActionException(e);
				}
			}
		});
	}

	/**
	 * This function is called if completing the action requires two stages.
	 * First stage does the action but it can still be undone (leaving some
	 * threads intact). This function is the second (optional stage). It
	 * completes the action in such a way that it cannot be undone.
	 */
	protected void finalizeAction() {

	}

	@Override
	protected void postAction() {
		/*
		 * Only add the action once to the Action manager
		 */
		if (!actionCompleted) {
			UIEnvironment.getActionManager().addReversableAction(this);
		}
	}

	/**
	 * 
	 * @return Whether the undo action was successful
	 */
	protected abstract void undo() throws ActionException;

}
