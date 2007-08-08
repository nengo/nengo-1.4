package ca.shu.ui.lib.objects.widgets;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.util.UIEnvironment;

public class TransientStatusMessage {
	String myMessage;

	public static void show(String msg, long duration) {
		new TransientStatusMessage(msg, duration);
	}

	long myDuration;
	protected TransientStatusMessage(String msg, long duration) {
		super();
		myMessage = msg;
		myDuration = duration;

		Thread myMsgRunner = new Thread() {
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						UIEnvironment.getInstance()
								.pushTaskStatusStr(myMessage);
					}
				});
				try {
					Thread.sleep(myDuration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						UIEnvironment.getInstance().popTaskStatusStr(myMessage);
					}
				});

			}
		};
		myMsgRunner.start();

	}

}
