package ca.neo.ui.configurable.descriptors.functions;

import java.awt.Dialog;

import ca.neo.math.Function;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.IConfigurable;
import ca.neo.ui.configurable.ConfigResult;
import ca.neo.ui.configurable.managers.UserConfigurer;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Describes how to configure a function through the IConfigurable interface.
 * 
 * @author Shu Wu
 */
/**
 * @author User
 */
public abstract class AbstractFn implements IConfigurable, ConfigurableFunction {

	private UserConfigurer configurer;

	/**
	 * Function to be created
	 */
	private Function function;

	/**
	 * What the type of function to be created is called
	 */
	private String typeName;

	private Class<? extends Function> functionType;

	/**
	 * @param typeName
	 * @param functionType
	 */
	public AbstractFn(String typeName, Class<? extends Function> functionType) {
		super();

		this.typeName = typeName;
		this.functionType = functionType;
	}

	protected abstract Function createFunction(ConfigResult props) throws ConfigException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.IConfigurable#completeConfiguration(ca.neo.ui.configurable.ConfigParam)
	 *      Creates the function through reflection of its constructor and
	 *      passing the user parameters to it
	 */
	public void completeConfiguration(ConfigResult props) throws ConfigException {
		try {
			Function function = createFunction(props);
			setFunction(function);
		} catch (Exception e) {
			throw new ConfigException("Error creating function");
		}
	}

	/**
	 * @return The function created
	 */
	public Function getFunction() {
		return function;
	}

	public Class<? extends Function> getFunctionType() {
		return functionType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.IConfigurable#getTypeName()
	 */
	public String getTypeName() {
		return typeName;
	}

	public void preConfiguration(ConfigResult props) throws ConfigException {
		// do nothing
	}

	public Function configureFunction(Dialog parent) {
		if (parent != null) {

			if (configurer == null) {
				configurer = new UserConfigurer(this, parent);
			}
			try {
				configurer.configureAndWait();
				return getFunction();
			} catch (ConfigException e) {
				e.defaultHandleBehavior();
			}
		} else {
			UserMessages.showError("Could not attach properties dialog");
		}
		return null;
	}

	/**
	 * @param function
	 *            function wrapper
	 * @throws ConfigException
	 */
	public final void setFunction(Function function) {
		if (function != null) {
			if (!getFunctionType().isInstance(function)) {
				throw new IllegalArgumentException("Unexpected function type");
			} else {
				this.function = function;
			}
		} else {
			this.function = null;
		}
	}

	@Override
	public String toString() {
		return getTypeName();
	}

}