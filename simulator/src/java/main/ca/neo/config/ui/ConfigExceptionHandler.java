package ca.neo.config.ui;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

/**
 * Handles UI-generated exceptions consistently. 
 * 
 * @author Bryan Tripp
 */
public class ConfigExceptionHandler {
	
	private static Logger ourLogger = Logger.getLogger(ConfigExceptionHandler.class);
	
	public static String DEFAULT_BUG_MESSAGE 
		= "There is a programming bug in the object you are editing. Its properties may not "
			+ "display properly. The log file may contain additional information. "; 
 
	
	/**
	 * @param e Exeption to handle
	 * @param userMessage A message that can be shown to the user
	 * @param parentComponent UI component to which exception is related (can be null)
	 */
	public static void handle(Exception e, String userMessage, Component parentComponent) {
		if (userMessage == null) userMessage = DEFAULT_BUG_MESSAGE;
		ourLogger.error("User message: " + userMessage, e);
		JOptionPane.showMessageDialog(parentComponent, userMessage, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
}
