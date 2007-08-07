package ca.shu.ui.lib.actions;

import java.awt.event.ActionEvent;
import java.io.Serializable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;

import ca.shu.ui.lib.util.Util;

public abstract class StandardAction implements Serializable {

	private static final long serialVersionUID = 1L;

	String actionName;

	String description;

	boolean actionCompleted = false;
	boolean isEnabled = true;

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

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

	protected void processActionException(ActionException e) {
		if (e.showWarning()) {
			System.out.println("Action Exception: " + e.toString());
			Util.Warning(e.getMessage());

		} else
			System.out.println("Action Exception: " + e.toString());
	}

	public void doAction() {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					action();
					postAction();
					actionCompleted = true;
				} catch (ActionException e) {
					processActionException(e);
				}
			}
		});
	}

	protected void postAction() {

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

	class SwingAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SwingAction(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent arg0) {
			doAction();
		}

	}

	protected boolean isActionCompleted() {
		return actionCompleted;
	}
}
