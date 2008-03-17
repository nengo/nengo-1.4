package ca.neo.ui.configurable.descriptors.functions;

import java.awt.Dialog;

import ca.neo.math.Function;

public interface ConfigurableFunction {
	/**
	 * @return Type of function to be created
	 */
	public Class<? extends Function> getFunctionType();

	/**
	 * This method is optional
	 * 
	 * @param function
	 *            Function to be configured.
	 */
	public void setFunction(Function function);

	/**
	 * Configures the function
	 * 
	 * @param parent
	 *            Component to attach the configuration dialog to
	 * @return Configured function
	 */
	public Function configureFunction(Dialog parent);
}
