package ca.shu.ui.lib.util;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * Displays messages to the user through a popup dialog.
 * 
 * @author Shu Wu
 */
public class UserMessages {
	public static void showError(String msg) {
		showError(msg, UIEnvironment.getInstance());
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
