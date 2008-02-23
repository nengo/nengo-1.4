package ca.neo.ui.configurable.descriptors.functions;

import javax.swing.JDialog;

import ca.neo.math.Function;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.IConfigurable;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.managers.UserConfigurer;

/**
 * Describes how to configure a function through the IConfigurable interface.
 * 
 * @author Shu Wu
 */
/**
 * @author User
 */
public abstract class AbstractFn implements IConfigurable {

	private UserConfigurer configurer;

	/**
	 * Function to be created
	 */
	private Function function;

	/**
	 * What the type of function to be created is called
	 */
	private String typeName;

	private Class<?> functionType;

	/**
	 * @param typeName
	 * @param functionType
	 */
	public AbstractFn(String typeName, Class<?> functionType) {
		super();

		this.typeName = typeName;
		this.functionType = functionType;
	}

	protected abstract Function createFunction(PropertySet props) throws ConfigException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.IConfigurable#completeConfiguration(ca.neo.ui.configurable.ConfigParam)
	 *      Creates the function through reflection of its constructor and
	 *      passing the user parameters to it
	 */
	public void completeConfiguration(PropertySet props) throws ConfigException {
		try {
			Function function = createFunction(props);
			setFunction(function);
		} catch (Exception e) {
			throw new ConfigException("Error creating function");
		}
	}

	public void configure(JDialog parent) {
		if (configurer == null) {
			configurer = new UserConfigurer(this, parent);
		}
		try {
			configurer.configureAndWait();
		} catch (ConfigException e) {
			e.defaultHandleBehavior();
		}

	}

	/**
	 * @return The function created
	 */
	public Function getFunction() {
		return function;
	}

	public Class<?> getFunctionType() {
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

	public void preConfiguration(PropertySet props) throws ConfigException {
		// do nothing
	}

	/**
	 * @param function
	 *            function wrapper
	 * @throws ConfigException
	 */
	public void setFunction(Function function) {
		if (!getFunctionType().isInstance(function)) {
			throw new IllegalArgumentException("Unexpected function type");
		} else {
			this.function = function;
		}
	}

	@Override
	public String toString() {
		return getTypeName();
	}

}