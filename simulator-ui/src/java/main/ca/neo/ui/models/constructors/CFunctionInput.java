package ca.neo.ui.models.constructors;

import ca.neo.math.Function;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.Units;
import ca.neo.model.impl.FunctionInput;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PFunctionArray;
import ca.neo.ui.configurable.descriptors.PString;

public class CFunctionInput extends Constructable {
	private static PropertyDescriptor pName = new PString("Name");
	private static PropertyDescriptor pFunctions = new PFunctionArray("Functions Generators", 1);

	/**
	 * Config Descriptors
	 */
	private static PropertyDescriptor[] zConfig = { pName, pFunctions };

	@Override
	protected Node configureModel(PropertySet props) throws ConfigException {

		Function[] functions = (Function[]) props.getProperty(pFunctions);

		try {
			// setName((String) getProperty(pName));
			return new FunctionInput((String) props.getProperty(pName), functions, Units.UNK);
		} catch (StructuralException e) {
			throw new ConfigException(e.getMessage());

		}

	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		return zConfig;
	}

	public String getTypeName() {
		return "Function Input";
	}
}
