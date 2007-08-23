package ca.shu.ui.lib.objects.widgets;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.world.WorldObject;

public abstract class TrackedActivity {

	String taskName;
	TrackedStatusMsg trackedMsg;

	WorldObject wo;

	public TrackedActivity() {
		super();
	}

	public TrackedActivity(String taskName) {
		this(taskName, null);

	}

	public TrackedActivity(String taskName, WorldObject wo) {
		super();
		this.taskName = taskName;
		this.wo = wo;

	}

	public abstract void doActivity();

	public void invokeAndWait() {
		try {
			SwingUtilities.invokeAndWait(getSwingThread());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void invokeLater() {
		SwingUtilities.invokeLater(getSwingThread());

	}

	public Thread startThread() {
		Thread runner = new Thread(getRunnableThreadSafe());
		runner.start();
		return runner;

	}

	/**
	 * @returns a Thread Safe instance of Runnable
	 */
	private Runnable getRunnableThreadSafe() {

		Runnable r = new Runnable() {
			public void run() {

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						trackedMsg = new TrackedStatusMsg(taskName, wo);
					}
				});

				try {
					doActivity();
				} catch (RuntimeException e) {
					e.printStackTrace();
				}

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						trackedMsg.finished();
					}
				});

			}
		};

		return r;
	}

	/**
	 * @returns a thread that must be invoked from a Swing Thread
	 */
	private Runnable getSwingThread() {

		Runnable r = new Runnable() {
			public void run() {

				trackedMsg = new TrackedStatusMsg(taskName, wo);

				try {
					doActivity();
				} catch (RuntimeException e) {
					e.printStackTrace();
				}

				trackedMsg.finished();

			}
		};

		return r;
	}

}
