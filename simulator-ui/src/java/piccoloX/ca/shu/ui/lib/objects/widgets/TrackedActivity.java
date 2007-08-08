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

	public void startThread() {

		(new Thread(getRunnableThreadSafe())).start();

	}

	public void invokeLater() {
		SwingUtilities.invokeLater(getSwingRunnable());

	}

	public void invokeAndWait() {
		try {
			SwingUtilities.invokeAndWait(getSwingRunnable());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @returns a Swing Thread Safe instance of Runnable
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
	 * @return Returns a instance of Runnable which can be invoked within the
	 *         Swing Event thread
	 */
	private Runnable getSwingRunnable() {
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
