package ca.neo.ui.configurable;

import ca.shu.ui.lib.exceptions.UIException;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Exception to be thrown is if there is an error during Configuration
 * 
 * @author Shu Wu
 */
public class ConfigException extends UIException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 *            Message describing the exception
	 */
	public ConfigException(String message) {
		super(message);
	}

	@Override
	public void defaultHandleBehavior() {
		UserMessages.showWarning("Could not complete configuration: " + getMessage());
	}

}
