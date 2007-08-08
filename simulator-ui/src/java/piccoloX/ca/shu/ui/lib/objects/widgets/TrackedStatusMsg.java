package ca.shu.ui.lib.objects.widgets;

import javax.swing.SwingUtilities;

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

	private void init() {
		UIEnvironment.getInstance().pushTaskStatusStr(getTaskName());

	}

	/*
	 * Stop tracking the task. ie. remove it from the User Interface
	 */
	public void finished() {
		UIEnvironment.getInstance().popTaskStatusStr(getTaskName());
	}

	protected void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	protected String getTaskName() {
		return taskName;
	}
}
