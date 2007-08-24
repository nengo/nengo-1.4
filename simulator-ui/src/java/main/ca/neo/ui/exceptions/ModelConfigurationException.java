package ca.neo.ui.exceptions;

import ca.shu.ui.lib.exceptions.UIException;

/**
 * Problem configuring the model inside the UI
 * 
 * @author Shu
 * 
 */
public class ModelConfigurationException extends UIException {

	private static final long serialVersionUID = 1L;

	public ModelConfigurationException() {
		super();
	}

	public ModelConfigurationException(String message) {
		super(message);
	}

}
