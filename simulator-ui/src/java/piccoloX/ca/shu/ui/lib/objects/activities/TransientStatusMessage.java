package ca.shu.ui.lib.objects.activities;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.util.UIEnvironment;

/**
 * Displays a task message in the status bar which appears for a finite
 * duration.
 * 
 * @author Shu Wu
 */
public class TransientStatusMessage {

	private long myDuration;

	private String myMessage;

	public TransientStatusMessage(String msg, long duration) {
		super();
		myMessage = msg;
		myDuration = duration;

		Thread myMsgRunner = new Thread() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						UIEnvironment.getInstance().addTaskStatusMsg(myMessage);
					}
				});
				try {
					Thread.sleep(myDuration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						UIEnvironment.getInstance().removeTaskStatusMsg(
								myMessage);
					}
				});

			}
		};
		myMsgRunner.start();

	}

}
