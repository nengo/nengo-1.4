package ca.shu.ui.lib.objects.activities;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;

/**
 * An action which is tracked by the UI. Since tracked actions are slow and have
 * UI messages associated with them, they do never execute inside the Swing
 * dispatcher thread.
 * 
 * @author Shu Wu
 */
public abstract class TrackedAction extends StandardAction {

	private static final long serialVersionUID = 1L;

	private String taskName;

	private TrackedStatusMsg trackedMsg;

	private WorldObjectImpl wo;

	public TrackedAction(String taskName) {
		this(taskName, null);

	}

	public TrackedAction(String taskName, WorldObjectImpl wo) {
		super(taskName, null, false);
		this.taskName = taskName;
		this.wo = wo;

	}

	@Override
	public void doAction() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				trackedMsg = new TrackedStatusMsg(taskName, wo);
			}
		});
		super.doAction();

	}

	@Override
	protected void postAction() {
		super.postAction();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				trackedMsg.finished();
			}
		});
	}

}
