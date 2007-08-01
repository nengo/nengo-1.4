package ca.shu.ui.lib.objects.widgets;

import ca.shu.ui.lib.util.GraphicsEnvironment;

/*
 * Tracks CPU Intensive tasks and displays it in the User Interfaace
 */
public class TrackedTask {
	String taskName;

	public TrackedTask(String taskName) {
		super();
		this.taskName = taskName;
		GraphicsEnvironment.getInstance().pushTaskStatusStr(taskName);
	}
	
	/*
	 * Stop tracking the task. ie. remove it from the User Interface
	 */
	public void finished() {
		GraphicsEnvironment.getInstance().popTaskStatusStr(taskName);
	}
}
