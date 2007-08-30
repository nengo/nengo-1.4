package ca.shu.ui.lib.actions;

import java.awt.event.ActionEvent;
import java.io.Serializable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;

import ca.shu.ui.lib.exceptions.ActionException;
import ca.shu.ui.lib.util.Util;

public abstract class StandardAction implements Serializable {

	private static final long serialVersionUID = 1L;

	boolean actionCompleted = false;

	String actionName;

	String description;
	boolean isEnabled = true;

	/**
	 * 
	 * @param description
	 *            Description of the action
	 */
	public StandardAction(String description) {
		super();
		this.description = description;
	}

	/**
	 * 
	 * @param description
	 *            Description of the action
	 * @param actionName
	 *            Name to give to the Swing Action Object
	 */
	public StandardAction(String description, String actionName) {
		this(description);

		this.actionName = actionName;
	}

	public boolean canBeUndone() {
		return false;
	}

	public void doAction() {

		try {
			action();
			postAction();
			actionCompleted = true;
		} catch (ActionException e) {
			processActionException(e);
		}
	}

	public void doActionLater() {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				doAction();
			}
		});
	}

	public Action getSwingAction() {
		SwingAction action;
		if (getActionName() != null) {
			action = new SwingAction(getActionName());
		} else {
			action = new SwingAction(getDescription());
		}

		if (!isEnabled()) {
			action.setEnabled(false);
		}
		return action;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * 
	 * @return Whether the action was successful
	 */
	protected abstract void action() throws ActionException;

	protected String getActionName() {
		return actionName;
	}

	protected String getDescription() {
		return description;
	}

	protected boolean isActionCompleted() {
		return actionCompleted;
	}

	protected void postAction() {

	}

	protected void processActionException(ActionException e) {
		if (e.showWarning()) {
			System.out.println("Action Exception: " + e.toString());
			Util.UserWarning(e.getMessage());

		} else
			System.out.println("Action Exception: " + e.toString());
	}

	class SwingAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SwingAction(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent arg0) {
			doActionLater();
		}

	}
}
