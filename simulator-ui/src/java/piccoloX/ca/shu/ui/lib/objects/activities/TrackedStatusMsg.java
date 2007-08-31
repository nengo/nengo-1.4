package ca.shu.ui.lib.objects.activities;

import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.world.WorldObject;

/**
 * Displays and removes a task message from the application status bar
 * 
 * @author Shu Wu
 */
public class TrackedStatusMsg {
	private String taskName;

	public TrackedStatusMsg(String taskName) {
		this(taskName, null);
	}

	public TrackedStatusMsg(String taskName, WorldObject wo) {
		super();

		if (wo != null) {
			setTaskName(wo.getName() + ": " + taskName);
		} else {
			setTaskName(taskName);
		}
		init();
	}

	private void init() {
		UIEnvironment.getInstance().addTaskStatusMsg(getTaskName());

	}

	protected String getTaskName() {
		return taskName;
	}

	protected void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * Removes the task message from the application status bar.
	 */
	public void finished() {
		UIEnvironment.getInstance().removeTaskStatusMsg(getTaskName());
	}
}
