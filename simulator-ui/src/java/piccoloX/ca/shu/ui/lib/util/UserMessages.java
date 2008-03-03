package ca.shu.ui.lib.util;

import java.awt.Component;

import javax.swing.JOptionPane;

import ca.shu.ui.lib.exceptions.UIException;

/**
 * Displays messages to the user through a popup dialog.
 * 
 * @author Shu Wu
 */
public class UserMessages {
	public static void showError(String msg) {
		showError(msg, UIEnvironment.getInstance());
	}

	public static String askDialog(String dialogMessage) throws DialogException {
		String userName = JOptionPane.showInputDialog(UIEnvironment
				.getInstance(), dialogMessage);

		if (userName == null || userName.compareTo("") == 0) {
			throw new DialogException();
		}
		return userName;
	}

	public static class DialogException extends UIException {

		private static final long serialVersionUID = 1L;

		public DialogException() {
			super("Dialog cancelled");
		}

		public DialogException(String arg0) {
			super(arg0);
		}
	}

	public static void showError(String msg, Component parent) {
		JOptionPane.showMessageDialog(parent, "<HTML>" + msg + "</HTML>",
				"Error", JOptionPane.ERROR_MESSAGE);
		(new Exception(msg)).printStackTrace();

	}

	public static void showWarning(String msg) {
		showWarning(msg, UIEnvironment.getInstance());
	}

	public static void showWarning(String msg, Component parent) {
		JOptionPane.showMessageDialog(parent, "<HTML>" + msg + "</HTML>",
				"Warning", JOptionPane.WARNING_MESSAGE);
	}
}
