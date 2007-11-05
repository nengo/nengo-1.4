package ca.shu.ui.lib.actions;

import java.awt.event.ActionEvent;
import java.io.Serializable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;

/**
 * A standard non-reversable action
 * 
 * @author Shu Wu
 */
public abstract class StandardAction implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean actionCompleted = false;

	private String actionName;

	private String description;

	private boolean isEnabled = true;

	/**
	 * If true, this action will execute inside the Swing Event dispatcher
	 * Thread.
	 */
	protected boolean doActionInSwingThread = true;

	/**
	 * @param description
	 *            Description of the action
	 */
	public StandardAction(String description) {
		super();
		this.description = description;
	}

	/**
	 * @param description
	 *            Description of the action
	 * @param actionName
	 *            Name to give to the Swing Action Object
	 */
	public StandardAction(String description, String actionName) {
		this(description);

		this.actionName = actionName;
	}

	/**
	 * Does the work
	 * 
	 * @return Whether the action was successful
	 */
	protected abstract void action() throws ActionException;

	/**
	 * @return Name of the action
	 */
	protected String getActionName() {
		return actionName;
	}

	/**
	 * @return Description of the action.
	 */
	protected String getDescription() {
		return description;
	}

	/**
	 * @return Whether the action succesffully completed
	 */
	protected boolean isActionCompleted() {
		return actionCompleted;
	}

	/**
	 * An subclass may put something here to do after an action has completed
	 * successfully
	 */
	protected void postAction() {

	}

	/**
	 * Does the action
	 */
	protected void doActionInternal() {

		try {
			action();			
			postAction();
			actionCompleted = true;
		} catch (ActionException e) {
			e.defaultHandleBehavior();
		}
	}

	/**
	 * Does the action layer, starts an appropriate thread
	 */
	public void doAction() {
		if (doActionInSwingThread) {
			if (SwingUtilities.isEventDispatchThread()) {
				doActionInternal();
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						doActionInternal();
					}
				});
			}

		} else {
			if (SwingUtilities.isEventDispatchThread()) {
				(new Thread() {
					public void run() {
						doActionInternal();
					}
				}).start();
			} else {
				doActionInternal();
			}
		}
	}
	
	protected void doSomething(boolean isUndo) {
		
	}

	/**
	 * @return Whether this action is enabled
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * @param isEnabled
	 *            True, if this action is enabled
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * @return Swing-type action, which can be used in Swing components
	 */
	public Action toSwingAction() {
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
	 * Action which can be used by swing components
	 * 
	 * @author Shu Wu
	 */
	class SwingAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SwingAction(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent arg0) {
			doAction();
		}
	}

	protected void setActionCompleted(boolean actionCompleted) {
		this.actionCompleted = actionCompleted;
	}

	/**
	 * @param doActionInSwingThread
	 *            If true, this action will execute inside the Swing Event
	 *            dispatcher Thread.
	 */
	protected void setThreadType(boolean doActionInSwingThread) {
		this.doActionInSwingThread = doActionInSwingThread;
	}
}
