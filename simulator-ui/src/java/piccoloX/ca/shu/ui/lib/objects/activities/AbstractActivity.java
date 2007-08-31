package ca.shu.ui.lib.objects.activities;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.world.WorldObject;

/**
 * An activity which is tracked by the application UI. An activity can be
 * executed in several ways, one of which is Swing thread safe.
 * 
 * @author Shu Wu
 */
public abstract class AbstractActivity {

	private String taskName;

	private TrackedStatusMsg trackedMsg;

	private WorldObject wo;

	public AbstractActivity() {
		super();
	}

	public AbstractActivity(String taskName) {
		this(taskName, null);

	}

	public AbstractActivity(String taskName, WorldObject wo) {
		super();
		this.taskName = taskName;
		this.wo = wo;

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

}
