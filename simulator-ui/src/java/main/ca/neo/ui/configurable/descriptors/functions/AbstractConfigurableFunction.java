package ca.neo.ui.configurable.descriptors.functions;

import javax.swing.JDialog;

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

	public PropertyDescriptor[] getConfigSchema() {
		return null;
	}

	public void preConfiguration(PropertySet props) throws ConfigException {
		// do nothing
	}

	/**
	 * Function to be created
	 */
	private FunctionWrapper functionWr;

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
	public AbstractConfigurableFunction(String typeName) {
		super();

		this.typeName = typeName;
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
	public void completeConfiguration(PropertySet props) throws ConfigException {

		Function function = createFunction(props);
		FunctionWrapper functionWr = new FunctionWrapper(function, props,
				getTypeName());

		setFunctionWrapper(functionWr);

	}

	public abstract void configure(JDialog parent);

	/**
	 * @return The function created
	 */
	public FunctionWrapper getFunctionWrapper() {
		return functionWr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.neo.ui.configurable.IConfigurable#getTypeName()
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @param functionWr
	 *            function wrapper
	 */
	public void setFunctionWrapper(FunctionWrapper functionWr) {
		this.functionWr = functionWr;
	}

	@Override
	public String toString() {
		return getTypeName();
	}

}