package ca.shu.ui.lib.objects.widgets;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.world.impl.WorldObjectImpl;

public abstract class TrackedActivity {

	String taskName;
	WorldObjectImpl wo;

	public TrackedActivity(String taskName) {
		this(taskName, null);

	}

	TrackedStatusMsg trackedMsg;

	public TrackedActivity(String taskName, WorldObjectImpl wo) {
		super();
		this.taskName = taskName;
		this.wo = wo;

	}

	public Thread startThread() {
		Thread runner = new Thread(getRunnableThreadSafe());
		runner.start();
		return runner;

	}

	public void invokeLater() {
		SwingUtilities.invokeLater(getSwingThread());

	}

	public void invokeAndWait() {
		try {
			SwingUtilities.invokeAndWait(getSwingThread());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @returns a Thread Safe instance of Runnable
	 */
	private Runnable getRunnableThreadSafe() {

		Runnable r = new Runnable() {
			public void run() {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							trackedMsg = new TrackedStatusMsg(taskName, wo);
						}
					});

					try {
						doActivity();
					} catch (RuntimeException e) {
						e.printStackTrace();
					}

					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							trackedMsg.finished();
						}
					});

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}

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

	public TrackedActivity() {
		super();
	}

	public abstract void doActivity();

}
