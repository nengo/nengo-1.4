package ca.neo.ui.exceptions;

/**
 * Problem configuring the model inside the UI
 * 
 * @author Shu
 * 
 */
public class ModelConfigurationException extends NeoGraphicsException {

	private static final long serialVersionUID = 1L;

	public ModelConfigurationException() {
		super();
	}

	public ModelConfigurationException(String message) {
		super(message);
	}

}
