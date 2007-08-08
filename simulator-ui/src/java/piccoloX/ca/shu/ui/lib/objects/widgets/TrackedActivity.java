package ca.shu.ui.lib.objects.widgets;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.world.impl.WorldObjectImpl;

public abstract class TrackedActivity {

	String taskName;
	WorldObjectImpl wo;

	public TrackedActivity(String taskName) {
		this(taskName, null);

	}

	TrackedMsg trackedMsg;

	public TrackedActivity(String taskName, WorldObjectImpl wo) {
		super();
		this.taskName = taskName;
		this.wo = wo;

	}

	public void startAsThread() {
		startThread(false);
	}

	/**
	 * 
	 * @param invokelater
	 *            If true, the thread will be invoked on the Swing event
	 *            dispatching thread
	 */
	public void startThread(boolean invokelater) {
		trackedMsg = new TrackedMsg(taskName, wo);
		Runnable r = new Runnable() {
			public void run() {

				try {
					doActivity();
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
				trackedMsg.finished();
			}
		};

		if (invokelater) {
			SwingUtilities.invokeLater(r);

		} else {
			(new Thread(r)).start();
		}
	}

	public TrackedActivity() {
		super();
	}

	public abstract void doActivity();

}
