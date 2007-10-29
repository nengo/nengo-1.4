package ca.neo.ui.configurable.descriptors.functions;

import ca.neo.math.Function;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.IConfigurable;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;

/**
 * Describes how to configure a function through the IConfigurable interface.
 * 
 * @author Shu Wu
 */
/**
 * @author User
 */
public abstract class AbstractConfigurableFunction implements IConfigurable {

	/**
	 * Function to be created
	 */
	private Function function;

	@SuppressWarnings("unchecked")
	private Class functionClass;

	/**
	 * A list of parameters required to create this function
	 */
	private PropertyDescriptor[] propStruct;

	/**
	 * What the type of function to be created is called
	 */
	private String typeName;

	/**
	 * @param functionClass
	 *            Class the function to be created belongs to
	 * @param typeName
	 *            Name of the type the function
	 * @param propStruct
	 *            A list of parameters referencing the constructor parameters
	 *            required to create the function
	 */
	public AbstractConfigurableFunction(Class<?> functionClass,
			String typeName, PropertyDescriptor[] propStruct) {
		super();

		this.functionClass = functionClass;
		this.propStruct = propStruct;
		this.typeName = typeName;
	}

	/**
	 * @param function
	 *            function
	 */
	public void setFunction(Function function) {
		this.function = function;
	}

	protected abstract Function createFunction(PropertySet props)
			throws ConfigException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.IConfigurable#completeConfiguration(ca.neo.ui.configurable.ConfigParam)
	 *      Creates the function through reflection of its constructor and
	 *      passing the user parameters to it
	 */
	@SuppressWarnings("unchecked")
	public void completeConfiguration(PropertySet props) throws ConfigException {

		Function function = createFunction(props);
		setFunction(function);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.IConfigurable#getConfigSchema()
	 */
	public PropertyDescriptor[] getConfigSchema() {
		return propStruct;
	}

	/**
	 * @return The function created
	 */
	public Function getFunction() {
		return function;
	}

	/**
	 * @return The Class type the function belongs to
	 */
	@SuppressWarnings("unchecked")
	public Class getFunctionType() {
		return functionClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.IConfigurable#getTypeName()
	 */
	public String getTypeName() {
		return typeName;
	}

	@Override
	public String toString() {
		return getTypeName();
	}

}