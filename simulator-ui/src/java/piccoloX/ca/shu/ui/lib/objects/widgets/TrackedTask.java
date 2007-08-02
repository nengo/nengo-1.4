package ca.shu.ui.lib.objects.widgets;

import ca.neo.ui.models.PModel;
import ca.shu.ui.lib.util.GraphicsEnvironment;
import ca.shu.ui.lib.world.WorldObject;

/*
 * Tracks CPU Intensive tasks and displays it in the User Interfaace
 */
public class TrackedTask {
	String taskName;

	public TrackedTask(String taskName) {
		super();
		setTaskName(taskName);
		init();
	}

	public TrackedTask(WorldObject wo, String taskName) {
		super();
		setTaskName(wo.getName() + ": " + taskName);
		init();
	}

	private void init() {
		GraphicsEnvironment.getInstance().pushTaskStatusStr(getTaskName());

	}

	/*
	 * Stop tracking the task. ie. remove it from the User Interface
	 */
	public void finished() {
		GraphicsEnvironment.getInstance().popTaskStatusStr(getTaskName());
	}

	protected void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	protected String getTaskName() {
		return taskName;
	}
}
