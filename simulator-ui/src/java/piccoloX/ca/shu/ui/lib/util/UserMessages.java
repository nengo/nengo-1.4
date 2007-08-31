package ca.shu.ui.lib.util;

import javax.swing.JOptionPane;

/**
 * Displays messages to the user through a popup dialog.
 * 
 * @author Shu Wu
 */
public class UserMessages {
	public static void showError(String msg) {
		JOptionPane.showMessageDialog(UIEnvironment.getInstance(), msg,
				"Error", JOptionPane.ERROR_MESSAGE);
		(new Exception(msg)).printStackTrace();

	}

	public static void showWarning(String msg) {
		JOptionPane.showMessageDialog(UIEnvironment.getInstance(), msg,
				"Warning", JOptionPane.WARNING_MESSAGE);
	}
}
