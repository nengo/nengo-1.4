package ca.nengo.ui.models.constructors;

import ca.nengo.math.Function;
import ca.nengo.model.Node;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Units;
import ca.nengo.model.impl.FunctionInput;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.ConfigResult;
import ca.nengo.ui.configurable.ConfigSchemaImpl;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.descriptors.PFunctionArray;
import ca.nengo.ui.models.nodes.UIFunctionInput;

public class CFunctionInput extends ConstructableNode {

	public CFunctionInput() {
		super();
	}

	private static Property pFunctions = new PFunctionArray("Functions Generators", 1);

	@Override
	protected Node createNode(ConfigResult props, String name) throws ConfigException {

		Function[] functions = (Function[]) props.getValue(pFunctions);

		try {
			// setName((String) getProperty(pName));
			return new FunctionInput(name, functions, Units.UNK);
		} catch (StructuralException e) {
			throw new ConfigException(e.getMessage());

		}

	}

	@Override
	public ConfigSchemaImpl getNodeConfigSchema() {
		return new ConfigSchemaImpl(pFunctions);
	}

	public String getTypeName() {
		return UIFunctionInput.typeName;
	}
}
