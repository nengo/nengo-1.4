package ca.nengo.ui.configurable.descriptors.functions;

import java.awt.Dialog;

import ca.nengo.math.Function;

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

	public Function getFunction();

	/**
	 * Configures the function
	 * 
	 * @param parent
	 *            Component to attach the configuration dialog to
	 * @return Configured function
	 */
	public Function configureFunction(Dialog parent);

}
