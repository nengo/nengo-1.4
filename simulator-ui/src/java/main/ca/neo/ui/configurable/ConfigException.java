package ca.neo.ui.configurable;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.exceptions.UIException;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Exception to be thrown is if there is an error during Configuration
 * 
 * @author Shu Wu
 */
public class ConfigException extends UIException {

	private static final long serialVersionUID = 1L;
	private boolean isRecoverable;

	/**
	 * @param message
	 *            Message describing the exception
	 */
	public ConfigException(String message) {
		this(message, true);
	}

	/**
	 * @param message
	 *            Message describing the exception
	 * @param isRecoverable
	 *            if true, the Configurer will try to recover from the exception
	 */
	public ConfigException(String message, boolean isRecoverable) {
		super(message);
		this.isRecoverable = isRecoverable;
	}

	@Override
	public void defaultHandleBehavior() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UserMessages.showWarning("Could not complete configuration: "
						+ getMessage());
			}
		});

	}

	public boolean isRecoverable() {
		return isRecoverable;
	}

}
