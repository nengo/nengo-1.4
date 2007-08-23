package ca.shu.ui.lib.objects.widgets;

import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.world.WorldObject;

/*
 * Tracks CPU Intensive tasks and displays it in the User Interface
 */
public class TrackedStatusMsg {
	String taskName;

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

	/*
	 * Stop tracking the task. ie. remove it from the User Interface
	 */
	public void finished() {
		UIEnvironment.getInstance().popTaskStatusStr(getTaskName());
	}

	private void init() {
		UIEnvironment.getInstance().pushTaskStatusStr(getTaskName());

	}

	protected String getTaskName() {
		return taskName;
	}

	protected void setTaskName(String taskName) {
		this.taskName = taskName;
	}
}
