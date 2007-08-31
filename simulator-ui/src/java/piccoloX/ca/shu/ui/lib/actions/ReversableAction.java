package ca.shu.ui.lib.actions;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.UserMessages;

/**
 * A reversable action than can be undone.
 * 
 * @author Shu
 */
public abstract class ReversableAction extends StandardAction {

	public ReversableAction(String description) {
		super(description);
	}

	public ReversableAction(String description, String actionName) {
		super(description, actionName);
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
		if (!isActionCompleted()) {
			UIEnvironment.getInstance().getActionManager().addReversableAction(this);
		}
	}

	/**
	 * Does the undo work
	 * 
	 * @return Whether the undo action was successful
	 */
	protected abstract void undo() throws ActionException;

	/**
	 * Undo the action
	 */
	public void undoAction() {
		if (!isActionCompleted()) {
			UserMessages.showError("Action was never done, so it can't be undone");
			return;
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					undo();
				} catch (ActionException e) {
					e.defaultHandleBehavior();
				}
			}
		});
	}

}
