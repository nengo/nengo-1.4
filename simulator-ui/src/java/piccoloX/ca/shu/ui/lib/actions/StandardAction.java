package ca.shu.ui.lib.actions;

import java.awt.event.ActionEvent;
import java.io.Serializable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;

import ca.shu.ui.lib.util.UserMessages;

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
	 * Thread. If false, the action can execute in any non-swing thread. The
	 * second type allows actions to proceed without blocking the UI.s
	 */
	protected RunThreadType runSwingType = RunThreadType.JAVA_SWING;

	/**
	 * @param description
	 *            Description of the action
	 */
	public StandardAction(String description) {
		this(description, null, true);

	}

	public StandardAction(String description, String actionName) {
		this(description, actionName, true);
	}

	/**
	 * @param description
	 *            Description of the action
	 * @param actionName
	 *            Name to give to the Swing Action Object
	 * @param threadType
	 */
	public StandardAction(String description, String actionName,
			boolean isSwingAction) {
		super();
		if (isSwingAction) {
			this.runSwingType = RunThreadType.JAVA_SWING;
		} else {
			this.runSwingType = RunThreadType.NON_SWING;
		}
		this.description = description;
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
		} catch (RuntimeException e) {
			e.printStackTrace();
			UserMessages.showWarning("Unexpected Exception: " + e.toString());
		}
	}

	/**
	 * Does the action layer, starts an appropriate thread
	 */
	public void doAction() {
		if (runSwingType == RunThreadType.JAVA_SWING) {
			if (SwingUtilities.isEventDispatchThread()) {
				doActionInternal();
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						doActionInternal();
					}
				});
			}

		} else if (runSwingType == RunThreadType.NON_SWING) {
			if (SwingUtilities.isEventDispatchThread()) {
				(new Thread(getDescription()) {
					public void run() {
						doActionInternal();
					}
				}).start();
			} else {
				doActionInternal();
			}
		} else {
			throw new UnsupportedOperationException();
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

	public enum RunThreadType {
		JAVA_SWING, NON_SWING
	}

}
